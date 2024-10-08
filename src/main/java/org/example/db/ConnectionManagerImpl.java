package org.example.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.example.exception.OperationException;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionManagerImpl implements ConnectionManager {
    private HikariDataSource dataSource;

    public ConnectionManagerImpl() {
        Properties properties = new Properties();
        try (InputStream input = ConnectionManagerImpl.class.getClassLoader().getResourceAsStream("db.properties")) {
            properties.load(input);
            initialize(properties.getProperty("dbUrl"),
                    properties.getProperty("dbUsername"),
                    properties.getProperty("dbPassword"),
                    properties.getProperty("dbDriverClassName"));
        } catch (IOException e) {
            throw new OperationException("Failed to load database configuration", e);
        }
    }

    public ConnectionManagerImpl(String url, String username, String password, String driverClassName) {
        initialize(url, username, password, driverClassName);
    }


    private void initialize(String url, String username, String password, String driverClassName) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName(driverClassName);
        config.setMaximumPoolSize(10);
        config.setIdleTimeout(50000);
        dataSource = new HikariDataSource(config);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public void close() throws Exception {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}
