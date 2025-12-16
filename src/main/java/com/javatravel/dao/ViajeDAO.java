package com.javatravel.dao;

import com.javatravel.config.DatabaseConfig;
import com.javatravel.interfaces.IGuardable;
import com.javatravel.model.Ruta;
import com.javatravel.model.Viaje;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ViajeDAO implements IGuardable<Viaje> {
    
    private BusDAO busDAO = new BusDAO();
    private RutaDAO rutaDAO = new RutaDAO();
    
    @Override
    public boolean guardar(Viaje viaje) {
        
        if (tieneSolapamiento(viaje)) {
            throw new RuntimeException("El bus ya está asignado a otro viaje en ese horario");
        }
        
        String sql = "INSERT INTO viajes (id_bus, id_ruta, fecha_salida, hora_salida) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, viaje.getIdBus());
            pstmt.setInt(2, viaje.getIdRuta());
            pstmt.setDate(3, Date.valueOf(viaje.getFechaSalida()));
            pstmt.setTime(4, Time.valueOf(viaje.getHoraSalida()));
            
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    viaje.setId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public boolean actualizar(Viaje viaje) {
        
        if (tieneSolapamiento(viaje)) {
            throw new RuntimeException("El bus ya está asignado a otro viaje en ese horario");
        }
        
        String sql = "UPDATE viajes SET id_bus=?, id_ruta=?, fecha_salida=?, hora_salida=? WHERE id_viaje=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, viaje.getIdBus());
            pstmt.setInt(2, viaje.getIdRuta());
            pstmt.setDate(3, Date.valueOf(viaje.getFechaSalida()));
            pstmt.setTime(4, Time.valueOf(viaje.getHoraSalida()));
            pstmt.setInt(5, viaje.getId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public boolean eliminar(int id) {
        String sql = "DELETE FROM viajes WHERE id_viaje=?";
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
    public Viaje buscarPorId(int id) {
        String sql = "SELECT * FROM viajes WHERE id_viaje=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Viaje viaje = crearViajeDesdeResultSet(rs);
                cargarRelaciones(viaje);
                return viaje;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    public List<Viaje> listarTodos() {
        List<Viaje> viajes = new ArrayList<>();
        String sql = "SELECT * FROM viajes ORDER BY fecha_salida, hora_salida";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Viaje viaje = crearViajeDesdeResultSet(rs);
                cargarRelaciones(viaje);
                viajes.add(viaje);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return viajes;
    }
    
    public List<Viaje> buscarPorOrigenDestinoFecha(String origen, String destino, LocalDate fecha) {
        List<Viaje> viajes = new ArrayList<>();
        String sql = "SELECT v.* FROM viajes v " +
                     "INNER JOIN rutas r ON v.id_ruta = r.id_ruta " +
                     "WHERE UPPER(r.origen) = UPPER(?) AND UPPER(r.destino) = UPPER(?) AND v.fecha_salida = ? " +
                     "ORDER BY v.hora_salida";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, origen);
            pstmt.setString(2, destino);
            pstmt.setDate(3, Date.valueOf(fecha));
            ResultSet rs = pstmt.executeQuery();
            
            System.out.println("Consulta SQL ejecutada: origen=" + origen + ", destino=" + destino + ", fecha=" + fecha);
            
            while (rs.next()) {
                Viaje viaje = crearViajeDesdeResultSet(rs);
                cargarRelaciones(viaje);
                if (viaje.getRuta() != null && viaje.getVehiculo() != null) {
                    viajes.add(viaje);
                } else {
                    System.err.println("Viaje " + viaje.getId() + " tiene relaciones nulas");
                }
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("Error al buscar viajes: " + e.getMessage());
            e.printStackTrace();
        }
        return viajes;
    }
    
    public List<Viaje> obtenerProximosViajes(int limite) {
        List<Viaje> viajes = new ArrayList<>();
        String sql = "SELECT * FROM viajes WHERE fecha_salida >= CURDATE() " +
                     "ORDER BY fecha_salida, hora_salida LIMIT ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, limite);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Viaje viaje = crearViajeDesdeResultSet(rs);
                cargarRelaciones(viaje);
                if (viaje.getRuta() != null && viaje.getVehiculo() != null) {
                    viajes.add(viaje);
                }
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("Error al obtener próximos viajes: " + e.getMessage());
            e.printStackTrace();
        }
        return viajes;
    }
    
    private Viaje crearViajeDesdeResultSet(ResultSet rs) throws SQLException {
        return new Viaje(
            rs.getInt("id_viaje"),
            rs.getInt("id_bus"),
            rs.getInt("id_ruta"),
            rs.getDate("fecha_salida").toLocalDate(),
            rs.getTime("hora_salida").toLocalTime()
        );
    }
    
    private void cargarRelaciones(Viaje viaje) {
        viaje.setVehiculo(busDAO.buscarPorId(viaje.getIdBus()));
        viaje.setRuta(rutaDAO.buscarPorId(viaje.getIdRuta()));
    }
    
    private boolean tieneSolapamiento(Viaje nuevoViaje) {
        
        Ruta ruta = rutaDAO.buscarPorId(nuevoViaje.getIdRuta());
        if (ruta == null) return false;
        
        int duracion = ruta.getDuracionHoras();
        
        String sql = "SELECT COUNT(*) FROM viajes v " +
                     "INNER JOIN rutas r ON v.id_ruta = r.id_ruta " +
                     "WHERE v.id_bus = ? AND v.fecha_salida = ? " +
                     "AND (v.id_viaje != ? OR ? = -1) " +
                     "AND (TIMESTAMPDIFF(HOUR, " +
                     "CONCAT(v.fecha_salida, ' ', v.hora_salida), " +
                     "CONCAT(?, ' ', ?)) BETWEEN -r.duracion_horas AND ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            int idViaje = nuevoViaje.getId() > 0 ? nuevoViaje.getId() : -1;
            pstmt.setInt(1, nuevoViaje.getIdBus());
            pstmt.setDate(2, Date.valueOf(nuevoViaje.getFechaSalida()));
            pstmt.setInt(3, idViaje);
            pstmt.setInt(4, idViaje);
            pstmt.setDate(5, Date.valueOf(nuevoViaje.getFechaSalida()));
            pstmt.setTime(6, Time.valueOf(nuevoViaje.getHoraSalida()));
            pstmt.setInt(7, duracion);
            
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


