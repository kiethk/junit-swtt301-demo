-- =====================================================================
-- V41: STANDARDIZE REGISTRATION_NUMBER AS UELN
--
-- UELN is a 15-character alphanumeric lifetime identifier.
-- registration_number remains nullable because an external/public source may
-- not expose the UELN. Never invent a UELN when it is unknown.
-- =====================================================================

-- Normalize known values first.
UPDATE horses
SET registration_number = UPPER(TRIM(registration_number))
WHERE registration_number IS NOT NULL;

UPDATE candidate_horse_profiles
SET registration_number = UPPER(TRIM(registration_number)),
    sire_registration_number = UPPER(TRIM(sire_registration_number)),
    dam_registration_number = UPPER(TRIM(dam_registration_number));

UPDATE horse_pedigrees
SET sire_registration_number = UPPER(TRIM(sire_registration_number)),
    dam_registration_number = UPPER(TRIM(dam_registration_number));

-- Remove legacy/demo identifiers that are not valid UELNs instead of carrying
-- false registration data forward. Historical Admission rows themselves stay.
UPDATE horses
SET registration_number = NULL
WHERE registration_number IS NOT NULL
  AND registration_number !~ '^[A-Z0-9]{15}$';

UPDATE candidate_horse_profiles
SET registration_number = NULL
WHERE registration_number IS NOT NULL
  AND registration_number !~ '^[A-Z0-9]{15}$';

UPDATE candidate_horse_profiles
SET sire_registration_number = NULL
WHERE sire_registration_number IS NOT NULL
  AND sire_registration_number !~ '^[A-Z0-9]{15}$';

UPDATE candidate_horse_profiles
SET dam_registration_number = NULL
WHERE dam_registration_number IS NOT NULL
  AND dam_registration_number !~ '^[A-Z0-9]{15}$';

UPDATE horse_pedigrees
SET sire_registration_number = NULL
WHERE sire_registration_number IS NOT NULL
  AND sire_registration_number !~ '^[A-Z0-9]{15}$';

UPDATE horse_pedigrees
SET dam_registration_number = NULL
WHERE dam_registration_number IS NOT NULL
  AND dam_registration_number !~ '^[A-Z0-9]{15}$';

-- Tighten column sizes after cleanup.
ALTER TABLE horses
    ALTER COLUMN registration_number TYPE VARCHAR(15);

ALTER TABLE candidate_horse_profiles
    ALTER COLUMN registration_number TYPE VARCHAR(15),
    ALTER COLUMN sire_registration_number TYPE VARCHAR(15),
    ALTER COLUMN dam_registration_number TYPE VARCHAR(15);

ALTER TABLE horse_pedigrees
    ALTER COLUMN sire_registration_number TYPE VARCHAR(15),
    ALTER COLUMN dam_registration_number TYPE VARCHAR(15);

-- Horse UELN is globally unique when known. PostgreSQL UNIQUE permits multiple
-- NULL values, which is required when an external source does not publish UELN.
ALTER TABLE horses
    ADD CONSTRAINT uq_horses_registration_number UNIQUE (registration_number);

ALTER TABLE horses
    ADD CONSTRAINT chk_horses_registration_number_ueln
        CHECK (
            registration_number IS NULL
            OR registration_number ~ '^[A-Z0-9]{15}$'
        );

ALTER TABLE candidate_horse_profiles
    ADD CONSTRAINT chk_candidate_registration_number_ueln
        CHECK (
            registration_number IS NULL
            OR registration_number ~ '^[A-Z0-9]{15}$'
        ),
    ADD CONSTRAINT chk_candidate_sire_registration_number_ueln
        CHECK (
            sire_registration_number IS NULL
            OR sire_registration_number ~ '^[A-Z0-9]{15}$'
        ),
    ADD CONSTRAINT chk_candidate_dam_registration_number_ueln
        CHECK (
            dam_registration_number IS NULL
            OR dam_registration_number ~ '^[A-Z0-9]{15}$'
        );

ALTER TABLE horse_pedigrees
    ADD CONSTRAINT chk_pedigree_sire_registration_number_ueln
        CHECK (
            sire_registration_number IS NULL
            OR sire_registration_number ~ '^[A-Z0-9]{15}$'
        ),
    ADD CONSTRAINT chk_pedigree_dam_registration_number_ueln
        CHECK (
            dam_registration_number IS NULL
            OR dam_registration_number ~ '^[A-Z0-9]{15}$'
        );
