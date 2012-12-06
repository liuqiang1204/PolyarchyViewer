Drop database IMDB;
Create database IMDB;
use imdb;

CREATE TABLE films(
	filmID INTEGER NOT NULL AUTO_INCREMENT,
	filmName Varchar(3000),
	language Varchar(200),
	plot Varchar(8000),
	PRIMARY KEY (filmID)
);

CREATE TABLE atom (
	displayName VARCHAR(45) NOT NULL,
	t_name VARCHAR(45),
	columnName VARCHAR(45),
	PRIMARY KEY (displayName)
);

CREATE TABLE hierarchy (
	idhierarchy INTEGER NOT NULL AUTO_INCREMENT,
	t_name VARCHAR(45),
	level INTEGER,
	PRIMARY KEY (idhierarchy)
);

CREATE TABLE actors_hierarchy (
	idhierarchy INTEGER  NOT NULL AUTO_INCREMENT,
	parentid INTEGER,
	Level INTEGER,
	Label VARCHAR(200),
	PRIMARY KEY (idhierarchy)
);

CREATE TABLE directors_hierarchy (
	idhierarchy INTEGER  NOT NULL AUTO_INCREMENT,
	parentid INTEGER,
	Level INTEGER,
	Label VARCHAR(200),
	PRIMARY KEY (idhierarchy)
);

CREATE TABLE genres_hierarchy (
	idhierarchy INTEGER  NOT NULL AUTO_INCREMENT,
	parentid INTEGER,
	Level INTEGER,
	Label VARCHAR(200),
	PRIMARY KEY (idhierarchy)
);

CREATE TABLE countries_hierarchy (
	idhierarchy INTEGER  NOT NULL AUTO_INCREMENT,
	parentid INTEGER,
	Level INTEGER,
	Label VARCHAR(200),
	PRIMARY KEY (idhierarchy)
);

CREATE TABLE awards_hierarchy (
	idhierarchy INTEGER  NOT NULL AUTO_INCREMENT,
	parentid INTEGER,
	Level INTEGER,
	Label VARCHAR(200),
	PRIMARY KEY (idhierarchy)
);

CREATE TABLE years_hierarchy (
	idhierarchy INTEGER  NOT NULL AUTO_INCREMENT,
	parentid INTEGER,
	Level INTEGER,
	Label VARCHAR(200),
	PRIMARY KEY (idhierarchy)
);

CREATE TABLE ranks_hierarchy (
	idhierarchy INTEGER  NOT NULL AUTO_INCREMENT,
	parentid INTEGER,
	Level INTEGER,
	Label VARCHAR(200),
	PRIMARY KEY (idhierarchy)
);

CREATE TABLE films_linking (
	idhierarchy INTEGER NOT NULL AUTO_INCREMENT,
	t_name VARCHAR(45),
	columnName VARCHAR(45),
	PRIMARY KEY (idhierarchy)
);


CREATE TABLE films_actors (
	idfilms_actors INTEGER  NOT NULL AUTO_INCREMENT,
	filmID INTEGER,
	actorID INTEGER,
	weighted_sum DECIMAL(10,2),
	PRIMARY KEY (idfilms_actors)
);

CREATE TABLE films_directors (
	idfilms_directors INTEGER  NOT NULL AUTO_INCREMENT,
	filmID INTEGER,
	directorID INTEGER,
	weighted_sum DECIMAL(10,2),
	PRIMARY KEY (idfilms_directors)
);

CREATE TABLE films_genres (
	idfilms_genres INTEGER  NOT NULL AUTO_INCREMENT,
	filmID INTEGER,
	genresID INTEGER,
	weighted_sum DECIMAL(10,2),
	PRIMARY KEY (idfilms_genres)
);

CREATE TABLE films_countries (
	idfilms_countries INTEGER  NOT NULL AUTO_INCREMENT,
	filmID INTEGER,
	countryID INTEGER,
	weighted_sum DECIMAL(10,2),
	PRIMARY KEY (idfilms_countries)
);

CREATE TABLE films_awards (
	idfilms_awards INTEGER  NOT NULL AUTO_INCREMENT,
	filmID INTEGER,
	awardID INTEGER,
	weighted_sum DECIMAL(10,2),
	PRIMARY KEY (idfilms_awards)
);

CREATE TABLE films_years (
	idfilms_years INTEGER  NOT NULL AUTO_INCREMENT,
	filmID INTEGER,
	yearID INTEGER,
	weighted_sum DECIMAL(10,2),
	PRIMARY KEY (idfilms_years)
);

CREATE TABLE films_ranks (
	idfilms_ranks INTEGER  NOT NULL AUTO_INCREMENT,
	filmID INTEGER,
	rankID INTEGER,
	weighted_sum DECIMAL(10,2),
	PRIMARY KEY (idfilms_ranks)
);


