INSERT INTO roles (name) VALUES
    ('HEAD_TRAINER'),
    ('VETERINARIAN'),
    ('GROOM'),
    ('HORSE_OWNER'),
    ('CLUB_MANAGER');

-- Tạo sẵn 1 permission mẫu + gán cho CLUB_MANAGER để test
INSERT INTO permissions (code, description) VALUES
    ('USER_MANAGE', 'Manage user account');

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'CLUB_MANAGER' AND p.code = 'USER_MANAGE';

-- Tạo sẵn 1 tài khoản Club Manager để test đăng nhập
-- Password: "admin123" (Hash by Bcrypt)
INSERT INTO users (full_name, email, password_hash, role_id, is_active)
SELECT 'Admin CLB', 'admin@rtms.com', '$2a$10$2rXcS5LHx7wVYOkClxQ1YuIvg4myh7UgloaGcwcgvnahK9pTWVEJ.', r.id, true
FROM roles r WHERE r.name = 'CLUB_MANAGER';