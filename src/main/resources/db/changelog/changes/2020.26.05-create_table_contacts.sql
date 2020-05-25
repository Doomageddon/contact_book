--liquibase formatted sql

--changeset Oleksii:Created table contacts

CREATE TABLE IF NOT EXISTS contacts
(
    id          INT             NOT NULL    AUTO_INCREMENT,
    first_name  VARCHAR(255)    NOT NULL,
    last_name   VARCHAR(255)    NOT NULL,
    phone       BIGINT,
    user_id     INT             NOT NULL ,

    CONSTRAINT contacts_PK PRIMARY KEY (id),
    CONSTRAINT contacts_users_FK FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE
);