package com.javatravel;

import com.javatravel.gui.DashboardFrame;
import com.javatravel.util.DataInitializer;
import com.javatravel.util.DatabaseMigration;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        
        System.out.println("========================================");
        System.out.println("  JavaTravel - Sistema de Gestión");
        System.out.println("========================================");
        
        
        System.out.println("Verificando esquema de base de datos...");
        try {
            DatabaseMigration.verificarYActualizarEsquema();
        } catch (Exception e) {
            System.err.println("Advertencia: No se pudo verificar/actualizar el esquema: " + e.getMessage());
            System.err.println("La aplicación continuará, pero puede haber errores si falta la columna tipo_asiento");
        }
        
        System.out.println("Verificando datos de prueba");
        try {
            DataInitializer.inicializarDatos();
        } catch (Exception e) {
            System.err.println("Error crítico al inicializar datos: " + e.getMessage());
            JOptionPane.showMessageDialog(null, 
                "Error al conectar con la base de datos:\n\n" + e.getMessage() + 
                "\n\nVerifique:\n" +
                "1. Que MySQL esté corriendo\n" +
                "2. Que la base de datos 'javatravel_db' exista\n" +
                "3. Que las credenciales en DatabaseConfig.java sean correctas", 
                "Error de Conexión", 
                JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        
        
        SwingUtilities.invokeLater(() -> {
            try {
                DashboardFrame dashboard = new DashboardFrame();
                dashboard.setVisible(true);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, 
                    "Error al iniciar la aplicación: " + e.getMessage() + 
                    "\n\nVerifique la conexión a la base de datos.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        });
    }
}


