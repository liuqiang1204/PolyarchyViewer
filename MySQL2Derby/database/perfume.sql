create database perfume;
use perfume;

create table OriginalTB(
	perfumeID INTEGER  primary key NOT NULL AUTO_INCREMENT,
	perfumeName varchar(100),
	Company varchar(100),
	Launched varchar(100),
	Gender varchar(200),
	Perfumer varchar(200),
	TopNotes varchar(200),
	MiddleNotes varchar(200),
	BaseNotes varchar(200)
	);
LOAD DATA LOCAL INFILE 'd:\\dbtmp\\perfume\\perfume.csv'
INTO TABLE OriginalTB 
FIELDS TERMINATED BY ',' 
ENCLOSED BY '"'
LINES TERMINATED BY '\n'
(perfumeName,Company,Launched,Gender,Perfumer,TopNotes,MiddleNotes,BaseNotes);

-- ---------------------------------------------------------------
-- create basic tables
-- ---------------------------------------------------------------
CREATE TABLE dbinfo(
	iddbinfo INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,
	dbname varchar(45) default NULL
	);
insert into dbinfo (dbname) Values('Perfumes');

CREATE TABLE atom (
	displayName VARCHAR(45) NOT NULL,
	t_name VARCHAR(45),
	columnName VARCHAR(45),
	PRIMARY KEY (displayName)
);
INSERT INTO atom (displayName, t_name, columnName) VALUES ('Perfumes', 'perfumes_linking', 'perfumeID');

CREATE TABLE hierarchy (
	idhierarchy INTEGER NOT NULL AUTO_INCREMENT,
	t_name VARCHAR(45),
	level INTEGER,
	PRIMARY KEY (idhierarchy)
);
INSERT INTO hierarchy (t_name, level) VALUES ('Company_hierarchy', 1);
INSERT INTO hierarchy (t_name, level) VALUES ('Launched_hierarchy', 1);
INSERT INTO hierarchy (t_name, level) VALUES ('Gender_hierarchy', 1);
INSERT INTO hierarchy (t_name, level) VALUES ('Perfumer_hierarchy', 2);
INSERT INTO hierarchy (t_name, level) VALUES ('TopNotes_hierarchy', 3);
INSERT INTO hierarchy (t_name, level) VALUES ('MiddleNotes_hierarchy', 3);
INSERT INTO hierarchy (t_name, level) VALUES ('BaseNotes_hierarchy', 3);

-- ---------------------------------------------------------------
-- perfumes
-- ---------------------------------------------------------------
Create table perfumes as (SELECT * FROM originaltb);
ALTER TABLE perfumes ADD PRIMARY KEY (perfumeID);

-- ---------------------------------------------------------------
-- create hierarchies: Company,Lanched,Gender,Perfumer,Notes
-- ---------------------------------------------------------------
CREATE TABLE Company_hierarchy (
	idhierarchy INTEGER  NOT NULL AUTO_INCREMENT,
	parentid INTEGER,
	Level INTEGER,
	Label VARCHAR(200),
	isLeaf bool,
	PRIMARY KEY (idhierarchy)
);
insert into Company_hierarchy (parentid,Level,Label,isLeaf)
SELECT 0,1,Company,true FROM perfume.originaltb
group by Company
order by Company;

-- ---------------------------------------------------------------
CREATE TABLE Launched_hierarchy (
	idhierarchy INTEGER  NOT NULL AUTO_INCREMENT,
	parentid INTEGER,
	Level INTEGER,
	Label VARCHAR(200),
	isLeaf bool,
	PRIMARY KEY (idhierarchy)
);
insert into Launched_hierarchy (parentid,Level,Label,isLeaf)
SELECT 0,1,Launched,true FROM perfume.originaltb
group by Launched
order by Launched;

-- ---------------------------------------------------------------
CREATE TABLE Gender_hierarchy (
	idhierarchy INTEGER  NOT NULL AUTO_INCREMENT,
	parentid INTEGER,
	Level INTEGER,
	Label VARCHAR(200),
	isLeaf bool,
	PRIMARY KEY (idhierarchy)
);
insert into Gender_hierarchy (parentid,Level,Label,isLeaf)
SELECT 0,1,Gender,true FROM perfume.originaltb
group by Gender
order by Gender;

