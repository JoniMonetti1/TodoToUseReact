CREATE TABLE IF NOT EXISTS groups (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS group_members (
    id BIGSERIAL PRIMARY KEY,
    group_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS group_todo_shares (
    id BIGSERIAL PRIMARY KEY,
    group_id BIGINT NOT NULL,
    todo_id BIGINT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

ALTER TABLE group_members
    ADD CONSTRAINT fk_group_members_group
        FOREIGN KEY (group_id) REFERENCES groups(id);

ALTER TABLE group_members
    ADD CONSTRAINT fk_group_members_user
        FOREIGN KEY (user_id) REFERENCES users(id);

ALTER TABLE group_todo_shares
    ADD CONSTRAINT fk_group_todo_shares_group
        FOREIGN KEY (group_id) REFERENCES groups(id);

ALTER TABLE group_todo_shares
    ADD CONSTRAINT fk_group_todo_shares_todo
        FOREIGN KEY (todo_id) REFERENCES todos(id);

CREATE UNIQUE INDEX uq_group_members_group_user
    ON group_members (group_id, user_id);

CREATE UNIQUE INDEX uq_group_todo_shares_group_todo
    ON group_todo_shares (group_id, todo_id);
