package org.example.config;

import org.example.db.ConnectionManager;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class InitSqlScheme {

    private static final String schemeSql;

    static {
        schemeSql = loadInitSQL();
    }

    private InitSqlScheme() {
        // Private constructor to prevent instantiation
    }

    public static void initSqlScheme(ConnectionManager connectionManager) {
        try (Connection connection = connectionManager.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(schemeSql);
        } catch (SQLException e) {
            throw new RuntimeException("Error executing SQL schema", e);
        }
    }

    private static String loadInitSQL() {
        try (InputStream inFile = InitSqlScheme.class.getClassLoader().getResourceAsStream("sql/schema.sql")) {
            if (inFile == null) {
                throw new IllegalStateException("Resource 'sql/schema.sql' not found.");
            }
            return new String(inFile.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException("Error loading SQL schema", e);
        }
    }
}
