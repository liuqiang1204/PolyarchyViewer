create database research_data;
use research_data;

Create table OriginalTB(
	recordID INTEGER  primary key NOT NULL AUTO_INCREMENT,
	SimLen varchar(20),
	SRate varchar(20),
	Rave varchar(20),
	Rskew varchar(20),
	RLKurt varchar(20),
	RCV varchar(20),
	Rutil varchar(20),
	DesServ varchar(20),
	RABS1X varchar(20),
	RABS2X varchar(20)
);

LOAD DATA LOCAL INFILE 'd:\\dbtmp\\Trial19CutValues.csv'
INTO TABLE OriginalTB 
FIELDS TERMINATED BY ',' 
ENCLOSED BY '"'
LINES TERMINATED BY '\r\n'
(SimLen,SRate,Rave,Rskew,RLKurt,RCV,Rutil,DesServ,RABS1X,RABS2X);

-- ---------------------------------------------------------------
-- create basic tables
-- ---------------------------------------------------------------
CREATE TABLE dbinfo(
	iddbinfo INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,
	dbname varchar(45) default NULL
	);
insert into dbinfo (dbname) Values('Research Data');

CREATE TABLE atom (
	displayName VARCHAR(45) NOT NULL,
	t_name VARCHAR(45),
	columnName VARCHAR(45),
	PRIMARY KEY (displayName)
);
INSERT INTO atom (displayName, t_name, columnName) VALUES ('Records', 'records_linking', 'recordID');

CREATE TABLE hierarchy (
	idhierarchy INTEGER NOT NULL AUTO_INCREMENT,
	t_name VARCHAR(45),
	level INTEGER,
	PRIMARY KEY (idhierarchy)
);
INSERT INTO hierarchy (t_name, level) VALUES ('SimLenAndRate_hierarchy', 2);
INSERT INTO hierarchy (t_name, level) VALUES ('Rave_hierarchy', 1);
INSERT INTO hierarchy (t_name, level) VALUES ('Rskew_hierarchy', 1);
INSERT INTO hierarchy (t_name, level) VALUES ('RLKurt_hierarchy', 1);
INSERT INTO hierarchy (t_name, level) VALUES ('RCV_hierarchy', 1);
INSERT INTO hierarchy (t_name, level) VALUES ('Rutil_hierarchy', 1);
INSERT INTO hierarchy (t_name, level) VALUES ('DesServ_hierarchy', 1);
INSERT INTO hierarchy (t_name, level) VALUES ('RABS1X_hierarchy', 1);
INSERT INTO hierarchy (t_name, level) VALUES ('RABS2X_hierarchy', 1);

-- ---------------------------------------------------------------
-- records
-- ---------------------------------------------------------------
Create table Records as (SELECT recordID,SimLen,SRate,Rave,Rskew,RLKurt,RCV,Rutil,DesServ,RABS1X,RABS2X FROM originaltb);
ALTER TABLE Records ADD PRIMARY KEY (recordID);

-- ---------------------------------------------------------------
-- create hierarchies: SimLenAndRate, Rave,Rskew,RLKurt,RCV,Rutil,DesServ,RABS1X,RABS2X
-- ---------------------------------------------------------------

CREATE TABLE SimLenAndRate_hierarchy (
	idhierarchy INTEGER  NOT NULL AUTO_INCREMENT,
	parentid INTEGER,
	Level INTEGER,
	Label VARCHAR(200),
	isLeaf bool,
	PRIMARY KEY (idhierarchy)
);
-- level 1
insert into SimLenAndRate_hierarchy (parentid,Level,Label,isLeaf)
select 0,1, SimLen as Label,false as isLeaf 
from originaltb group by SimLen order by SimLen;
-- level 2
insert into SimLenAndRate_hierarchy (parentid,Level,Label,isLeaf)
select idhierarchy,2, SRate as Label,true as isLeaf 
from originaltb 
JOIN SimLenAndRate_hierarchy
on originaltb.SimLen = SimLenAndRate_hierarchy.label
group by SimLen, SRate
order by SimLen, SRate;


CREATE TABLE Rave_hierarchy (
	idhierarchy INTEGER  NOT NULL AUTO_INCREMENT,
	parentid INTEGER,
	Level INTEGER,
	Label VARCHAR(200),
	isLeaf bool,
	PRIMARY KEY (idhierarchy)
);
insert into Rave_hierarchy (parentid,Level,Label,isLeaf)
select 0,1, Rave as Label,true as isLeaf 
from originaltb group by Rave order by cast(Rave as SIGNED);