-- ---------------------------------------------------------------
CREATE TABLE Perfumer_hierarchy (
	idhierarchy INTEGER  NOT NULL AUTO_INCREMENT,
	parentid INTEGER,
	Level INTEGER,
	Label VARCHAR(200),
	isLeaf bool,
	PRIMARY KEY (idhierarchy)
);
-- insert top level data only. others use java app
insert into Gender_hierarchy (parentid,Level,Label,isLeaf) values (0,1,'A-F',false);
insert into Gender_hierarchy (parentid,Level,Label,isLeaf) values (0,1,'G-L',false);
insert into Gender_hierarchy (parentid,Level,Label,isLeaf) values (0,1,'M-R',false);
insert into Gender_hierarchy (parentid,Level,Label,isLeaf) values (0,1,'S-X',false);
insert into Gender_hierarchy (parentid,Level,Label,isLeaf) values (0,1,'Y-Z',false);

-- ---------------------------------------------------------------
CREATE TABLE TopNotes_hierarchy (
	idhierarchy INTEGER  NOT NULL AUTO_INCREMENT,
	parentid INTEGER,
	Level INTEGER,
	Label VARCHAR(200),
	isLeaf bool,
	PRIMARY KEY (idhierarchy)
);
-- level 1
insert into TopNotes_hierarchy (idhierarchy,parentid,Level,Label,isLeaf) values (1,0,1,'Animalic',false);
insert into TopNotes_hierarchy (idhierarchy,parentid,Level,Label,isLeaf) values (2,0,1,'Floral',false);
insert into TopNotes_hierarchy (idhierarchy,parentid,Level,Label,isLeaf) values (3,0,1,'Food',false);
insert into TopNotes_hierarchy (idhierarchy,parentid,Level,Label,isLeaf) values (4,0,1,'Fruit',false);
insert into TopNotes_hierarchy (idhierarchy,parentid,Level,Label,isLeaf) values (5,0,1,'Leaf',false);
insert into TopNotes_hierarchy (idhierarchy,parentid,Level,Label,isLeaf) values (6,0,1,'Resin',false);
insert into TopNotes_hierarchy (idhierarchy,parentid,Level,Label,isLeaf) values (7,0,1,'Root',false);
insert into TopNotes_hierarchy (idhierarchy,parentid,Level,Label,isLeaf) values (8,0,1,'Spice',false);
insert into TopNotes_hierarchy (idhierarchy,parentid,Level,Label,isLeaf) values (9,0,1,'Synthetic',false);
insert into TopNotes_hierarchy (idhierarchy,parentid,Level,Label,isLeaf) values (10,0,1,'Wood',false);
-- level 2
insert into TopNotes_hierarchy (idhierarchy,parentid,Level,Label,isLeaf) values (11,2,2,'Exotic',false);
insert into TopNotes_hierarchy (idhierarchy,parentid,Level,Label,isLeaf) values (12,2,2,'Lily',false);
insert into TopNotes_hierarchy (idhierarchy,parentid,Level,Label,isLeaf) values (13,2,2,'Rose',false);
insert into TopNotes_hierarchy (idhierarchy,parentid,Level,Label,isLeaf) values (14,4,2,'Berry',false);
insert into TopNotes_hierarchy (idhierarchy,parentid,Level,Label,isLeaf) values (15,4,2,'Citrus',false);
insert into TopNotes_hierarchy (idhierarchy,parentid,Level,Label,isLeaf) values (16,4,2,'Rosaceae',false);
insert into TopNotes_hierarchy (idhierarchy,parentid,Level,Label,isLeaf) values (17,4,2,'Tropical',false);
insert into TopNotes_hierarchy (idhierarchy,parentid,Level,Label,isLeaf) values (18,5,2,'Conifer',false);
-- leaves
insert into TopNotes_hierarchy (parentid,Level,Label,isLeaf) values
(1,3,'Ambergris',true),
(1,3,'Ambrette',true),
(1,3,'Ambrox',true),
(1,3,'Animalic notes',true),
(1,3,'Asil',true),
(1,3,'Civet',true),
(1,3,'Leather',true),
(1,3,'Musk',true),
(1,3,'White Musk',true),
(11,3,'Cascarilla',true),
(11,3,'Curcuma',true),
(11,3,'Frangipani',true),
(11,3,'Gardenia',true),
(11,3,'Hibiscus',true),
(11,3,'Honeysuckle',true),
(11,3,'Jasmine',true),
(11,3,'Lotus',true),
(11,3,'Magnolia',true),
(11,3,'Night Queen Flower',true),
(11,3,'Orange Blossom',true),
(11,3,'Orchid',true),
(11,3,'Passion flower',true),
(11,3,'Tiare',true),
(11,3,'Tuberose',true),
(11,3,'Water flowers',true),
(11,3,'Water Hyacinth',true),
(11,3,'Water Lily',true),
(11,3,'Ylang-Ylang',true),
(12,3,'Amarillis',true),
(12,3,'Freesia',true),
(12,3,'Hyacinth',true),
(12,3,'Iris',true),
(12,3,'Lily',true),
(12,3,'Muguet',true),
(12,3,'Narcissus',true),
(13,3,'Bulgarian Rose',true),
(13,3,'Damask Rose',true),
(13,3,'Rose',true),
(13,3,'Turkish Rose',true),
(2,3,'Black elder',true),
(2,3,'Boronia',true),
(2,3,'Buddleia',true),
(2,3,'Carnation',true),
(2,3,'Cherry Blossom',true),
(2,3,'Cyclamen',true),
(2,3,'Everlasting flower',true),
(2,3,'Floral Notes',true),
(2,3,'Genista',true),
(2,3,'Geranium',true),
(2,3,'Heliotrope',true),
(2,3,'Lilac',true),
(2,3,'Mimosa',true),
(2,3,'Nettle Flower',true),
(2,3,'Peony',true),
(2,3,'Silk Tree Flower',true),
(2,3,'Sweet Pea',true),
(2,3,'Tagete',true),
(2,3,'Violet',true),
(3,3,'Beeswax',true),
(3,3,'Caviar',true),
(3,3,'Cocoa',true),
(3,3,'Frozen Cocktail Accord',true),
(3,3,'Gingerbread',true),
(3,3,'Honey',true),
(3,3,'Rosewater',true),
(3,3,'Rum',true),
(3,3,'Tonka Bean',true),
(3,3,'Vanilla',true),
(14,3,'Berries',true),
(14,3,'Black cherry',true),
(14,3,'Blackberry',true),
(14,3,'Blackcurrant',true),
(14,3,'Blueberry',true),
(14,3,'Cassis',true),
(14,3,'Cherry',true),
(14,3,'Cranberry',true),
(14,3,'Raspberry',true),
(14,3,'Redcurrant',true),
(15,3,'Bergamot',true),
(15,3,'Neroli',true),
(15,3,'Citrus',true),
(15,3,'Clementine',true),
(15,3,'Grapefruit',true),
(15,3,'Hesperidic Notes',true),
(15,3,'Kumquat',true),
(15,3,'Lemon',true),
(15,3,'Lime',true),
(15,3,'Mandarin',true),
(15,3,'Orange',true),
(15,3,'Tangerine',true),
(16,3,'Almond',true),
(16,3,'Apple',true),
(16,3,'Apricot',true),
(16,3,'Bitter Almond',true),
(16,3,'Chestnut',true),
(16,3,'Hazelnut',true),
(16,3,'Peach',true),
(16,3,'Pear',true),
(16,3,'Plum',true),
(16,3,'Quince',true),
(17,3,'Banana',true),
(17,3,'Cactus Flesh',true),
(17,3,'Coconut',true),
(17,3,'Fig',true),
(17,3,'Green Banana',true),
(17,3,'Green Fig',true),
(17,3,'Green Mango',true),
(17,3,'Lush fruits',true),
(17,3,'Lychee',true),
(17,3,'Mango',true),
(17,3,'Melon',true),
(17,3,'Papaya',true),
(17,3,'Passion fruit',true),
(17,3,'Pineapple',true),
(17,3,'Pomegranate',true),
(17,3,'Water fruits',true),
(4,3,'Fruit Notes',true),
(18,3,'Camphor',true),
(18,3,'Cedar',true),
(18,3,'Cypress',true),
(18,3,'Fir',true),
(18,3,'Hinoki',true),
(18,3,'Juniper',true),
(18,3,'Pine',true),
(5,3,'Angelica',true),
(5,3,'Artemisia',true),
(5,3,'Bamboo',true),
(5,3,'Basil',true),
(5,3,'Birch leaf',true),
(5,3,'Black tea',true),
(5,3,'Blackcurrant Leaf',true),
(5,3,'Calamus',true),
(5,3,'Chinese Cypriol',true),
(5,3,'Clary Sage',true),
(5,3,'Clover Leaf',true),
(5,3,'Coumarin',true),
(5,3,'Davana',true),
(5,3,'Gorse',true),
(5,3,'Green Notes',true),
(5,3,'Green Tea',true),
(5,3,'Ivy leaves',true),
(5,3,'Laurel',true),
(5,3,'Marshmallow',true),
(5,3,'Mate',true),
(5,3,'Mint',true),
(5,3,'Moss',true),
(5,3,'Nasturcia',true),
(5,3,'Oakmoss',true),
(5,3,'Patchouli',true),
(5,3,'Peppermint',true),
(5,3,'Petitgrain',true),
(5,3,'Rhubarb',true),
(5,3,'Rosemary',true),
(5,3,'Sage',true),
(5,3,'Shiso',true),
(5,3,'Smoked black tea',true),
(5,3,'Spearmint',true),
(5,3,'Tarragon',true),
(5,3,'Tea',true),
(5,3,'Thyme',true),
(5,3,'Tobacco',true),
(5,3,'Verbena',true),
(5,3,'Vetiver',true),
(5,3,'Violet Leaf',true),
(5,3,'Wild Reed',true),
(6,3,'Amber',true),
(6,3,'Balsam',true),
(6,3,'Benzoin',true),
(6,3,'Elemi',true),
(6,3,'Frankincense',true),
(6,3,'Galbanum',true),
(6,3,'Incense',true),
(6,3,'Labdanum',true),
(6,3,'Mastic',true),
(6,3,'Myrrh',true),
(6,3,'Resin',true),
(7,3,'Gentian',true),
(7,3,'Licorice',true),
(7,3,'Mandrake root',true),
(7,3,'Orris',true),
(7,3,'Undergrowth note',true),
(8,3,'Anise',true),
(8,3,'Black Pepper',true),
(8,3,'Capiscum',true),
(8,3,'Caraway',true),
(8,3,'Cardamom',true),
(8,3,'Cinnamon',true),
(8,3,'Clove',true),
(8,3,'Coriander',true),
(8,3,'Cumin',true),
(8,3,'Exotic Spices',true),
(8,3,'Ginger',true),
(8,3,'Mace',true),
(8,3,'Nutmeg',true),
(8,3,'Pepper',true),
(8,3,'Pimento',true),
(8,3,'Pink Peppercorns',true),
(8,3,'Red Pepper',true),
(8,3,'Saffron',true),
(8,3,'Spices',true),
(8,3,'Szechuan Pepper',true),
(9,3,'Aldehyde',true),
(9,3,'Anethol',true),
(9,3,'Aquatic Note',true),
(9,3,'Cashmeran',true),
(9,3,'Flint accord',true),
(9,3,'Fresh Notes',true),
(9,3,'Hedione',true),
(9,3,'Mineral Notes',true),
(9,3,'Rhodinol',true),
(9,3,'Salicylate',true),
(9,3,'Sea spray accord',true),
(9,3,'Sweet Notes',true),
(9,3,'Vinyl accord',true),
(10,3,'Agarwood',true),
(10,3,'Amirys wood',true),
(10,3,'Basalm',true),
(10,3,'Blonde Woods',true),
(10,3,'Driftwood',true),
(10,3,'Ebony',true),
(10,3,'Eucalyptus',true),
(10,3,'Gaiac Wood',true),
(10,3,'Hawthorn',true),
(10,3,'Jacaranda Wood',true),
(10,3,'Magnetic Wood',true),
(10,3,'Massoia bark',true),
(10,3,'Oak',true),
(10,3,'Oud',true),
(10,3,'Pear tree wood',true),
(10,3,'Rosewood',true),
(10,3,'Sandalwood',true),
(10,3,'Sap notes',true),
(10,3,'Silver Birch',true),
(10,3,'Sycamore',true),
(10,3,'Teak',true),
(10,3,'Wood',true);


