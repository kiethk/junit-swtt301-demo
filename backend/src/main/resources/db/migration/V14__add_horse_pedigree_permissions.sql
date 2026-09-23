INSERT INTO permissions (code, description) VALUES
                                                ('HORSE_PEDIGREE_VIEW', 'View horse pedigree information'),
                                                ('HORSE_PEDIGREE_MANAGE', 'Create or update horse pedigree information');

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE p.code = 'HORSE_PEDIGREE_VIEW'
  AND r.name = 'HORSE_OWNER';

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE p.code = 'HORSE_PEDIGREE_MANAGE'
  AND r.name = 'HORSE_OWNER';