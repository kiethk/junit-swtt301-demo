-- =====================================================================
-- V42: SEED PUBLIC EQUINE REFERENCE DATA
--
-- Source: API Equides (public IFCE-derived data), documented example:
--   QABALAH MERCURY
--   IFCE public id: Z4ogLhlkS2CeUdq0bZ0YFw
--   race: Trotteur Francais
--   birth year: 2026
--   sire: BOOSTER WINNER
--   dam: JEWELLE DARK
--   maternal grandsire: CHARLY DU NOYER
--
-- IMPORTANT:
-- API Equides exposes the IFCE public fiche ID, not UELN. Therefore
-- registration_number is intentionally NULL. Do NOT place the 22-char IFCE ID
-- into registration_number because RTMS reserves that column for UELN.
--
-- RTMS owner/status/stall/admission values below are synthetic test-state only;
-- horse identity/pedigree names are the source-derived part.
-- =====================================================================

-- 1. Seed two known parents as ordinary RTMS horses so pedigree resolution can
--    demonstrate internal sire/dam links. Exact DOB is intentionally NULL: the
--    API example does not publish a full birth date.
INSERT INTO horses (
    name,
    breed,
    date_of_birth,
    current_status,
    stable_location,
    current_stall_id,
    owner_id,
    registry_name,
    registration_number,
    created_at,
    updated_at
)
SELECT
    'BOOSTER WINNER',
    'Trotteur Francais',
    NULL,
    'ELIGIBLE',
    NULL,
    ss.id,
    u.id,
    'SIRE',
    NULL,
    NOW(),
    NOW()
FROM users u
JOIN stable_stalls ss ON ss.stall_code = 'A1' AND ss.status = 'AVAILABLE'
WHERE u.email = 'owner@rtms.com'
  AND NOT EXISTS (SELECT 1 FROM horses h WHERE h.name = 'BOOSTER WINNER' AND h.registry_name = 'SIRE');

UPDATE stable_stalls ss
SET status = 'OCCUPIED', updated_at = NOW()
WHERE ss.stall_code = 'A1'
  AND EXISTS (
      SELECT 1 FROM horses h
      WHERE h.name = 'BOOSTER WINNER'
        AND h.registry_name = 'SIRE'
        AND h.current_stall_id = ss.id
  );

INSERT INTO horses (
    name,
    breed,
    date_of_birth,
    current_status,
    stable_location,
    current_stall_id,
    owner_id,
    registry_name,
    registration_number,
    created_at,
    updated_at
)
SELECT
    'JEWELLE DARK',
    'Trotteur Francais',
    NULL,
    'ELIGIBLE',
    NULL,
    ss.id,
    u.id,
    'SIRE',
    NULL,
    NOW(),
    NOW()
FROM users u
JOIN stable_stalls ss ON ss.stall_code = 'A2' AND ss.status = 'AVAILABLE'
WHERE u.email = 'owner@rtms.com'
  AND NOT EXISTS (SELECT 1 FROM horses h WHERE h.name = 'JEWELLE DARK' AND h.registry_name = 'SIRE');

UPDATE stable_stalls ss
SET status = 'OCCUPIED', updated_at = NOW()
WHERE ss.stall_code = 'A2'
  AND EXISTS (
      SELECT 1 FROM horses h
      WHERE h.name = 'JEWELLE DARK'
        AND h.registry_name = 'SIRE'
        AND h.current_stall_id = ss.id
  );

-- 2. Seed QABALAH MERCURY in the NEW admission flow: Horse already exists only
--    because Groom has accepted the dossier and assigned quarantine stall Q1.
INSERT INTO horses (
    name,
    breed,
    date_of_birth,
    current_status,
    stable_location,
    current_stall_id,
    owner_id,
    registry_name,
    registration_number,
    created_at,
    updated_at
)
SELECT
    'QABALAH MERCURY',
    'Trotteur Francais',
    NULL,
    'CANDIDATE',
    NULL,
    ss.id,
    u.id,
    'SIRE',
    NULL,
    NOW(),
    NOW()
FROM users u
JOIN stable_stalls ss ON ss.stall_code = 'Q1' AND ss.status = 'AVAILABLE'
WHERE u.email = 'owner@rtms.com'
  AND NOT EXISTS (SELECT 1 FROM horses h WHERE h.name = 'QABALAH MERCURY' AND h.registry_name = 'SIRE');

