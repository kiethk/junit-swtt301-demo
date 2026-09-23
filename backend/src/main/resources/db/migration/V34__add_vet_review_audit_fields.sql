ALTER TABLE admission_applications
    ADD COLUMN physical_exam_confirmed_at TIMESTAMP NULL,
    ADD COLUMN vet_reviewed_by BIGINT NULL REFERENCES users(id);
