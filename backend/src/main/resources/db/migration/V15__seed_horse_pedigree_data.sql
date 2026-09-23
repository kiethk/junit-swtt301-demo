-- Lightning Bolt: has both sire and dam
INSERT INTO horse_pedigrees (
    horse_id,
    sire_id,
    dam_id,
    registration_number,
    registry_name,
    pedigree_notes
)
SELECT
    child.id,
    sire.id,
    dam.id,
    'VN-TB-2022-001',
    'RTMS National Stud Registry',
    'Registered Thoroughbred pedigree. Both parents are recorded in the system.'
FROM horses child
         JOIN horses sire ON sire.name = 'Storm King'
         JOIN horses dam ON dam.name = 'Silver Moon'
WHERE child.name = 'Lightning Bolt';


-- Golden Star: sire known, dam not available in the system
INSERT INTO horse_pedigrees (
    horse_id,
    sire_id,
    dam_id,
    registration_number,
    registry_name,
    pedigree_notes
)
SELECT
    child.id,
    sire.id,
    NULL,
    'VN-TB-2023-014',
    'RTMS National Stud Registry',
    'Sire information is available. Dam information has not yet been recorded.'
FROM horses child
         JOIN horses sire ON sire.name = 'Storm King'
WHERE child.name = 'Golden Star';


-- Storm King: dam known, sire not available in the system
INSERT INTO horse_pedigrees (
    horse_id,
    sire_id,
    dam_id,
    registration_number,
    registry_name,
    pedigree_notes
)
SELECT
    child.id,
    NULL,
    dam.id,
    'VN-QH-2022-008',
    'Vietnam Quarter Horse Registry',
    'Dam is registered in the current system. Sire information is pending verification.'
FROM horses child
         JOIN horses dam ON dam.name = 'Silver Moon'
WHERE child.name = 'Storm King';


-- Silver Moon: pedigree record exists but parents are not registered in the system
INSERT INTO horse_pedigrees (
    horse_id,
    sire_id,
    dam_id,
    registration_number,
    registry_name,
    pedigree_notes
)
SELECT
    child.id,
    NULL,
    NULL,
    'VN-AR-2021-003',
    'Vietnam Arabian Registry',
    'Pedigree registration exists, but parent records are not available in the current database.'
FROM horses child
WHERE child.name = 'Silver Moon';