CREATE TABLE report
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    created_at datetime              NULL,
    updated_at datetime              NULL,
    deleted_at datetime              NULL,
    member_id  BIGINT                NULL,
    article_id BIGINT                NULL,
    CONSTRAINT pk_report PRIMARY KEY (id)
);

ALTER TABLE report
    ADD CONSTRAINT FK_REPORT_ON_ARTICLE FOREIGN KEY (article_id) REFERENCES article (id);

ALTER TABLE report
    ADD CONSTRAINT FK_REPORT_ON_MEMBER FOREIGN KEY (member_id) REFERENCES member (id);