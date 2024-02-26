CREATE TABLE BootCamp
(
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_at        datetime NULL,
    updated_at        datetime NULL,
    deleted_at        datetime NULL,
    name              VARCHAR(255) NOT NULL,
    description       TEXT,
    image             TEXT,
    government_funded BOOLEAN      NOT NULL,
    organizer         VARCHAR(255),
    website           VARCHAR(255),
    course            TEXT
);