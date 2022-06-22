CREATE TABLE `vacancies`
(
    `title`        text NOT NULL,
    `salary`       text NOT NULL,
    `city`         text NOT NULL,
    `company_name` text NOT NULL,
    `site_name`    text NOT NULL,
    `url`          text NOT NULL,
    `id`           int(11) NOT NULL AUTO_INCREMENT,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;