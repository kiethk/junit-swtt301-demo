-- =====================================================================
-- V39: ALIGN RBAC WITH THE REFACTORED FLOW
-- =====================================================================

-- 1. MedicalRecord was renamed conceptually to HealthRecord.
UPDATE permissions
SET code = 'HEALTH_RECORD_CREATE',
    description = 'Create horse health records from veterinary examinations and care events'
WHERE code = 'MEDICAL_RECORD_CREATE';

-- Preventive care permission now manages schedules only; actual work is stored
-- as HealthRecord.
UPDATE permissions
SET description = 'Create and manage preventive care schedules'
WHERE code = 'PREVENTIVE_CARE_CREATE';

-- 2. Groom now creates/reuses the Horse when the candidate is admitted to Q.
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
CROSS JOIN permissions p
WHERE r.name = 'GROOM'
  AND p.code = 'HORSE_CREATE'
ON CONFLICT DO NOTHING;

-- Club Manager no longer creates the Horse during final approval.
DELETE FROM role_permissions rp
USING roles r, permissions p
WHERE rp.role_id = r.id
  AND rp.permission_id = p.id
  AND r.name = 'CLUB_MANAGER'
  AND p.code = 'HORSE_CREATE';

-- 3. Official HorsePedigree is created/updated when Groom creates/reuses Horse.
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
CROSS JOIN permissions p
WHERE r.name = 'GROOM'
  AND p.code = 'HORSE_PEDIGREE_MANAGE'
ON CONFLICT DO NOTHING;

-- Owner supplies pedigree data through CandidateHorseProfile only.
DELETE FROM role_permissions rp
USING roles r, permissions p
WHERE rp.role_id = r.id
  AND rp.permission_id = p.id
  AND r.name = 'HORSE_OWNER'
  AND p.code = 'HORSE_PEDIGREE_MANAGE';
