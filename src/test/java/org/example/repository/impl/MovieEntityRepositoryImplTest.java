package org.example.repository.impl;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.example.db.ConnectionManagerImpl;
import org.example.model.MovieEntity;
import org.example.repository.GenreEntityRepository;
import org.example.repository.MovieEntityRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MySQLContainer;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class MovieEntityRepositoryImplTest {
    private static MySQLContainer<?> mysqlContainer;
    private static HikariDataSource dataSource;
    private MovieEntityRepository movieEntityRepository;
    private GenreEntityRepository genreEntityRepository;

    @BeforeAll
    public static void startContainer() {
        // Загружаем данные из db.properties
        Properties properties = new Properties();
        try (InputStream input = GenreEntityRepositoryImplTest.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input == null) {
                throw new RuntimeException("Не удалось найти файл db.properties");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при загрузке файла db.properties", e);
        }

        String dbUsername = properties.getProperty("dbUsername");
        String dbPassword = properties.getProperty("dbPassword");

        // Инициализируем MySQL контейнер с Testcontainers, используя данные из db.properties
        mysqlContainer = new MySQLContainer<>("mysql:8.0")
                .withDatabaseName("filmografy")
                .withUsername(dbUsername) // Используем username из db.properties
                .withPassword(dbPassword) // Используем password из db.properties
                .withInitScript("SQL/initialization.sql"); // SQL-скрипт для инициализации данных
        mysqlContainer.start();

        // Настройка HikariCP для подключения к контейнеру
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(mysqlContainer.getJdbcUrl() + "?serverTimezone=Europe/Moscow");
        config.setUsername(mysqlContainer.getUsername());
        config.setPassword(mysqlContainer.getPassword());
        config.setDriverClassName(mysqlContainer.getDriverClassName());

        dataSource = new HikariDataSource(config);
    }

    @AfterAll
    public static void stopContainer() {
        if (dataSource != null) {
            dataSource.close();
        }
        if (mysqlContainer != null) {
            mysqlContainer.stop();
        }
    }

    @BeforeEach
    public void setUp() {
        genreEntityRepository = new GenreEntityRepositoryImpl();
        ConnectionManagerImpl connectionManager = new ConnectionManagerImpl(dataSource);
        movieEntityRepository = new MovieEntityRepositoryImpl(connectionManager);
    }

    @Test
    void findById() {
        MovieEntity movie = movieEntityRepository.findById(1L);

        assertNotNull(movie);
        assertEquals(1L, movie.getId());
        assertEquals("Матрица", movie.getTitle());
        assertEquals(1999, movie.getReleaseYear());

    }

    @Test
    void deleteById() {
        boolean deleted = movieEntityRepository.deleteById(1L);
        assertTrue(deleted);

        MovieEntity movie = movieEntityRepository.findById(1L);
        assertNull(movie);
    }

    @Test
    void findAll() {
        List<MovieEntity> movieEntities = movieEntityRepository.findAll();

        assertFalse(movieEntities.isEmpty());
        assertEquals(5, movieEntities.size());
    }

    @Test
    void save() {
        MovieEntity movieEntity = new MovieEntity(null, "новый фильм", 1999, genreEntityRepository.findById(3L), null);
        MovieEntity savedMovieEntity = movieEntityRepository.save(movieEntity);

        assertNotNull(savedMovieEntity.getId());
        assertEquals(movieEntity.getTitle(), savedMovieEntity.getTitle());
        assertEquals(movieEntity.getReleaseYear(), savedMovieEntity.getReleaseYear());
        assertEquals(movieEntity.getGenre(), savedMovieEntity.getGenre());
    }

    @Test
    void update() {
        MovieEntity movie = movieEntityRepository.findById(2L);
        assertNotNull(movie);

        movie.setTitle("Достать ножи");
        movie.setReleaseYear(2001);
        movie.setGenre(genreEntityRepository.findById(2L));

        MovieEntity updated = movieEntityRepository.update(movie);

        assertEquals("Достать ножи", updated.getTitle());
        assertEquals(2001, updated.getReleaseYear());
        assertEquals(genreEntityRepository.findById(2L), updated.getGenre());
    }

    @Test
    void exists() {
        boolean exists = movieEntityRepository.exists(2L);
        assertTrue(exists);

        exists = movieEntityRepository.exists(333L);
        assertFalse(exists);
    }

    @Test
    void findMoviesByGenreId() {
        List<MovieEntity> movieEntities = movieEntityRepository.findMoviesByGenreId(3L);
        assertFalse(movieEntities.isEmpty());
        assertEquals(1, movieEntities.size());
    }

    @Test
    void deleteConstraintByGenreId() {
        boolean deleted = movieEntityRepository.deleteConstraintByGenreId(3L);
        assertTrue(deleted);

        List<MovieEntity> movieEntities = movieEntityRepository.findMoviesByGenreId(3L);
        assertTrue(movieEntities.isEmpty());
    }
}