package com.javatravel.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {
    private static final String URL = "jdbc:mysql://localhost:3306/javatravel_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASSWORD = "admin"; 
    
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Intentando conectar con usuario: " + USER);
            System.out.println("Contraseña configurada: " + (PASSWORD.isEmpty() ? "(vacía)" : "***"));
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Conexión exitosa a la base de datos");
            return conn;
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver MySQL no encontrado", e);
        } catch (SQLException e) {
            System.err.println("Error de conexión: " + e.getMessage());
            System.err.println("URL: " + URL);
            System.err.println("Usuario: " + USER);
            System.err.println("Contraseña: " + (PASSWORD.isEmpty() ? "(vacía)" : "configurada"));
            throw e;
        }
    }
}


