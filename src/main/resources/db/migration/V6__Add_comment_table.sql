CREATE TABLE comment
(
    id                BIGINT AUTO_INCREMENT NOT NULL,
    created_at        datetime              NULL,
    updated_at        datetime              NULL,
    deleted_at        datetime              NULL,
    article_id        BIGINT                NULL,
    member_id         BIGINT                NULL,
    content           VARCHAR(255)          NULL,
    anonymity         BIT(1)                NOT NULL,
    comment_parent_id BIGINT                NULL,
    CONSTRAINT pk_comment PRIMARY KEY (id)
);

ALTER TABLE comment
    ADD CONSTRAINT FK_COMMENT_ON_ARTICLE FOREIGN KEY (article_id) REFERENCES article (id);

ALTER TABLE comment
    ADD CONSTRAINT FK_COMMENT_ON_COMMENT_PARENT FOREIGN KEY (comment_parent_id) REFERENCES comment (id);

ALTER TABLE comment
    ADD CONSTRAINT FK_COMMENT_ON_MEMBER FOREIGN KEY (member_id) REFERENCES member (id);