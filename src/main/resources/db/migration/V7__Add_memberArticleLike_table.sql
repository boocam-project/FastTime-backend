CREATE TABLE member_article_like
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    created_at datetime              NULL,
    updated_at datetime              NULL,
    deleted_at datetime              NULL,
    member_id  BIGINT                NULL,
    article_id BIGINT                NULL,
    type       BIT(1)                NULL COMMENT '좋아요 = 1, 싫어요 = 0',
    CONSTRAINT pk_member_article_like PRIMARY KEY (id)
);

ALTER TABLE member_article_like
    ADD CONSTRAINT uc_ab6eca244dfc0cf696a171e0b UNIQUE (member_id, article_id);

ALTER TABLE member_article_like
    ADD CONSTRAINT FK_MEMBER_ARTICLE_LIKE_ON_ARTICLE FOREIGN KEY (article_id) REFERENCES article (id);

ALTER TABLE member_article_like
    ADD CONSTRAINT FK_MEMBER_ARTICLE_LIKE_ON_MEMBER FOREIGN KEY (member_id) REFERENCES member (id);