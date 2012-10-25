/*
-- Query: SELECT * FROM honours.publications_linking
LIMIT 0, 1000

-- Date: 2012-10-23 20:31
*/
Use hornours;
UPDATE `honours`.`publications_linking` SET `name`='publications_themes' WHERE `idhierarchy`='3';
UPDATE `honours`.`publications_linking` SET `name`='publications_forcodes' WHERE `idhierarchy`='1';
UPDATE `honours`.`publications_linking` SET `name`='publications_researchers' WHERE `idhierarchy`='2';

Drop table `join_publications_forcodes_publications_researchers`;
Drop table `join_publications_themes_publications_researchers`;
Drop table `join_publications_forcodes_publications_themes`;
Drop table `join_publications_researchers_publications_themes`;
Drop table `join_publications_researchers_publications_forcodes`;
Drop table `join_publications_themes_publications_forcodes`;

CREATE 
Table `join_publications_forcodes_publications_researchers` AS
    select 
        `c`.`idhierarchy` AS `id1`,
        (`g`.`weighted_sum` * `c`.`weighted_sum`) AS `wsum`,
        `g`.`idhierarchy` AS `id2`
    from
        (`publications_forcodes` `c`
        join `publications_researchers` `g` ON ((`c`.`publicationid` = `g`.`publicationid`)));

CREATE 
Table `join_publications_themes_publications_researchers` AS
    select 
        `c`.`idhierarchy` AS `id1`,
        (`g`.`weighted_sum` * `c`.`weighted_sum`) AS `wsum`,
        `g`.`idhierarchy` AS `id2`
    from
        (`publications_themes` `c`
        join `publications_researchers` `g` ON ((`c`.`publicationid` = `g`.`publicationid`)));

CREATE Table `join_publications_forcodes_publications_themes` 
AS select `c`.`idhierarchy` AS `id1`,
    (`g`.`weighted_sum` * `c`.`weighted_sum`) AS `wsum`,
    `g`.`idhierarchy` AS `id2` 
from
    (`publications_forcodes` `c`
    join `publications_themes` `g` ON ((`c`.`publicationid` = `g`.`publicationid`)));

CREATE Table `join_publications_researchers_publications_themes` 
AS select `c`.`idhierarchy` AS `id1`,
    (`g`.`weighted_sum` * `c`.`weighted_sum`) AS `wsum`,
    `g`.`idhierarchy` AS `id2` 
from
    (`publications_researchers` `c`
    join `publications_themes` `g` ON ((`c`.`publicationid` = `g`.`publicationid`)));

CREATE Table `join_publications_researchers_publications_forcodes` 
AS select `c`.`idhierarchy` AS `id1`,
    (`g`.`weighted_sum` * `c`.`weighted_sum`) AS `wsum`,
    `g`.`idhierarchy` AS `id2` 
from
    (`publications_researchers` `c`
    join `publications_forcodes` `g` ON ((`c`.`publicationid` = `g`.`publicationid`)));

CREATE Table `join_publications_themes_publications_forcodes` 
AS select `c`.`idhierarchy` AS `id1`,
    (`g`.`weighted_sum` * `c`.`weighted_sum`) AS `wsum`,
    `g`.`idhierarchy` AS `id2` 
from
    (`publications_themes` `c`
    join `publications_forcodes` `g` ON ((`c`.`publicationid` = `g`.`publicationid`)));


ALTER TABLE `honours`.`connection` RENAME TO  `honours`.`t_connection` ;
ALTER TABLE `sample`.`atom` CHANGE COLUMN `name` `t_name` VARCHAR(45) NULL DEFAULT NULL  ;
ALTER TABLE `publication_small`.`atom` CHANGE COLUMN `name` `t_name` VARCHAR(45) NULL DEFAULT NULL  ;
ALTER TABLE `movie_small`.`atom` CHANGE COLUMN `name` `t_name` VARCHAR(45) NULL DEFAULT NULL  ;
ALTER TABLE `honours`.`atom` CHANGE COLUMN `name` `t_name` VARCHAR(45) NULL DEFAULT NULL  ;

Create index opt001 on  publications_forcodes(idhierarchy,weighted_sum);
Create index opt002 on publications_themes(idhierarchy,weighted_sum);
Create index opt003 on  publications_researchers(idhierarchy,weighted_sum);