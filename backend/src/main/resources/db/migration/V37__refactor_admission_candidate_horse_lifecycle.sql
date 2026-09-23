-- =====================================================================
-- V37: REFACTOR ADMISSION -> CANDIDATE HORSE LIFECYCLE
--
-- New lifecycle:
--   Owner submit                         -> no Horse yet
--   Groom approves + Q stall assigned   -> create/reuse Horse(CANDIDATE)
--   Vet review                          -> Horse remains CANDIDATE
--   Trainer assessment                  -> Horse remains CANDIDATE
--   Manager approve                     -> Horse becomes ELIGIBLE
--   Vet/Manager reject after Horse exists -> Horse becomes REJECTED
--
-- A REJECTED Horse may be reused by a later Admission. Therefore one Horse
-- can have many historical AdmissionApplication rows over time.
-- =====================================================================

-- 1. Horse status now distinguishes admission candidates from normal
--    quarantine used for already-admitted horses.
ALTER TABLE horses
    DROP CONSTRAINT IF EXISTS chk_horses_current_status;

ALTER TABLE horses
    ALTER COLUMN current_status SET DEFAULT 'CANDIDATE';

ALTER TABLE horses
    ADD CONSTRAINT chk_horses_current_status
        CHECK (current_status IN (
            'CANDIDATE',
            'ELIGIBLE',
            'MONITORING',
            'INJURED',
            'QUARANTINED',
            'REJECTED'
        ));

-- 2. The Horse is now created/reused during Groom processing, not during
--    Manager final approval. Rename the admission FK accordingly.
--
--    The old UNIQUE constraint must be removed because the same Horse may
--    have Admission #1 = REJECTED and Admission #2 = APPROVED later.
ALTER TABLE admission_applications
    DROP CONSTRAINT IF EXISTS admission_applications_resulting_horse_id_key;

ALTER TABLE admission_applications
    RENAME COLUMN resulting_horse_id TO horse_id;

-- PostgreSQL keeps the old FK constraint name after a column rename.
-- Rename it only for schema readability.
ALTER TABLE admission_applications
    RENAME CONSTRAINT admission_applications_resulting_horse_id_fkey
    TO admission_applications_horse_id_fkey;

CREATE INDEX idx_admission_horse
    ON admission_applications(horse_id);

-- At most one active post-Groom admission may reference a Horse at a time.
-- Historical APPROVED/REJECTED admissions are intentionally allowed.
CREATE UNIQUE INDEX uq_admission_active_horse
    ON admission_applications(horse_id)
    WHERE horse_id IS NOT NULL
      AND status NOT IN ('APPROVED', 'REJECTED');

-- 3. Trainer no longer APPROVES/REJECTS admission.
--    Trainer records readiness assessment + feedback + reviewed_at, then the
--    service advances the Admission to MANAGER_REVIEW.
ALTER TABLE admission_applications
    DROP CONSTRAINT IF EXISTS chk_admission_trainer_decision;

ALTER TABLE admission_applications
    DROP COLUMN IF EXISTS trainer_decision;

-- 4. Once admission has moved to Vet/Trainer/Manager review, a candidate
--    Horse must already exist. REJECTED is excluded because Groom rejection
--    happens before Horse creation while Vet/Manager rejection happens after.
ALTER TABLE admission_applications
    ADD CONSTRAINT chk_admission_post_groom_has_horse
        CHECK (
            status NOT IN ('VET_REVIEW', 'TRAINER_REVIEW', 'MANAGER_REVIEW', 'APPROVED')
            OR horse_id IS NOT NULL
        ) NOT VALID;

-- NOT VALID intentionally avoids breaking developer databases containing
-- manually-created rows from the old flow. PostgreSQL still enforces this
-- constraint for new/updated rows. After legacy rows are cleaned, the team may
-- run VALIDATE CONSTRAINT in a later migration.
--
-- Existing APPROVED invariant remains valid after the column rename because
-- PostgreSQL rewrites the referenced column inside the constraint expression.
