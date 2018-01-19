/*
 * Authors: N. van Urk, E. Kalksma
 * Date: 20-1-2018
 */

/* Create our databases */
DROP SCHEMA IF EXISTS staging;
DROP SCHEMA IF EXISTS bigmovie;
CREATE SCHEMA staging DEFAULT CHARACTER SET utf8;
CREATE SCHEMA bigmovie DEFAULT CHARACTER SET utf8;

/* Create the staging database structure */
DROP TABLE IF EXISTS staging.movies;
CREATE TABLE staging.movies (
  title VARCHAR(500) NOT NULL,
  release_year YEAR(4),
  occurance VARCHAR(10) NULL)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

DROP TABLE IF EXISTS staging.locations;
CREATE TABLE staging.locations (
  title VARCHAR(500) NOT NULL,
  release_year YEAR(4),
  occurance VARCHAR(10) NULL,
  location VARCHAR(200) NULL)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

DROP TABLE IF EXISTS staging.genres;
CREATE TABLE staging.genres (
  title VARCHAR(500) NOT NULL,
  release_year YEAR(4),
  occurance VARCHAR(10) NULL,
  genre VARCHAR(100) NULL)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

DROP TABLE IF EXISTS staging.countries;
CREATE TABLE staging.countries (
  title VARCHAR(500) NOT NULL,
  release_year YEAR(4),
  occurance VARCHAR(10) NULL,
  country VARCHAR(100) NULL)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

DROP TABLE IF EXISTS staging.ratings;
CREATE TABLE staging.ratings (
  title VARCHAR(500) NOT NULL,
  release_year YEAR(4),
  occurance VARCHAR(10) NULL,
  votes INT NULL,
  rating FLOAT NULL)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

DROP TABLE IF EXISTS staging.runningtimes;
CREATE TABLE staging.runningtimes (
  title VARCHAR(500) NOT NULL,
  release_year YEAR(4),
  occurance VARCHAR(10) NULL,
  country VARCHAR(100) NULL,
  running_time INT NULL)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

/* Load staging data */
TRUNCATE staging.movies;
LOAD DATA
LOCAL INFILE '~/Documents/nhl/big-movie/Parser/data/dest/movies.csv'
INTO TABLE staging.movies
FIELDS
TERMINATED BY '|'
LINES
TERMINATED BY '\n';

TRUNCATE staging.locations;
LOAD DATA
LOCAL INFILE '~/Documents/nhl/big-movie/Parser/data/dest/locations.csv'
INTO TABLE staging.locations
FIELDS
TERMINATED BY '|'
LINES
TERMINATED BY '\n';

TRUNCATE staging.genres;
LOAD DATA
LOCAL INFILE '~/Documents/nhl/big-movie/Parser/data/dest/genres.csv'
INTO TABLE staging.genres
FIELDS
TERMINATED BY '|'
LINES
TERMINATED BY '\n';

TRUNCATE staging.countries;
LOAD DATA
LOCAL INFILE '~/Documents/nhl/big-movie/Parser/data/dest/countries.csv'
INTO TABLE staging.countries
FIELDS
TERMINATED BY '|'
LINES
TERMINATED BY '\n';

TRUNCATE staging.ratings;
LOAD DATA
LOCAL INFILE '~/Documents/nhl/big-movie/Parser/data/dest/ratings.csv'
INTO TABLE staging.ratings
FIELDS
TERMINATED BY '|'
LINES
TERMINATED BY '\n';

TRUNCATE staging.runningtimes;
LOAD DATA
LOCAL INFILE '~/Documents/nhl/big-movie/Parser/data/dest/running-times.csv'
INTO TABLE staging.runningtimes
FIELDS
TERMINATED BY '|'
LINES
TERMINATED BY '\n';

/* Create the final (bigmovie) database structure */
/* ID's to be added later to boost performance */
DROP TABLE IF EXISTS bigmovie.movies;
CREATE TABLE bigmovie.movies (
  title VARCHAR(500) NOT NULL,
  release_year YEAR(4),
  occurance VARCHAR(10) NULL,
  votes INT NULL,
  rating FLOAT NULL)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

DROP TABLE IF EXISTS bigmovie.locations;
CREATE TABLE bigmovie.locations (
  location VARCHAR(200) NULL)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

