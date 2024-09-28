package org.example.repository.impl;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.example.db.ConnectionManagerImpl;
import org.example.model.GenreEntity;
import org.example.repository.GenreEntityRepository;
import org.example.repository.mapper.impl.GenreResultSetMapperImpl;
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

class GenreEntityRepositoryImplTest {

    private static MySQLContainer<?> mysqlContainer;
    private static HikariDataSource dataSource;
    private GenreEntityRepository genreRepository;

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
        config.setJdbcUrl(mysqlContainer.getJdbcUrl());
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
        ConnectionManagerImpl connectionManager = new ConnectionManagerImpl(dataSource);
        genreRepository = new GenreEntityRepositoryImpl(connectionManager, new GenreResultSetMapperImpl());
    }

    @Test
    void findById() {
        GenreEntity genre = genreRepository.findById(1L);
        assertNotNull(genre);
        assertEquals("Боевик", genre.getName());
    }

    @Test
    void deleteById() {
        boolean isDeleted = genreRepository.deleteById(1L);
        assertTrue(isDeleted);

        // Проверяем, что жанр действительно удалён
        GenreEntity deletedGenre = genreRepository.findById(1L);
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