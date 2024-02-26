ALTER TABLE Member
DROP
COLUMN bootcamp;

ALTER TABLE Member
    ADD COLUMN bootcamp_id BIGINT,
ADD FOREIGN KEY (bootcamp_id) REFERENCES BootCamp(id);