ALTER TABLE groups
    ADD COLUMN owner_id BIGINT NOT NULL;

ALTER TABLE groups
    ADD COLUMN join_code VARCHAR(20) NOT NULL;

ALTER TABLE groups
    ADD CONSTRAINT fk_groups_owner
        FOREIGN KEY (owner_id) REFERENCES users(id);

CREATE UNIQUE INDEX uq_groups_join_code
    ON groups (join_code);
