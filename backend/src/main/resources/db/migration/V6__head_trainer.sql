CREATE TABLE courses (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    target_goal TEXT,
    duration_weeks INT NOT NULL,
    total_sessions INT NOT NULL,
    created_by_id BIGINT REFERENCES users(id),
    status VARCHAR(50) DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP
);

CREATE TABLE course_subjects (
    id BIGSERIAL PRIMARY KEY,
    course_id BIGINT NOT NULL REFERENCES courses(id),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    surface_type VARCHAR(50) NOT NULL,
    target_distance_meters DECIMAL,
    intensity_level VARCHAR(50),
    order_index INT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP
);

CREATE TABLE horse_training_plans (
    id BIGSERIAL PRIMARY KEY,
    horse_id BIGINT NOT NULL REFERENCES horses(id),
    course_id BIGINT NOT NULL REFERENCES courses(id),
    trainer_id BIGINT NOT NULL REFERENCES users(id),
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    status VARCHAR(50) DEFAULT 'ACTIVE',
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP
);

CREATE TABLE training_sessions (
    id BIGSERIAL PRIMARY KEY,
    plan_id BIGINT NOT NULL REFERENCES horse_training_plans(id),
    subject_id BIGINT NOT NULL REFERENCES course_subjects(id),
    horse_id BIGINT NOT NULL REFERENCES horses(id),
    assigned_to_id BIGINT REFERENCES users(id),
    session_date DATE NOT NULL,
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    session_type VARCHAR(50) DEFAULT 'REGULAR',
    status VARCHAR(50) DEFAULT 'SCHEDULED',

    actual_distance_meters DECIMAL,
    actual_duration_minutes DECIMAL,
    top_speed_kmh DECIMAL,
    average_speed_kmh DECIMAL,
    average_heart_rate INT,
    max_heart_rate INT,
    recovery_heart_rate INT,

    performance_rating INT,
    trainer_feedback TEXT,
    video_url VARCHAR(500),

    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP
);

CREATE TABLE race_registrations (
    id BIGSERIAL PRIMARY KEY,
    horse_id BIGINT NOT NULL REFERENCES horses(id),
    trainer_id BIGINT NOT NULL REFERENCES users(id),
    race_name VARCHAR(255) NOT NULL,
    location VARCHAR(255),
    event_date TIMESTAMP NOT NULL,
    distance_meters DECIMAL,
    track_type VARCHAR(50),
    jockey_name VARCHAR(255),
    entry_fee DECIMAL,
    trainer_notes TEXT,

    status VARCHAR(50) DEFAULT 'PENDING',
    reviewed_by_id BIGINT REFERENCES users(id),
    manager_feedback TEXT,
    reviewed_at TIMESTAMP,

    final_position INT,
    finish_time_seconds DECIMAL,
    prize_money DECIMAL,

    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP
);