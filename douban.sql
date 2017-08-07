DROP DATABASE DouBanMovieInfo;
CREATE DATABASE DouBanMovieInfo;
USE DouBanMovieInfo;
CREATE TABLE movie(
	id BIGINT PRIMARY KEY AUTO_INCREMENT,
	NAME VARCHAR(50),
	TYPES VARCHAR(50),
	release_date VARCHAR(20),
	score DECIMAL(10,1), -- 10：整数位数  1：小数位数
	movieUrl VARCHAR(200),
	is_playable VARCHAR(20)
);
-- INSERT INTO movie(NAME,TYPES,release_date,score,movieUrl,is_playable) VALUE('abc','剧情','2017-7-2',9.6,'http://','true');
-- select count(1) from movie;
-- select * from movie;

-- select * from movie where movieUrl like '%25884801%'

#SELECT COUNT(1) FROM movie WHERE score<7.5;
#SELECT COUNT(1) FROM movie WHERE score>=7.5 && score<8;
#SELECT COUNT(1) FROM movie WHERE score>=8 && score<8.5;
#SELECT COUNT(1) FROM movie WHERE score>=8.5 && score<9;
#SELECT COUNT(1) FROM movie WHERE score>=9;

#SELECT COUNT(*) FROM movie WHERE score = 7.5
#SELECT COUNT(*) FROM movie WHERE score = '9.7'
