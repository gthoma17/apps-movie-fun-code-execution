DROP TABLE IF EXISTS movies.album_scheduler_task;

CREATE TABLE movies.album_scheduler_task (started_at TIMESTAMP NULL DEFAULT NULL);

INSERT INTO movies.album_scheduler_task (started_at) VALUES (date_sub(now(), INTERVAL 2 MINUTE));