DROP TABLE IF EXISTS bigmovie.genres;
CREATE TABLE bigmovie.genres (
  genre VARCHAR(100) NULL)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

DROP TABLE IF EXISTS bigmovie.countries;
CREATE TABLE bigmovie.countries (
  country VARCHAR(100) NULL)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

DROP TABLE IF EXISTS bigmovie.runningtimes;
CREATE TABLE bigmovie.runningtimes (
  country VARCHAR(100) NULL,
  running_time INT NULL)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

/* Import all the staging data into the final (bigmovie) database */
ALTER TABLE staging.movies
ADD INDEX (title, release_year, occurance);

ALTER TABLE staging.ratings
ADD INDEX (title, release_year, occurance);

INSERT INTO bigmovie.movies (
  title,
  release_year,
  occurance,
  votes,
  rating
)
SELECT DISTINCT
  m.title,
  m.release_year,
  m.occurance,
  votes,
  rating
FROM staging.movies AS m
LEFT JOIN staging.ratings AS r
	ON m.title = r.title
	AND m.release_year = r.release_year
	AND m.occurance = r.occurance;

ALTER TABLE bigmovie.movies
ADD INDEX (title, release_year, occurance);

ALTER TABLE bigmovie.movies
ADD id INT NOT NULL AUTO_INCREMENT PRIMARY KEY FIRST;

/*---------------------------------------------------*/

INSERT INTO bigmovie.genres (genre)
SELECT DISTINCT genre FROM staging.genres;

ALTER TABLE bigmovie.genres
ADD id INT NOT NULL AUTO_INCREMENT PRIMARY KEY FIRST;

DROP TABLE IF EXISTS bigmovie.movie_genre;
CREATE TABLE bigmovie.movie_genre (
  movie_id INT NULL,
  genre_id INT NULL)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

ALTER TABLE staging.genres
ADD INDEX (title, release_year, occurance, genre);

ALTER TABLE bigmovie.genres
ADD INDEX (genre);

INSERT INTO bigmovie.movie_genre (
  movie_id,
  genre_id
)
SELECT DISTINCT movie_id, genre_id
FROM (
	SELECT  m.id AS movie_id, sg.title, sg.release_year, sg.occurance
	FROM bigmovie.movies AS m
	INNER JOIN staging.genres AS sg
		ON m.title = sg.title
		AND m.release_year = sg.release_year
		AND m.occurance = sg.occurance
)
AS A
INNER JOIN (
	SELECT g.id AS genre_id, sg.title, sg.release_year, sg.occurance
	FROM bigmovie.genres AS g
	INNER JOIN staging.genres AS sg
		ON g.genre = sg.genre
) 
AS B
ON A.release_year = B.release_year
AND A.occurance = B.occurance
AND A.title = B.title;

ALTER TABLE bigmovie.movie_genre
ADD CONSTRAINT FK_Movie FOREIGN KEY (movie_id) REFERENCES bigmovie.movies(id),
ADD CONSTRAINT FK_Genre FOREIGN KEY (genre_id) REFERENCES bigmovie.genres(id);

/*---------------------------------------------------*/

INSERT INTO bigmovie.countries (country)
SELECT DISTINCT country FROM staging.countries;

ALTER TABLE bigmovie.countries
ADD id INT NOT NULL AUTO_INCREMENT PRIMARY KEY FIRST;

DROP TABLE IF EXISTS bigmovie.movie_country;
CREATE TABLE bigmovie.movie_country (
  movie_id INT NULL,
  country_id INT NULL)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

ALTER TABLE staging.countries
ADD INDEX (title, release_year, occurance, country);

ALTER TABLE bigmovie.countries
ADD INDEX (country);

INSERT INTO bigmovie.movie_country (
  movie_id,
  country_id
)
SELECT DISTINCT movie_id, country_id
FROM (
	SELECT m.id as movie_id, sc.title, sc.release_year, sc.occurance
	FROM bigmovie.movies AS m
	INNER JOIN staging.countries AS sc
		ON m.title = sc.title
		AND m.release_year = sc.release_year
		AND m.occurance = sc.occurance
)
AS A
INNER JOIN (
	SELECT g.id AS country_id, sc.title, sc.release_year, sc.occurance
	FROM bigmovie.countries AS g
	INNER JOIN staging.countries AS sc
		ON g.country = sc.country
) 
AS B
ON A.release_year = B.release_year
AND A.occurance = B.occurance
AND A.title = B.title;

