package com.pao.laboratory14.exercise2.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Singleton care gestioneaza conexiunea la baza de date.
 * Citeste configuratia din db.properties de pe classpath.
 *
 * Configurare IntelliJ: marcheaza 'exercise2/resources/' ca Resources Root.
 */
public class DatabaseConnection {

    private static DatabaseConnection instance;
    private Connection connection;

    // Locatia pe disc, relativa la radacina proiectului (folosita ca fallback
    // cand resources/ nu e marcat ca Resources Root in IntelliJ).
    private static final Path PROPERTIES_PATH =
            Path.of("src", "com", "pao", "laboratory14", "exercise2", "resources", "db.properties");

    private DatabaseConnection() throws IOException, SQLException {
        Properties props = loadProperties();
        String url      = props.getProperty("db.url");
        String user     = props.getProperty("db.user", "");
        String password = props.getProperty("db.password", "");
        this.connection = DriverManager.getConnection(url, user, password);
    }

    /**
     * Incarca db.properties intai de pe classpath (IntelliJ cu Resources Root),
     * apoi de pe disc, relativ la radacina proiectului (fallback pentru terminal / config lipsa).
     */
    private Properties loadProperties() throws IOException {
        Properties props = new Properties();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("db.properties")) {
            if (is != null) {
                props.load(is);
                return props;
            }
        }
        if (Files.exists(PROPERTIES_PATH)) {
            try (InputStream is = Files.newInputStream(PROPERTIES_PATH)) {
                props.load(is);
                return props;
            }
        }
        throw new IOException("db.properties negasit nici pe classpath, nici la " + PROPERTIES_PATH.toAbsolutePath());
    }

    public static DatabaseConnection getInstance() throws IOException, SQLException {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}

