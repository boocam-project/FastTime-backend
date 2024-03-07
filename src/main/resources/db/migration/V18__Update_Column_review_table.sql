ALTER TABLE review
DROP
COLUMN bootcamp;

ALTER TABLE review
    ADD COLUMN bootcamp_id BIGINT,
ADD FOREIGN KEY (bootcamp_id) REFERENCES BootCamp(id);