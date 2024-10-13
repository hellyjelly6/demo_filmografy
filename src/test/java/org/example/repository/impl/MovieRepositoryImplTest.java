package org.example.repository.impl;

import org.example.db.ConnectionManagerImpl;
import org.example.model.MovieEntity;
import org.example.repository.GenreRepository;
import org.example.repository.MovieRepository;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class MovieRepositoryImplTest {

    @Container
    // Инициализируем MySQL контейнер с Testcontainers, используя данные из db.properties
    public static MySQLContainer<?> mysqlContainerDemo = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("filmografy")
            .withUsername("test") // Используем username из db.properties
            .withPassword("test")// Используем password из db.properties
            .withInitScript("SQL/initialization.sql"); // SQL-скрипт для инициализации данных

    private MovieRepository movieRepository;
    private GenreRepository genreRepository;
    private ConnectionManagerImpl connectionManager;


    @BeforeEach
    public void setUp() throws SQLException {
        connectionManager = new ConnectionManagerImpl(mysqlContainerDemo.getJdbcUrl() + "?serverTimezone=Europe/Moscow",
                mysqlContainerDemo.getUsername(),
                mysqlContainerDemo.getPassword(),
                mysqlContainerDemo.getDriverClassName());
        try(Connection connection = connectionManager.getConnection()) {
            genreRepository = new GenreRepositoryImpl(connectionManager);
            movieRepository = new MovieRepositoryImpl(connectionManager);
        }
    }

    @AfterEach
    public void tearDown() throws Exception {
        if (connectionManager != null) {
            connectionManager.close();
        }
    }

    @Test
    void findById() {
        MovieEntity movie = movieRepository.findById(1L);

        assertNotNull(movie);
        assertEquals(1L, movie.getId());
        assertEquals("Матрица", movie.getTitle());
        assertEquals(1999, movie.getReleaseYear());

    }

    @Test
    void deleteById() {
        boolean deleted = movieRepository.deleteById(1L);
        assertTrue(deleted);

        MovieEntity movie = movieRepository.findById(1L);
        assertNull(movie);
    }

    @Test
    void findAll() {
        List<MovieEntity> movieEntities = movieRepository.findAll();

        assertFalse(movieEntities.isEmpty());
        assertEquals(5, movieEntities.size());
    }

    @Test
    void save() {
        MovieEntity movieEntity = new MovieEntity(null, "новый фильм", 1999, genreRepository.findById(3L), null);
        MovieEntity savedMovieEntity = movieRepository.save(movieEntity);

        assertNotNull(savedMovieEntity.getId());
        assertEquals(movieEntity.getTitle(), savedMovieEntity.getTitle());
        assertEquals(movieEntity.getReleaseYear(), savedMovieEntity.getReleaseYear());
        assertEquals(movieEntity.getGenre(), savedMovieEntity.getGenre());
    }

    @Test
    void update() {
        MovieEntity movie = movieRepository.findById(2L);
        assertNotNull(movie);

        movie.setTitle("Достать ножи");
        movie.setReleaseYear(2001);
        movie.setGenre(genreRepository.findById(2L));

        MovieEntity updated = movieRepository.update(movie);

        assertEquals("Достать ножи", updated.getTitle());
        assertEquals(2001, updated.getReleaseYear());
        assertEquals(genreRepository.findById(2L), updated.getGenre());
    }

    @Test
    void exists() {
        boolean exists = movieRepository.exists(2L);
        assertTrue(exists);

        exists = movieRepository.exists(333L);
        assertFalse(exists);
    }

    @Test
    void findMoviesByGenreId() {
        List<MovieEntity> movieEntities = movieRepository.findMoviesByGenreId(3L);
        assertFalse(movieEntities.isEmpty());
        assertEquals(1, movieEntities.size());
    }

    @Test
    void deleteConstraintByGenreId() {
        boolean deleted = movieRepository.deleteConstraintByGenreId(3L);
        assertTrue(deleted);

        List<MovieEntity> movieEntities = movieRepository.findMoviesByGenreId(3L);
        assertTrue(movieEntities.isEmpty());
    }
}