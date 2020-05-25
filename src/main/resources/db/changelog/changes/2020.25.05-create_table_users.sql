--liquibase formatted sql

--changeset Oleksii:Created table users

CREATE TABLE IF NOT EXISTS users
(
    id          INT             NOT NULL    AUTO_INCREMENT,
    name        VARCHAR(255)    NOT NULL,
    password    VARCHAR(255)    NOT NULL,
    is_enabled  TINYINT         NOT NULL    DEFAULT TRUE,
    role        VARCHAR(255)    NOT NULL,

    CONSTRAINT users_PK PRIMARY KEY (id)
);