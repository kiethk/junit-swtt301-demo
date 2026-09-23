
ALTER TABLE horses DROP COLUMN pedigree_info;

CREATE TABLE veterinarian_profiles (
    user_id BIGINT PRIMARY KEY REFERENCES users(id),
    license_number VARCHAR(100) NOT NULL,
    license_issued_date DATE,
    specialization VARCHAR(255)
);

CREATE TABLE trainer_profiles (
    user_id BIGINT PRIMARY KEY REFERENCES users(id),
    certification_number VARCHAR(100) NOT NULL,
    certification_issued_date DATE,
    years_of_experience INT
);

CREATE TABLE groom_profiles (
    user_id BIGINT PRIMARY KEY REFERENCES users(id),
    assigned_area VARCHAR(100),
    shift VARCHAR(50)
);

CREATE TABLE audit_logs (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    action VARCHAR(100) NOT NULL,
    entity_name VARCHAR(100) NOT NULL,
    entity_id BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);