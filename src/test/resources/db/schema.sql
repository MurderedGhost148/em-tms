create table users
(
    id       int auto_increment
        primary key,
    email    varchar(255)                          not null,
    password varchar(255)                          not null,
    role     enum ('USER', 'ADMIN') default 'USER' not null,
    constraint email
        unique (email)
);

create table tasks
(
    id          bigint auto_increment
        primary key,
    title       varchar(500)                                                             not null,
    description text                                                                     null,
    status      enum ('NEW', 'WAITING', 'IN_PROGRESS', 'DONE', 'CANCELED') default 'NEW' not null,
    priority    enum ('LOW', 'MEDIUM', 'HIGH', 'CRITICAL')                 default 'LOW' not null,
    executor_id int                                                                      null,
    author_id   int                                                                      not null,
    created_at  datetime                                                                 not null,
    updated_at  datetime                                                                 not null,
    constraint tasks_ibfk_2
        foreign key (author_id) references users (id)
            on update cascade on delete cascade,
    constraint tasks_users_id_fk
        foreign key (executor_id) references users (id)
            on update cascade on delete set null
);

create table comments
(
    id         bigint auto_increment
        primary key,
    task_id    bigint       not null,
    content    varchar(500) not null,
    author_id  int          not null,
    created_at datetime     not null,
    updated_at datetime     not null,
    constraint comments_ibfk_1
        foreign key (task_id) references tasks (id)
            on update cascade on delete cascade,
    constraint comments_ibfk_2
        foreign key (author_id) references users (id)
            on update cascade on delete cascade
);