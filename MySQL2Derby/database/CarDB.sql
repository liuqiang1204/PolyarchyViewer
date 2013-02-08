Drop database CarDB;
Create database CarDB;
use CarDB;
-- ---------------------------------------------------------------
-- create table and get the original data
-- ---------------------------------------------------------------
Create table OriginalTB(
	carID INTEGER  primary key NOT NULL AUTO_INCREMENT,
	C_Make Varchar(50),
	C_Year INTEGER,
	C_NameOfModel Varchar(100),
	C_Country Varchar(100),
	C_Continent Varchar(20),
	C_Type Varchar(50),
	C_Doors INTEGER,
	C_Seats INTEGER,
	C_Weight_Kg INTEGER,
	C_Power_HP INTEGER,
	C_Cylinders INTEGER,
	C_EngineType Varchar(50)
);

-- load from file
LOAD DATA LOCAL INFILE 'd:\\carDB_Since2000_20130208.csv'
INTO TABLE OriginalTB 
FIELDS TERMINATED BY ',' 
ENCLOSED BY '"'
LINES TERMINATED BY '\r\n'
(C_Make,C_Year,C_NameOfModel,C_Country,C_Continent,C_Type,C_Doors,C_Seats,C_Weight_Kg,C_Power_HP,C_Cylinders,C_EngineType);
delete FROM originaltb where c_year<2000;

-- ---------------------------------------------------------------
-- create basic tables
-- ---------------------------------------------------------------
CREATE TABLE atom (
	displayName VARCHAR(45) NOT NULL,
	t_name VARCHAR(45),
	columnName VARCHAR(45),
	PRIMARY KEY (displayName)
);
INSERT INTO atom (displayName, t_name, columnName) VALUES ('Cars', 'Cars_linking', 'carID');

CREATE TABLE hierarchy (
	idhierarchy INTEGER NOT NULL AUTO_INCREMENT,
	t_name VARCHAR(45),
	level INTEGER,
	PRIMARY KEY (idhierarchy)
);
INSERT INTO hierarchy (t_name, level) VALUES ('Make_hierarchy', 1);
INSERT INTO hierarchy (t_name, level) VALUES ('Year_hierarchy', 1);
INSERT INTO hierarchy (t_name, level) VALUES ('Country_hierarchy', 2);
INSERT INTO hierarchy (t_name, level) VALUES ('Body_hierarchy', 3);
INSERT INTO hierarchy (t_name, level) VALUES ('Weight_hierarchy', 1);
INSERT INTO hierarchy (t_name, level) VALUES ('Engine_hierarchy', 3);

-- ---------------------------------------------------------------
-- cars
-- ---------------------------------------------------------------
Create table Cars as (SELECT carID, C_Make,C_Year,C_NameOfModel FROM cardb.originaltb);
ALTER TABLE Cars ADD PRIMARY KEY (carID);

-- ---------------------------------------------------------------
-- create hierarchies: Make, Year, Country, Body, Weight, ENGINE
-- ---------------------------------------------------------------
CREATE TABLE Make_hierarchy (
	idhierarchy INTEGER  NOT NULL AUTO_INCREMENT,
	parentid INTEGER,
	Level INTEGER,
	Label VARCHAR(200),
	isLeaf bool,
	PRIMARY KEY (idhierarchy)
);
insert into Make_hierarchy (parentid,Level,Label,isLeaf)
select 0,1, C_Make as Label,true as isLeaf 
from originaltb group by C_Make order by C_Make;

-- ---------------------------------------------------------------
CREATE TABLE Year_hierarchy (
	idhierarchy INTEGER  NOT NULL AUTO_INCREMENT,
	parentid INTEGER,
	Level INTEGER,
	Label VARCHAR(200),
	isLeaf bool,
	PRIMARY KEY (idhierarchy)
);
insert into Year_hierarchy (parentid,Level,Label,isLeaf)
select 0,1, C_Year as Label,true as isLeaf 
from originaltb group by C_Year order by C_Year;

-- ---------------------------------------------------------------
CREATE TABLE Country_hierarchy (
	idhierarchy INTEGER  NOT NULL AUTO_INCREMENT,
	parentid INTEGER,
	Level INTEGER,
	Label VARCHAR(200),
	isLeaf bool,
	PRIMARY KEY (idhierarchy)
);
-- level 1 Continent
insert into Country_hierarchy (parentid,Level,Label,isLeaf)
select 0,1, C_Continent as Label,false as isLeaf 
from originaltb group by C_Continent order by C_Continent;
-- level 2 Country
insert into Country_hierarchy (parentid,Level,Label,isLeaf)
select Country_hierarchy.idhierarchy,2, C_Country as Label,true as isLeaf 
from originaltb 
JOIN Country_hierarchy
on Country_hierarchy.label = originaltb.C_Continent
group by C_Country order by C_Country;

