package com.javatravel.dao;

import com.javatravel.config.DatabaseConfig;
import com.javatravel.interfaces.IGuardable;
import com.javatravel.model.BusDosPisos;
import com.javatravel.model.BusEstandar;
import com.javatravel.model.Vehiculo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BusDAO implements IGuardable<Vehiculo> {
    
    @Override
    public boolean guardar(Vehiculo vehiculo) {
        String sql = "INSERT INTO buses (placa, marca, tipo_bus, capacidad) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, vehiculo.getPlaca());
            pstmt.setString(2, vehiculo.getMarca());
            pstmt.setString(3, vehiculo.getTipoVehiculo());
            pstmt.setInt(4, vehiculo.getCapacidad());
            
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    vehiculo.setId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public boolean actualizar(Vehiculo vehiculo) {
        String sql = "UPDATE buses SET placa=?, marca=?, capacidad=? WHERE id_bus=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, vehiculo.getPlaca());
            pstmt.setString(2, vehiculo.getMarca());
            pstmt.setInt(3, vehiculo.getCapacidad());
            pstmt.setInt(4, vehiculo.getId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public boolean eliminar(int id) {
        String sql = "DELETE FROM buses WHERE id_bus=?";
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
    public Vehiculo buscarPorId(int id) {
        String sql = "SELECT * FROM buses WHERE id_bus=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return crearVehiculoDesdeResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    public List<Vehiculo> listarTodos() {
        List<Vehiculo> vehiculos = new ArrayList<>();
        String sql = "SELECT * FROM buses ORDER BY placa";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                vehiculos.add(crearVehiculoDesdeResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vehiculos;
    }
    
    private Vehiculo crearVehiculoDesdeResultSet(ResultSet rs) throws SQLException {
        String tipo = rs.getString("tipo_bus");
        int id = rs.getInt("id_bus");
        String placa = rs.getString("placa");
        String marca = rs.getString("marca");
        int capacidad = rs.getInt("capacidad");
        
        if ("DOS_PISOS".equals(tipo)) {
            return new BusDosPisos(id, placa, marca, capacidad);
        } else {
            return new BusEstandar(id, placa, marca, capacidad);
        }
    }
}


