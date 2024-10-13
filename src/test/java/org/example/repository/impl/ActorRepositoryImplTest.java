package org.example.repository.impl;

import org.example.db.ConnectionManagerImpl;
import org.example.model.ActorEntity;
import org.example.repository.ActorRepository;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@Testcontainers
class ActorRepositoryImplTest {
    @Container
    public static MySQLContainer<?> mysqlContainerDemo = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("filmografy")
            .withUsername("test") // Используем username из db.properties
            .withPassword("test") // Используем password из db.properties
            .withInitScript("SQL/initialization.sql"); // SQL-скрипт для инициализации данных

    private ActorRepository actorRepository;
    private ConnectionManagerImpl connectionManager;

    @BeforeEach
    public void setUp() throws SQLException {
        connectionManager = new ConnectionManagerImpl(mysqlContainerDemo.getJdbcUrl() + "?serverTimezone=Europe/Moscow",
                mysqlContainerDemo.getUsername(),
                mysqlContainerDemo.getPassword(),
                mysqlContainerDemo.getDriverClassName());
        try(Connection connection = connectionManager.getConnection()) {
            actorRepository = new ActorRepositoryImpl(connectionManager);
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
        ActorEntity actorEntity = actorRepository.findById(1L);
        assertNotNull(actorEntity);
        assertEquals(1L, actorEntity.getId());
        assertEquals("Киану", actorEntity.getFirstName());
        assertEquals("Ривз", actorEntity.getLastName());
        assertEquals(java.sql.Date.valueOf("1964-09-02").toLocalDate(), actorEntity.getBirthDate().toLocalDate());
    }

    @Test
    void deleteById() {
        boolean deleted = actorRepository.deleteById(1L);
        assertTrue(deleted);

        ActorEntity actorEntity = actorRepository.findById(1L);
        assertNull(actorEntity);
    }

    @Test
    void findAll() {
        List<ActorEntity> actorEntities = actorRepository.findAll();
        assertFalse(actorEntities.isEmpty());
        assertEquals(7, actorEntities.size());
    }

    @Test
    void save() {
        ActorEntity actorEntity = new ActorEntity(null, "New", "Actor", java.sql.Date.valueOf("1965-09-12"), null);
        ActorEntity savedActorEntity = actorRepository.save(actorEntity);

        assertNotNull(savedActorEntity.getId());
        assertEquals("New", savedActorEntity.getFirstName());
        assertEquals("Actor", savedActorEntity.getLastName());
        assertEquals(java.sql.Date.valueOf("1965-09-12").toLocalDate(), savedActorEntity.getBirthDate().toLocalDate());
    }

    @Test
    void update() {
        ActorEntity actorEntity = actorRepository.findById(2L);
        assertNotNull(actorEntity);

        actorEntity.setFirstName("Эмилия");
        actorEntity.setLastName("Кларк");
        actorEntity.setBirthDate(java.sql.Date.valueOf("1999-09-12"));

        actorRepository.update(actorEntity);

        assertEquals("Эмилия", actorEntity.getFirstName());
        assertEquals("Кларк", actorEntity.getLastName());
        assertEquals(java.sql.Date.valueOf("1999-09-12").toLocalDate(), actorEntity.getBirthDate().toLocalDate());
    }

    @Test
    void exists() {
        boolean exists = actorRepository.exists(2L);
        assertTrue(exists);

        exists = actorRepository.exists(666L);
        assertFalse(exists);
    }
}