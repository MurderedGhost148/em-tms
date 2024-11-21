INSERT INTO users (id, email, password, role) VALUES
    (1, 'admin1@test.ru', '$2a$10$rE8zT/MhlQlwRAFqoeSoAOXxnopkdVxXBJ/1Tgu8ws9dujqOsU1j.', 'ADMIN'),
    (2, 'user1@test.ru', '$2a$10$st6KaLrhWiFAw6mA3vSVseVDwAHCIJrOSyqvqs75f5Kbno8SAF2rW', 'USER'),
    (3, 'user2@test.ru', '$2a$10$st6KaLrhWiFAw6mA3vSVseVDwAHCIJrOSyqvqs75f5Kbno8SAF2rW', 'USER'),
    (4, 'user3@test.ru', '$2a$10$st6KaLrhWiFAw6mA3vSVseVDwAHCIJrOSyqvqs75f5Kbno8SAF2rW', 'USER'),
    (5, 'admin2@test.ru', '$2a$10$rE8zT/MhlQlwRAFqoeSoAOXxnopkdVxXBJ/1Tgu8ws9dujqOsU1j.', 'ADMIN'),
    (6, 'admin3@test.ru', '$2a$10$st6KaLrhWiFAw6mA3vSVseVDwAHCIJrOSyqvqs75f5Kbno8SAF2rW', 'USER');

INSERT INTO tasks (id, title, description, status, priority, executor_id, author_id, created_at, updated_at) VALUES
    (1, 'Задача 1', 'Описание задачи 1', 'WAITING', 'LOW', 2, 1, '2022-01-01 00:00:00', '2022-01-01 00:00:00'),
    (2, 'Задача 2', 'Описание задачи 2', 'NEW', 'MEDIUM', 3, 1, '2022-01-01 00:00:00', '2022-01-01 00:00:00'),
    (3, 'Задача 3', 'Описание задачи 3', 'CANCELED', 'HIGH', 4, 5, '2022-01-01 00:00:00', '2022-01-01 00:00:00'),
    (4, 'Задача 4', 'Описание задачи 4', 'NEW', 'CRITICAL', 4, 1, '2022-01-01 00:00:00', '2022-01-01 00:00:00'),
    (5, 'Задача 5', 'Описание задачи 5', 'IN_PROGRESS', 'LOW', 2, 1, '2022-01-01 00:00:00', '2022-01-01 00:00:00');

INSERT INTO comments (id, task_id, content, author_id, created_at, updated_at) VALUES
    (1, 1, 'Комментарий 1', 1, '2022-01-01 01:00:00', '2022-01-01 01:00:00'),
    (2, 2, 'Комментарий 2', 3, '2022-01-01 02:00:00', '2022-01-01 02:00:00'),
    (3, 3, 'Комментарий 3', 4, '2022-01-01 03:00:00', '2022-01-01 03:00:00'),
    (4, 1, 'Комментарий 4', 2, '2022-01-01 04:00:00', '2022-01-01 04:00:00'),
    (5, 3, 'Комментарий 5', 4, '2022-01-01 05:00:00', '2022-01-01 05:00:00'),
    (6, 4, 'Комментарий 6', 4, '2022-01-01 06:00:00', '2022-01-01 06:00:00'),
    (7, 4, 'Комментарий 7', 5, '2022-01-01 07:00:00', '2022-01-01 07:00:00'),
    (8, 5, 'Комментарий 8', 2, '2022-01-01 08:00:00', '2022-01-01 08:00:00'),
    (9, 5, 'Комментарий 9', 2, '2022-01-01 09:00:00', '2022-01-01 09:00:00'),
    (10, 5, 'Комментарий 10', 5, '2022-01-01 10:00:00', '2022-01-01 10:00:00');
