package org.example.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionManagerImpl implements ConnectionManager {
    private static HikariDataSource dataSource;
    public ConnectionManagerImpl() {}
    public ConnectionManagerImpl(HikariDataSource dataSource){
        ConnectionManagerImpl.dataSource = dataSource;
    }


    static {
        Properties properties = new Properties();
        try (InputStream input = ConnectionManagerImpl.class.getClassLoader().getResourceAsStream("db.properties")) {
            properties.load(input);
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(properties.getProperty("dbUrl"));
            config.setUsername(properties.getProperty("dbUsername"));
            config.setPassword(properties.getProperty("dbPassword"));
            config.setDriverClassName(properties.getProperty("dbDriverClassName"));
            dataSource = new HikariDataSource(config);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load database configuration", e);
        }
    }

    public static DataSource getDataSource() {
        return dataSource;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
