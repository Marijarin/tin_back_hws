CREATE TABLE IF NOT EXISTS chat
(
    id         bigint NOT NULL PRIMARY KEY,
    created_at timestamp with time zone NOT NULL
);

CREATE TABLE IF NOT EXISTS link
(
    id          SERIAL PRIMARY KEY,
    url         varchar         NOT NULL,
    description varchar         ,
    last_updated timestamp with time zone NOT NULL
);

CREATE TABLE IF NOT EXISTS assignment
(
    chat_id       bigint NOT NULL REFERENCES chat(id) ON DELETE CASCADE,
    link_id       bigint NOT NULL REFERENCES link(id) ON DELETE CASCADE
);
