CREATE TABLE horse_pedigrees (
    id BIGSERIAL PRIMARY KEY,
    horse_id BIGINT NOT NULL UNIQUE REFERENCES horses(id),
    sire_id BIGINT REFERENCES horses(id),
    dam_id BIGINT REFERENCES horses(id),
    registration_number VARCHAR(100),
    registry_name VARCHAR(255),
    pedigree_notes VARCHAR(2000)
);

CREATE TABLE horse_health_metrics (
    id BIGSERIAL PRIMARY KEY,
    horse_id BIGINT NOT NULL REFERENCES horses(id),
    recorded_at TIMESTAMP NOT NULL,
    heart_rate DECIMAL(6,2),
    temperature DECIMAL(5,2),
    weight DECIMAL(8,2),
    respiratory_rate DECIMAL(6,2),
    hydration_status VARCHAR(50),
    body_condition_score DECIMAL(3,1),
    notes VARCHAR(2000),
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE racing_readiness_assessments (
    id BIGSERIAL PRIMARY KEY,
    horse_id BIGINT NOT NULL REFERENCES horses(id),
    readiness_status VARCHAR(50) NOT NULL,
    fitness_score DECIMAL(5,2),
    health_score DECIMAL(5,2),
    assessment_date DATE NOT NULL,
    valid_until DATE,
    remarks VARCHAR(3000),
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE financial_reports (
    id BIGSERIAL PRIMARY KEY,
    horse_id BIGINT NOT NULL REFERENCES horses(id),
    period_start DATE NOT NULL,
    period_end DATE NOT NULL,
    total_revenue DECIMAL(15,2) DEFAULT 0,
    total_expense DECIMAL(15,2) DEFAULT 0,
    net_profit DECIMAL(15,2) DEFAULT 0,
    status VARCHAR(50) DEFAULT 'DRAFT',
    generated_at TIMESTAMP DEFAULT NOW()
);