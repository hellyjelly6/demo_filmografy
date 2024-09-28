-- Создание базы данных
CREATE DATABASE IF NOT EXISTS filmografy;

-- Выбор базы данных
USE filmografy;

-- Удаляем таблицы, если они существуют
DROP TABLE IF EXISTS actor_movie;
DROP TABLE IF EXISTS actor;
DROP TABLE IF EXISTS movie;
DROP TABLE IF EXISTS genre;

-- Создаём таблицу жанров
CREATE TABLE IF NOT EXISTS genre
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    genre_name VARCHAR(255) NOT NULL UNIQUE
    );

-- Создаём таблицу фильмов
CREATE TABLE IF NOT EXISTS movie
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL UNIQUE,
    release_year INTEGER NOT NULL,
    genre_id BIGINT,
    FOREIGN KEY (genre_id) REFERENCES genre(id)
    );

-- Создаём таблицу актёров
CREATE TABLE IF NOT EXISTS actor
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    birthdate DATE NOT NULL
    );

-- Создаём таблицу для связи актёров и фильмов
CREATE TABLE IF NOT EXISTS actor_movie
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    movie_id BIGINT,
    actor_id BIGINT,
    FOREIGN KEY (movie_id) REFERENCES movie(id),
    FOREIGN KEY (actor_id) REFERENCES actor(id),
    CONSTRAINT unique_link UNIQUE (actor_id, movie_id)
    );

-- Заполняем таблицу жанров
INSERT INTO genre (genre_name)
VALUES
    ('Боевик'),
    ('Драма'),
    ('Комедия'),
    ('Триллер'),
    ('Фэнтези');

-- Заполняем таблицу фильмов
INSERT INTO movie (title, release_year, genre_id)
VALUES
    ('Матрица', 1999, 1),            -- Боевик
    ('Крёстный отец', 1972, 2),      -- Драма
    ('Назад в будущее', 1985, 5),    -- Фэнтези
    ('Криминальное чтиво', 1994, 4), -- Триллер
    ('Один дома', 1990, 3);          -- Комедия

-- Заполняем таблицу актёров
INSERT INTO actor (first_name, last_name, birthdate)
VALUES
    ('Киану', 'Ривз', '1964-09-02'),      -- Киану Ривз
    ('Аль', 'Пачино', '1940-04-25'),      -- Аль Пачино
    ('Майкл', 'Джей Фокс', '1961-06-09'), -- Майкл Джей Фокс
    ('Джон', 'Траволта', '1954-02-18'),   -- Джон Траволта
    ('Макаулай', 'Калкин', '1980-08-26'), -- Макаулай Калкин
    ('Джо', 'Пеши', '1943-02-09'),        -- Джо Пеши
    ('Дэниел', 'Стерн', '1957-08-28');    -- Дэниел Стерн

-- Заполняем таблицу связей актёров и фильмов
INSERT INTO actor_movie (movie_id, actor_id)
VALUES
    (1, 1),  -- Киану Ривз в "Матрица"
    (2, 2),  -- Аль Пачино в "Крёстный отец"
    (3, 3),  -- Майкл Джей Фокс в "Назад в будущее"
    (4, 4),  -- Джон Траволта в "Криминальное чтиво"
    (5, 5),  -- Макаулай Калкин в "Один дома"
    (5, 6),  -- Джо Пеши в "Один дома"
    (5, 7);  -- Дэниел Стерн в "Один дома"