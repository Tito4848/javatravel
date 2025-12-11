package com.javatravel.util;

import com.javatravel.config.DatabaseConfig;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

public class TestConnection {
    
    public static void testConnection() {
        System.out.println("========================================");
        System.out.println("  Test de Conexión a Base de Datos");
        System.out.println("========================================");
        
        try {
            Connection conn = DatabaseConfig.getConnection();
            System.out.println("Conexión establecida correctamente");
            
            DatabaseMetaData metaData = conn.getMetaData();
            System.out.println("Base de datos: " + metaData.getDatabaseProductName());
            System.out.println("Versión: " + metaData.getDatabaseProductVersion());
            
            // Verificar tablas
            System.out.println("\nVerificando tablas...");
            String[] tables = {"buses", "rutas", "viajes", "pasajeros", "boletos"};
            boolean allTablesExist = true;
            
            for (String table : tables) {
                ResultSet rs = metaData.getTables(null, null, table, null);
                if (rs.next()) {
                    System.out.println("  Tabla '" + table + "' existe");
                } else {
                    System.out.println("  Tabla '" + table + "' NO existe");
                    allTablesExist = false;
                }
                rs.close();
            }
            
            if (!allTablesExist) {
                System.out.println("\nERROR: Faltan tablas en la base de datos.");
                System.out.println("Por favor, ejecute el script SQL de creación de tablas.");
            } else {
                System.out.println("\nTodas las tablas existen");
            }
            
            // Contar registros
            System.out.println("\nContando registros...");
            java.sql.Statement stmt = conn.createStatement();
            
            String[] countQueries = {
                "SELECT COUNT(*) FROM buses",
                "SELECT COUNT(*) FROM rutas",
                "SELECT COUNT(*) FROM viajes",
                "SELECT COUNT(*) FROM pasajeros",
                "SELECT COUNT(*) FROM boletos"
            };
            
            String[] tableNames = {"buses", "rutas", "viajes", "pasajeros", "boletos"};
            
            for (int i = 0; i < countQueries.length; i++) {
                ResultSet rs = stmt.executeQuery(countQueries[i]);
                if (rs.next()) {
                    int count = rs.getInt(1);
                    System.out.println("  " + tableNames[i] + ": " + count + " registros");
                }
                rs.close();
            }
            
            stmt.close();
            conn.close();
            
            System.out.println("\n========================================");
            System.out.println("  Test completado");
            System.out.println("========================================\n");
            
        } catch (Exception e) {
            System.err.println("\nERROR de conexión:");
            System.err.println("  " + e.getMessage());
            System.err.println("\nVerifique:");
            System.err.println("  1. Que MySQL esté corriendo");
            System.err.println("  2. Que la base de datos 'javatravel_db' exista");
            System.err.println("  3. Que las credenciales en DatabaseConfig.java sean correctas");
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        testConnection();
    }
}

