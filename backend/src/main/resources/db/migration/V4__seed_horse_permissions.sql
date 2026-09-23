-- Permission cho HorseController
INSERT INTO permissions (code, description) VALUES
    ('HORSE_VIEW', 'Xem danh sách và chi tiết hồ sơ ngựa'),
    ('HORSE_CREATE', 'Tạo mới hồ sơ ngựa');

-- Gán HORSE_VIEW cho tất cả 5 role
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE p.code = 'HORSE_VIEW'
  AND r.name IN ('HEAD_TRAINER', 'VETERINARIAN', 'GROOM', 'HORSE_OWNER', 'CLUB_MANAGER');

-- Gán HORSE_CREATE chỉ cho CLUB_MANAGER
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE p.code = 'HORSE_CREATE'
  AND r.name = 'CLUB_MANAGER';