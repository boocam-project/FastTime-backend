CREATE TABLE activity
(
    id               BIGINT AUTO_INCREMENT NOT NULL,
    title            VARCHAR(255) NOT NULL,
    organization     VARCHAR(255) NOT NULL,
    corporate_type   VARCHAR(255) NOT NULL,
    participate      VARCHAR(255) NOT NULL,
    start_date       DATE         NOT NULL,
    end_date         DATE         NOT NULL,
    `period`         VARCHAR(255) NOT NULL,
    recruitment      INT          NOT NULL,
    area             VARCHAR(255) NOT NULL,
    preferred_skill  VARCHAR(255) NOT NULL,
    homepage_url     VARCHAR(255) NOT NULL,
    field            VARCHAR(255) NOT NULL,
    activity_benefit VARCHAR(255) NOT NULL,
    bonus_benefit    VARCHAR(255) NOT NULL,
    description      TEXT(1000) NOT NULL,
    image_url        VARCHAR(255) NOT NULL,
    status           enum ('BEFORE','DURING','CLOSED') NOT NULL,
    CONSTRAINT pk_activity PRIMARY KEY (id)
);