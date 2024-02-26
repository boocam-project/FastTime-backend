ALTER TABLE Review
DROP
COLUMN bootcamp;

ALTER TABLE Review
    ADD COLUMN bootcamp_id BIGINT,
ADD FOREIGN KEY (bootcamp_id) REFERENCES BootCamp(id);