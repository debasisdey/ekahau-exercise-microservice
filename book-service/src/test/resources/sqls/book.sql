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