CREATE TABLE article
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    created_at    datetime              NULL,
    updated_at    datetime              NULL,
    deleted_at    datetime              NULL,
    member_id     BIGINT                NULL,
    title         VARCHAR(255)          NULL,
    anonymity     BIT(1)                NOT NULL,
    like_count    INT                   NOT NULL,
    hate_count    INT                   NOT NULL,
    comment_count INT                   NOT NULL,
    report_status enum('NORMAL', 'WAIT_FOR_REPORT_REVIEW', 'REPORT_ACCEPT', 'REPORT_REJECT')          NULL,
    content       LONGTEXT              NULL,
    CONSTRAINT pk_article PRIMARY KEY (id)
);

CREATE INDEX idx_created_at ON article (created_at);