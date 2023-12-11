CREATE TABLE member
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    created_at datetime              NULL,
    updated_at datetime              NULL,
    deleted_at datetime              NULL,
    email      VARCHAR(255)          NULL,
    password   VARCHAR(255)          NULL,
    nickname   VARCHAR(255)          NULL,
    `role`     VARCHAR(255)          NULL,
    image      TEXT                  NULL,
    CONSTRAINT pk_member PRIMARY KEY (id)
);