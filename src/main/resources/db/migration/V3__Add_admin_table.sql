CREATE TABLE `admin`
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    created_at datetime              NULL,
    updated_at datetime              NULL,
    deleted_at datetime              NULL,
    member_id  BIGINT                NULL,
    CONSTRAINT pk_admin PRIMARY KEY (id)
);

ALTER TABLE `admin`
    ADD CONSTRAINT uc_admin_member UNIQUE (member_id);

ALTER TABLE `admin`
    ADD CONSTRAINT FK_ADMIN_ON_MEMBER FOREIGN KEY (member_id) REFERENCES member (id);