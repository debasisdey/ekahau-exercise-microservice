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