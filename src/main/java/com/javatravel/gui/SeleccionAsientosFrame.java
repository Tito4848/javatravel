package com.javatravel.gui;

import com.javatravel.business.CalculadoraPrecio;
import com.javatravel.dao.*;
import com.javatravel.model.*;

import javax.swing.*;
import java.awt.*;
import java.util.Set;

public class SeleccionAsientosFrame extends JFrame {
    private Viaje viaje;
    private JPanel panelAsientos;
    private JButton[][] botonesAsientos;
    private String asientoSeleccionado;
    private BoletoDAO boletoDAO;
    private PasajeroDAO pasajeroDAO;
    private CalculadoraPrecio calculadora;
    private JFrame parent;
    private JLabel lblPrecio;
    private JSpinner spnEdad;
    private JTextField txtDni;
    private JTextField txtNombre;
    
    public SeleccionAsientosFrame(JFrame parent, Viaje viaje) {
        this.parent = parent;
        this.viaje = viaje;
        boletoDAO = new BoletoDAO();
        pasajeroDAO = new PasajeroDAO();
        calculadora = new CalculadoraPrecio();
        initComponents();
        cargarAsientos();
    }
    
    private void initComponents() {
        setTitle("Selección de Asientos - " + viaje.getRuta().toString());
        setSize(1000, 750);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Información del viaje
        JPanel panelInfo = new JPanel(new GridLayout(2, 2, 5, 5));
        panelInfo.setBorder(BorderFactory.createTitledBorder("Información del Viaje"));
        panelInfo.add(new JLabel("Ruta: " + viaje.getRuta().toString()));
        panelInfo.add(new JLabel("Bus: " + viaje.getVehiculo().getPlaca() + " (" + viaje.getVehiculo().getTipoVehiculo() + ")"));
        panelInfo.add(new JLabel("Fecha: " + viaje.getFechaSalida()));
        panelInfo.add(new JLabel("Hora: " + viaje.getHoraSalida()));
        
        // Panel de asientos - REPRESENTACIÓN GRÁFICA DEL BUS
        panelAsientos = new JPanel();
        panelAsientos.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createTitledBorder("Vista Gráfica del Bus - Seleccione un Asiento")));
        panelAsientos.setBackground(new Color(240, 240, 240));
        
        // Leyenda mejorada
        JPanel panelLeyenda = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        panelLeyenda.setBorder(BorderFactory.createTitledBorder("Leyenda de Estados"));
        
        // Disponible
        JPanel panelVerde = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        JButton btnEjemploVerde = new JButton("  ");
        btnEjemploVerde.setBackground(Color.GREEN);
        btnEjemploVerde.setEnabled(false);
        btnEjemploVerde.setPreferredSize(new Dimension(30, 20));
        panelVerde.add(btnEjemploVerde);
        panelVerde.add(new JLabel("Disponible"));
        
        // Ocupado
        JPanel panelRojo = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        JButton btnEjemploRojo = new JButton("  ");
        btnEjemploRojo.setBackground(Color.RED);
        btnEjemploRojo.setEnabled(false);
        btnEjemploRojo.setPreferredSize(new Dimension(30, 20));
        panelRojo.add(btnEjemploRojo);
        panelRojo.add(new JLabel("Ocupado"));
        
        // Seleccionado
        JPanel panelAmarillo = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        JButton btnEjemploAmarillo = new JButton("  ");
        btnEjemploAmarillo.setBackground(Color.YELLOW);
        btnEjemploAmarillo.setEnabled(false);
        btnEjemploAmarillo.setPreferredSize(new Dimension(30, 20));
        panelAmarillo.add(btnEjemploAmarillo);
        panelAmarillo.add(new JLabel("Seleccionado"));
        
        panelLeyenda.add(panelVerde);
        panelLeyenda.add(panelRojo);
        panelLeyenda.add(panelAmarillo);
        
        // Panel de pasajero y confirmación - DISEÑO MEJORADO
        JPanel panelPasajero = new JPanel();
        panelPasajero.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), 
            "Datos del Pasajero",
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12)));
        panelPasajero.setLayout(new BoxLayout(panelPasajero, BoxLayout.Y_AXIS));
        panelPasajero.setPreferredSize(new Dimension(300, 0));
        
        // Campo DNI con búsqueda
        JPanel panelDni = new JPanel(new BorderLayout(10, 5));
        panelDni.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        JLabel lblDni = new JLabel("DNI:");
        lblDni.setFont(new Font("Arial", Font.BOLD, 11));
        panelDni.add(lblDni, BorderLayout.WEST);
        
        JPanel panelDniInput = new JPanel(new BorderLayout(8, 0));
        txtDni = new JTextField();
        txtDni.setPreferredSize(new Dimension(0, 30));
        txtDni.setFont(new Font("Arial", Font.PLAIN, 11));
        panelDniInput.add(txtDni, BorderLayout.CENTER);
        
        JButton btnBuscarDni = new JButton("Buscar");
        btnBuscarDni.setPreferredSize(new Dimension(80, 30));
        btnBuscarDni.setFont(new Font("Arial", Font.PLAIN, 10));
        btnBuscarDni.addActionListener(e -> {
            String dni = txtDni.getText().trim();
            if (!dni.isEmpty()) {
                Pasajero pasajero = pasajeroDAO.buscarPorDni(dni);
                if (pasajero != null) {
                    txtNombre.setText(pasajero.getNombre());
                    spnEdad.setValue(pasajero.getEdad());
                    actualizarPrecio(lblPrecio, (Integer) spnEdad.getValue());
                    JOptionPane.showMessageDialog(this, 
                        "Pasajero encontrado:\n" + pasajero.getNombre(),
                        "Búsqueda Exitosa",
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Pasajero no encontrado.\nComplete los datos manualmente.",
                        "No Encontrado",
                        JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        panelDniInput.add(btnBuscarDni, BorderLayout.EAST);
        panelDni.add(panelDniInput, BorderLayout.CENTER);
        
        // Campo Nombre
        JPanel panelNombre = new JPanel(new BorderLayout(10, 5));
        panelNombre.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        JLabel lblNombre = new JLabel("Nombre Completo:");
        lblNombre.setFont(new Font("Arial", Font.BOLD, 11));
        panelNombre.add(lblNombre, BorderLayout.WEST);
        txtNombre = new JTextField();
        txtNombre.setPreferredSize(new Dimension(0, 30));
        txtNombre.setFont(new Font("Arial", Font.PLAIN, 11));
        panelNombre.add(txtNombre, BorderLayout.CENTER);
        
        // Campo Edad
        JPanel panelEdad = new JPanel(new BorderLayout(10, 5));
        panelEdad.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        JLabel lblEdad = new JLabel("Edad:");
        lblEdad.setFont(new Font("Arial", Font.BOLD, 11));
        panelEdad.add(lblEdad, BorderLayout.WEST);
        spnEdad = new JSpinner(new SpinnerNumberModel(25, 1, 120, 1));
        spnEdad.setPreferredSize(new Dimension(0, 30));
        spnEdad.setFont(new Font("Arial", Font.PLAIN, 11));
        spnEdad.addChangeListener(e -> {
            if (asientoSeleccionado != null) {
                actualizarPrecio(lblPrecio, (Integer) spnEdad.getValue());
            }
        });
        panelEdad.add(spnEdad, BorderLayout.CENTER);
        
        // Panel de Precio (destacado)
        JPanel panelPrecio = new JPanel(new BorderLayout(10, 5));
        panelPrecio.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Precio Final"),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        panelPrecio.setBackground(new Color(240, 248, 255));
        lblPrecio = new JLabel("S/. 0.00", JLabel.CENTER);
        lblPrecio.setFont(new Font("Arial", Font.BOLD, 18));
        lblPrecio.setForeground(new Color(0, 100, 0));
        lblPrecio.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panelPrecio.add(lblPrecio, BorderLayout.CENTER);
        
        // Agregar componentes al panel principal
        panelPasajero.add(panelDni);
        panelPasajero.add(panelNombre);
        panelPasajero.add(panelEdad);
        panelPasajero.add(Box.createVerticalStrut(10));
        panelPasajero.add(panelPrecio);
        
        // Botones de acción - DISEÑO MEJORADO
        JPanel panelBotones = new JPanel(new GridLayout(2, 1, 5, 5));
        panelBotones.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JButton btnConfirmar = new JButton("Confirmar Venta");
        btnConfirmar.setFont(new Font("Arial", Font.BOLD, 14));
        btnConfirmar.setPreferredSize(new Dimension(0, 45));
        btnConfirmar.setBackground(new Color(50, 150, 50));
        btnConfirmar.setForeground(Color.WHITE);
        btnConfirmar.setFocusPainted(false);
        btnConfirmar.addActionListener(e -> {
            if (asientoSeleccionado == null) {
                JOptionPane.showMessageDialog(this, 
                    "Debe seleccionar un asiento antes de confirmar la venta.",
                    "Asiento No Seleccionado",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String dni = txtDni.getText().trim();
            String nombre = txtNombre.getText().trim();
            int edad = (Integer) spnEdad.getValue();
            
            if (dni.isEmpty() || nombre.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Complete todos los datos del pasajero:\n" +
                    "- DNI (8 dígitos)\n" +
                    "- Nombre completo",
                    "Datos Incompletos",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (dni.length() != 8 || !dni.matches("\\d+")) {
                JOptionPane.showMessageDialog(this, 
                    "El DNI debe tener exactamente 8 dígitos numéricos.",
                    "DNI Inválido",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            confirmarVenta(dni, nombre, edad, lblPrecio);
        });
        
        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setFont(new Font("Arial", Font.PLAIN, 12));
        btnCancelar.setPreferredSize(new Dimension(0, 35));
        btnCancelar.addActionListener(e -> dispose());
        
        panelBotones.add(btnConfirmar);
        panelBotones.add(btnCancelar);
        
        // Panel derecho con datos del pasajero y botones
        JPanel panelDerecho = new JPanel(new BorderLayout(0, 10));
        panelDerecho.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        panelDerecho.add(panelPasajero, BorderLayout.CENTER);
        panelDerecho.add(panelBotones, BorderLayout.SOUTH);
        
        // Panel inferior con leyenda y nota
        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.add(panelLeyenda, BorderLayout.CENTER);
        
        JLabel lblNota = new JLabel(
            "<html><center><b>NOTA:</b> La matriz de botones representa la distribución del bus.<br/>" +
            "Cada botón es un asiento. Colores: <font color='green'>Verde=Disponible</font>, " +
            "<font color='red'>Rojo=Ocupado</font>, <font color='yellow'>Amarillo=Seleccionado</font></center></html>",
            JLabel.CENTER);
        lblNota.setFont(new Font("Arial", Font.ITALIC, 9));
        lblNota.setForeground(Color.DARK_GRAY);
        lblNota.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        panelInferior.add(lblNota, BorderLayout.SOUTH);
        
        mainPanel.add(panelInfo, BorderLayout.NORTH);
        mainPanel.add(panelAsientos, BorderLayout.CENTER);
        mainPanel.add(panelInferior, BorderLayout.SOUTH);
        mainPanel.add(panelDerecho, BorderLayout.EAST);
        
        add(mainPanel);
    }
    
    /**
     * Método que DIBUJA el bus como una matriz de botones
     * Representa visualmente la distribución de asientos del vehículo
     */
    private void cargarAsientos() {
        Vehiculo vehiculo = viaje.getVehiculo();
        int capacidad = vehiculo.getCapacidad();
        Set<String> asientosOcupados = boletoDAO.obtenerAsientosOcupados(viaje.getId());
        
        // DISTRIBUCIÓN DEL BUS: 4 columnas (A, B, C, D) con pasillo entre B y C
        // Estructura: [A] [B] | PASILLO | [C] [D]
        int filas = (int) Math.ceil(capacidad / 4.0);
        int columnas = 4;
        
        panelAsientos.removeAll();
        
        // Panel principal con BorderLayout para organizar mejor
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        
        // Panel central con la matriz de asientos - REPRESENTACIÓN GRÁFICA DEL BUS
        // Estructura: [Fila] [A] [B] [Pasillo] [C] [D] = 6 columnas
        JPanel panelMatriz = new JPanel(new GridLayout(filas + 1, 6, 5, 5));
        panelMatriz.setBackground(new Color(240, 240, 240));
        
        // Fila de encabezados
        panelMatriz.add(new JLabel("Fila", JLabel.CENTER));
        panelMatriz.add(new JLabel("A", JLabel.CENTER));
        panelMatriz.add(new JLabel("B", JLabel.CENTER));
        JLabel lblPasilloHeader = new JLabel("│", JLabel.CENTER);
        lblPasilloHeader.setFont(new Font("Arial", Font.BOLD, 16));
        lblPasilloHeader.setForeground(Color.GRAY);
        panelMatriz.add(lblPasilloHeader);
        panelMatriz.add(new JLabel("C", JLabel.CENTER));
        panelMatriz.add(new JLabel("D", JLabel.CENTER));
        
        botonesAsientos = new JButton[filas][columnas];
        String[] letras = {"A", "B", "C", "D"};
        
        // PRIMERO: Crear todos los botones y guardarlos en la matriz
        int contador = 1;
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                if (contador <= capacidad) {
                    // Formato: A1, A2, B1, B2, etc.
                    String codigoAsiento = letras[j] + contador;
                    JButton btn = new JButton(codigoAsiento);
                    btn.setFont(new Font("Arial", Font.BOLD, 11));
                    btn.setPreferredSize(new Dimension(55, 50));
                    btn.setFocusPainted(false);
                    btn.setBorderPainted(true);
                    
                    // Determinar tipo de asiento (para buses de dos pisos)
                    String tipoAsiento = "NORMAL";
                    if (vehiculo instanceof BusDosPisos) {
                        BusDosPisos busDosPisos = (BusDosPisos) vehiculo;
                        if (busDosPisos.esAsientoVIP(codigoAsiento)) {
                            tipoAsiento = "VIP";
                            btn.setToolTipText("Asiento VIP - Primer Piso");
                            btn.setBorder(BorderFactory.createLineBorder(Color.ORANGE, 2));
                        } else {
                            btn.setToolTipText("Asiento Normal - Segundo Piso");
                        }
                    } else {
                        btn.setToolTipText("Asiento Normal");
                    }
                    
                    // ESTADO VISUAL DEL ASIENTO - GESTIÓN DE ESTADOS
                    if (asientosOcupados.contains(codigoAsiento)) {
                        // ROJO: OCUPADO (Vendido)
                        btn.setBackground(new Color(220, 20, 20));
                        btn.setForeground(Color.WHITE);
                        btn.setEnabled(false);
                        btn.setToolTipText("OCUPADO - " + btn.getToolTipText());
                    } else {
                        // VERDE: DISPONIBLE
                        btn.setBackground(new Color(50, 200, 50));
                        btn.setForeground(Color.BLACK);
                        btn.setEnabled(true);
                        btn.setOpaque(true);
                        btn.setContentAreaFilled(true);
                        
                        final String codigo = codigoAsiento;
                        final String tipo = tipoAsiento;
                        final JButton btnFinal = btn;
                        
                        // ActionListener para gestionar la selección visual
                        btn.addActionListener(e -> {
                            // Resetear botón anterior seleccionado
                            if (asientoSeleccionado != null) {
                                resetearBotones();
                            }
                            
                            // AMARILLO: SELECCIONADO (en proceso de compra)
                            asientoSeleccionado = codigo;
                            btnFinal.setBackground(new Color(255, 255, 0));
                            btnFinal.setForeground(Color.BLACK);
                            btnFinal.setToolTipText("SELECCIONADO - " + tipo);
                            
                            // Actualizar precio automáticamente
                            actualizarPrecio(lblPrecio, (Integer) spnEdad.getValue());
                        });
                    }
                    
                    botonesAsientos[i][j] = btn;
                    contador++;
                } else {
                    botonesAsientos[i][j] = null;
                }
            }
        }
        
        // SEGUNDO: Agregar los botones al panel en el orden correcto con pasillo
        for (int i = 0; i < filas; i++) {
            // Número de fila
            JLabel lblFila = new JLabel(String.valueOf(i + 1), JLabel.CENTER);
            lblFila.setFont(new Font("Arial", Font.BOLD, 11));
            panelMatriz.add(lblFila);
            
            // Agregar asientos A y B
            if (botonesAsientos[i][0] != null) {
                panelMatriz.add(botonesAsientos[i][0]); // A
            } else {
                panelMatriz.add(new JLabel(""));
            }
            
            if (botonesAsientos[i][1] != null) {
                panelMatriz.add(botonesAsientos[i][1]); // B
            } else {
                panelMatriz.add(new JLabel(""));
            }
            
            // Pasillo visual
            JLabel lblPasilloFila = new JLabel("│", JLabel.CENTER);
            lblPasilloFila.setFont(new Font("Arial", Font.BOLD, 18));
            lblPasilloFila.setForeground(new Color(150, 150, 150));
            panelMatriz.add(lblPasilloFila);
            
            // Agregar asientos C y D
            if (botonesAsientos[i][2] != null) {
                panelMatriz.add(botonesAsientos[i][2]); // C
            } else {
                panelMatriz.add(new JLabel(""));
            }
            
            if (botonesAsientos[i][3] != null) {
                panelMatriz.add(botonesAsientos[i][3]); // D
            } else {
                panelMatriz.add(new JLabel(""));
            }
        }
        
        // Panel con scroll para asientos
        JScrollPane scrollAsientos = new JScrollPane(panelMatriz);
        scrollAsientos.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLoweredBevelBorder(), 
            "Distribución de Asientos del Bus (Matriz de Botones)"));
        scrollAsientos.setPreferredSize(new Dimension(400, 400));
        
        panelPrincipal.add(scrollAsientos, BorderLayout.CENTER);
        panelAsientos.add(panelPrincipal);
        
        panelAsientos.revalidate();
        panelAsientos.repaint();
    }
    
    private void resetearBotones() {
        if (botonesAsientos == null) return;
        
        Set<String> asientosOcupados = boletoDAO.obtenerAsientosOcupados(viaje.getId());
        
        for (int i = 0; i < botonesAsientos.length; i++) {
            for (int j = 0; j < botonesAsientos[i].length; j++) {
                if (botonesAsientos[i][j] != null) {
                    String codigo = botonesAsientos[i][j].getText();
                    if (!asientosOcupados.contains(codigo) && botonesAsientos[i][j].isEnabled()) {
                        botonesAsientos[i][j].setBackground(Color.GREEN);
                    }
                }
            }
        }
    }
    
    private void actualizarPrecio(JLabel lblPrecio, int edad) {
        if (asientoSeleccionado == null) {
            lblPrecio.setText("Precio: S/. 0.00");
            return;
        }
        
        // Determinar tipo de asiento
        String tipoAsiento = "NORMAL";
        Vehiculo vehiculo = viaje.getVehiculo();
        if (vehiculo instanceof BusDosPisos) {
            BusDosPisos busDosPisos = (BusDosPisos) vehiculo;
            if (busDosPisos.esAsientoVIP(asientoSeleccionado)) {
                tipoAsiento = "VIP";
            }
        }
        
        double precioBase = viaje.getRuta().getPrecioBase();
        double precioFinal = calculadora.calcularPrecioFinal(precioBase, tipoAsiento, edad);
        lblPrecio.setText(String.format("Precio: S/. %.2f", precioFinal));
    }
    
    private void confirmarVenta(String dni, String nombre, int edad, JLabel lblPrecio) {
        try {
            // Validar DNI
            if (dni.length() != 8 || !dni.matches("\\d+")) {
                throw new Exception("El DNI debe tener 8 dígitos numéricos");
            }
            
            // Buscar o crear pasajero
            Pasajero pasajero = pasajeroDAO.buscarPorDni(dni);
            if (pasajero == null) {
                pasajero = new Pasajero(dni, nombre, edad);
                if (!pasajeroDAO.guardar(pasajero)) {
                    throw new Exception("Error al registrar pasajero");
                }
            } else {
                // Actualizar datos si es necesario
                pasajero.setNombre(nombre);
                pasajero.setEdad(edad);
                pasajeroDAO.actualizar(pasajero);
            }
            
            // Calcular precio final
            String tipoAsiento = "NORMAL";
            Vehiculo vehiculo = viaje.getVehiculo();
            if (vehiculo instanceof BusDosPisos) {
                BusDosPisos busDosPisos = (BusDosPisos) vehiculo;
                if (busDosPisos.esAsientoVIP(asientoSeleccionado)) {
                    tipoAsiento = "VIP";
                }
            }
            
            double precioBase = viaje.getRuta().getPrecioBase();
            double precioFinal = calculadora.calcularPrecioFinal(precioBase, tipoAsiento, edad);
            
            // Crear boleto
            Boleto boleto = new Boleto(viaje.getId(), pasajero.getId(), asientoSeleccionado, precioFinal);
            
            if (boletoDAO.guardar(boleto)) {
                String mensaje = String.format(
                    "Venta confirmada exitosamente!\n\n" +
                    "Pasajero: %s\n" +
                    "DNI: %s\n" +
                    "Ruta: %s\n" +
                    "Asiento: %s (%s)\n" +
                    "Fecha: %s\n" +
                    "Hora: %s\n" +
                    "Precio Final: S/. %.2f",
                    nombre, dni, viaje.getRuta().toString(), 
                    asientoSeleccionado, tipoAsiento,
                    viaje.getFechaSalida(),
                    viaje.getHoraSalida(),
                    precioFinal
                );
                
                JOptionPane.showMessageDialog(this, mensaje, "Venta Exitosa", JOptionPane.INFORMATION_MESSAGE);
                
                // Actualizar dashboard
                if (parent instanceof VentaPasajesFrame) {
                    ((VentaPasajesFrame) parent).actualizarVentas();
                }
                
                dispose();
            } else {
                throw new Exception("Error al registrar boleto. El asiento puede estar ocupado.");
            }
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, 
                "Error: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
}

