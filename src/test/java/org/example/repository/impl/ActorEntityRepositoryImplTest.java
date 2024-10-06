package org.example.repository.impl;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.example.db.ConnectionManagerImpl;
import org.example.model.ActorEntity;
import org.example.repository.ActorEntityRepository;
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
class ActorEntityRepositoryImplTest {
    @Container
    public static MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("filmografy")
            .withUsername("test") // Используем username из db.properties
            .withPassword("test") // Используем password из db.properties
            .withInitScript("SQL/initialization.sql"); // SQL-скрипт для инициализации данных

    private ActorEntityRepository actorEntityRepository;
    private ConnectionManagerImpl connectionManager;

    @BeforeEach
    public void setUp() throws SQLException {
        connectionManager = new ConnectionManagerImpl(mysqlContainer.getJdbcUrl() + "?useSSL=false&serverTimezone=Europe/Moscow",
                mysqlContainer.getUsername(),
                mysqlContainer.getPassword(),
                mysqlContainer.getDriverClassName());
        try(Connection connection = connectionManager.getConnection()) {
            actorEntityRepository = new ActorEntityRepositoryImpl(connectionManager);
        }
    }

    @AfterEach
    public void tearDown() {
        if (connectionManager != null) {
            connectionManager.closeDataSource();
        }
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