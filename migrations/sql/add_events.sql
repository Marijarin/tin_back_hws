CREATE TABLE IF NOT EXISTS event
(
    id      BIGSERIAL PRIMARY KEY,
    type   varchar NOT NULL,
    link_id bigint  NOT NULL REFERENCES link (id) ON DELETE CASCADE
);
