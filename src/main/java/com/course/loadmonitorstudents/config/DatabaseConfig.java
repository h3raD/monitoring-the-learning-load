package com.course.loadmonitorstudents.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConfig {

    private static final String URL = "jdbc:sqlite:loadmonitor.db";

    private static Connection connection;
    private static boolean initialized = false;

    /**
     * Приватный конструктор.
     */
    private DatabaseConfig() {
    }

    /**
     * Получает соединение с базой данных.
     * Инициализирует таблицы если они не существуют.
     *
     * @return соединение с базой данных
     * @throws SQLException если не найден драйвер
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("org.sqlite.JDBC");
            } catch (ClassNotFoundException e) {
                throw new SQLException("SQLite JDBC driver not found", e);
            }
            connection = DriverManager.getConnection(URL);
            
            if (!initialized) {
                initializeDatabase(connection);
                initialized = true;
            }
        }

        return connection;
    }

    /**
     * Инициализирует базу данных выполняя SQL скрипты из init.sql.
     *
     * @param conn соединение с базой
     * @throws SQLException если не можно выполнить init.sql
     */
    private static void initializeDatabase(Connection conn) throws SQLException {
        try (InputStream is = DatabaseConfig.class.getResourceAsStream("/com/course/loadmonitorstudents/db/init.sql")) {
            if (is == null) {
                throw new SQLException("init.sql not found in resources");
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            StringBuilder sql = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                sql.append(line).append("\n");
            }

            String[] statements = sql.toString().split(";");

            try (Statement stmt = conn.createStatement()) {
                for (String statement : statements) {
                    String trimmed = statement.trim();
                    if (!trimmed.isEmpty()) {
                        stmt.execute(trimmed);
                    }
                }
            }

            System.out.println("Database initialized successfully");
        } catch (IOException e) {
            throw new SQLException("Error reading init.sql", e);
        }
    }
}