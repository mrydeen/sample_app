-- Create the DB
-- DROP DATABASE IF EXISTS smartgridz;
CREATE DATABASE IF NOT EXISTS smartgridz;

-- Create the User Table
CREATE TABLE smartgridz.users (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
    `login` VARCHAR(255) NOT NULL,
    `userName` VARCHAR(255) NOT NULL,
    `email` VARCHAR(255) NOT NULL,
    `password` VARCHAR(255) NOT NULL,
    `role_id` BIGINT(20) NOT NULL,
    `token_date` DATETIME,
    `reset_password_token` VARCHAR(255),
    PRIMARY KEY (`id`),
    UNIQUE (login),
    UNIQUE (email)
) ENGINE=INNODB;

-- Create the Role Table
CREATE TABLE smartgridz.roles (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
    `type` VARCHAR(128) NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT UC_role UNIQUE (id, type)
) ENGINE=INNODB;

-- Create the File Table
CREATE TABLE smartgridz.files (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
    `filename` VARCHAR(128) NOT NULL,
    `pathname` VARCHAR(512) NOT NULL,
    `description` VARCHAR(1024),
    `file_type` VARCHAR(16) NOT NULL,
    `create_date` DATETIME,
    `size` INT,
    `added_by` BIGINT(20),
    `file_format_version` VARCHAR(64),
    PRIMARY KEY (`id`),
    UNIQUE(filename),
    FOREIGN KEY (added_by) REFERENCES users(id)
) ENGINE=INNODB;

CREATE INDEX idx_UserDate on smartgridz.files (added_by, create_date);
CREATE INDEX idx_DateFile on smartgridz.files (create_date, pathname, filename);


INSERT INTO smartgridz.roles VALUE (NULL, "ADMINISTRATOR");
INSERT INTO smartgridz.roles VALUE (NULL, "OPERATOR");
