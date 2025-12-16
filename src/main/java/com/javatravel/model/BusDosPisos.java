package com.javatravel.model;

public class BusDosPisos extends Vehiculo {
    private int capacidadPiso1; 
    private int capacidadPiso2; 
    
    public BusDosPisos() {
        super();
    }
    
    public BusDosPisos(String placa, String marca, int capacidad) {
        super(placa, marca, capacidad);
        
        this.capacidadPiso1 = (int) (capacidad * 0.6);
        this.capacidadPiso2 = capacidad - capacidadPiso1;
    }
    
    public BusDosPisos(int id, String placa, String marca, int capacidad) {
        super(placa, marca, capacidad);
        this.id = id;
        this.capacidadPiso1 = (int) (capacidad * 0.6);
        this.capacidadPiso2 = capacidad - capacidadPiso1;
    }
    
    @Override
    public String getTipoVehiculo() {
        return "DOS_PISOS";
    }
    
    @Override
    public int[][] getDistribucionAsientos() {
        
        int filasPiso1 = (int) Math.ceil(capacidadPiso1 / 4.0);
        int filasPiso2 = (int) Math.ceil(capacidadPiso2 / 4.0);
        return new int[filasPiso1 + filasPiso2][4];
    }
    
    public int getCapacidadPiso1() {
        return capacidadPiso1;
    }
    
    public void setCapacidadPiso1(int capacidadPiso1) {
        this.capacidadPiso1 = capacidadPiso1;
    }
    
    public int getCapacidadPiso2() {
        return capacidadPiso2;
    }
    
    public void setCapacidadPiso2(int capacidadPiso2) {
        this.capacidadPiso2 = capacidadPiso2;
    }
    
    public boolean esAsientoVIP(String codigoAsiento) {
        
        try {
            if (codigoAsiento == null || codigoAsiento.length() < 2) {
                return false;
            }
            String numeroStr = codigoAsiento.substring(1);
            int numeroAsiento = Integer.parseInt(numeroStr);
            return numeroAsiento <= capacidadPiso1;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}


