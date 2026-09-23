INSERT INTO horses (name, breed, date_of_birth, current_status, stable_location, owner_id, created_at, updated_at)
SELECT 'Lightning Bolt', 'Thoroughbred', '2022-03-15', 'ELIGIBLE', 'Stable A-01', u.id, NOW(), NOW()
FROM users u WHERE u.email = 'owner@rtms.com';

INSERT INTO horses (name, breed, date_of_birth, current_status, stable_location, owner_id, created_at, updated_at)
SELECT 'Silver Moon', 'Arabian', '2021-07-22', 'ELIGIBLE', 'Stable A-02', u.id, NOW(), NOW()
FROM users u WHERE u.email = 'owner@rtms.com';

INSERT INTO horses (name, breed, date_of_birth, current_status, stable_location, owner_id, created_at, updated_at)
SELECT 'Golden Star', 'Thoroughbred', '2023-01-10', 'MONITORING', 'Stable B-01', u.id, NOW(), NOW()
FROM users u WHERE u.email = 'owner@rtms.com';

INSERT INTO horses (name, breed, date_of_birth, current_status, stable_location, owner_id, created_at, updated_at)
SELECT 'Storm King', 'Quarter Horse', '2022-11-05', 'ELIGIBLE', 'Stable B-02', u.id, NOW(), NOW()
FROM users u WHERE u.email = 'owner@rtms.com';