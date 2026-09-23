-- =====================================================================
-- V32: SEED GROOM DAILY TASK AND INCIDENT REPORT PERMISSIONS
-- =====================================================================

-- 1. Thêm permissions mới vào bảng permissions
INSERT INTO permissions (code, description) VALUES
    ('GROOM_DAILY_TASK_VIEW', 'Xem danh sách công việc hàng ngày của Groom'),
    ('GROOM_DAILY_TASK_CREATE', 'Tạo công việc hàng ngày cho Groom'),
    ('GROOM_DAILY_TASK_UPDATE', 'Cập nhật trạng thái hoàn thành công việc của Groom'),
    ('GROOM_INCIDENT_REPORT_CREATE', 'Tạo báo cáo sự cố của ngựa'),
    ('GROOM_INCIDENT_REPORT_VIEW', 'Xem danh sách và chi tiết báo cáo sự cố của ngựa')
ON CONFLICT (code) DO NOTHING;

-- 2. Phân đủ 5 quyền cho Role GROOM và HEAD_TRAINER
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE p.code IN (
    'GROOM_DAILY_TASK_VIEW',
    'GROOM_DAILY_TASK_CREATE',
    'GROOM_DAILY_TASK_UPDATE',
    'GROOM_INCIDENT_REPORT_CREATE',
    'GROOM_INCIDENT_REPORT_VIEW'
)
AND r.name IN ('GROOM', 'HEAD_TRAINER')
ON CONFLICT DO NOTHING;

-- 3. Phân quyền xem báo cáo sự cố cho VETERINARIAN (để kịp thời chẩn đoán, điều trị)
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE p.code = 'GROOM_INCIDENT_REPORT_VIEW'
AND r.name = 'VETERINARIAN'
ON CONFLICT DO NOTHING;

-- 4. Phân quyền xem công việc và sự cố cho CLUB_MANAGER (để giám sát hoạt động)
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE p.code IN ('GROOM_DAILY_TASK_VIEW', 'GROOM_INCIDENT_REPORT_VIEW')
AND r.name = 'CLUB_MANAGER'
ON CONFLICT DO NOTHING;
