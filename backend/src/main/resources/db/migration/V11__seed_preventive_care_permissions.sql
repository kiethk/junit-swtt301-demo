INSERT INTO permissions (code, description) VALUES
    ('PREVENTIVE_CARE_VIEW', 'View the routine care schedule'),
    ('PREVENTIVE_CARE_CREATE', 'Schedule and record routine care');

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE p.code = 'PREVENTIVE_CARE_VIEW'
  AND r.name IN ('VETERINARIAN', 'HEAD_TRAINER', 'HORSE_OWNER', 'CLUB_MANAGER');

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE p.code = 'PREVENTIVE_CARE_CREATE'
  AND r.name = 'VETERINARIAN';