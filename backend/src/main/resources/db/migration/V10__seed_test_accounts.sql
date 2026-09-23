INSERT INTO users (full_name, email, password_hash, role_id, is_active)
SELECT 'Test Trainer', 'trainer@rtms.com', '$2a$10$2rXcS5LHx7wVYOkClxQ1YuIvg4myh7UgloaGcwcgvnahK9pTWVEJ.', r.id, true
FROM roles r WHERE r.name = 'HEAD_TRAINER';

INSERT INTO users (full_name, email, password_hash, role_id, is_active)
SELECT 'Test Vet', 'vet@rtms.com', '$2a$10$2rXcS5LHx7wVYOkClxQ1YuIvg4myh7UgloaGcwcgvnahK9pTWVEJ.', r.id, true
FROM roles r WHERE r.name = 'VETERINARIAN';

INSERT INTO users (full_name, email, password_hash, role_id, is_active)
SELECT 'Test Groom', 'groom@rtms.com', '$2a$10$2rXcS5LHx7wVYOkClxQ1YuIvg4myh7UgloaGcwcgvnahK9pTWVEJ.', r.id, true
FROM roles r WHERE r.name = 'GROOM';

INSERT INTO users (full_name, email, password_hash, role_id, is_active)
SELECT 'Test Owner', 'owner@rtms.com', '$2a$10$2rXcS5LHx7wVYOkClxQ1YuIvg4myh7UgloaGcwcgvnahK9pTWVEJ.', r.id, true
FROM roles r WHERE r.name = 'HORSE_OWNER';