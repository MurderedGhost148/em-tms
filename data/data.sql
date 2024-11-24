USE tms_db;

INSERT INTO users (email, password, role) VALUES
    ('admin1@test.ru', '$2a$10$rE8zT/MhlQlwRAFqoeSoAOXxnopkdVxXBJ/1Tgu8ws9dujqOsU1j.', 'ADMIN'), /* password = 87654321cxZ! */
    ('user1@test.ru', '$2a$10$st6KaLrhWiFAw6mA3vSVseVDwAHCIJrOSyqvqs75f5Kbno8SAF2rW', 'USER'), /* password = 12345zxC! */
    ('user2@test.ru', '$2a$10$st6KaLrhWiFAw6mA3vSVseVDwAHCIJrOSyqvqs75f5Kbno8SAF2rW', 'USER'), /* password = 12345zxC! */
    ('user3@test.ru', '$2a$10$st6KaLrhWiFAw6mA3vSVseVDwAHCIJrOSyqvqs75f5Kbno8SAF2rW', 'USER'), /* password = 12345zxC! */
    ('admin2@test.ru', '$2a$10$rE8zT/MhlQlwRAFqoeSoAOXxnopkdVxXBJ/1Tgu8ws9dujqOsU1j.', 'ADMIN'), /* password = 87654321cxZ! */
    ('user4@test.ru', '$2a$10$st6KaLrhWiFAw6mA3vSVseVDwAHCIJrOSyqvqs75f5Kbno8SAF2rW', 'USER'); /* password = 12345zxC! */
;