-- =====================================================================
-- V26: EXTEND HORSE PEDIGREE FOR PARENTS NOT YET IN RTMS
-- =====================================================================

ALTER TABLE horse_pedigrees
    ADD COLUMN sire_name VARCHAR(255),
    ADD COLUMN sire_registration_number VARCHAR(100),
    ADD COLUMN dam_name VARCHAR(255),
    ADD COLUMN dam_registration_number VARCHAR(100);

CREATE INDEX idx_horse_pedigrees_registration_number
    ON horse_pedigrees(registration_number);

CREATE INDEX idx_horse_pedigrees_sire_registration_number
    ON horse_pedigrees(sire_registration_number);

CREATE INDEX idx_horse_pedigrees_dam_registration_number
    ON horse_pedigrees(dam_registration_number);

-- Resolution rule belongs in service logic:
-- 1. Look up an existing official Horse through
--    horse_pedigrees.registration_number.
-- 2. If the parent can be resolved unambiguously, set sire_id/dam_id.
-- 3. Otherwise keep sire_id/dam_id NULL and preserve parent name /
--    registration number.
