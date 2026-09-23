-- =====================================================================
-- V29: REFACTOR TRAINER - GROOM - STALL RELATIONSHIP
-- =====================================================================

-- 1. Groom thuộc quản lý trực tiếp của Trainer
ALTER TABLE groom_profiles
    ADD COLUMN trainer_id BIGINT REFERENCES users(id) ON DELETE SET NULL;

CREATE INDEX idx_groom_profiles_trainer ON groom_profiles(trainer_id);

-- 2. Chuồng (Stall) được phân công cho Groom phụ trách chăm sóc
ALTER TABLE stable_stalls
    ADD COLUMN groom_id BIGINT REFERENCES users(id) ON DELETE SET NULL;

CREATE INDEX idx_stable_stalls_groom ON stable_stalls(groom_id);

-- 3. Xóa groom_id khỏi bảng areas (chuẩn hóa phân công Groom theo từng Stall)
ALTER TABLE areas
DROP COLUMN IF EXISTS groom_id;