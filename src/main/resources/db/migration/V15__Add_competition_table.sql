CREATE TABLE competition
(
    id               BIGINT AUTO_INCREMENT NOT NULL,
    title            VARCHAR(255) NOT NULL,
    organization     VARCHAR(255) NOT NULL,
    corporate_type   VARCHAR(255) NOT NULL,
    participate      INT          NOT NULL,
    award_scale      VARCHAR(255) NOT NULL,
    start_date       VARCHAR(255) NOT NULL,
    end_date         VARCHAR(255) NOT NULL,
    homepage_url     VARCHAR(255) NOT NULL,
    field            VARCHAR(255) NOT NULL,
    activity_benefit VARCHAR(255) NOT NULL,
    bonus_benefit    VARCHAR(255) NOT NULL,
    description      TEXT(1000) NOT NULL,
    image_url        VARCHAR(255) NOT NULL,
    status           VARCHAR(255) NOT NULL
);