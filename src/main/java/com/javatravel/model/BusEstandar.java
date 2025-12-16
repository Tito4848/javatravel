package com.javatravel.model;

public class BusEstandar extends Vehiculo {
    
    public BusEstandar() {
        super();
    }
    
    public BusEstandar(String placa, String marca, int capacidad) {
        super(placa, marca, capacidad);
    }
    
    public BusEstandar(int id, String placa, String marca, int capacidad) {
        super(placa, marca, capacidad);
        this.id = id;
    }
    
    @Override
    public String getTipoVehiculo() {
        return "ESTANDAR";
    }
    
    @Override
    public int[][] getDistribucionAsientos() {
        
        int filas = (int) Math.ceil(capacidad / 4.0);
        return new int[filas][4];
    }
}