-- ---------------------------------------------------------------
CREATE TABLE Body_hierarchy (
	idhierarchy INTEGER  NOT NULL AUTO_INCREMENT,
	parentid INTEGER,
	Level INTEGER,
	Label VARCHAR(200),
	isLeaf bool,
	PRIMARY KEY (idhierarchy)
);
-- level 1 Type
insert into Body_hierarchy (parentid,Level,Label,isLeaf)
select 0,1, C_type as Label,false as isLeaf 
from originaltb group by C_type order by C_type;
-- level 2 doors
insert into Body_hierarchy (parentid,Level,Label,isLeaf)
select idhierarchy,2, Concat(C_doors,' doors') as Label,false as isLeaf 
from originaltb 
JOIN Body_hierarchy
on originaltb.C_type = Body_hierarchy.label
group by C_type, C_doors
order by C_type, C_doors;
-- level 3 seats
insert into Body_hierarchy (parentid,Level,Label,isLeaf)
select distinct tmp1.idhierarchy as parentid, 3 as level, Concat(tmp2.c_seats, ' seats') as label, true as isleaf
from
	(select t1.parentid, Cast(SUBSTRING_INDEX(t1.label,' ',1) as SIGNED) as door, t1.idhierarchy, t2.label as type
	from Body_hierarchy as t1
	join Body_hierarchy as t2
	on t1.parentid = t2.idhierarchy
	where t1.level=2 and t2.level=1) as tmp1
join originaltb as tmp2
on tmp1.type = tmp2.c_type and tmp1.door=tmp2.c_doors
order by tmp1.type,tmp1.door,tmp2.C_seats;

-- ---------------------------------------------------------------
CREATE TABLE Weight_hierarchy (
	idhierarchy INTEGER  NOT NULL AUTO_INCREMENT,
	parentid INTEGER,
	Level INTEGER,
	Label VARCHAR(200),
	isLeaf bool,
	PRIMARY KEY (idhierarchy)
);
insert into Weight_hierarchy (parentid,Level,Label,isLeaf)
select 0,1, CONCAT(C_Weight_kg,' Kg') as Label,true as isLeaf 
from originaltb group by C_Weight_kg order by C_Weight_kg;

-- ---------------------------------------------------------------
CREATE TABLE Engine_hierarchy (
	idhierarchy INTEGER  NOT NULL AUTO_INCREMENT,
	parentid INTEGER,
	Level INTEGER,
	Label VARCHAR(200),
	isLeaf bool,
	PRIMARY KEY (idhierarchy)
);

-- level 1 EngineType
insert into engine_hierarchy (parentid,Level,Label,isLeaf)
select 0,1, C_engineType as Label,false as isLeaf 
from originaltb 
group by C_engineType 
order by C_engineType;

-- level 2 Cylinders
insert into engine_hierarchy (parentid,Level,Label,isLeaf)
select idhierarchy,2, Concat(C_Cylinders,' cylinders') as Label,false as isLeaf 
from originaltb 
JOIN engine_hierarchy
on originaltb.C_engineType = engine_hierarchy.label
group by C_engineType, C_Cylinders
order by C_engineType, C_Cylinders;

-- level 3 power
insert into engine_hierarchy (parentid,Level,Label,isLeaf)
select distinct tmp1.idhierarchy as parentid, 3 as level, Concat(tmp2.C_power_hp, ' HP') as label, true as isleaf
from
	(select t1.parentid, Cast(SUBSTRING_INDEX(t1.label,' ',1) as SIGNED) as cylinders, t1.idhierarchy, t2.label as enginetype
	from engine_hierarchy as t1
	join engine_hierarchy as t2
	on t1.parentid = t2.idhierarchy
	where t1.level=2 and t2.level=1) as tmp1
join originaltb as tmp2
on tmp1.enginetype = tmp2.c_enginetype and tmp1.cylinders=tmp2.c_cylinders
order by tmp1.enginetype,tmp1.cylinders,tmp2.C_power_hp;

-- ---------------------------------------------------------------
-- cars_linking
-- ---------------------------------------------------------------

CREATE TABLE Cars_linking (
	idhierarchy INTEGER NOT NULL AUTO_INCREMENT,
	t_name VARCHAR(45),
	columnName VARCHAR(45),
	PRIMARY KEY (idhierarchy)
);
insert into cars_linking (t_name,columnName) values ('cars_make','makeID');
insert into cars_linking (t_name,columnName) values ('cars_year','yearID');
insert into cars_linking (t_name,columnName) values ('cars_country','countryID');
insert into cars_linking (t_name,columnName) values ('cars_body','bodyID');
insert into cars_linking (t_name,columnName) values ('cars_weight','weightID');
insert into cars_linking (t_name,columnName) values ('cars_engine','engineID');

