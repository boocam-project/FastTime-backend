CREATE TABLE review_tag
(
    id        BIGINT AUTO_INCREMENT NOT NULL,
    review_id BIGINT                NULL,
    tag_id    BIGINT                NULL,
    CONSTRAINT pk_reviewtag PRIMARY KEY (id)
);

ALTER TABLE review_tag
    ADD CONSTRAINT FK_REVIEWTAG_ON_REVIEW FOREIGN KEY (review_id) REFERENCES review (id);

ALTER TABLE review_tag
    ADD CONSTRAINT FK_REVIEWTAG_ON_TAG FOREIGN KEY (tag_id) REFERENCES tag (id);