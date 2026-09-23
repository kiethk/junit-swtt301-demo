INSERT INTO permissions (code, description) VALUES
                                                ('RACE_REGISTRATION_VIEW', 'Xem danh sách đăng ký giải đua'),
                                                ('RACE_REGISTRATION_CREATE', 'Tạo đăng ký giải đua cho ngựa'),
                                                ('RACE_REGISTRATION_REVIEW', 'Duyệt hoặc từ chối đăng ký giải đua');

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE p.code = 'RACE_REGISTRATION_VIEW'
  AND r.name IN ('HEAD_TRAINER', 'CLUB_MANAGER');

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE p.code = 'RACE_REGISTRATION_CREATE'
  AND r.name = 'HEAD_TRAINER';

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE p.code = 'RACE_REGISTRATION_REVIEW'
  AND r.name = 'CLUB_MANAGER';