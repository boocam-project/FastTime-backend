CREATE TABLE tag
(
    id      BIGINT AUTO_INCREMENT NOT NULL,
    content VARCHAR(255)          NULL,
    CONSTRAINT pk_tag PRIMARY KEY (id)
);