package com.javatravel.dao;

import com.javatravel.config.DatabaseConfig;
import com.javatravel.interfaces.IGuardable;
import com.javatravel.model.Pasajero;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PasajeroDAO implements IGuardable<Pasajero> {
    
    @Override
    public boolean guardar(Pasajero pasajero) {
        String sql = "INSERT INTO pasajeros (dni, nombre, edad) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, pasajero.getDni());
            pstmt.setString(2, pasajero.getNombre());
            pstmt.setInt(3, pasajero.getEdad());
            
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    pasajero.setId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public boolean actualizar(Pasajero pasajero) {
        String sql = "UPDATE pasajeros SET dni=?, nombre=?, edad=? WHERE id_pasajero=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, pasajero.getDni());
            pstmt.setString(2, pasajero.getNombre());
            pstmt.setInt(3, pasajero.getEdad());
            pstmt.setInt(4, pasajero.getId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public boolean eliminar(int id) {
        String sql = "DELETE FROM pasajeros WHERE id_pasajero=?";
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
    public Pasajero buscarPorId(int id) {
        String sql = "SELECT * FROM pasajeros WHERE id_pasajero=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new Pasajero(
                    rs.getInt("id_pasajero"),
                    rs.getString("dni"),
                    rs.getString("nombre"),
                    rs.getInt("edad")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    public List<Pasajero> listarTodos() {
        List<Pasajero> pasajeros = new ArrayList<>();
        String sql = "SELECT * FROM pasajeros ORDER BY nombre";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                pasajeros.add(new Pasajero(
                    rs.getInt("id_pasajero"),
                    rs.getString("dni"),
                    rs.getString("nombre"),
                    rs.getInt("edad")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pasajeros;
    }
    
    public Pasajero buscarPorDni(String dni) {
        String sql = "SELECT * FROM pasajeros WHERE dni=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, dni);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new Pasajero(
                    rs.getInt("id_pasajero"),
                    rs.getString("dni"),
                    rs.getString("nombre"),
                    rs.getInt("edad")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}


