-- =====================================================================
-- V27: RBAC PERMISSIONS FOR FLOW 1
-- =====================================================================

INSERT INTO permissions (code, description) VALUES
    ('ADMISSION_APPLICATION_VIEW', 'View horse admission applications'),
    ('ADMISSION_APPLICATION_CREATE', 'Create and submit a horse admission application'),

    ('ADMISSION_APPLICATION_GROOM_REVIEW', 'Perform Groom review for a horse admission application'),
    ('ADMISSION_APPLICATION_VET_REVIEW', 'Perform Veterinarian admission examination review'),
    ('ADMISSION_APPLICATION_TRAINER_REVIEW', 'Perform Head Trainer admission suitability review'),
    ('ADMISSION_APPLICATION_MANAGER_REVIEW', 'Perform Club Manager final admission review'),

    ('ADMISSION_DOCUMENT_VIEW', 'View documents attached to a horse admission application'),
    ('ADMISSION_DOCUMENT_CREATE', 'Upload documents to a horse admission application'),

    ('AREA_VIEW', 'View stable areas'),
    ('AREA_CREATE', 'Create stable areas'),
    ('AREA_UPDATE', 'Update stable areas and staff assignment'),

    ('STABLE_STALL_VIEW', 'View stable stalls and availability'),
    ('STABLE_STALL_CREATE', 'Create stable stalls'),
    ('STABLE_STALL_UPDATE', 'Update stable stall information and operational status');


-- All Flow 1 actors can view AdmissionApplication.
-- Service logic must still enforce data scope, e.g. Owner sees own applications.
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
CROSS JOIN permissions p
WHERE p.code = 'ADMISSION_APPLICATION_VIEW'
  AND r.name IN (
      'HORSE_OWNER',
      'GROOM',
      'VETERINARIAN',
      'HEAD_TRAINER',
      'CLUB_MANAGER'
  );

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
CROSS JOIN permissions p
WHERE p.code = 'ADMISSION_APPLICATION_CREATE'
  AND r.name = 'HORSE_OWNER';


-- Actor-specific review permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
CROSS JOIN permissions p
WHERE p.code = 'ADMISSION_APPLICATION_GROOM_REVIEW'
  AND r.name = 'GROOM';

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
CROSS JOIN permissions p
WHERE p.code = 'ADMISSION_APPLICATION_VET_REVIEW'
  AND r.name = 'VETERINARIAN';

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
CROSS JOIN permissions p
WHERE p.code = 'ADMISSION_APPLICATION_TRAINER_REVIEW'
  AND r.name = 'HEAD_TRAINER';

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
CROSS JOIN permissions p
WHERE p.code = 'ADMISSION_APPLICATION_MANAGER_REVIEW'
  AND r.name = 'CLUB_MANAGER';


-- Admission documents
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
CROSS JOIN permissions p
WHERE p.code = 'ADMISSION_DOCUMENT_VIEW'
  AND r.name IN (
      'HORSE_OWNER',
      'GROOM',
      'VETERINARIAN',
      'HEAD_TRAINER',
      'CLUB_MANAGER'
  );

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
CROSS JOIN permissions p
WHERE p.code = 'ADMISSION_DOCUMENT_CREATE'
  AND r.name = 'HORSE_OWNER';


-- Area / Stall visibility
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
CROSS JOIN permissions p
WHERE p.code IN ('AREA_VIEW', 'STABLE_STALL_VIEW')
  AND r.name IN (
      'GROOM',
      'VETERINARIAN',
      'HEAD_TRAINER',
      'CLUB_MANAGER'
  );

-- Area / Stall master-data management belongs to Club Manager.
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
CROSS JOIN permissions p
WHERE p.code IN (
      'AREA_CREATE',
      'AREA_UPDATE',
      'STABLE_STALL_CREATE',
      'STABLE_STALL_UPDATE'
  )
  AND r.name = 'CLUB_MANAGER';
