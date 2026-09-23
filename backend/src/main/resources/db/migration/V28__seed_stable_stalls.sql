-- Seed quarantine stalls: Q1 - Q5
INSERT INTO stable_stalls (
    area_id,
    stall_number,
    stall_code,
    status
)
SELECT
    a.id,
    n.stall_number,
    'Q' || n.stall_number,
    'AVAILABLE'
FROM areas a
CROSS JOIN generate_series(1, 5) AS n(stall_number)
WHERE a.code = 'Q';


-- Seed regular stalls: A1-A10, B1-B10, C1-C10, D1-D10
INSERT INTO stable_stalls (
    area_id,
    stall_number,
    stall_code,
    status
)
SELECT
    a.id,
    n.stall_number,
    a.code || n.stall_number,
    'AVAILABLE'
FROM areas a
CROSS JOIN generate_series(1, 10) AS n(stall_number)
WHERE a.code IN ('A', 'B', 'C', 'D');