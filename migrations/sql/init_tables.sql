CREATE TABLE IF NOT EXISTS tracker.chat
(
    id         bigint                     NOT NULL PRIMARY KEY,
    created_at timestamp with time zone NOT NULL
);

CREATE TABLE IF NOT EXISTS tracker.link
(
    id          bigint       NOT NULL PRIMARY KEY,
    url         text  NOT NULL,
    description text  NOT NULL
);

CREATE TABLE IF NOT EXISTS tracker.assignment
(
    chat_id       bigint NOT NULL REFERENCES chat(id),
    link_id       bigint NOT NULL REFERENCES link(id)
);
