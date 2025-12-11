package com.javatravel.model;

public class Ruta {
    private int id;
    private String origen;
    private String destino;
    private double precioBase;
    private int duracionHoras;
    
    public Ruta() {}
    
    public Ruta(String origen, String destino, double precioBase, int duracionHoras) {
        this.origen = origen;
        this.destino = destino;
        this.precioBase = precioBase;
        this.duracionHoras = duracionHoras;
    }
    
    public Ruta(int id, String origen, String destino, double precioBase, int duracionHoras) {
        this.id = id;
        this.origen = origen;
        this.destino = destino;
        this.precioBase = precioBase;
        this.duracionHoras = duracionHoras;
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
    
    @Override
    public String toString() {
        return origen + " - " + destino;
    }
}