-- ---------------------------------------------------------------
CREATE TABLE MiddleNotes_hierarchy (
	idhierarchy INTEGER  NOT NULL AUTO_INCREMENT,
	parentid INTEGER,
	Level INTEGER,
	Label VARCHAR(200),
	isLeaf bool,
	PRIMARY KEY (idhierarchy)
);
insert into MiddleNotes_hierarchy (idhierarchy,parentid,Level,Label,isLeaf)
select idhierarchy,parentid,Level,Label,isLeaf from TopNotes_hierarchy;
-- ---------------------------------------------------------------
CREATE TABLE BaseNotes_hierarchy (
	idhierarchy INTEGER  NOT NULL AUTO_INCREMENT,
	parentid INTEGER,
	Level INTEGER,
	Label VARCHAR(200),
	isLeaf bool,
	PRIMARY KEY (idhierarchy)
);
insert into BaseNotes_hierarchy (idhierarchy,parentid,Level,Label,isLeaf)
select idhierarchy,parentid,Level,Label,isLeaf from TopNotes_hierarchy;

-- ---------------------------------------------------------------
-- perfumes_linking
-- ---------------------------------------------------------------
CREATE TABLE perfumes_linking (
	idhierarchy INTEGER NOT NULL AUTO_INCREMENT,
	t_name VARCHAR(45),
	columnName VARCHAR(45),
	PRIMARY KEY (idhierarchy)
);
insert into perfumes_linking (t_name,columnName) values ('perfumes_Company','CompanyID');
insert into perfumes_linking (t_name,columnName) values ('perfumes_Launched','LaunchedID');
insert into perfumes_linking (t_name,columnName) values ('perfumes_Gender','GenderID');
insert into perfumes_linking (t_name,columnName) values ('perfumes_Perfumer','PerfumerID');
insert into perfumes_linking (t_name,columnName) values ('perfumes_TopNotes','TopNotesID');
insert into perfumes_linking (t_name,columnName) values ('perfumes_MiddleNotes','MiddleNotesID');
insert into perfumes_linking (t_name,columnName) values ('perfumes_BaseNotes','BaseNotesID');

