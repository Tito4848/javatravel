package com.javatravel.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Ruta {
    private int id;
    private String origen;
    private String destino;
    private double precioBase;
    private int duracionHoras;
    private LocalDate fecha;
    private LocalTime horaSalida;
    
    public Ruta() {
        this.fecha = LocalDate.now();
        this.horaSalida = LocalTime.of(8, 0);
    }
    
    public Ruta(String origen, String destino, double precioBase, int duracionHoras) {
        this(origen, destino, precioBase, duracionHoras, LocalDate.now(), LocalTime.of(8, 0));
    }
    
    public Ruta(String origen, String destino, double precioBase, int duracionHoras, LocalDate fecha, LocalTime horaSalida) {
        this.origen = origen;
        this.destino = destino;
        this.precioBase = precioBase;
        this.duracionHoras = duracionHoras;
        this.fecha = fecha;
        this.horaSalida = horaSalida;
    }
    
    public Ruta(int id, String origen, String destino, double precioBase, int duracionHoras, LocalDate fecha, LocalTime horaSalida) {
        this.id = id;
        this.origen = origen;
        this.destino = destino;
        this.precioBase = precioBase;
        this.duracionHoras = duracionHoras;
        this.fecha = fecha;
        this.horaSalida = horaSalida;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getOrigen() {
        return origen;
    }
    
    public void setOrigen(String origen) {
        this.origen = origen;
    }
    
    public String getDestino() {
        return destino;
    }
    
    public void setDestino(String destino) {
        this.destino = destino;
    }
    
    public double getPrecioBase() {
        return precioBase;
    }
    
    public void setPrecioBase(double precioBase) {
        this.precioBase = precioBase;
    }
    
    public int getDuracionHoras() {
        return duracionHoras;
    }
    
    public void setDuracionHoras(int duracionHoras) {
        this.duracionHoras = duracionHoras;
    }
    
    public LocalDate getFecha() {
        return fecha;
    }
    
    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }
    
    public LocalTime getHoraSalida() {
        return horaSalida;
    }
    
    public void setHoraSalida(LocalTime horaSalida) {
        this.horaSalida = horaSalida;
    }
    
    @Override
    public String toString() {
        return origen + " - " + destino;
    }
}