ALTER TABLE bigmovie.movie_country
ADD CONSTRAINT FK_Movie2 FOREIGN KEY (movie_id) REFERENCES bigmovie.movies(id),
ADD CONSTRAINT FK_Country FOREIGN KEY (country_id) REFERENCES bigmovie.countries(id);

/*---------------------------------------------------*/

INSERT INTO bigmovie.locations (location)
SELECT DISTINCT location FROM staging.locations;

ALTER TABLE bigmovie.locations
ADD id INT NOT NULL AUTO_INCREMENT PRIMARY KEY FIRST;

DROP TABLE IF EXISTS bigmovie.movie_location;
CREATE TABLE bigmovie.movie_location (
  movie_id INT NULL,
  location_id INT NULL)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

ALTER TABLE staging.locations
ADD INDEX (title, release_year, occurance, location);

ALTER TABLE bigmovie.locations
ADD INDEX (location);

INSERT INTO bigmovie.movie_location (
  movie_id,
  location_id
)
SELECT DISTINCT movie_id, location_id
FROM (
	SELECT m.id as movie_id, sl.title, sl.release_year, sl.occurance
	FROM bigmovie.movies AS m
	INNER JOIN staging.locations AS sl
		ON m.title = sl.title
		AND m.release_year = sl.release_year
		AND m.occurance = sl.occurance
)
AS A
INNER JOIN (
	SELECT g.id AS location_id, sl.title, sl.release_year, sl.occurance
	FROM bigmovie.locations AS g
	INNER JOIN staging.locations AS sl
		ON g.location = sl.location
) 
AS B
ON A.release_year = B.release_year
AND A.occurance = B.occurance
AND A.title = B.title;

ALTER TABLE bigmovie.movie_location
ADD CONSTRAINT FK_Movie3 FOREIGN KEY (movie_id) REFERENCES bigmovie.movies(id),
ADD CONSTRAINT FK_Location FOREIGN KEY (location_id) REFERENCES bigmovie.locations(id);

/*---------------------------------------------------*/

INSERT INTO bigmovie.runningtimes (
  country,
  running_time
)
SELECT DISTINCT country, running_time
FROM staging.runningtimes;

ALTER TABLE bigmovie.runningtimes
ADD id INT NOT NULL AUTO_INCREMENT PRIMARY KEY FIRST;

DROP TABLE IF EXISTS bigmovie.movie_runningtime;
CREATE TABLE bigmovie.movie_runningtime (
  movie_id INT NULL,
  runningtime_id INT NULL)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

ALTER TABLE staging.runningtimes
ADD INDEX (title, release_year, occurance, country, running_time);

ALTER TABLE bigmovie.runningtimes
ADD INDEX (country, running_time);

INSERT INTO bigmovie.movie_runningtime (
  movie_id,
  runningtime_id
)
SELECT DISTINCT movie_id, runningtime_id
FROM (
	SELECT m.id as movie_id, sr.title, sr.release_year, sr.occurance
	FROM bigmovie.movies AS m
	INNER JOIN staging.runningtimes AS sr
		ON m.title = sr.title
		AND m.release_year = sr.release_year
		AND m.occurance = sr.occurance
)
as A
INNER JOIN (
	SELECT g.id as runningtime_id, sr.title, sr.release_year, sr.occurance
	FROM bigmovie.runningtimes AS g
	INNER JOIN staging.runningtimes AS sr
		ON g.country = sr.country
		AND g.running_time = sr.running_time
) 
as B
ON A.release_year = B.release_year
AND A.occurance = B.occurance
AND A.title = B.title;

ALTER TABLE bigmovie.movie_runningtime
ADD CONSTRAINT FK_Movie4 FOREIGN KEY (movie_id) REFERENCES bigmovie.movies(id),
ADD CONSTRAINT FK_Runningtime FOREIGN KEY (runningtime_id) REFERENCES bigmovie.runningtimes(id);

/*---------------------------------------------------*/

/* Delete staging database */
DROP SCHEMA staging;