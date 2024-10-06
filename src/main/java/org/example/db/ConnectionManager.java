package org.example.db;
import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionManager extends AutoCloseable {
    Connection getConnection() throws SQLException;
}