UPDATE stable_stalls ss
SET status = 'OCCUPIED', updated_at = NOW()
WHERE ss.stall_code = 'Q1'
  AND EXISTS (
      SELECT 1 FROM horses h
      WHERE h.name = 'QABALAH MERCURY'
        AND h.registry_name = 'SIRE'
        AND h.current_stall_id = ss.id
  );

-- 3. Official pedigree exists because Horse was created by Groom.
INSERT INTO horse_pedigrees (
    horse_id,
    sire_id,
    dam_id,
    sire_name,
    sire_registration_number,
    dam_name,
    dam_registration_number,
    pedigree_notes
)
SELECT
    child.id,
    sire.id,
    dam.id,
    'BOOSTER WINNER',
    NULL,
    'JEWELLE DARK',
    NULL,
    'Public pedigree names sourced from API Equides / IFCE-derived data. UELNs are not exposed by the API and are intentionally not fabricated.'
FROM horses child
JOIN horses sire ON sire.name = 'BOOSTER WINNER' AND sire.registry_name = 'SIRE'
JOIN horses dam ON dam.name = 'JEWELLE DARK' AND dam.registry_name = 'SIRE'
WHERE child.name = 'QABALAH MERCURY'
  AND child.registry_name = 'SIRE'
  AND NOT EXISTS (
      SELECT 1 FROM horse_pedigrees hp WHERE hp.horse_id = child.id
  );

-- 4. Seed one coherent Admission currently waiting for Vet initial examination.
INSERT INTO admission_applications (
    owner_id,
    status,
    quarantine_stall_id,
    groom_id,
    groom_decision,
    groom_feedback,
    groom_reviewed_at,
    horse_id,
    submitted_at,
    created_at,
    updated_at
)
SELECT
    owner_user.id,
    'VET_REVIEW',
    q.id,
    groom_user.id,
    'APPROVED',
    'Seed: dossier complete and quarantine stall assigned.',
    NOW(),
    h.id,
    NOW(),
    NOW(),
    NOW()
FROM users owner_user
JOIN users groom_user ON groom_user.email = 'groom@rtms.com'
JOIN horses h ON h.name = 'QABALAH MERCURY' AND h.registry_name = 'SIRE'
JOIN stable_stalls q ON q.id = h.current_stall_id AND q.stall_code = 'Q1'
WHERE owner_user.email = 'owner@rtms.com'
  AND NOT EXISTS (
      SELECT 1
      FROM admission_applications aa
      WHERE aa.horse_id = h.id
        AND aa.status NOT IN ('APPROVED', 'REJECTED')
  );

INSERT INTO candidate_horse_profiles (
    admission_id,
    name,
    breed,
    date_of_birth,
    registration_number,
    registry_name,
    sire_name,
    sire_registration_number,
    dam_name,
    dam_registration_number,
    pedigree_notes,
    created_at,
    updated_at
)
SELECT
    aa.id,
    'QABALAH MERCURY',
    'Trotteur Francais',
    NULL,
    NULL,
    'SIRE',
    'BOOSTER WINNER',
    NULL,
    'JEWELLE DARK',
    NULL,
    'API Equides example reports birth year 2026 and the documented sire/dam names; full date and UELN are not fabricated.',
    NOW(),
    NOW()
FROM admission_applications aa
JOIN horses h ON h.id = aa.horse_id
WHERE h.name = 'QABALAH MERCURY'
  AND h.registry_name = 'SIRE'
  AND aa.status = 'VET_REVIEW'
  AND NOT EXISTS (
      SELECT 1 FROM candidate_horse_profiles chp WHERE chp.admission_id = aa.id
  );

-- 5. Auto-generated INITIAL_EXAM has no date and no assigned Vet yet.
INSERT INTO preventive_care_schedules (
    horse_id,
    veterinarian_id,
    care_type,
    scheduled_date,
    description,
    status,
    created_at,
    updated_at
)
SELECT
    h.id,
    NULL,
    'INITIAL_EXAM',
    NULL,
    'Initial admission physical examination in quarantine area',
    'PENDING',
    NOW(),
    NOW()
FROM horses h
WHERE h.name = 'QABALAH MERCURY'
  AND h.registry_name = 'SIRE'
  AND NOT EXISTS (
      SELECT 1
      FROM preventive_care_schedules pcs
      WHERE pcs.horse_id = h.id
        AND pcs.care_type = 'INITIAL_EXAM'
        AND pcs.status = 'PENDING'
  );
