-- =====================================================================
-- V38: MERGE MEDICAL_RECORDS + PREVENTIVE_CARE_RECORDS -> HEALTH_RECORDS
--
-- Concept:
--   preventive_care_schedules = planned/future work
--   health_records            = actual Vet encounter/event
--   horse_health_metrics      = measurements captured during an encounter
--   injury_records            = injury-specific extension
--   treatment_plans           = optional treatment for a health record
--   prescriptions             = optional medication under a treatment plan
-- =====================================================================

-- 1. Preventive schedules support an unscheduled INITIAL_EXAM generated when
--    Groom creates/reuses a Horse and assigns a quarantine stall.
ALTER TABLE preventive_care_schedules
    ALTER COLUMN veterinarian_id DROP NOT NULL,
    ALTER COLUMN scheduled_date DROP NOT NULL;

-- New work uses PENDING instead of SCHEDULED. Keep OVERDUE for compatibility
-- with existing data/code; it may be retained or later derived in service/UI.
UPDATE preventive_care_schedules
SET status = 'PENDING'
WHERE status = 'SCHEDULED';

ALTER TABLE preventive_care_schedules
    ALTER COLUMN status SET DEFAULT 'PENDING';

ALTER TABLE preventive_care_schedules
    ADD CONSTRAINT chk_preventive_care_schedule_status
        CHECK (status IN ('PENDING', 'COMPLETED', 'OVERDUE', 'CANCELLED'));

-- Only INITIAL_EXAM is intentionally allowed to be created without a date.
ALTER TABLE preventive_care_schedules
    ADD CONSTRAINT chk_preventive_care_schedule_date
        CHECK (care_type = 'INITIAL_EXAM' OR scheduled_date IS NOT NULL);

-- A Horse can have only one outstanding admission INITIAL_EXAM at a time.
CREATE UNIQUE INDEX uq_pcs_pending_initial_exam_per_horse
    ON preventive_care_schedules(horse_id)
    WHERE care_type = 'INITIAL_EXAM'
      AND status IN ('PENDING', 'OVERDUE');

-- 2. Reuse the existing medical_records table so all existing treatment and
--    injury foreign keys keep their data. It becomes the unified health log.
ALTER TABLE medical_records
    RENAME TO health_records;

ALTER INDEX IF EXISTS idx_medical_records_horse_examined
    RENAME TO idx_health_records_horse_examined;

ALTER INDEX IF EXISTS idx_medical_records_vet_examined
    RENAME TO idx_health_records_vet_examined;

ALTER TABLE health_records
    RENAME COLUMN clinical_findings TO findings;

-- Preventive/routine encounters may have no diagnosis.
ALTER TABLE health_records
    ALTER COLUMN diagnosis DROP NOT NULL;

ALTER TABLE health_records
    ADD COLUMN record_type VARCHAR(50) NOT NULL DEFAULT 'ILLNESS',
    ADD COLUMN preventive_care_schedule_id BIGINT NULL,
    ADD COLUMN product_or_service VARCHAR(255) NULL;

ALTER TABLE health_records
    ADD CONSTRAINT fk_health_records_preventive_schedule
        FOREIGN KEY (preventive_care_schedule_id)
        REFERENCES preventive_care_schedules(id)
        ON DELETE SET NULL;

ALTER TABLE health_records
    ADD CONSTRAINT uq_health_records_preventive_schedule
        UNIQUE (preventive_care_schedule_id);

CREATE INDEX idx_health_records_type_examined
    ON health_records(record_type, examined_at);

-- Medical records that already have an injury extension are injury encounters.
UPDATE health_records hr
SET record_type = 'INJURY'
WHERE EXISTS (
    SELECT 1
    FROM injury_records ir
    WHERE ir.medical_record_id = hr.id
);

-- 3. Migrate historical preventive care records into the unified table.
--    Unknown legacy care types are preserved as their original uppercase text
--    rather than being silently reclassified.
INSERT INTO health_records (
    horse_id,
    veterinarian_id,
    source_training_workout_id,
    examined_at,
    symptoms,
    findings,
    diagnosis,
    notes,
    follow_up_date,
    created_at,
    updated_at,
    record_type,
    preventive_care_schedule_id,
    product_or_service
)
SELECT
    pcr.horse_id,
    pcr.veterinarian_id,
    NULL,
    pcr.performed_at,
    NULL,
    pcr.result,
    NULL,
    pcr.notes,
    pcr.next_due_date,
    pcr.created_at,
    pcr.created_at,
    UPPER(TRIM(pcr.care_type)),
    pcr.schedule_id,
    pcr.product_or_service
FROM preventive_care_records pcr;

-- 4. Preserve legacy next_due_date semantics by materializing a NEW pending
--    schedule row instead of mutating the completed schedule in place.
INSERT INTO preventive_care_schedules (
    horse_id,
    veterinarian_id,
    care_type,
    scheduled_date,
    description,
    status,
    created_at,
    updated_at
)
SELECT
    pcr.horse_id,
    pcr.veterinarian_id,
    pcr.care_type,
    pcr.next_due_date,
    'Migrated next due date from legacy preventive care record #' || pcr.id,
    'PENDING',
    NOW(),
    NOW()
FROM preventive_care_records pcr
WHERE pcr.next_due_date IS NOT NULL
  AND NOT EXISTS (
      SELECT 1
      FROM preventive_care_schedules pcs
      WHERE pcs.horse_id = pcr.horse_id
        AND pcs.care_type = pcr.care_type
        AND pcs.scheduled_date = pcr.next_due_date
        AND pcs.status IN ('PENDING', 'OVERDUE')
  );

-- 5. TreatmentPlan / InjuryRecord now reference HealthRecord by name too.
ALTER TABLE treatment_plans
    RENAME COLUMN medical_record_id TO health_record_id;

ALTER INDEX IF EXISTS idx_treatment_plans_record_status
    RENAME TO idx_treatment_plans_health_record_status;

ALTER TABLE treatment_plans
    RENAME CONSTRAINT treatment_plans_medical_record_id_fkey
    TO treatment_plans_health_record_id_fkey;

ALTER TABLE injury_records
    RENAME COLUMN medical_record_id TO health_record_id;

ALTER INDEX IF EXISTS idx_injury_records_medical_record
    RENAME TO idx_injury_records_health_record;

ALTER TABLE injury_records
    RENAME CONSTRAINT injury_records_medical_record_id_fkey
    TO injury_records_health_record_id_fkey;

-- Rename workout FK constraint left from V22 for schema readability.
ALTER TABLE health_records
    RENAME CONSTRAINT fk_medical_records_workout
    TO fk_health_records_workout;

-- 6. Link measurement snapshots back to the Vet encounter that produced them.
ALTER TABLE horse_health_metrics
    ADD COLUMN health_record_id BIGINT NULL;

ALTER TABLE horse_health_metrics
    ADD CONSTRAINT fk_horse_health_metrics_health_record
        FOREIGN KEY (health_record_id)
        REFERENCES health_records(id)
        ON DELETE SET NULL;

CREATE INDEX idx_horse_health_metrics_health_record
    ON horse_health_metrics(health_record_id);

-- 7. All actual-care data is now represented by health_records; future work
--    remains in preventive_care_schedules.
DROP TABLE preventive_care_records;
