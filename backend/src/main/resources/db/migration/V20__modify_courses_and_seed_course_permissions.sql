-- 1. Bỏ cột duration_weeks theo thống nhất thiết kế
ALTER TABLE courses DROP COLUMN IF EXISTS duration_weeks;

-- 2. Thêm các quyền (permissions) cho Course & CourseSubject
INSERT INTO permissions (code, description) VALUES
                                                ('COURSE_VIEW', 'Xem danh sách và chi tiết khóa học, bài tập'),
                                                ('COURSE_CREATE', 'Tạo mới khóa học và bài tập trong khóa'),
                                                ('COURSE_UPDATE', 'Cập nhật thông tin khóa học và bài tập');

-- 3. Phân quyền xem (COURSE_VIEW) cho HLV, Quản lý, Chủ ngựa và Bác sĩ thú y
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE p.code = 'COURSE_VIEW'
  AND r.name IN ('HEAD_TRAINER', 'CLUB_MANAGER', 'HORSE_OWNER', 'VETERINARIAN');

-- 4. Phân quyền tạo và sửa (COURSE_CREATE, COURSE_UPDATE) chỉ cho HEAD_TRAINER
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE p.code IN ('COURSE_CREATE', 'COURSE_UPDATE')
  AND r.name = 'HEAD_TRAINER';