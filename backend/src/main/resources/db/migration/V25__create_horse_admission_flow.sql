-- =====================================================================
-- V25: HORSE ADMISSION FLOW
--
-- AdmissionStatus:
--   GROOM_REVIEW
--   WAITING_FOR_STALL
--   VET_REVIEW
--   TRAINER_REVIEW
--   MANAGER_REVIEW
--   APPROVED
--   REJECTED
--
-- ReviewDecision:
--   APPROVED
--   REJECTED
--   NULL = not reviewed yet
--
-- Service-layer invariant:
--   Candidate may enter Q only when:
--     available quarantine stall >= 1
--     AND available regular stalls >= occupied Q stalls + 1
-- =====================================================================

CREATE TABLE admission_applications (
    id BIGSERIAL PRIMARY KEY,

    owner_id BIGINT NOT NULL REFERENCES users(id),

    status VARCHAR(40) NOT NULL DEFAULT 'GROOM_REVIEW',

    -- Historical quarantine stall used by this application.
    quarantine_stall_id BIGINT REFERENCES stable_stalls(id) ON DELETE RESTRICT,

    groom_id BIGINT REFERENCES users(id),
    groom_decision VARCHAR(20),
    groom_feedback TEXT,
    groom_reviewed_at TIMESTAMP,

    veterinarian_id BIGINT REFERENCES users(id),
    vet_decision VARCHAR(20),
    vet_feedback TEXT,
    vet_reviewed_at TIMESTAMP,

    trainer_id BIGINT REFERENCES users(id),
    trainer_decision VARCHAR(20),
    trainer_feedback TEXT,
    trainer_reviewed_at TIMESTAMP,

    manager_id BIGINT REFERENCES users(id),
    manager_decision VARCHAR(20),
    manager_feedback TEXT,
    manager_reviewed_at TIMESTAMP,

    -- NULL until Manager approval creates the official Horse.
    resulting_horse_id BIGINT UNIQUE REFERENCES horses(id) ON DELETE RESTRICT,

    submitted_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_admission_status
        CHECK (status IN (
            'GROOM_REVIEW',
            'WAITING_FOR_STALL',
            'VET_REVIEW',
            'TRAINER_REVIEW',
            'MANAGER_REVIEW',
            'APPROVED',
            'REJECTED'
        )),

    CONSTRAINT chk_admission_groom_decision
        CHECK (groom_decision IS NULL OR groom_decision IN ('APPROVED', 'REJECTED')),

    CONSTRAINT chk_admission_vet_decision
        CHECK (vet_decision IS NULL OR vet_decision IN ('APPROVED', 'REJECTED')),

    CONSTRAINT chk_admission_trainer_decision
        CHECK (trainer_decision IS NULL OR trainer_decision IN ('APPROVED', 'REJECTED')),

    CONSTRAINT chk_admission_manager_decision
        CHECK (manager_decision IS NULL OR manager_decision IN ('APPROVED', 'REJECTED')),

    CONSTRAINT chk_admission_q_stall_required
        CHECK (
            status NOT IN ('VET_REVIEW', 'TRAINER_REVIEW', 'MANAGER_REVIEW', 'APPROVED')
            OR quarantine_stall_id IS NOT NULL
        ),

    CONSTRAINT chk_admission_approved_has_horse
        CHECK (
            status <> 'APPROVED'
            OR resulting_horse_id IS NOT NULL
        )
);

CREATE INDEX idx_admission_owner
    ON admission_applications(owner_id, submitted_at DESC);

CREATE INDEX idx_admission_status
    ON admission_applications(status);

CREATE INDEX idx_admission_quarantine_stall
    ON admission_applications(quarantine_stall_id);


CREATE TABLE candidate_horse_profiles (
    id BIGSERIAL PRIMARY KEY,

    admission_id BIGINT NOT NULL UNIQUE
        REFERENCES admission_applications(id)
        ON DELETE CASCADE,

    name VARCHAR(255) NOT NULL,
    breed VARCHAR(255),
    date_of_birth DATE,

    registration_number VARCHAR(100),
    registry_name VARCHAR(255),

    sire_name VARCHAR(255),
    sire_registration_number VARCHAR(100),

    dam_name VARCHAR(255),
    dam_registration_number VARCHAR(100),

    pedigree_notes VARCHAR(2000),

    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_candidate_registration_number
    ON candidate_horse_profiles(registration_number);

CREATE INDEX idx_candidate_sire_registration_number
    ON candidate_horse_profiles(sire_registration_number);

CREATE INDEX idx_candidate_dam_registration_number
    ON candidate_horse_profiles(dam_registration_number);


CREATE TABLE admission_documents (
    id BIGSERIAL PRIMARY KEY,

    admission_id BIGINT NOT NULL
        REFERENCES admission_applications(id)
        ON DELETE CASCADE,

    document_type VARCHAR(50) NOT NULL,
    file_url VARCHAR(1000) NOT NULL,

    record_date DATE,
    note TEXT,

    uploaded_at TIMESTAMP NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_admission_document_type
        CHECK (document_type IN (
            'HORSE_PHOTO',
            'REGISTRATION_DOCUMENT',
            'PEDIGREE_CERTIFICATE',
            'VACCINATION_RECORD',
            'DEWORMING_RECORD',
            'HEALTH_CERTIFICATE',
            'PREVIOUS_MEDICAL_RECORD',
            'PREVIOUS_INJURY_RECORD'
        ))
);

CREATE INDEX idx_admission_documents_admission
    ON admission_documents(admission_id);

CREATE INDEX idx_admission_documents_type
    ON admission_documents(document_type);
