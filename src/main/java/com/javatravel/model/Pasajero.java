package com.javatravel.model;

public class Pasajero extends Persona {
    
    public Pasajero() {
        super();
    }
    
    public Pasajero(String dni, String nombre, int edad) {
        super(dni, nombre, edad);
    }
    
    public Pasajero(int id, String dni, String nombre, int edad) {
        super(dni, nombre, edad);
        this.id = id;
    }
    
    @Override
    public String getTipoPersona() {
        return "PASAJERO";
    }
    
    public boolean esMenorDeEdad() {
        return edad < 18;
    }
    
    public boolean esAdultoMayor() {
        return edad >= 65;
    }
    
    @Override
    public String toString() {
        return nombre + " (DNI: " + dni + ")";
    }
}


