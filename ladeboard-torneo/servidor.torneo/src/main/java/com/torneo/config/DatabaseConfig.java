package com.torneo.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {
    private static final String URL = "jdbc:postgresql://localhost:5432/torneo_db";
    private static final String USER = "postgres";
    private static final String PASSWORD = "1234"; // Reemplaza por la contraseña con la que te conectaste en pgAdmin

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println(" No se encontró el driver JDBC de PostgreSQL.");
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}