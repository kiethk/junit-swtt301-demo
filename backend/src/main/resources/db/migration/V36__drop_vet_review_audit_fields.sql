ALTER TABLE admission_applications
    DROP COLUMN IF EXISTS physical_exam_confirmed_at,
    DROP COLUMN IF EXISTS vet_reviewed_by;
