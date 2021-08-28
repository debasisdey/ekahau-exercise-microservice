CREATE DATABASE IF NOT EXISTS ekahau_exercise;
USE ekahau_exercise;

CREATE TABLE `book`
(
    `book_id`   int(11) NOT NULL AUTO_INCREMENT,
    `title`     varchar(200) NOT NULL ,
    `author`    varchar(100) NULL DEFAULT '',
    `year`      varchar(10) NULL DEFAULT '',
    `price`     varchar(10) NULL DEFAULT '0',
    PRIMARY KEY (`book_id`),
    UNIQUE KEY `title_idx` (`title`)
) ENGINE=InnoDB;

CREATE TABLE `user`
(
    `userid`       int(11) NOT NULL AUTO_INCREMENT,
    `firstname`    varchar(50) NULL DEFAULT '',
    `lastname`    varchar(50) NULL DEFAULT '',
    `emailaddress` varchar(255) NULL DEFAULT '',
    `password`     varchar(255) NULL DEFAULT '',

    PRIMARY KEY (`userid`),
    UNIQUE KEY `emailaddress_idx` (`emailaddress`)
) ENGINE=InnoDB;

CREATE USER test IDENTIFIED BY 'test';
GRANT ALL PRIVILEGES ON ekahau_exercise.* TO test;
FLUSH PRIVILEGES;