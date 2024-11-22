USE tms_db;

INSERT INTO users (email, password, role) VALUES
    ('admin1@test.ru', '$2a$10$rE8zT/MhlQlwRAFqoeSoAOXxnopkdVxXBJ/1Tgu8ws9dujqOsU1j.', 'ADMIN'),
    ('user1@test.ru', '$2a$10$st6KaLrhWiFAw6mA3vSVseVDwAHCIJrOSyqvqs75f5Kbno8SAF2rW', 'USER'),
    ('user2@test.ru', '$2a$10$st6KaLrhWiFAw6mA3vSVseVDwAHCIJrOSyqvqs75f5Kbno8SAF2rW', 'USER'),
    ('user3@test.ru', '$2a$10$st6KaLrhWiFAw6mA3vSVseVDwAHCIJrOSyqvqs75f5Kbno8SAF2rW', 'USER'),
    ('admin2@test.ru', '$2a$10$rE8zT/MhlQlwRAFqoeSoAOXxnopkdVxXBJ/1Tgu8ws9dujqOsU1j.', 'ADMIN'),
    ('admin3@test.ru', '$2a$10$st6KaLrhWiFAw6mA3vSVseVDwAHCIJrOSyqvqs75f5Kbno8SAF2rW', 'USER');
;