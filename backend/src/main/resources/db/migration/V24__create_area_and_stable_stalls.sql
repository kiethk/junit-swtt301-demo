-- =====================================================================
-- V24: AREA & STABLE STALL MANAGEMENT
-- =====================================================================

CREATE TABLE areas (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(30) NOT NULL,

    trainer_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
    groom_id BIGINT REFERENCES users(id) ON DELETE SET NULL,

    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_areas_type
        CHECK (type IN ('QUARANTINE', 'REGULAR'))
);

CREATE INDEX idx_areas_trainer ON areas(trainer_id);
CREATE INDEX idx_areas_groom ON areas(groom_id);
CREATE INDEX idx_areas_type ON areas(type);

-- Logical areas agreed by the team.
-- Stall counts are intentionally not seeded here because the number of stalls
-- per area has not been fixed.
INSERT INTO areas (code, name, type) VALUES
    ('Q', 'Quarantine Area', 'QUARANTINE'),
    ('A', 'Area A', 'REGULAR'),
    ('B', 'Area B', 'REGULAR'),
    ('C', 'Area C', 'REGULAR'),
    ('D', 'Area D', 'REGULAR');

CREATE TABLE stable_stalls (
    id BIGSERIAL PRIMARY KEY,
    area_id BIGINT NOT NULL REFERENCES areas(id) ON DELETE RESTRICT,

    stall_number INT NOT NULL,
    stall_code VARCHAR(50) NOT NULL UNIQUE,

    status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',

    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),

    CONSTRAINT uq_stable_stalls_area_number
        UNIQUE (area_id, stall_number),

    CONSTRAINT chk_stable_stalls_number
        CHECK (stall_number > 0),

    CONSTRAINT chk_stable_stalls_status
        CHECK (status IN ('AVAILABLE', 'OCCUPIED'))
);

CREATE INDEX idx_stable_stalls_area_status
    ON stable_stalls(area_id, status);

ALTER TABLE horses
    ADD COLUMN current_stall_id BIGINT;

ALTER TABLE horses
    ADD CONSTRAINT fk_horses_current_stall
        FOREIGN KEY (current_stall_id)
        REFERENCES stable_stalls(id)
        ON DELETE SET NULL;

-- One physical stall can host at most one official Horse at a time.
ALTER TABLE horses
    ADD CONSTRAINT uq_horses_current_stall
        UNIQUE (current_stall_id);

ALTER TABLE horses
    ADD CONSTRAINT chk_horses_current_status
        CHECK (current_status IN (
            'ELIGIBLE',
            'MONITORING',
            'INJURED',
            'QUARANTINED'
        ));

-- Area assignment is now normalized through areas.groom_id.
ALTER TABLE groom_profiles
    DROP COLUMN IF EXISTS assigned_area;

-- IMPORTANT:
-- horses.stable_location is intentionally kept for now to avoid destructive
-- loss of existing development data. New code must use current_stall_id.
