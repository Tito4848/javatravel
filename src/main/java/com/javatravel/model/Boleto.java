package com.javatravel.model;

import java.time.LocalDateTime;

public class Boleto {
    private int id;
    private int idViaje;
    private int idPasajero;
    private String asiento;
    private String tipoAsiento; // VIP o NORMAL
    private double precioFinal;
    private LocalDateTime fechaCompra;
    private Viaje viaje;
    private Pasajero pasajero;
    
    public Boleto() {}
    
    public Boleto(int idViaje, int idPasajero, String asiento, double precioFinal) {
        this.idViaje = idViaje;
        this.idPasajero = idPasajero;
        this.asiento = asiento;
        this.tipoAsiento = "NORMAL";
        this.precioFinal = precioFinal;
        this.fechaCompra = LocalDateTime.now();
    }
    
    public Boleto(int idViaje, int idPasajero, String asiento, String tipoAsiento, double precioFinal) {
        this.idViaje = idViaje;
        this.idPasajero = idPasajero;
        this.asiento = asiento;
        this.tipoAsiento = tipoAsiento;
        this.precioFinal = precioFinal;
        this.fechaCompra = LocalDateTime.now();
    }
    
    public Boleto(int id, int idViaje, int idPasajero, String asiento, double precioFinal, LocalDateTime fechaCompra) {
        this.id = id;
        this.idViaje = idViaje;
        this.idPasajero = idPasajero;
        this.asiento = asiento;
        this.tipoAsiento = "NORMAL";
        this.precioFinal = precioFinal;
        this.fechaCompra = fechaCompra;
    }
    
    public Boleto(int id, int idViaje, int idPasajero, String asiento, String tipoAsiento, double precioFinal, LocalDateTime fechaCompra) {
        this.id = id;
        this.idViaje = idViaje;
        this.idPasajero = idPasajero;
        this.asiento = asiento;
        this.tipoAsiento = tipoAsiento;
        this.precioFinal = precioFinal;
        this.fechaCompra = fechaCompra;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getIdViaje() {
        return idViaje;
    }
    
    public void setIdViaje(int idViaje) {
        this.idViaje = idViaje;
    }
    
    public int getIdPasajero() {
        return idPasajero;
    }
    
    public void setIdPasajero(int idPasajero) {
        this.idPasajero = idPasajero;
    }
    
    public String getAsiento() {
        return asiento;
    }
    
    public void setAsiento(String asiento) {
        this.asiento = asiento;
    }
    
    public String getTipoAsiento() {
        return tipoAsiento;
    }
    
    public void setTipoAsiento(String tipoAsiento) {
        this.tipoAsiento = tipoAsiento;
    }
    
    public double getPrecioFinal() {
        return precioFinal;
    }
    
    public void setPrecioFinal(double precioFinal) {
        this.precioFinal = precioFinal;
    }
    
    public LocalDateTime getFechaCompra() {
        return fechaCompra;
    }
    
    public void setFechaCompra(LocalDateTime fechaCompra) {
        this.fechaCompra = fechaCompra;
    }
    
    public Viaje getViaje() {
        return viaje;
    }
    
    public void setViaje(Viaje viaje) {
        this.viaje = viaje;
    }
    
    public Pasajero getPasajero() {
        return pasajero;
    }
    
    public void setPasajero(Pasajero pasajero) {
        this.pasajero = pasajero;
    }
}


