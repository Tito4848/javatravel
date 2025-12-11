package com.javatravel.model;

public class BusDosPisos extends Vehiculo {
    private int capacidadPiso1; // VIP
    private int capacidadPiso2; // Normal
    
    public BusDosPisos() {
        super();
    }
    
    public BusDosPisos(String placa, String marca, int capacidad) {
        super(placa, marca, capacidad);
        // Distribución típica: 60% piso 1 (VIP), 40% piso 2 (Normal)
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
        // Piso 1: 4 columnas, Piso 2: 4 columnas
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
        // Asientos VIP son del piso 1 (A1-A30, B1-B30, etc.)
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


