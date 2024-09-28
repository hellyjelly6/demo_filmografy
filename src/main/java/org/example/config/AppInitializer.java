package org.example.config;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.example.db.DatabaseInitializer;

@WebListener
public class AppInitializer implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("Приложение запускается...");
        DatabaseInitializer.initializeDatabase();
        System.out.println("База данных инициализирована.");
    }
}