CREATE TABLE Rskew_hierarchy (
	idhierarchy INTEGER  NOT NULL AUTO_INCREMENT,
	parentid INTEGER,
	Level INTEGER,
	Label VARCHAR(200),
	isLeaf bool,
	PRIMARY KEY (idhierarchy)
);
insert into Rskew_hierarchy (parentid,Level,Label,isLeaf)
select 0,1, Rskew as Label,true as isLeaf 
from originaltb group by Rskew order by cast(Rskew as SIGNED);

CREATE TABLE RLKurt_hierarchy (
	idhierarchy INTEGER  NOT NULL AUTO_INCREMENT,
	parentid INTEGER,
	Level INTEGER,
	Label VARCHAR(200),
	isLeaf bool,
	PRIMARY KEY (idhierarchy)
);
insert into RLKurt_hierarchy (parentid,Level,Label,isLeaf)
select 0,1, RLKurt as Label,true as isLeaf 
from originaltb group by RLKurt order by cast(RLKurt as Decimal(10,6));

CREATE TABLE RCV_hierarchy (
	idhierarchy INTEGER  NOT NULL AUTO_INCREMENT,
	parentid INTEGER,
	Level INTEGER,
	Label VARCHAR(200),
	isLeaf bool,
	PRIMARY KEY (idhierarchy)
);
insert into RCV_hierarchy (parentid,Level,Label,isLeaf)
select 0,1, RCV as Label,true as isLeaf 
from originaltb group by RCV order by cast(RCV as SIGNED);

CREATE TABLE Rutil_hierarchy (
	idhierarchy INTEGER  NOT NULL AUTO_INCREMENT,
	parentid INTEGER,
	Level INTEGER,
	Label VARCHAR(200),
	isLeaf bool,
	PRIMARY KEY (idhierarchy)
);
insert into Rutil_hierarchy (parentid,Level,Label,isLeaf)
select 0,1, Rutil as Label,true as isLeaf 
from originaltb group by Rutil order by cast(Rutil as Decimal(10,6));

CREATE TABLE DesServ_hierarchy (
	idhierarchy INTEGER  NOT NULL AUTO_INCREMENT,
	parentid INTEGER,
	Level INTEGER,
	Label VARCHAR(200),
	isLeaf bool,
	PRIMARY KEY (idhierarchy)
);
insert into DesServ_hierarchy (parentid,Level,Label,isLeaf)
select 0,1, DesServ as Label,true as isLeaf 
from originaltb group by DesServ order by cast(DesServ as Decimal(10,6));

CREATE TABLE RABS1X_hierarchy (
	idhierarchy INTEGER  NOT NULL AUTO_INCREMENT,
	parentid INTEGER,
	Level INTEGER,
	Label VARCHAR(200),
	isLeaf bool,
	PRIMARY KEY (idhierarchy)
);
insert into RABS1X_hierarchy (parentid,Level,Label,isLeaf)
select 0,1, RABS1X as Label,true as isLeaf 
from originaltb group by RABS1X order by cast(RABS1X as Decimal(10,6));

CREATE TABLE RABS2X_hierarchy (
	idhierarchy INTEGER  NOT NULL AUTO_INCREMENT,
	parentid INTEGER,
	Level INTEGER,
	Label VARCHAR(200),
	isLeaf bool,
	PRIMARY KEY (idhierarchy)
);
insert into RABS2X_hierarchy (parentid,Level,Label,isLeaf)
select 0,1, RABS2X as Label,true as isLeaf 
from originaltb group by RABS2X order by cast(RABS2X as Decimal(10,6));

-- ---------------------------------------------------------------
-- records_linking
-- ---------------------------------------------------------------

CREATE TABLE records_linking (
	idhierarchy INTEGER NOT NULL AUTO_INCREMENT,
	t_name VARCHAR(45),
	columnName VARCHAR(45),
	PRIMARY KEY (idhierarchy)
);
insert into records_linking (t_name,columnName) values ('records_SimLenAndRate','SimLenAndRateID');
insert into records_linking (t_name,columnName) values ('records_Rave','RaveID');
insert into records_linking (t_name,columnName) values ('records_Rskew','RskewID');
insert into records_linking (t_name,columnName) values ('records_RLKurt','RLKurtID');
insert into records_linking (t_name,columnName) values ('records_RCV','RCVID');
insert into records_linking (t_name,columnName) values ('records_Rutil','RutilID');
insert into records_linking (t_name,columnName) values ('records_DesServ','DesServID');
insert into records_linking (t_name,columnName) values ('records_RABS1X','RABS1XID');
insert into records_linking (t_name,columnName) values ('records_RABS2X','RABS2XID');

-- ---------------------------------------------------------------
-- records<->hierarchies linking
-- ---------------------------------------------------------------

