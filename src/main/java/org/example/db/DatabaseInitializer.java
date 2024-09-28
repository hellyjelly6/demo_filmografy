package org.example.db;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Statement;

public class DatabaseInitializer {
    public static void initializeDatabase() {
        String sqlFilePath = "/sql/initialization.sql";

        try (InputStream inputStream = DatabaseInitializer.class.getResourceAsStream(sqlFilePath);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
             Connection conn = ConnectionManagerImpl.getDataSource().getConnection();
             Statement stmt = conn.createStatement()) {

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            String sql = sb.toString();

            stmt.execute(sql);
            System.out.println("База данных успешно инициализирована.");

        } catch (Exception e) {
            System.err.println("Ошибка при инициализации базы данных:");
            e.printStackTrace();
        }
    }
}