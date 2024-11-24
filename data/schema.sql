create database if not exists tms_db character set utf8mb4 default collate utf8mb4_unicode_ci;

use tms_db;

CREATE TABLE users
(
    id       INT AUTO_INCREMENT
        PRIMARY KEY,
    email    VARCHAR(255)                          NOT NULL,
    password VARCHAR(255)                          NOT NULL,
    role     ENUM ('USER', 'ADMIN') DEFAULT 'USER' NOT NULL,
    CONSTRAINT email UNIQUE (email)
);

CREATE TABLE tasks
(
    id          BIGINT AUTO_INCREMENT
        PRIMARY KEY,
    title       VARCHAR(500)                                                             NOT NULL,
    description TEXT                                                                     NULL,
    status      ENUM ('NEW', 'WAITING', 'IN_PROGRESS', 'DONE', 'CANCELED') DEFAULT 'NEW' NOT NULL,
    priority    ENUM ('LOW', 'MEDIUM', 'HIGH', 'CRITICAL')                 DEFAULT 'LOW' NOT NULL,
    executor_id INT                                                                      NULL,
    author_id   INT                                                                      NOT NULL,
    created_at  DATETIME                                                                 NOT NULL,
    updated_at  DATETIME                                                                 NOT NULL,
    CONSTRAINT tasks_ibfk_2
        FOREIGN KEY (author_id) REFERENCES users (id)
            ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT tasks_users_id_fk
        FOREIGN KEY (executor_id) REFERENCES users (id)
            ON UPDATE CASCADE ON DELETE SET NULL
);

CREATE TABLE comments
(
    id         BIGINT AUTO_INCREMENT
        PRIMARY KEY,
    task_id    BIGINT       NOT NULL,
    content    VARCHAR(500) NOT NULL,
    author_id  INT          NOT NULL,
    created_at DATETIME     NOT NULL,
    updated_at DATETIME     NOT NULL,
    CONSTRAINT comments_ibfk_1
        FOREIGN KEY (task_id) REFERENCES tasks (id)
            ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT comments_ibfk_2
        FOREIGN KEY (author_id) REFERENCES users (id)
            ON UPDATE CASCADE ON DELETE CASCADE
);

DELIMITER $$

CREATE TRIGGER before_user_delete
    BEFORE DELETE
    ON users
    FOR EACH ROW
BEGIN
    UPDATE tasks
    SET executor_id = author_id
    WHERE executor_id = OLD.id;
END; $$

DELIMITER ;