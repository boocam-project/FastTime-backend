CREATE TABLE review
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    created_at datetime              NULL,
    updated_at datetime              NULL,
    deleted_at datetime              NULL,
    title      VARCHAR(255)          NULL,
    bootcamp   VARCHAR(255)          NULL,
    rating     INT                   NOT NULL,
    content    VARCHAR(255)          NULL,
    member_id  BIGINT                NULL,
    CONSTRAINT pk_review PRIMARY KEY (id)
);

ALTER TABLE review
    ADD CONSTRAINT FK_REVIEW_ON_MEMBER FOREIGN KEY (member_id) REFERENCES member (id);