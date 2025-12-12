package com.javatravel.util;

import com.javatravel.config.DatabaseConfig;

import java.sql.*;

/**
 * Utilidad para migraciones de base de datos
 * Verifica y actualiza el esquema de la base de datos automáticamente
 */
public class DatabaseMigration {
    
    /**
     * Verifica y agrega la columna tipo_asiento a la tabla boletos si no existe
     */
    public static void verificarYActualizarEsquema() {
        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            
            // Verificar si la columna tipo_asiento existe
            if (!existeColumna(conn, "boletos", "tipo_asiento")) {
                System.out.println("Columna 'tipo_asiento' no existe. Agregándola...");
                agregarColumnaTipoAsiento(conn);
                System.out.println("Columna 'tipo_asiento' agregada correctamente.");
            } else {
                System.out.println("Columna 'tipo_asiento' ya existe en la tabla 'boletos'.");
            }
            
        } catch (SQLException e) {
            System.err.println("Error al verificar/actualizar esquema de base de datos: " + e.getMessage());
            System.err.println("Por favor, ejecute manualmente el script: database/actualizar_boletos.sql");
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * Verifica si una columna existe en una tabla
     */
    private static boolean existeColumna(Connection conn, String tabla, String columna) throws SQLException {
        DatabaseMetaData metaData = conn.getMetaData();
        ResultSet rs = metaData.getColumns(null, null, tabla, columna);
        boolean existe = rs.next();
        rs.close();
        return existe;
    }
    
    /**
     * Agrega la columna tipo_asiento a la tabla boletos
     */
    private static void agregarColumnaTipoAsiento(Connection conn) throws SQLException {
        String sql = "ALTER TABLE boletos ADD COLUMN tipo_asiento VARCHAR(10) NOT NULL DEFAULT 'NORMAL' AFTER asiento";
        
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            
            // Actualizar registros existentes
            String updateSql = "UPDATE boletos SET tipo_asiento = 'NORMAL' WHERE tipo_asiento IS NULL OR tipo_asiento = ''";
            stmt.executeUpdate(updateSql);
        } catch (SQLException e) {
            // Error 1060 = Duplicate column name (columna ya existe)
            // Error 1054 = Unknown column (otro tipo de error)
            if (e.getErrorCode() == 1060) {
                System.out.println("La columna 'tipo_asiento' ya existe. Continuando...");
                // No es un error crítico, la columna ya existe
            } else {
                // Re-lanzar otros errores
                throw e;
            }
        }
    }
}

