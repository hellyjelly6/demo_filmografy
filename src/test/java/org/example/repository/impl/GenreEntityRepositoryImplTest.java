package org.example.repository.impl;

import org.example.db.ConnectionManagerImpl;
import org.example.model.GenreEntity;
import org.example.repository.GenreEntityRepository;
import org.example.repository.mapper.impl.GenreResultSetMapperImpl;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class GenreEntityRepositoryImplTest {
    @Container
    // Инициализируем MySQL контейнер с Testcontainers, используя данные из db.properties
    public static MySQLContainer<?> mysqlContainerDemo = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("filmografy")
            .withUsername("test") // Используем username из db.properties
            .withPassword("test") // Используем password из db.properties
            .withInitScript("SQL/initialization.sql"); // SQL-скрипт для инициализации данных


    private GenreEntityRepository genreRepository;
    private static ConnectionManagerImpl connectionManager;

    @BeforeEach
    public void setUp() throws SQLException {
        connectionManager = new ConnectionManagerImpl(mysqlContainerDemo.getJdbcUrl() + "?serverTimezone=Europe/Moscow",
                mysqlContainerDemo.getUsername(),
                mysqlContainerDemo.getPassword(),
                mysqlContainerDemo.getDriverClassName());
        try(Connection connection = connectionManager.getConnection()) {
            genreRepository = new GenreEntityRepositoryImpl(connectionManager, new GenreResultSetMapperImpl());
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
        GenreEntity genre = genreRepository.findById(1L);
        assertNotNull(genre);
        assertEquals("Боевик", genre.getName());
    }

    @Test
    void deleteById() {
        boolean isDeleted = genreRepository.deleteById(5L);
        assertTrue(isDeleted);

        // Проверяем, что жанр действительно удалён
        GenreEntity deletedGenre = genreRepository.findById(5L);
        assertNull(deletedGenre);
    }

    @Test
    void findAll() {
        List<GenreEntity> genres = genreRepository.findAll();
        assertFalse(genres.isEmpty());
        assertEquals(5, genres.size());
    }

    @Test
    void save() {
        GenreEntity newGenre = new GenreEntity(null, "Ужасы", null);
        GenreEntity savedGenre = genreRepository.save(newGenre);

        assertNotNull(savedGenre.getId());
        assertEquals("Ужасы", savedGenre.getName());
    }

    @Test
    void update() {
        GenreEntity genre = genreRepository.findById(2L);
        assertNotNull(genre);

        genre.setName("Новая драма");
        GenreEntity updatedGenre = genreRepository.update(genre);

        assertEquals("Новая драма", updatedGenre.getName());
    }

    @Test
    void exists() {
        boolean exists = genreRepository.exists(2L);
        assertTrue(exists);

        exists = genreRepository.exists(999L);
        assertFalse(exists);
    }
}