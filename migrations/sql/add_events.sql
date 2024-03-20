CREATE TABLE IF NOT EXISTS events
(
    event   varchar NOT NULL,
    link_id bigint  NOT NULL REFERENCES link (id) ON DELETE CASCADE
);