CREATE TABLE records_SimLenAndRate (
	idrecords_SimLenAndRate INTEGER  NOT NULL AUTO_INCREMENT,
	recordID INTEGER,
	SimLenAndRateID INTEGER,
	weighted_sum DECIMAL(10,2),
	PRIMARY KEY (idrecords_SimLenAndRate)
);
insert into records_SimLenAndRate (recordID, SimLenAndRateID, weighted_sum)
select ta.recordID, tb.cID, 1.00 
from records as ta
join 
	(SELECT t1.idhierarchy as PID,t1.label as SimLen, t2.idhierarchy as cID, t2.Label as SRate
	from simlenandrate_hierarchy as t1
	join simlenandrate_hierarchy as t2
	on t1.idhierarchy = t2.parentID) as tb
on ta.SimLen = tb.SimLen and ta.SRate = tb.SRate;

CREATE TABLE records_Rave (
	idrecords_Rave INTEGER  PRIMARY KEY NOT NULL AUTO_INCREMENT,
	recordID INTEGER,
	RaveID INTEGER,
	weighted_sum DECIMAL(10,2)
);
insert into records_Rave (recordID, RaveID, weighted_sum)
select ta.recordID, tb.idhierarchy, 1.00
from records as ta
join Rave_Hierarchy as tb
where ta.Rave = tb.Label;

CREATE TABLE records_Rskew (
	idrecords_Rskew INTEGER  PRIMARY KEY NOT NULL AUTO_INCREMENT,
	recordID INTEGER,
	RskewID INTEGER,
	weighted_sum DECIMAL(10,2)
);
insert into records_Rskew (recordID, RskewID, weighted_sum)
select ta.recordID, tb.idhierarchy, 1.00
from records as ta
join Rskew_Hierarchy as tb
where ta.Rskew = tb.Label;

CREATE TABLE records_RLKurt (
	idrecords_RLKurt INTEGER  PRIMARY KEY NOT NULL AUTO_INCREMENT,
	recordID INTEGER,
	RLKurtID INTEGER,
	weighted_sum DECIMAL(10,2)
);
insert into records_RLKurt (recordID, RLKurtID, weighted_sum)
select ta.recordID, tb.idhierarchy, 1.00
from records as ta
join RLKurt_Hierarchy as tb
where ta.RLKurt = tb.Label;

CREATE TABLE records_RCV (
	idrecords_RCV INTEGER  PRIMARY KEY NOT NULL AUTO_INCREMENT,
	recordID INTEGER,
	RCVID INTEGER,
	weighted_sum DECIMAL(10,2)
);
insert into records_RCV (recordID, RCVID, weighted_sum)
select ta.recordID, tb.idhierarchy, 1.00
from records as ta
join RCV_Hierarchy as tb
where ta.RCV = tb.Label;

CREATE TABLE records_Rutil (
	idrecords_Rutil INTEGER  PRIMARY KEY NOT NULL AUTO_INCREMENT,
	recordID INTEGER,
	RutilID INTEGER,
	weighted_sum DECIMAL(10,2)
);
insert into records_Rutil (recordID, RutilID, weighted_sum)
select ta.recordID, tb.idhierarchy, 1.00
from records as ta
join Rutil_Hierarchy as tb
where ta.Rutil = tb.Label;

CREATE TABLE records_DesServ (
	idrecords_DesServ INTEGER  PRIMARY KEY NOT NULL AUTO_INCREMENT,
	recordID INTEGER,
	DesServID INTEGER,
	weighted_sum DECIMAL(10,2)
);
insert into records_DesServ (recordID, DesServID, weighted_sum)
select ta.recordID, tb.idhierarchy, 1.00
from records as ta
join DesServ_Hierarchy as tb
where ta.DesServ = tb.Label;

CREATE TABLE records_RABS1X (
	idrecords_RABS1X INTEGER  PRIMARY KEY NOT NULL AUTO_INCREMENT,
	recordID INTEGER,
	RABS1XID INTEGER,
	weighted_sum DECIMAL(10,2)
);
insert into records_RABS1X (recordID, RABS1XID, weighted_sum)
select ta.recordID, tb.idhierarchy, 1.00
from records as ta
join RABS1X_Hierarchy as tb
where ta.RABS1X = tb.Label;

CREATE TABLE records_RABS2X (
	idrecords_RABS2X INTEGER  PRIMARY KEY NOT NULL AUTO_INCREMENT,
	recordID INTEGER,
	RABS2XID INTEGER,
	weighted_sum DECIMAL(10,2)
);
insert into records_RABS2X (recordID, RABS2XID, weighted_sum)
select ta.recordID, tb.idhierarchy, 1.00
from records as ta
join RABS2X_Hierarchy as tb
where ta.RABS2X = tb.Label;