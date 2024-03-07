ALTER TABLE member
DROP
COLUMN bootcamp;

ALTER TABLE member
    ADD COLUMN bootcamp_id BIGINT,
ADD FOREIGN KEY (bootcamp_id) REFERENCES BootCamp(id);