CREATE TABLE report
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    created_at datetime              NULL,
    updated_at datetime              NULL,
    deleted_at datetime              NULL,
    member_id  BIGINT                NULL,
    post_id    BIGINT                NULL,
    CONSTRAINT pk_report PRIMARY KEY (id)
);

ALTER TABLE report
    ADD CONSTRAINT FK_REPORT_ON_MEMBER FOREIGN KEY (member_id) REFERENCES member (id);

ALTER TABLE report
    ADD CONSTRAINT FK_REPORT_ON_POST FOREIGN KEY (post_id) REFERENCES article (id);