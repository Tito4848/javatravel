package com.javatravel.business;

import com.javatravel.interfaces.ICalculable;

public class CalculadoraPrecio implements ICalculable {
    
    private static final double DESCUENTO_MENOR = 0.15; // 15% descuento
    private static final double DESCUENTO_ADULTO_MAYOR = 0.20; // 20% descuento
    private static final double RECARGO_VIP = 0.20; // 20% recargo
    
    @Override
    public double calcularPrecioFinal(double precioBase, String tipoAsiento, int edad) {
        double precio = precioBase;
        
        // Aplicar recargo por asiento VIP
        if ("VIP".equals(tipoAsiento)) {
            precio = precio * (1 + RECARGO_VIP);
        }
        
        // Aplicar descuentos por edad
        if (edad < 18) {
            precio = precio * (1 - DESCUENTO_MENOR);
        } else if (edad >= 65) {
            precio = precio * (1 - DESCUENTO_ADULTO_MAYOR);
        }
        
        return Math.round(precio * 100.0) / 100.0; // Redondear a 2 decimales
    }
}


