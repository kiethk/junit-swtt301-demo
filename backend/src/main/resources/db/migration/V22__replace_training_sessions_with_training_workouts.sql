-- ====================================================================
-- MIGRATION V22: THAY THẾ TRAINING_SESSIONS THÀNH TRAINING_WORKOUTS
-- ====================================================================

-- 1. Gỡ ràng buộc khóa ngoại cũ từ medical_records (nếu có)
ALTER TABLE medical_records
DROP CONSTRAINT IF EXISTS medical_records_source_training_session_id_fkey;

-- 2. Đổi tên cột tham chiếu trong medical_records sang workout
ALTER TABLE medical_records
    RENAME COLUMN source_training_session_id TO source_training_workout_id;

-- 3. Xóa bảng training_sessions cũ
DROP TABLE IF EXISTS training_sessions CASCADE;

-- 4. Tạo bảng training_workouts mới
CREATE TABLE training_workouts (
                                   id BIGSERIAL PRIMARY KEY,
                                   plan_id BIGINT NOT NULL REFERENCES horse_training_plans(id) ON DELETE CASCADE,
                                   subject_id BIGINT NOT NULL REFERENCES course_subjects(id),
                                   horse_id BIGINT NOT NULL REFERENCES horses(id),
                                   assigned_to_id BIGINT REFERENCES users(id),
                                   workout_date DATE NOT NULL,
                                   start_time TIMESTAMP,
                                   end_time TIMESTAMP,
                                   workout_type VARCHAR(50) DEFAULT 'REGULAR',
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

-- 5. Tạo chỉ mục (Index) để tối ưu truy vấn lịch tập
CREATE INDEX idx_training_workouts_plan ON training_workouts(plan_id, workout_date);
CREATE INDEX idx_training_workouts_horse ON training_workouts(horse_id, workout_date);

-- 6. Gắn lại khóa ngoại từ medical_records sang bảng training_workouts mới
ALTER TABLE medical_records
    ADD CONSTRAINT fk_medical_records_workout
        FOREIGN KEY (source_training_workout_id) REFERENCES training_workouts(id) ON DELETE SET NULL;

-- 7. Cập nhật mã Permission trong bảng permissions (giữ nguyên role_permissions đã cấp)
UPDATE permissions
SET code = 'TRAINING_WORKOUT_VIEW',
    description = 'Xem lịch tập luyện (workout) hàng ngày của ngựa'
WHERE code = 'TRAINING_SESSION_VIEW';

UPDATE permissions
SET code = 'TRAINING_WORKOUT_UPDATE',
    description = 'Cập nhật kết quả bài tập (workout) và chỉ số thể lực'
WHERE code = 'TRAINING_SESSION_UPDATE';