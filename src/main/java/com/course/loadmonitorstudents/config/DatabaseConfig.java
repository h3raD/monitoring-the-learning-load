package com.course.loadmonitorstudents.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {

    private static final String URL = "jdbc:postgresql://localhost:5432/loadmonitor";
    private static final String USER = "postgres";
    private static final String PASSWORD = "root";

    private static Connection connection;

    private DatabaseConfig() {
    }

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed())
            connection = DriverManager.getConnection(URL, USER, PASSWORD);

        return connection;
    }
}