-- ---------------------------------------------------------------
-- Cars<->hierarchies linking
-- ---------------------------------------------------------------

CREATE TABLE cars_make (
	idcars_make INTEGER  NOT NULL AUTO_INCREMENT,
	carID INTEGER,
	makeID INTEGER,
	weighted_sum DECIMAL(10,2),
	PRIMARY KEY (idcars_make)
);
insert into cars_make (carID, makeID, weighted_sum)
SELECT t1.carid , t2.idhierarchy, 1.00
from cars as t1
join make_hierarchy as t2
on t1.C_make = t2.label;

-- ---------------------------------------------------------------
CREATE TABLE cars_year (
	idcars_year INTEGER  NOT NULL AUTO_INCREMENT,
	carID INTEGER,
	yearID INTEGER,
	weighted_sum DECIMAL(10,2),
	PRIMARY KEY (idcars_year)
);
insert into cars_year (carID, yearID, weighted_sum)
SELECT t1.carid , t2.idhierarchy, 1.00
from cars as t1
join year_hierarchy as t2
on t1.C_year = t2.label;

-- ---------------------------------------------------------------
CREATE TABLE cars_country (
	idcars_country INTEGER  NOT NULL AUTO_INCREMENT,
	carID INTEGER,
	countryID INTEGER,
	weighted_sum DECIMAL(10,2),
	PRIMARY KEY (idcars_country)
);
insert into cars_country (carID, countryID, weighted_sum)
SELECT t1.carid , t2.idhierarchy, 1.00
from originaltb as t1
join country_hierarchy as t2
on t1.C_country = t2.label
where t2.isleaf = true;

-- ---------------------------------------------------------------
CREATE TABLE cars_body (
	idcars_body INTEGER  NOT NULL AUTO_INCREMENT,
	carID INTEGER,
	bodyID INTEGER,
	weighted_sum DECIMAL(10,2),
	PRIMARY KEY (idcars_body)
);
insert into cars_body (carID, bodyID, weighted_sum)
Select a1.carID,a2.seatID,1.00
from originaltb as a1
join
	(select t1.seatID,t1.seat, t1.doorid, t2.label as Door,t3.idhierarchy as typeID, t3.label as Type 
	from
		(SELECT idhierarchy as seatID, parentID as doorID, label as Seat
			FROM cardb.body_hierarchy
			where level = 3) as t1
	join cardb.body_hierarchy as t2
	on t1.doorid = t2.idhierarchy
	join body_hierarchy as t3
	on t2.parentid = t3.idhierarchy) as a2
on 	a1.c_type = a2.type 
	and a1.c_doors = Cast(SUBSTRING_INDEX(a2.door,' ',1) as SIGNED)
	and a1.c_seats = cast(substring_index(a2.seat,' ',1) as SIGNED)
;

-- ---------------------------------------------------------------
CREATE TABLE cars_weight (
	idcars_weight INTEGER  NOT NULL AUTO_INCREMENT,
	carID INTEGER,
	weightID INTEGER,
	weighted_sum DECIMAL(10,2),
	PRIMARY KEY (idcars_weight)
);
insert into cars_weight (carID, weightID, weighted_sum)
SELECT t1.carid , t2.idhierarchy, 1.00
from originaltb as t1
join weight_hierarchy as t2
on t1.C_weight_kg = Cast(SUBSTRING_INDEX(t2.label,' ',1) as SIGNED);

-- ---------------------------------------------------------------
CREATE TABLE cars_engine (
	idcars_engine INTEGER  NOT NULL AUTO_INCREMENT,
	carID INTEGER,
	engineID INTEGER,
	weighted_sum DECIMAL(10,2),
	PRIMARY KEY (idcars_engine)
);
insert into cars_engine (carID, engineID, weighted_sum)
Select a1.carID,a2.powerID,1.00
from originaltb as a1
join
	(select t1.powerID,t1.power, t1.cylinderid, t2.label as cylinder,t3.idhierarchy as typeID, t3.label as Type 
	from
		(SELECT idhierarchy as powerID, parentID as cylinderid, label as power
			FROM cardb.engine_hierarchy
			where level = 3) as t1
	join cardb.engine_hierarchy as t2
	on t1.cylinderid = t2.idhierarchy
	join engine_hierarchy as t3
	on t2.parentid = t3.idhierarchy) as a2
on 	a1.c_Enginetype = a2.type 
	and a1.c_cylinders = Cast(SUBSTRING_INDEX(a2.cylinder,' ',1) as SIGNED)
	and a1.c_power_hp = cast(substring_index(a2.power,' ',1) as SIGNED);