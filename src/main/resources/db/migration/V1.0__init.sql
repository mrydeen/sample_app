-- Create the DB
-- DROP DATABASE IF EXISTS smartgridz;
CREATE DATABASE IF NOT EXISTS smartgridz;

-- Create the User Table
CREATE TABLE smartgridz.users (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
    `username` VARCHAR(255) NOT NULL,
    `name` VARCHAR(255) NOT NULL,
    `email` VARCHAR(255) NOT NULL,
    `password` VARCHAR(255) NOT NULL,
    `role_id` BIGINT(20) NOT NULL,
    `token_date` DATETIME,
    `reset_password_token` VARCHAR(255),
    PRIMARY KEY (`id`),
    CONSTRAINT UC_User UNIQUE (id, email)
) ENGINE=INNODB;

-- Create the Role Table
CREATE TABLE smartgridz.roles (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
    `type` VARCHAR(128) NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT UC_role UNIQUE (id, type)
) ENGINE=INNODB;

INSERT INTO smartgridz.roles VALUE (NULL, "ADMINISTRATOR");
INSERT INTO smartgridz.roles VALUE (NULL, "OPERATOR");
