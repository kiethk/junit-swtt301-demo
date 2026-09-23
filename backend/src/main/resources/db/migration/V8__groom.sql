CREATE TABLE groom_daily_tasks (
    id BIGSERIAL PRIMARY KEY,
    groom_id BIGINT NOT NULL REFERENCES users(id),
    horse_id BIGINT NOT NULL REFERENCES horses(id),
    task_type VARCHAR(100) NOT NULL,
    scheduled_time TIMESTAMP,
    completed_at TIMESTAMP,
    is_completed BOOLEAN DEFAULT false,
    notes TEXT
);

CREATE TABLE groom_incident_reports (
    id BIGSERIAL PRIMARY KEY,
    groom_id BIGINT NOT NULL REFERENCES users(id),
    horse_id BIGINT NOT NULL REFERENCES horses(id),
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    image_url VARCHAR(500),
    severity VARCHAR(50) DEFAULT 'MEDIUM',
    reported_at TIMESTAMP DEFAULT NOW()
);