package com.javatravel.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Viaje {
    private int id;
    private int idBus;
    private int idRuta;
    private LocalDate fechaSalida;
    private LocalTime horaSalida;
    private Vehiculo vehiculo;
    private Ruta ruta;
    
    public Viaje() {}
    
    public Viaje(int idBus, int idRuta, LocalDate fechaSalida, LocalTime horaSalida) {
        this.idBus = idBus;
        this.idRuta = idRuta;
        this.fechaSalida = fechaSalida;
        this.horaSalida = horaSalida;
    }
    
    public Viaje(int id, int idBus, int idRuta, LocalDate fechaSalida, LocalTime horaSalida) {
        this.id = id;
        this.idBus = idBus;
        this.idRuta = idRuta;
        this.fechaSalida = fechaSalida;
        this.horaSalida = horaSalida;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getIdBus() {
        return idBus;
    }
    
    public void setIdBus(int idBus) {
        this.idBus = idBus;
    }
    
    public int getIdRuta() {
        return idRuta;
    }
    
    public void setIdRuta(int idRuta) {
        this.idRuta = idRuta;
    }
    
    public LocalDate getFechaSalida() {
        return fechaSalida;
    }
    
    public void setFechaSalida(LocalDate fechaSalida) {
        this.fechaSalida = fechaSalida;
    }
    
    public LocalTime getHoraSalida() {
        return horaSalida;
    }
    
    public void setHoraSalida(LocalTime horaSalida) {
        this.horaSalida = horaSalida;
    }
    
    public Vehiculo getVehiculo() {
        return vehiculo;
    }
    
    public void setVehiculo(Vehiculo vehiculo) {
        this.vehiculo = vehiculo;
    }
    
    public Ruta getRuta() {
        return ruta;
    }
    
    public void setRuta(Ruta ruta) {
        this.ruta = ruta;
    }
}

