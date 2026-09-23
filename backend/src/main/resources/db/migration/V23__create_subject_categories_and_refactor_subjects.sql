-- ====================================================================
-- MIGRATION V23: TÁCH BÀI TẬP (SUBJECTS) VÀ THÊM DANH MỤC (CATEGORIES)
-- ====================================================================

-- 1. Tạo bảng danh mục bài tập (subject_categories)
CREATE TABLE subject_categories (
                                    id BIGSERIAL PRIMARY KEY,
                                    name VARCHAR(255) NOT NULL UNIQUE,
                                    description TEXT,
                                    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Seed sẵn 4 danh mục chuẩn đua ngựa
INSERT INTO subject_categories (name, description) VALUES
                                                       ('Thể lực & Sức bền', 'Các bài tập chạy cự ly dài, chạy dốc nhằm nâng cao thể lực và sức bền tim mạch'),
                                                       ('Tốc độ & Nước rút', 'Các bài tập bứt tốc cự ly ngắn đến trung bình, rèn phản xạ và tốc độ tối đa'),
                                                       ('Kỹ thuật lồng xuất phát', 'Các bài tập làm quen với cổng xuất phát, rèn phản xạ bung cổng và ổn định tâm lý'),
                                                       ('Hồi phục & Thả lỏng', 'Các bài tập đi bộ nhẹ, bơi lội, thả lỏng cơ sau các đợt chạy cường độ cao');

-- 2. Tạo bảng ngân hàng môn học/bài tập dùng chung (subjects)
CREATE TABLE subjects (
                          id BIGSERIAL PRIMARY KEY,
                          category_id BIGINT NOT NULL REFERENCES subject_categories(id) ON DELETE RESTRICT,
                          name VARCHAR(255) NOT NULL,
                          description TEXT,
                          surface_type VARCHAR(50) NOT NULL,    -- TURF (cỏ), DIRT (cát), SYNTHETIC (nhân tạo)
                          target_distance_meters DECIMAL,        -- Cự ly mục tiêu của bài tập
                          intensity_level VARCHAR(50) NOT NULL, -- LOW, MEDIUM, HIGH
                          created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                          updated_at TIMESTAMP
);

-- Seed sẵn 4 bài tập mẫu vào ngân hàng môn học
INSERT INTO subjects (category_id, name, description, surface_type, target_distance_meters, intensity_level) VALUES
                                                                                                                 (1, 'Khởi động & Chạy bền nhịp đều', 'Duy trì nhịp thở và nhịp tim ổn định cự ly dài', 'TURF', 1500, 'MEDIUM'),
                                                                                                                 (2, 'Biến tốc tăng tốc nước rút', 'Rèn luyện khả năng bứt tốc nước rút ở đoạn thẳng', 'DIRT', 800, 'HIGH'),
                                                                                                                 (2, 'Chạy thử tính giờ (Trial Run)', 'Chạy hết sức tính thời gian vòng đua chuẩn', 'TURF', 1200, 'HIGH'),
                                                                                                                 (4, 'Đi bộ và thả lỏng cơ sau tập', 'Hồi phục nhịp tim về mức bình thường', 'SYNTHETIC', 500, 'LOW');

-- 3. Gỡ ràng buộc khóa ngoại cũ của training_workouts
ALTER TABLE training_workouts
DROP CONSTRAINT IF EXISTS training_workouts_subject_id_fkey;

-- 4. Xóa bảng course_subjects cũ và tạo lại thành bảng liên kết thuần túy Nhiều - Nhiều (N-N)
DROP TABLE IF EXISTS course_subjects CASCADE;

CREATE TABLE course_subjects (
                                 id BIGSERIAL PRIMARY KEY,
                                 course_id BIGINT NOT NULL REFERENCES courses(id) ON DELETE CASCADE,
                                 subject_id BIGINT NOT NULL REFERENCES subjects(id) ON DELETE RESTRICT,
                                 order_index INT NOT NULL, -- Thứ tự thực hiện bài tập trong khóa (1, 2, 3...)
                                 created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                                 CONSTRAINT uq_course_subject_order UNIQUE (course_id, order_index)
);

CREATE INDEX idx_course_subjects_course ON course_subjects(course_id, order_index);

-- 5. Xóa các buổi tập test cũ (vì mang ID bài tập cũ không còn tồn tại)
TRUNCATE TABLE training_workouts CASCADE;

-- 6. Gắn lại khóa ngoại từ training_workouts trỏ trực tiếp sang ngân hàng môn học (subjects)
ALTER TABLE training_workouts
    ADD CONSTRAINT fk_training_workouts_subject
        FOREIGN KEY (subject_id) REFERENCES subjects(id) ON DELETE RESTRICT;

-- 7. Phân quyền (Permissions)
INSERT INTO permissions (code, description) VALUES
                                                ('SUBJECT_CATEGORY_VIEW', 'Xem danh sách danh mục bài tập huấn luyện'),
                                                ('SUBJECT_VIEW', 'Xem danh sách ngân hàng bài tập huấn luyện'),
                                                ('SUBJECT_MANAGE', 'Tạo và chỉnh sửa bài tập trong ngân hàng bài tập');

-- Phân quyền xem cho các role
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE p.code IN ('SUBJECT_CATEGORY_VIEW', 'SUBJECT_VIEW')
  AND r.name IN ('HEAD_TRAINER', 'CLUB_MANAGER', 'HORSE_OWNER', 'VETERINARIAN');

-- Phân quyền quản lý cho HEAD_TRAINER
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE p.code = 'SUBJECT_MANAGE'
  AND r.name = 'HEAD_TRAINER';