INSERT INTO atom(displayName, t_name, columnName) VALUES ('Films', 'films_linking', 'filmid');

INSERT INTO hierarchy(idhierarchy, t_name, level) VALUES (1, 'actors_hierarchy', 2);
INSERT INTO hierarchy(idhierarchy, t_name, level) VALUES (2, 'directors_hierarchy', 2);
INSERT INTO hierarchy(idhierarchy, t_name, level) VALUES (3, 'genres_hierarchy', 1);
INSERT INTO hierarchy(idhierarchy, t_name, level) VALUES (4, 'countries_hierarchy', 2);
INSERT INTO hierarchy(idhierarchy, t_name, level) VALUES (5, 'awards_hierarchy', 3);
INSERT INTO hierarchy(idhierarchy, t_name, level) VALUES (6, 'years_hierarchy', 2);
INSERT INTO hierarchy(idhierarchy, t_name, level) VALUES (7, 'ranks_hierarchy', 1);

INSERT INTO films_linking(idhierarchy, t_name, columnName) VALUES (1, 'films_actors', 'actorID');
INSERT INTO films_linking(idhierarchy, t_name, columnName) VALUES (2, 'films_directors', 'directorID');
INSERT INTO films_linking(idhierarchy, t_name, columnName) VALUES (3, 'films_genres', 'genresID');
INSERT INTO films_linking(idhierarchy, t_name, columnName) VALUES (4, 'films_countries', 'countryID');
INSERT INTO films_linking(idhierarchy, t_name, columnName) VALUES (5, 'films_awards', 'awardID');
INSERT INTO films_linking(idhierarchy, t_name, columnName) VALUES (6, 'films_years', 'yearID');
INSERT INTO films_linking(idhierarchy, t_name, columnName) VALUES (7, 'films_ranks', 'rankID');

INSERT INTO actors_hierarchy(parentid, Level, Label) VALUES (0, 1, 'Male');
INSERT INTO actors_hierarchy(parentid, Level, Label) VALUES (0, 1, 'Female');

INSERT INTO directors_hierarchy(parentid, Level, Label) VALUES (0, 1, 'Male');
INSERT INTO directors_hierarchy(parentid, Level, Label) VALUES (0, 1, 'Female');

INSERT INTO countries_hierarchy (parentid, Level, Label) VALUES (0, 1, 'North America');
INSERT INTO countries_hierarchy (parentid, Level, Label) VALUES (0, 1, 'South America');
INSERT INTO countries_hierarchy (parentid, Level, Label) VALUES (0, 1, 'Asia');
INSERT INTO countries_hierarchy (parentid, Level, Label) VALUES (0, 1, 'Europe');
INSERT INTO countries_hierarchy (parentid, Level, Label) VALUES (0, 1, 'Africa');
INSERT INTO countries_hierarchy (parentid, Level, Label) VALUES (0, 1, 'Australia');
INSERT INTO countries_hierarchy (parentid, Level, Label) VALUES (0, 1, 'Antartica');

INSERT INTO ranks_hierarchy (parentid, Level, Label) VALUES (0, 1, '1-20');
INSERT INTO ranks_hierarchy (parentid, Level, Label) VALUES (0, 1, '21-50');
INSERT INTO ranks_hierarchy (parentid, Level, Label) VALUES (0, 1, '51-100');
INSERT INTO ranks_hierarchy (parentid, Level, Label) VALUES (0, 1, '100-150');
INSERT INTO ranks_hierarchy (parentid, Level, Label) VALUES (0, 1, '150-200');
INSERT INTO ranks_hierarchy (parentid, Level, Label) VALUES (0, 1, 'others');

INSERT INTO years_hierarchy (parentid, Level, Label) VALUES (0, 1, '1920s');
INSERT INTO years_hierarchy (parentid, Level, Label) VALUES (0, 1, '1930s');
INSERT INTO years_hierarchy (parentid, Level, Label) VALUES (0, 1, '1940s');
INSERT INTO years_hierarchy (parentid, Level, Label) VALUES (0, 1, '1950s');
INSERT INTO years_hierarchy (parentid, Level, Label) VALUES (0, 1, '1960s');
INSERT INTO years_hierarchy (parentid, Level, Label) VALUES (0, 1, '1970s');
INSERT INTO years_hierarchy (parentid, Level, Label) VALUES (0, 1, '1980s');
INSERT INTO years_hierarchy (parentid, Level, Label) VALUES (0, 1, '1990s');
INSERT INTO years_hierarchy (parentid, Level, Label) VALUES (0, 1, '2000s');
INSERT INTO years_hierarchy (parentid, Level, Label) VALUES (0, 1, '2010s');