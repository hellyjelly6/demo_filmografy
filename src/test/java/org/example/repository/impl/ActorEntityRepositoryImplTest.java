package org.example.repository.impl;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.example.db.ConnectionManagerImpl;
import org.example.model.ActorEntity;
import org.example.repository.ActorEntityRepository;
import org.example.repository.GenreEntityRepository;
import org.example.repository.mapper.impl.ActorResultSetMapperImpl;
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

class ActorEntityRepositoryImplTest {
    private static MySQLContainer<?> mysqlContainer;
    private static HikariDataSource dataSource;
    private ActorEntityRepository actorEntityRepository;

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
        ConnectionManagerImpl connectionManager = new ConnectionManagerImpl(dataSource);
        actorEntityRepository = new ActorEntityRepositoryImpl();
    }

    @Test
    void findById() {
        ActorEntity actorEntity = actorEntityRepository.findById(1L);
        assertNotNull(actorEntity);
        assertEquals(1L, actorEntity.getId());
        assertEquals("Киану", actorEntity.getFirstName());
        assertEquals("Ривз", actorEntity.getLastName());
        assertEquals(java.sql.Date.valueOf("1964-09-02").getTime(), actorEntity.getBirthDate().getTime());
    }

    @Test
    void deleteById() {
        boolean deleted = actorEntityRepository.deleteById(1L);
        assertTrue(deleted);

        ActorEntity actorEntity = actorEntityRepository.findById(1L);
        assertNull(actorEntity);
    }

    @Test
    void findAll() {
        List<ActorEntity> actorEntities = actorEntityRepository.findAll();
        assertFalse(actorEntities.isEmpty());
        assertEquals(7, actorEntities.size());
    }

    @Test
    void save() {
        ActorEntity actorEntity = new ActorEntity(null, "New", "Actor", java.sql.Date.valueOf("1965-09-12"), null);
        ActorEntity savedActorEntity = actorEntityRepository.save(actorEntity);

        assertNotNull(savedActorEntity.getId());
        assertEquals("New", savedActorEntity.getFirstName());
        assertEquals("Actor", savedActorEntity.getLastName());
        assertEquals(java.sql.Date.valueOf("1965-09-12"), savedActorEntity.getBirthDate());
    }

    @Test
    void update() {
        ActorEntity actorEntity = actorEntityRepository.findById(2L);
        assertNotNull(actorEntity);

        actorEntity.setFirstName("Эмилия");
        actorEntity.setLastName("Кларк");
        actorEntity.setBirthDate(java.sql.Date.valueOf("1999-09-12"));

        actorEntityRepository.update(actorEntity);

        assertEquals("Эмилия", actorEntity.getFirstName());
        assertEquals("Кларк", actorEntity.getLastName());
        assertEquals(java.sql.Date.valueOf("1999-09-12"), actorEntity.getBirthDate());
    }

    @Test
    void exists() {
        boolean exists = actorEntityRepository.exists(2L);
        assertTrue(exists);

        exists = actorEntityRepository.exists(666L);
        assertFalse(exists);
    }
}