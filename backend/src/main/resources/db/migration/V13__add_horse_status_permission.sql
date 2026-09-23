-- 1. Thêm permission mới vào bảng permissions
INSERT INTO permissions (code, description) 
VALUES ('HORSE_STATUS_EDIT', 'Edit horse status');

-- 2. Cấp quyền này cho role VETERINARIAN
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'VETERINARIAN' AND p.code = 'HORSE_STATUS_EDIT';
