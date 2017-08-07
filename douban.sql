DROP DATABASE DouBanMovieInfo;
CREATE DATABASE DouBanMovieInfo;
USE DouBanMovieInfo;
CREATE TABLE movie(
	id BIGINT PRIMARY KEY AUTO_INCREMENT,
	NAME VARCHAR(50),
	TYPES VARCHAR(50),
	release_date VARCHAR(20),
	score FLOAT,
	movieUrl VARCHAR(200),
	is_playable VARCHAR(20)
);
-- INSERT INTO movie(NAME,TYPES,release_date,score,movieUrl,is_playable) VALUE('abc','剧情','2017-7-2',9.6,'http://','true');
-- select count(1) from movie;
-- select * from movie;

