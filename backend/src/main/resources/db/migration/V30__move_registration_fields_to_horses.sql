ALTER TABLE horses ADD COLUMN registry_name VARCHAR(255);
ALTER TABLE horses ADD COLUMN registration_number VARCHAR(100);

UPDATE horses h
SET registry_name = p.registry_name,
    registration_number = p.registration_number
FROM horse_pedigrees p
WHERE h.id = p.horse_id;

ALTER TABLE horse_pedigrees DROP COLUMN registry_name;
ALTER TABLE horse_pedigrees DROP COLUMN registration_number;
