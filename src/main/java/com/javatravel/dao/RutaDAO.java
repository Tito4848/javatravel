package com.javatravel.dao;

import com.javatravel.config.DatabaseConfig;
import com.javatravel.interfaces.IGuardable;
import com.javatravel.model.Ruta;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RutaDAO implements IGuardable<Ruta> {
    
    @Override
    public boolean guardar(Ruta ruta) {
        String sql = "INSERT INTO rutas (origen, destino, precio_base, duracion_horas) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, ruta.getOrigen());
            pstmt.setString(2, ruta.getDestino());
            pstmt.setDouble(3, ruta.getPrecioBase());
            pstmt.setInt(4, ruta.getDuracionHoras());
            
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    ruta.setId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public boolean actualizar(Ruta ruta) {
        String sql = "UPDATE rutas SET origen=?, destino=?, precio_base=?, duracion_horas=? WHERE id_ruta=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, ruta.getOrigen());
            pstmt.setString(2, ruta.getDestino());
            pstmt.setDouble(3, ruta.getPrecioBase());
            pstmt.setInt(4, ruta.getDuracionHoras());
            pstmt.setInt(5, ruta.getId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public boolean eliminar(int id) {
        String sql = "DELETE FROM rutas WHERE id_ruta=?";
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
    public Ruta buscarPorId(int id) {
        String sql = "SELECT * FROM rutas WHERE id_ruta=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new Ruta(
                    rs.getInt("id_ruta"),
                    rs.getString("origen"),
                    rs.getString("destino"),
                    rs.getDouble("precio_base"),
                    rs.getInt("duracion_horas")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    public List<Ruta> listarTodos() {
        List<Ruta> rutas = new ArrayList<>();
        String sql = "SELECT * FROM rutas ORDER BY origen, destino";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                rutas.add(new Ruta(
                    rs.getInt("id_ruta"),
                    rs.getString("origen"),
                    rs.getString("destino"),
                    rs.getDouble("precio_base"),
                    rs.getInt("duracion_horas")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rutas;
    }
    
    public List<Ruta> buscarPorOrigenDestino(String origen, String destino) {
        List<Ruta> rutas = new ArrayList<>();
        String sql = "SELECT * FROM rutas WHERE origen=? AND destino=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, origen);
            pstmt.setString(2, destino);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                rutas.add(new Ruta(
                    rs.getInt("id_ruta"),
                    rs.getString("origen"),
                    rs.getString("destino"),
                    rs.getDouble("precio_base"),
                    rs.getInt("duracion_horas")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rutas;
    }
}


