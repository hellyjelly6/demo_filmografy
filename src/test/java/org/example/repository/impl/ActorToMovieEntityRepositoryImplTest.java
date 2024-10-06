package org.example.repository.impl;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.example.db.ConnectionManagerImpl;
import org.example.model.ActorEntity;
import org.example.model.MovieEntity;
import org.example.repository.ActorToMovieEntityRepository;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class ActorToMovieEntityRepositoryImplTest {
    @Container
    // Инициализируем MySQL контейнер с Testcontainers, используя данные из db.properties
    public static MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("filmografy")
            .withUsername("test") // Используем username из db.properties
            .withPassword("test") // Используем password из db.properties
            .withInitScript("SQL/initialization.sql"); // SQL-скрипт для инициализации данных

    private ActorToMovieEntityRepository actorToMovieEntityRepository;
    private static ConnectionManagerImpl connectionManager;


    @BeforeEach
    public void setUp() throws SQLException {
        connectionManager = new ConnectionManagerImpl(mysqlContainer.getJdbcUrl() + "?useSSL=false&serverTimezone=Europe/Moscow",
                mysqlContainer.getUsername(),
                mysqlContainer.getPassword());
               // mysqlContainer.getDriverClassName());
        try (Connection connection = connectionManager.getConnection()) {
            actorToMovieEntityRepository = new ActorToMovieEntityRepositoryImpl(connectionManager);
        }
    }

    @AfterEach
    public void tearDown() throws Exception {
        if (connectionManager != null) {
            connectionManager.close();
        }
    }

    @Test
    void deleteByMovieId() {
        boolean deleted = actorToMovieEntityRepository.deleteByMovieId(2L);
        assertTrue(deleted);

        List<ActorEntity> actors = actorToMovieEntityRepository.findActorsByMovieId(2L);
        assertTrue(actors.isEmpty());
    }

    @Test
    void deleteByActorId() {
        boolean deleted = actorToMovieEntityRepository.deleteByActorId(3L);
        assertTrue(deleted);

        List<MovieEntity> movies = actorToMovieEntityRepository.findMoviesByActorId(3L);
        assertTrue(movies.isEmpty());
    }

    @Test
    void findMoviesByActorId() {
        List<MovieEntity> moviesList = actorToMovieEntityRepository.findMoviesByActorId(4L);
        assertFalse(moviesList.isEmpty());
        assertEquals( 1, moviesList.size());
    }

    @Test
    void findActorsByMovieId() {
        List<ActorEntity> actors = actorToMovieEntityRepository.findActorsByMovieId(5L);
        assertFalse(actors.isEmpty());
        assertEquals( 3, actors.size());
    }

    @Test
    void saveActorsToMovieByUserName() {
        Long movieId = 4L;
        ActorEntity actorEntity = new ActorEntity();
        actorEntity.setFirstName("Киану");
        actorEntity.setLastName("Ривз");

        actorToMovieEntityRepository.saveActorsToMovieByUserName(movieId, actorEntity);

        List<ActorEntity> actors = actorToMovieEntityRepository.findActorsByMovieId(movieId);

        boolean exists = actors.stream()
                .anyMatch(a -> a.getFirstName().equals("Киану") &&
                        a.getLastName().equals("Ривз")
                );
        assertTrue(exists);
    }
}