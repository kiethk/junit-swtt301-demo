INSERT INTO permissions (code, description) VALUES
                                                ('TREATMENT_PLAN_VIEW', 'View treatment plans and prescriptions'),
                                                ('TREATMENT_PLAN_MANAGE', 'Create treatment plans and prescriptions');

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'VETERINARIAN'
  AND p.code = 'TREATMENT_PLAN_VIEW';

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'VETERINARIAN'
  AND p.code = 'TREATMENT_PLAN_MANAGE';