-- =====================================================================
-- V35: GRANT STABLE STALL UPDATE TO HEAD TRAINER
-- =====================================================================

-- Cấp quyền STABLE_STALL_UPDATE cho HEAD_TRAINER để quản lý xếp chuồng cho ngựa và phân công Groom
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE p.code = 'STABLE_STALL_UPDATE'
  AND r.name = 'HEAD_TRAINER'
ON CONFLICT DO NOTHING;