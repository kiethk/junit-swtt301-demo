-- =====================================================================
-- V40: REMOVE LEGACY FICTIONAL HORSE SEED DATA FROM V12/V15
--
-- Targets only the four original V12 sample horses owned by owner@rtms.com:
--   Lightning Bolt, Silver Moon, Golden Star, Storm King
--
-- Dependent development/test rows are removed first so the migration remains
-- valid even when teammates used those seed horses during manual testing.
-- =====================================================================

CREATE TEMP TABLE legacy_fake_horse_ids ON COMMIT DROP AS
SELECT h.id
FROM horses h
JOIN users u ON u.id = h.owner_id
WHERE u.email = 'owner@rtms.com'
  AND h.name IN ('Lightning Bolt', 'Silver Moon', 'Golden Star', 'Storm King');

-- Release any stalls occupied by these legacy seed horses.
UPDATE stable_stalls ss
SET status = 'AVAILABLE',
    updated_at = NOW()
WHERE ss.id IN (
    SELECT h.current_stall_id
    FROM horses h
    WHERE h.id IN (SELECT id FROM legacy_fake_horse_ids)
      AND h.current_stall_id IS NOT NULL
);

-- Admissions linked to these fake seed horses are development data as well.
-- Admission documents/candidate profiles are removed by ON DELETE CASCADE.
DELETE FROM admission_applications
WHERE horse_id IN (SELECT id FROM legacy_fake_horse_ids);

DELETE FROM prescriptions
WHERE treatment_plan_id IN (
    SELECT tp.id
    FROM treatment_plans tp
    JOIN health_records hr ON hr.id = tp.health_record_id
    WHERE hr.horse_id IN (SELECT id FROM legacy_fake_horse_ids)
);

DELETE FROM treatment_plans
WHERE health_record_id IN (
    SELECT id
    FROM health_records
    WHERE horse_id IN (SELECT id FROM legacy_fake_horse_ids)
);

DELETE FROM injury_records
WHERE horse_id IN (SELECT id FROM legacy_fake_horse_ids);

DELETE FROM horse_health_metrics
WHERE horse_id IN (SELECT id FROM legacy_fake_horse_ids);

DELETE FROM health_records
WHERE horse_id IN (SELECT id FROM legacy_fake_horse_ids);

DELETE FROM preventive_care_schedules
WHERE horse_id IN (SELECT id FROM legacy_fake_horse_ids);

DELETE FROM racing_readiness_assessments
WHERE horse_id IN (SELECT id FROM legacy_fake_horse_ids);

DELETE FROM financial_reports
WHERE horse_id IN (SELECT id FROM legacy_fake_horse_ids);

DELETE FROM groom_daily_tasks
WHERE horse_id IN (SELECT id FROM legacy_fake_horse_ids);

DELETE FROM groom_incident_reports
WHERE horse_id IN (SELECT id FROM legacy_fake_horse_ids);

DELETE FROM race_registrations
WHERE horse_id IN (SELECT id FROM legacy_fake_horse_ids);

-- Workouts are also deleted by plan ON DELETE CASCADE, but delete directly
-- first for databases containing orphaned development rows.
DELETE FROM training_workouts
WHERE horse_id IN (SELECT id FROM legacy_fake_horse_ids);

DELETE FROM horse_training_plans
WHERE horse_id IN (SELECT id FROM legacy_fake_horse_ids);

-- Preserve pedigrees of other horses: only remove references to a fake parent.
UPDATE horse_pedigrees
SET sire_id = NULL
WHERE sire_id IN (SELECT id FROM legacy_fake_horse_ids)
  AND horse_id NOT IN (SELECT id FROM legacy_fake_horse_ids);

UPDATE horse_pedigrees
SET dam_id = NULL
WHERE dam_id IN (SELECT id FROM legacy_fake_horse_ids)
  AND horse_id NOT IN (SELECT id FROM legacy_fake_horse_ids);

DELETE FROM horse_pedigrees
WHERE horse_id IN (SELECT id FROM legacy_fake_horse_ids);

DELETE FROM horses
WHERE id IN (SELECT id FROM legacy_fake_horse_ids);
