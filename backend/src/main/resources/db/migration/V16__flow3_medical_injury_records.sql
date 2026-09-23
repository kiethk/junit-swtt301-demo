-- Flow 3: permissions for medical and injury records; VETERINARIAN-only grant this round.
-- Body-region seed is temporary pending frontend 3D-model key sync.

INSERT INTO permissions (code, description) VALUES
    ('MEDICAL_RECORD_CREATE', 'Create veterinary medical records'),
    ('INJURY_RECORD_CREATE', 'Create injury records and lock horse training status'),
    ('HORSE_TRAINING_LOCK_VIEW', 'View horse training lock status'),
    ('HORSE_BODY_REGION_VIEW', 'View horse body region catalogue');

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'VETERINARIAN' AND p.code = 'MEDICAL_RECORD_CREATE';

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'VETERINARIAN' AND p.code = 'INJURY_RECORD_CREATE';

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'VETERINARIAN' AND p.code = 'HORSE_TRAINING_LOCK_VIEW';

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'VETERINARIAN' AND p.code = 'HORSE_BODY_REGION_VIEW';

INSERT INTO horse_body_regions (code, display_name, model_node_key, is_active) VALUES
    ('HEAD', 'Head', 'head', true),
    ('NECK', 'Neck', 'neck', true),
    ('BACK', 'Back', 'back', true),
    ('ABDOMEN', 'Abdomen', 'abdomen', true),
    ('FORELEG_LEFT', 'Foreleg (Left)', 'foreleg_left', true),
    ('FORELEG_RIGHT', 'Foreleg (Right)', 'foreleg_right', true),
    ('HINDLEG_LEFT', 'Hindleg (Left)', 'hindleg_left', true),
    ('HINDLEG_RIGHT', 'Hindleg (Right)', 'hindleg_right', true);
