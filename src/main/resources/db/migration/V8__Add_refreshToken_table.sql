CREATE TABLE refresh_token
(
    id    BIGINT       NOT NULL COMMENT 'member_id',
    token VARCHAR(255) NULL,
    CONSTRAINT pk_refresh_token PRIMARY KEY (id)
);