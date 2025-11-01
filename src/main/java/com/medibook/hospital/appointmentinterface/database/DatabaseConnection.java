package com.medibook.hospital.appointmentinterface.database;

import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    // Load environment variables from the .env file
    private static final Dotenv dotenv = Dotenv.load();

    // Read the values from the environment variables
    private static final String URL = dotenv.get("DB_URL");
    private static final String USER = dotenv.get("DB_USER");
    private static final String PASSWORD = dotenv.get("DB_PASSWORD");

    public static Connection getConnection() throws SQLException {
        // Defensive check in case the .env file is missing or variables aren't set
        if (URL == null || USER == null || PASSWORD == null) {
            throw new SQLException("Database credentials not found in .env file. Please ensure DB_URL, DB_USER, and DB_PASSWORD are set.");
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found!");
            e.printStackTrace();
            throw new SQLException("JDBC Driver not found", e);
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}