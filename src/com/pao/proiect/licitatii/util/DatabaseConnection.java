package com.pao.proiect.licitatii.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Singleton care detine unica conexiune JDBC pentru intreaga aplicatie.
 * Citeste configurarea din db.properties (intai de pe classpath, apoi de pe disc).
 */
public final class DatabaseConnection {

    private static final Path PROPERTIES_PATH =
            Path.of("src", "com", "pao", "proiect", "licitatii", "resources", "db.properties");
    private static final Path SCHEMA_PATH = Path.of("schema.sql");

    private static DatabaseConnection instance;
    private final Connection connection;

    private DatabaseConnection() throws IOException, SQLException {
        Properties props = loadProperties();
        String url      = props.getProperty("db.url");
        String user     = props.getProperty("db.user", "");
        String password = props.getProperty("db.password", "");

        this.connection = DriverManager.getConnection(url, user, password);
        // SQLite ignora FK-urile fara acest pragma.
        try (Statement st = connection.createStatement()) {
            st.execute("PRAGMA foreign_keys = ON");
        }
    }

    public static synchronized DatabaseConnection getInstance() throws IOException, SQLException {
        if (instance == null || instance.connection.isClosed()) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    /** Creeaza tabelele daca nu exista (idempotent) — datele existente se pastreaza. */
    public void initSchema() throws IOException, SQLException {
        executaStatements(false);
    }

    /** Sterge si recreeaza schema (DROP + CREATE) — pierde toate datele. */
    public void resetSchema() throws IOException, SQLException {
        executaStatements(true);
    }

    private void executaStatements(boolean includeDropuri) throws IOException, SQLException {
        String sql = stripComments(Files.readString(SCHEMA_PATH));
        try (Statement st = connection.createStatement()) {
            for (String statement : sql.split(";")) {
                String trimmed = statement.trim();
                if (trimmed.isEmpty()) {
                    continue;
                }
                if (!includeDropuri && trimmed.toUpperCase().startsWith("DROP")) {
                    continue;
                }
                st.execute(trimmed);
            }
        }
    }

    /** Elimina comentariile de linie (--) ca sa nu interfereze cu impartirea pe ';'. */
    private static String stripComments(String sql) {
        StringBuilder sb = new StringBuilder();
        for (String line : sql.split("\n")) {
            int comentariu = line.indexOf("--");
            sb.append(comentariu >= 0 ? line.substring(0, comentariu) : line).append('\n');
        }
        return sb.toString();
    }

    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

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
}
