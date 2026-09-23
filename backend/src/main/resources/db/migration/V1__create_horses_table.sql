CREATE TABLE horses (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    breed VARCHAR(255),
    date_of_birth DATE,
    pedigree_info TEXT,
    current_status VARCHAR(50) NOT NULL DEFAULT 'ELIGIBLE',
    stable_location VARCHAR(100),
    owner_id BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);