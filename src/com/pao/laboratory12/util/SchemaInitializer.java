package com.pao.laboratory12.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class SchemaInitializer {

    public static void init(Connection conn) throws SQLException, IOException {
        try (InputStream is = SchemaInitializer.class.getClassLoader().getResourceAsStream("schema.sql")) {
            if (is == null) throw new IOException("schema.sql not found in classpath");
            String sql = new String(is.readAllBytes());
            for (String stmt : sql.split(";")) {
                String trimmed = stmt.trim();
                if (!trimmed.isEmpty()) {
                    try (Statement s = conn.createStatement()) {
                        s.execute(trimmed);
                    }
                }
            }
        }
    }
}