-- ---------------------------------------------------------------
-- perfumes<->hierarchies linking
-- ---------------------------------------------------------------
CREATE TABLE perfumes_Company (
	idperfumes_Company INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,
	perfumeID INTEGER,
	CompanyID INTEGER,
	weighted_sum DECIMAL(10,2)
);
insert into perfumes_Company (perfumeID, CompanyID, weighted_sum)
select ta.perfumeID, tb.idhierarchy, 1.00
from perfumes as ta
join Company_Hierarchy as tb
where ta.company = tb.Label;

CREATE TABLE perfumes_Launched (
	idperfumes_Launched INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,
	perfumeID INTEGER,
	LaunchedID INTEGER,
	weighted_sum DECIMAL(10,2)
);
insert into perfumes_Launched (perfumeID, LaunchedID, weighted_sum)
select ta.perfumeID, tb.idhierarchy, 1.00
from perfumes as ta
join Launched_Hierarchy as tb
where ta.Launched = tb.Label;

CREATE TABLE perfumes_Gender (
	idperfumes_Gender INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,
	perfumeID INTEGER,
	GenderID INTEGER,
	weighted_sum DECIMAL(10,2)
);
insert into perfumes_Gender (perfumeID, GenderID, weighted_sum)
select ta.perfumeID, tb.idhierarchy, 1.00
from perfumes as ta
join Gender_Hierarchy as tb
where ta.Gender = tb.Label;

CREATE TABLE perfumes_Perfumer (
	idperfumes_Perfumer INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,
	perfumeID INTEGER,
	PerfumerID INTEGER,
	weighted_sum DECIMAL(10,2)
);

CREATE TABLE perfumes_TopNotes (
	idperfumes_TopNotes INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,
	perfumeID INTEGER,
	TopNotesID INTEGER,
	weighted_sum DECIMAL(10,2)
);

CREATE TABLE perfumes_MiddleNotes (
	idperfumes_MiddleNotes INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,
	perfumeID INTEGER,
	MiddleNotesID INTEGER,
	weighted_sum DECIMAL(10,2)
);

CREATE TABLE perfumes_BaseNotes (
	idperfumes_BaseNotes INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,
	perfumeID INTEGER,
	BaseNotesID INTEGER,
	weighted_sum DECIMAL(10,2)
);

-- Warn: some data need to be insert by Java APP. 1)Perfumer_hierarchy 2)perfumes_perfumer/TopNotes/MiddleNotes/BaseNotes