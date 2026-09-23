CREATE TABLE horse_body_regions (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(100) NOT NULL UNIQUE,
    display_name VARCHAR(255) NOT NULL,
    model_node_key VARCHAR(255),
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP
);

CREATE TABLE medical_records (
    id BIGSERIAL PRIMARY KEY,
    horse_id BIGINT NOT NULL REFERENCES horses(id),
    veterinarian_id BIGINT NOT NULL REFERENCES users(id),
    source_training_session_id BIGINT REFERENCES training_sessions(id),

    examined_at TIMESTAMP NOT NULL,
    symptoms TEXT,
    clinical_findings TEXT,
    diagnosis TEXT NOT NULL,
    notes TEXT,
    follow_up_date DATE,

    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP
);
CREATE INDEX idx_medical_records_horse_examined ON medical_records(horse_id, examined_at);
CREATE INDEX idx_medical_records_vet_examined ON medical_records(veterinarian_id, examined_at);

CREATE TABLE treatment_plans (
    id BIGSERIAL PRIMARY KEY,
    medical_record_id BIGINT NOT NULL REFERENCES medical_records(id),

    treatment_name VARCHAR(255) NOT NULL,
    instructions TEXT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE,
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',

    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP
);
CREATE INDEX idx_treatment_plans_record_status ON treatment_plans(medical_record_id, status);

CREATE TABLE prescriptions (
    id BIGSERIAL PRIMARY KEY,
    treatment_plan_id BIGINT NOT NULL REFERENCES treatment_plans(id),

    medication_name VARCHAR(255) NOT NULL,
    dosage VARCHAR(100) NOT NULL,
    frequency VARCHAR(100),
    route VARCHAR(100),
    start_date DATE,
    end_date DATE,
    instructions TEXT,

    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP
);

CREATE TABLE injury_records (
    id BIGSERIAL PRIMARY KEY,
    horse_id BIGINT NOT NULL REFERENCES horses(id),
    medical_record_id BIGINT NOT NULL REFERENCES medical_records(id),

    injury_type VARCHAR(255) NOT NULL,
    body_region_id BIGINT NOT NULL REFERENCES horse_body_regions(id),
    severity VARCHAR(50) NOT NULL,
    description TEXT,

    diagnosed_at TIMESTAMP NOT NULL,
    expected_recovery_date DATE,
    recovered_at TIMESTAMP,
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',

    position_x DECIMAL(10,4),
    position_y DECIMAL(10,4),
    position_z DECIMAL(10,4),

    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP
);
CREATE INDEX idx_injury_records_horse_status ON injury_records(horse_id, status);
CREATE INDEX idx_injury_records_body_region ON injury_records(body_region_id);
CREATE INDEX idx_injury_records_medical_record ON injury_records(medical_record_id);

CREATE TABLE preventive_care_schedules (
    id BIGSERIAL PRIMARY KEY,
    horse_id BIGINT NOT NULL REFERENCES horses(id),
    veterinarian_id BIGINT NOT NULL REFERENCES users(id),

    care_type VARCHAR(50) NOT NULL,
    scheduled_date DATE NOT NULL,
    description TEXT,
    status VARCHAR(50) NOT NULL DEFAULT 'SCHEDULED',

    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP
);
CREATE INDEX idx_pcs_horse_date ON preventive_care_schedules(horse_id, scheduled_date);
CREATE INDEX idx_pcs_horse_status ON preventive_care_schedules(horse_id, status);
CREATE INDEX idx_pcs_type_date ON preventive_care_schedules(care_type, scheduled_date);

CREATE TABLE preventive_care_records (
    id BIGSERIAL PRIMARY KEY,
    horse_id BIGINT NOT NULL REFERENCES horses(id),
    schedule_id BIGINT UNIQUE REFERENCES preventive_care_schedules(id),
    veterinarian_id BIGINT NOT NULL REFERENCES users(id),

    care_type VARCHAR(50) NOT NULL,
    performed_at TIMESTAMP NOT NULL,
    performed_by_name VARCHAR(255),
    product_or_service VARCHAR(255),
    result TEXT,
    notes TEXT,
    next_due_date DATE,

    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_pcr_horse_performed ON preventive_care_records(horse_id, performed_at);
CREATE INDEX idx_pcr_type_performed ON preventive_care_records(care_type, performed_at);