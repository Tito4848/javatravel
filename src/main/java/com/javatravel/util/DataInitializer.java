package com.javatravel.util;

import com.javatravel.business.CalculadoraPrecio;
import com.javatravel.config.DatabaseConfig;
import com.javatravel.dao.*;
import com.javatravel.model.*;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;

public class DataInitializer {
    
    public static void inicializarDatos() {
        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            System.out.println("Conexión a la base de datos establecida.");
            
            // Verificar si ya hay datos suficientes
            int totalBuses = contarRegistros(conn, "buses");
            int totalRutas = contarRegistros(conn, "rutas");
            int totalViajes = contarRegistros(conn, "viajes");
            
            System.out.println("Estado actual de la base de datos:");
            System.out.println("  - Buses: " + totalBuses);
            System.out.println("  - Rutas: " + totalRutas);
            System.out.println("  - Viajes: " + totalViajes);
            
            // Si hay menos de 3 buses, asumimos que necesita datos
            if (totalBuses >= 3 && totalRutas >= 5 && totalViajes >= 5) {
                System.out.println("La base de datos ya contiene datos suficientes.");
                System.out.println("  Si necesitas recargar datos, limpia las tablas manualmente.");
                return;
            }
            
            System.out.println("Inicializando datos de prueba...");
            
            BusDAO busDAO = new BusDAO();
            RutaDAO rutaDAO = new RutaDAO();
            ViajeDAO viajeDAO = new ViajeDAO();
            PasajeroDAO pasajeroDAO = new PasajeroDAO();
            BoletoDAO boletoDAO = new BoletoDAO();
            
            // Insertar Buses
            System.out.println("Insertando buses...");
            Vehiculo bus1 = new BusEstandar("ABC-123", "Mercedes Benz", 40);
            Vehiculo bus2 = new BusEstandar("XYZ-456", "Volvo", 45);
            Vehiculo bus3 = new BusDosPisos("DEF-789", "Scania", 60);
            Vehiculo bus4 = new BusDosPisos("GHI-012", "Mercedes Benz", 55);
            Vehiculo bus5 = new BusEstandar("JKL-345", "Volvo", 42);
            Vehiculo bus6 = new BusDosPisos("MNO-678", "Scania", 58);
            
            busDAO.guardar(bus1);
            busDAO.guardar(bus2);
            busDAO.guardar(bus3);
            busDAO.guardar(bus4);
            busDAO.guardar(bus5);
            busDAO.guardar(bus6);
            
            // Insertar Rutas
            System.out.println("Insertando rutas...");
            Ruta ruta1 = new Ruta("Lima", "Arequipa", 80.00, 12);
            Ruta ruta2 = new Ruta("Lima", "Trujillo", 50.00, 8);
            Ruta ruta3 = new Ruta("Lima", "Cusco", 100.00, 20);
            Ruta ruta4 = new Ruta("Lima", "Chiclayo", 60.00, 10);
            Ruta ruta5 = new Ruta("Lima", "Piura", 70.00, 12);
            Ruta ruta6 = new Ruta("Arequipa", "Lima", 80.00, 12);
            Ruta ruta7 = new Ruta("Arequipa", "Cusco", 70.00, 10);
            Ruta ruta8 = new Ruta("Trujillo", "Lima", 50.00, 8);
            Ruta ruta9 = new Ruta("Trujillo", "Chiclayo", 30.00, 4);
            Ruta ruta10 = new Ruta("Cusco", "Lima", 100.00, 20);
            Ruta ruta11 = new Ruta("Cusco", "Arequipa", 70.00, 10);
            Ruta ruta12 = new Ruta("Chiclayo", "Lima", 60.00, 10);
            
            rutaDAO.guardar(ruta1);
            rutaDAO.guardar(ruta2);
            rutaDAO.guardar(ruta3);
            rutaDAO.guardar(ruta4);
            rutaDAO.guardar(ruta5);
            rutaDAO.guardar(ruta6);
            rutaDAO.guardar(ruta7);
            rutaDAO.guardar(ruta8);
            rutaDAO.guardar(ruta9);
            rutaDAO.guardar(ruta10);
            rutaDAO.guardar(ruta11);
            rutaDAO.guardar(ruta12);
            
            // Insertar Viajes
            System.out.println("Insertando viajes...");
            LocalDate hoy = LocalDate.now();
            LocalDate manana = hoy.plusDays(1);
            LocalDate pasadoManana = hoy.plusDays(2);
            
            // Viajes para hoy
            Viaje viaje1 = new Viaje(bus1.getId(), ruta1.getId(), hoy, LocalTime.of(8, 0));
            Viaje viaje2 = new Viaje(bus2.getId(), ruta2.getId(), hoy, LocalTime.of(10, 0));
            Viaje viaje3 = new Viaje(bus3.getId(), ruta1.getId(), hoy, LocalTime.of(14, 0));
            Viaje viaje4 = new Viaje(bus4.getId(), ruta3.getId(), hoy, LocalTime.of(20, 0));
            Viaje viaje5 = new Viaje(bus5.getId(), ruta4.getId(), hoy, LocalTime.of(6, 0));
            Viaje viaje6 = new Viaje(bus6.getId(), ruta1.getId(), hoy, LocalTime.of(16, 0));
            
            // Viajes para mañana
            Viaje viaje7 = new Viaje(bus1.getId(), ruta6.getId(), manana, LocalTime.of(8, 0));
            Viaje viaje8 = new Viaje(bus2.getId(), ruta3.getId(), manana, LocalTime.of(20, 0));
            Viaje viaje9 = new Viaje(bus3.getId(), ruta1.getId(), manana, LocalTime.of(14, 0));
            Viaje viaje10 = new Viaje(bus4.getId(), ruta2.getId(), manana, LocalTime.of(10, 0));
            Viaje viaje11 = new Viaje(bus5.getId(), ruta5.getId(), manana, LocalTime.of(6, 0));
            Viaje viaje12 = new Viaje(bus6.getId(), ruta1.getId(), manana, LocalTime.of(16, 0));
            
            // Viajes para pasado mañana
            Viaje viaje13 = new Viaje(bus1.getId(), ruta1.getId(), pasadoManana, LocalTime.of(8, 0));
            Viaje viaje14 = new Viaje(bus2.getId(), ruta2.getId(), pasadoManana, LocalTime.of(10, 0));
            Viaje viaje15 = new Viaje(bus3.getId(), ruta3.getId(), pasadoManana, LocalTime.of(20, 0));
            Viaje viaje16 = new Viaje(bus4.getId(), ruta4.getId(), pasadoManana, LocalTime.of(6, 0));
            
            viajeDAO.guardar(viaje1);
            viajeDAO.guardar(viaje2);
            viajeDAO.guardar(viaje3);
            viajeDAO.guardar(viaje4);
            viajeDAO.guardar(viaje5);
            viajeDAO.guardar(viaje6);
            viajeDAO.guardar(viaje7);
            viajeDAO.guardar(viaje8);
            viajeDAO.guardar(viaje9);
            viajeDAO.guardar(viaje10);
            viajeDAO.guardar(viaje11);
            viajeDAO.guardar(viaje12);
            viajeDAO.guardar(viaje13);
            viajeDAO.guardar(viaje14);
            viajeDAO.guardar(viaje15);
            viajeDAO.guardar(viaje16);
            
            // Insertar Pasajeros
            System.out.println("Insertando pasajeros...");
            Pasajero p1 = new Pasajero("12345678", "Juan Pérez García", 35);
            Pasajero p2 = new Pasajero("87654321", "María García López", 28);
            Pasajero p3 = new Pasajero("11223344", "Carlos López Martínez", 45);
            Pasajero p4 = new Pasajero("44332211", "Ana Martínez Sánchez", 15);
            Pasajero p5 = new Pasajero("55667788", "Pedro Sánchez Fernández", 70);
            Pasajero p6 = new Pasajero("99887766", "Laura Fernández Torres", 25);
            Pasajero p7 = new Pasajero("22334455", "Roberto Torres Vargas", 32);
            Pasajero p8 = new Pasajero("33445566", "Carmen Vargas Rojas", 55);
            Pasajero p9 = new Pasajero("44556677", "Miguel Rojas Díaz", 12);
            Pasajero p10 = new Pasajero("66778899", "Sofía Díaz Morales", 68);
            Pasajero p11 = new Pasajero("77889900", "Diego Morales Castro", 40);
            Pasajero p12 = new Pasajero("88990011", "Patricia Castro Ruiz", 22);
            
            pasajeroDAO.guardar(p1);
            pasajeroDAO.guardar(p2);
            pasajeroDAO.guardar(p3);
            pasajeroDAO.guardar(p4);
            pasajeroDAO.guardar(p5);
            pasajeroDAO.guardar(p6);
            pasajeroDAO.guardar(p7);
            pasajeroDAO.guardar(p8);
            pasajeroDAO.guardar(p9);
            pasajeroDAO.guardar(p10);
            pasajeroDAO.guardar(p11);
            pasajeroDAO.guardar(p12);
            
            // Insertar algunos Boletos (para mostrar asientos ocupados)
            System.out.println("Insertando boletos de ejemplo...");
            CalculadoraPrecio calc = new CalculadoraPrecio();
            
            // Viaje 1 - Algunos asientos ocupados
            double precio1 = calc.calcularPrecioFinal(80.00, "NORMAL", 35);
            Boleto b1 = new Boleto(viaje1.getId(), p1.getId(), "A1", "NORMAL", precio1);
            boletoDAO.guardar(b1);
            
            double precio2 = calc.calcularPrecioFinal(80.00, "NORMAL", 28);
            Boleto b2 = new Boleto(viaje1.getId(), p2.getId(), "B1", "NORMAL", precio2);
            boletoDAO.guardar(b2);
            
            double precio3 = calc.calcularPrecioFinal(80.00, "NORMAL", 45);
            Boleto b3 = new Boleto(viaje1.getId(), p3.getId(), "C1", "NORMAL", precio3);
            boletoDAO.guardar(b3);
            
            double precio4 = calc.calcularPrecioFinal(80.00, "NORMAL", 15);
            Boleto b4 = new Boleto(viaje1.getId(), p4.getId(), "D1", "NORMAL", precio4);
            boletoDAO.guardar(b4);
            
            double precio5 = calc.calcularPrecioFinal(80.00, "NORMAL", 70);
            Boleto b5 = new Boleto(viaje1.getId(), p5.getId(), "A2", "NORMAL", precio5);
            boletoDAO.guardar(b5);
            
            // Viaje 2 - Algunos asientos ocupados
            double precio6 = calc.calcularPrecioFinal(50.00, "NORMAL", 25);
            Boleto b6 = new Boleto(viaje2.getId(), p6.getId(), "A1", "NORMAL", precio6);
            boletoDAO.guardar(b6);
            
            double precio7 = calc.calcularPrecioFinal(50.00, "NORMAL", 32);
            Boleto b7 = new Boleto(viaje2.getId(), p7.getId(), "B1", "NORMAL", precio7);
            boletoDAO.guardar(b7);
            
            double precio8 = calc.calcularPrecioFinal(50.00, "NORMAL", 55);
            Boleto b8 = new Boleto(viaje2.getId(), p8.getId(), "C1", "NORMAL", precio8);
            boletoDAO.guardar(b8);
            
            double precio9 = calc.calcularPrecioFinal(50.00, "NORMAL", 12);
            Boleto b9 = new Boleto(viaje2.getId(), p9.getId(), "D1", "NORMAL", precio9);
            boletoDAO.guardar(b9);
            
            double precio10 = calc.calcularPrecioFinal(50.00, "NORMAL", 68);
            Boleto b10 = new Boleto(viaje2.getId(), p10.getId(), "A2", "NORMAL", precio10);
            boletoDAO.guardar(b10);
            
            // Viaje 3 (Bus DOS_PISOS) - Algunos VIP ocupados
            double precio11 = calc.calcularPrecioFinal(80.00, "VIP", 40);
            Boleto b11 = new Boleto(viaje3.getId(), p11.getId(), "A1", "VIP", precio11);
            boletoDAO.guardar(b11);
            
            double precio12 = calc.calcularPrecioFinal(80.00, "VIP", 22);
            Boleto b12 = new Boleto(viaje3.getId(), p12.getId(), "B1", "VIP", precio12);
            boletoDAO.guardar(b12);
            
            double precio13 = calc.calcularPrecioFinal(80.00, "VIP", 15);
            Boleto b13 = new Boleto(viaje3.getId(), p4.getId(), "A2", "VIP", precio13);
            boletoDAO.guardar(b13);
            
            double precio14 = calc.calcularPrecioFinal(80.00, "VIP", 70);
            Boleto b14 = new Boleto(viaje3.getId(), p5.getId(), "B2", "VIP", precio14);
            boletoDAO.guardar(b14);
            
            System.out.println("Datos de prueba inicializados correctamente!");
            System.out.println("  - 6 Buses (3 Estándar, 3 Dos Pisos)");
            System.out.println("  - 12 Rutas");
            System.out.println("  - 16 Viajes programados");
            System.out.println("  - 12 Pasajeros");
            System.out.println("  - 14 Boletos de ejemplo");
            System.out.println("\nDatos inicializados correctamente!");
            
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
            
        } catch (SQLException e) {
            System.err.println("Error al inicializar datos: " + e.getMessage());
            System.err.println("Verifique:");
            System.err.println("  1. Que MySQL esté corriendo");
            System.err.println("  2. Que la base de datos 'javatravel_db' exista");
            System.err.println("  3. Que las credenciales en DatabaseConfig.java sean correctas");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static int contarRegistros(Connection conn, String tabla) throws SQLException {
        try {
            String sql = "SELECT COUNT(*) as total FROM " + tabla;
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                int total = rs.getInt("total");
                rs.close();
                stmt.close();
                return total;
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Error al contar registros en " + tabla + ": " + e.getMessage());
            return 0;
        }
        return 0;
    }
    
}

