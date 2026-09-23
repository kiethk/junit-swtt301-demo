-- 1. Thêm permissions cho HorseTrainingPlan và TrainingSession
INSERT INTO permissions (code, description) VALUES
                                                ('TRAINING_PLAN_VIEW', 'Xem danh sách và chi tiết kế hoạch huấn luyện của ngựa'),
                                                ('TRAINING_PLAN_CREATE', 'Gán ngựa vào khóa huấn luyện mới'),
                                                ('TRAINING_PLAN_UPDATE', 'Cập nhật kế hoạch huấn luyện'),
                                                ('TRAINING_SESSION_VIEW', 'Xem lịch tập luyện hàng ngày'),
                                                ('TRAINING_SESSION_UPDATE', 'Cập nhật kết quả buổi tập và chỉ số thể lực');

-- 2. Phân quyền xem (VIEW) cho các Role liên quan
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE p.code IN ('TRAINING_PLAN_VIEW', 'TRAINING_SESSION_VIEW')
  AND r.name IN ('HEAD_TRAINER', 'CLUB_MANAGER', 'HORSE_OWNER', 'VETERINARIAN');

-- 3. Phân quyền tạo và cập nhật cho HEAD_TRAINER
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE p.code IN ('TRAINING_PLAN_CREATE', 'TRAINING_PLAN_UPDATE', 'TRAINING_SESSION_UPDATE')
  AND r.name = 'HEAD_TRAINER';