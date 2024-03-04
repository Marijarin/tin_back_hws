CREATE TABLE IF NOT EXISTS student
(
    id         bigint       NOT NULL,
    name       varchar(255) NOT NULL,
    address    text         NOT NULL,
    dept_id       bigint NOT NULL REFERENCES department(id)
);

drop table student;

CREATE TABLE IF NOT EXISTS department
(
    id   bigint       NOT NULL PRIMARY KEY,
    name varchar(255) NOT NULL
);
