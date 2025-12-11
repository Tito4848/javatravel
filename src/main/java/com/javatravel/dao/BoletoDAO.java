package com.javatravel.dao;

import com.javatravel.config.DatabaseConfig;
import com.javatravel.interfaces.IGuardable;
import com.javatravel.model.Boleto;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BoletoDAO implements IGuardable<Boleto> {
    
    private ViajeDAO viajeDAO = new ViajeDAO();
    private PasajeroDAO pasajeroDAO = new PasajeroDAO();
    
    @Override
    public boolean guardar(Boleto boleto) {
        // Validar que el asiento no esté ocupado
        if (asientoOcupado(boleto.getIdViaje(), boleto.getAsiento())) {
            throw new RuntimeException("El asiento " + boleto.getAsiento() + " ya está ocupado");
        }
        
        String sql = "INSERT INTO boletos (id_viaje, id_pasajero, asiento, precio_final, fecha_compra) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, boleto.getIdViaje());
            pstmt.setInt(2, boleto.getIdPasajero());
            pstmt.setString(3, boleto.getAsiento());
            pstmt.setDouble(4, boleto.getPrecioFinal());
            pstmt.setTimestamp(5, Timestamp.valueOf(boleto.getFechaCompra()));
            
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    boleto.setId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public boolean actualizar(Boleto boleto) {
        String sql = "UPDATE boletos SET id_viaje=?, id_pasajero=?, asiento=?, precio_final=? WHERE id_boleto=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, boleto.getIdViaje());
            pstmt.setInt(2, boleto.getIdPasajero());
            pstmt.setString(3, boleto.getAsiento());
            pstmt.setDouble(4, boleto.getPrecioFinal());
            pstmt.setInt(5, boleto.getId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public boolean eliminar(int id) {
        String sql = "DELETE FROM boletos WHERE id_boleto=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public Boleto buscarPorId(int id) {
        String sql = "SELECT * FROM boletos WHERE id_boleto=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Boleto boleto = crearBoletoDesdeResultSet(rs);
                cargarRelaciones(boleto);
                return boleto;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    public List<Boleto> listarTodos() {
        List<Boleto> boletos = new ArrayList<>();
        String sql = "SELECT * FROM boletos ORDER BY fecha_compra DESC";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Boleto boleto = crearBoletoDesdeResultSet(rs);
                cargarRelaciones(boleto);
                boletos.add(boleto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return boletos;
    }
    
    public Set<String> obtenerAsientosOcupados(int idViaje) {
        Set<String> asientos = new HashSet<>();
        String sql = "SELECT asiento FROM boletos WHERE id_viaje=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idViaje);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                asientos.add(rs.getString("asiento"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return asientos;
    }
    
    public double obtenerTotalVentasDelDia() {
        String sql = "SELECT SUM(precio_final) as total FROM boletos WHERE DATE(fecha_compra) = CURDATE()";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }
    
    private Boleto crearBoletoDesdeResultSet(ResultSet rs) throws SQLException {
        return new Boleto(
            rs.getInt("id_boleto"),
            rs.getInt("id_viaje"),
            rs.getInt("id_pasajero"),
            rs.getString("asiento"),
            rs.getDouble("precio_final"),
            rs.getTimestamp("fecha_compra").toLocalDateTime()
        );
    }
    
    private void cargarRelaciones(Boleto boleto) {
        boleto.setViaje(viajeDAO.buscarPorId(boleto.getIdViaje()));
        boleto.setPasajero(pasajeroDAO.buscarPorId(boleto.getIdPasajero()));
    }
    
    private boolean asientoOcupado(int idViaje, String asiento) {
        String sql = "SELECT COUNT(*) FROM boletos WHERE id_viaje=? AND asiento=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idViaje);
            pstmt.setString(2, asiento);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}


