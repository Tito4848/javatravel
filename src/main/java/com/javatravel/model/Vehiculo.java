package com.javatravel.model;

public abstract class Vehiculo {
    protected int id;
    protected String placa;
    protected String marca;
    protected int capacidad;
    
    public Vehiculo() {}
    
    public Vehiculo(String placa, String marca, int capacidad) {
        this.placa = placa;
        this.marca = marca;
        this.capacidad = capacidad;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getPlaca() {
        return placa;
    }
    
    public void setPlaca(String placa) {
        this.placa = placa;
    }
    
    public String getMarca() {
        return marca;
    }
    
    public void setMarca(String marca) {
        this.marca = marca;
    }
    
    public int getCapacidad() {
        return capacidad;
    }
    
    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }
    
    public abstract String getTipoVehiculo();
    public abstract int[][] getDistribucionAsientos();
}


