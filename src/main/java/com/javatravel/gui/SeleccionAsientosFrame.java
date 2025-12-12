package com.javatravel.gui;

import com.javatravel.business.CalculadoraPrecio;
import com.javatravel.dao.*;
import com.javatravel.model.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
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
    private JLabel lblDetallePrecio;
    private JLabel lblCategoriaPasajero;
    private JSpinner spnEdad;
    private JTextField txtDni;
    private JTextField txtNombre;
    private JTable tablaPasajeros;
    private DefaultTableModel modeloPasajeros;
    
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
        
        // Panel de listado de pasajeros de este viaje
        modeloPasajeros = new DefaultTableModel(new String[]{"Pasajero", "DNI", "Asiento", "Tipo"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaPasajeros = new JTable(modeloPasajeros);
        JScrollPane scrollPasajeros = new JScrollPane(tablaPasajeros);
        scrollPasajeros.setPreferredSize(new Dimension(0, 180));
        JPanel panelPasajeros = new JPanel(new BorderLayout());
        panelPasajeros.setBorder(BorderFactory.createTitledBorder("Pasajeros y asientos ocupados"));
        panelPasajeros.add(scrollPasajeros, BorderLayout.CENTER);
        
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
                    actualizarCategoriaPasajero(pasajero.getEdad());
                    if (asientoSeleccionado != null) {
                        actualizarPrecio(lblPrecio, pasajero.getEdad());
                    }
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
            int edad = (Integer) spnEdad.getValue();
            actualizarCategoriaPasajero(edad);
            if (asientoSeleccionado != null) {
                actualizarPrecio(lblPrecio, edad);
            }
        });
        panelEdad.add(spnEdad, BorderLayout.CENTER);
        
        // Etiqueta de categoría del pasajero
        JPanel panelCategoria = new JPanel(new BorderLayout(10, 5));
        panelCategoria.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        lblCategoriaPasajero = new JLabel("Categoría: Adulto", JLabel.LEFT);
        lblCategoriaPasajero.setFont(new Font("Arial", Font.BOLD, 12));
        lblCategoriaPasajero.setForeground(new Color(0, 100, 200));
        panelCategoria.add(lblCategoriaPasajero, BorderLayout.CENTER);
        
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

        lblDetallePrecio = new JLabel("Seleccione asiento para ver recargos/descuentos", JLabel.CENTER);
        lblDetallePrecio.setFont(new Font("Arial", Font.PLAIN, 11));
        lblDetallePrecio.setForeground(new Color(60, 60, 60));
        panelPrecio.add(lblDetallePrecio, BorderLayout.SOUTH);
        
        // Agregar componentes al panel principal
        panelPasajero.add(panelDni);
        panelPasajero.add(panelNombre);
        panelPasajero.add(panelEdad);
        panelPasajero.add(panelCategoria);
        panelPasajero.add(Box.createVerticalStrut(10));
        panelPasajero.add(panelPrecio);
        
        // Inicializar categoría del pasajero con la edad por defecto
        actualizarCategoriaPasajero((Integer) spnEdad.getValue());
        
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
        
        JPanel panelCentro = new JPanel(new BorderLayout(0, 10));
        panelCentro.add(panelAsientos, BorderLayout.CENTER);
        panelCentro.add(panelPasajeros, BorderLayout.SOUTH);
        
        mainPanel.add(panelInfo, BorderLayout.NORTH);
        mainPanel.add(panelCentro, BorderLayout.CENTER);
        mainPanel.add(panelInferior, BorderLayout.SOUTH);
        mainPanel.add(panelDerecho, BorderLayout.EAST);
        
        add(mainPanel);
    }
    
    /**
     * Método que DIBUJA el bus como una matriz de botones
     * Representa visualmente la distribución de asientos del vehículo
     * Para buses de dos pisos, muestra los pisos separados visualmente
     */
    private void cargarAsientos() {
        Vehiculo vehiculo = viaje.getVehiculo();
        int capacidad = vehiculo.getCapacidad();
        Set<String> asientosOcupados = boletoDAO.obtenerAsientosOcupados(viaje.getId());
        
        panelAsientos.removeAll();
        panelAsientos.setLayout(new BorderLayout());
        
        // Si es bus de dos pisos, mostrar pisos separados
        if (vehiculo instanceof BusDosPisos) {
            BusDosPisos busDosPisos = (BusDosPisos) vehiculo;
            int capacidadPiso1 = busDosPisos.getCapacidadPiso1();
            int capacidadPiso2 = busDosPisos.getCapacidadPiso2();
            
            JPanel panelContenedor = new JPanel();
            panelContenedor.setLayout(new BoxLayout(panelContenedor, BoxLayout.Y_AXIS));
            
            // PISO 1 - VIP
            JPanel panelPiso1 = crearPanelPiso("PISO 1 - ASIENTOS VIP", capacidadPiso1, 1, busDosPisos, asientosOcupados);
            panelPiso1.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(255, 165, 0), 3),
                "PISO 1 - ASIENTOS VIP (Primer Piso)",
                javax.swing.border.TitledBorder.CENTER,
                javax.swing.border.TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14),
                new Color(255, 140, 0)));
            panelPiso1.setBackground(new Color(255, 250, 240));
            
            // Separador visual entre pisos
            JPanel panelSeparador = new JPanel();
            panelSeparador.setPreferredSize(new Dimension(0, 20));
            panelSeparador.setBackground(new Color(200, 200, 200));
            JLabel lblSeparador = new JLabel("═══════════════════════════════════════════════════════", JLabel.CENTER);
            lblSeparador.setFont(new Font("Arial", Font.BOLD, 12));
            lblSeparador.setForeground(new Color(100, 100, 100));
            panelSeparador.add(lblSeparador);
            
            // PISO 2 - NORMAL
            JPanel panelPiso2 = crearPanelPiso("PISO 2 - ASIENTOS NORMALES", capacidadPiso2, capacidadPiso1 + 1, busDosPisos, asientosOcupados);
            panelPiso2.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.BLUE, 2),
                "PISO 2 - ASIENTOS NORMALES (Segundo Piso)",
                javax.swing.border.TitledBorder.CENTER,
                javax.swing.border.TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14),
                Color.BLUE));
            panelPiso2.setBackground(new Color(240, 248, 255));
            
            panelContenedor.add(panelPiso1);
            panelContenedor.add(panelSeparador);
            panelContenedor.add(panelPiso2);
            
            JScrollPane scrollAsientos = new JScrollPane(panelContenedor);
            scrollAsientos.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLoweredBevelBorder(), 
                "Distribución de Asientos del Bus de Dos Pisos"));
            scrollAsientos.setPreferredSize(new Dimension(500, 500));
            
            panelAsientos.add(scrollAsientos, BorderLayout.CENTER);
        } else {
            // Bus estándar - una sola matriz
            int filas = (int) Math.ceil(capacidad / 4.0);
            int columnas = 4;
            
            JPanel panelMatriz = crearMatrizAsientos(capacidad, filas, columnas, vehiculo, asientosOcupados);
            
            JScrollPane scrollAsientos = new JScrollPane(panelMatriz);
            scrollAsientos.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLoweredBevelBorder(), 
                "Distribución de Asientos del Bus Estándar"));
            scrollAsientos.setPreferredSize(new Dimension(400, 400));
            
            panelAsientos.add(scrollAsientos, BorderLayout.CENTER);
        }
        
        panelAsientos.revalidate();
        panelAsientos.repaint();
        
        cargarPasajeros();
    }
    
    /**
     * Crea un panel para un piso específico del bus
     */
    private JPanel crearPanelPiso(String titulo, int capacidadPiso, int inicioContador, BusDosPisos busDosPisos, Set<String> asientosOcupados) {
        int filas = (int) Math.ceil(capacidadPiso / 4.0);
        int columnas = 4;
        
        JPanel panelPiso = new JPanel(new BorderLayout(5, 5));
        panelPiso.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panel con la matriz de asientos
        JPanel panelMatriz = new JPanel(new GridLayout(filas + 1, 6, 5, 5));
        panelMatriz.setBackground(new Color(250, 250, 250));
        
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
        
        JButton[][] botonesPiso = new JButton[filas][columnas];
        String[] letras = {"A", "B", "C", "D"};
        
        int contador = inicioContador;
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                if (contador <= (inicioContador + capacidadPiso - 1)) {
                    String codigoAsiento = letras[j] + contador;
                    JButton btn = crearBotonAsiento(codigoAsiento, busDosPisos.esAsientoVIP(codigoAsiento) ? "VIP" : "NORMAL", asientosOcupados);
                    botonesPiso[i][j] = btn;
                    contador++;
                } else {
                    botonesPiso[i][j] = null;
                }
            }
        }
        
        // Agregar botones al panel
        for (int i = 0; i < filas; i++) {
            JLabel lblFila = new JLabel(String.valueOf(i + 1), JLabel.CENTER);
            lblFila.setFont(new Font("Arial", Font.BOLD, 11));
            panelMatriz.add(lblFila);
            
            for (int j = 0; j < columnas; j++) {
                if (j == 2) {
                    // Pasillo visual
                    JLabel lblPasillo = new JLabel("│", JLabel.CENTER);
                    lblPasillo.setFont(new Font("Arial", Font.BOLD, 18));
                    lblPasillo.setForeground(new Color(150, 150, 150));
                    panelMatriz.add(lblPasillo);
                }
                
                if (botonesPiso[i][j] != null) {
                    panelMatriz.add(botonesPiso[i][j]);
                } else {
                    panelMatriz.add(new JLabel(""));
                }
            }
        }
        
        panelPiso.add(panelMatriz, BorderLayout.CENTER);
        return panelPiso;
    }
    
    /**
     * Crea una matriz de asientos para bus estándar
     */
    private JPanel crearMatrizAsientos(int capacidad, int filas, int columnas, Vehiculo vehiculo, Set<String> asientosOcupados) {
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
        
        int contador = 1;
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                if (contador <= capacidad) {
                    String codigoAsiento = letras[j] + contador;
                    botonesAsientos[i][j] = crearBotonAsiento(codigoAsiento, "NORMAL", asientosOcupados);
                    contador++;
                } else {
                    botonesAsientos[i][j] = null;
                }
            }
        }
        
        // Agregar botones al panel
        for (int i = 0; i < filas; i++) {
            JLabel lblFila = new JLabel(String.valueOf(i + 1), JLabel.CENTER);
            lblFila.setFont(new Font("Arial", Font.BOLD, 11));
            panelMatriz.add(lblFila);
            
            for (int j = 0; j < columnas; j++) {
                if (j == 2) {
                    JLabel lblPasillo = new JLabel("│", JLabel.CENTER);
                    lblPasillo.setFont(new Font("Arial", Font.BOLD, 18));
                    lblPasillo.setForeground(new Color(150, 150, 150));
                    panelMatriz.add(lblPasillo);
                }
                
                if (botonesAsientos[i][j] != null) {
                    panelMatriz.add(botonesAsientos[i][j]);
                } else {
                    panelMatriz.add(new JLabel(""));
                }
            }
        }
        
        return panelMatriz;
    }
    
    /**
     * Crea un botón de asiento con su configuración visual
     */
    private JButton crearBotonAsiento(String codigoAsiento, String tipoAsiento, Set<String> asientosOcupados) {
        JButton btn = new JButton(codigoAsiento);
        btn.setFont(new Font("Arial", Font.BOLD, 11));
        btn.setPreferredSize(new Dimension(55, 50));
        btn.setFocusPainted(false);
        btn.setBorderPainted(true);
        
        if ("VIP".equals(tipoAsiento)) {
            btn.setToolTipText("Asiento VIP - Primer Piso");
            btn.setBorder(BorderFactory.createLineBorder(new Color(255, 165, 0), 2));
        } else {
            btn.setToolTipText("Asiento Normal" + (viaje.getVehiculo() instanceof BusDosPisos ? " - Segundo Piso" : ""));
        }
        
        // ESTADO VISUAL DEL ASIENTO
        if (asientosOcupados.contains(codigoAsiento)) {
            // ROJO: OCUPADO
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
            
            btn.addActionListener(e -> {
                if (asientoSeleccionado != null) {
                    resetearBotones();
                }
                
                asientoSeleccionado = codigo;
                btnFinal.setBackground(new Color(255, 255, 0));
                btnFinal.setForeground(Color.BLACK);
                btnFinal.setToolTipText("SELECCIONADO - " + tipo);
                
                actualizarPrecio(lblPrecio, (Integer) spnEdad.getValue());
            });
        }
        
        return btn;
    }
    
    private void resetearBotones() {
        if (botonesAsientos == null) return;
        
        Set<String> asientosOcupados = boletoDAO.obtenerAsientosOcupados(viaje.getId());
        
        for (int i = 0; i < botonesAsientos.length; i++) {
            for (int j = 0; j < botonesAsientos[i].length; j++) {
                if (botonesAsientos[i][j] != null) {
                    String codigo = botonesAsientos[i][j].getText();
                    if (!asientosOcupados.contains(codigo) && botonesAsientos[i][j].isEnabled()) {
                        // Determinar color según tipo de asiento
                        Vehiculo vehiculo = viaje.getVehiculo();
                        if (vehiculo instanceof BusDosPisos) {
                            BusDosPisos busDosPisos = (BusDosPisos) vehiculo;
                            if (busDosPisos.esAsientoVIP(codigo)) {
                                botonesAsientos[i][j].setBackground(new Color(50, 200, 50));
                            } else {
                                botonesAsientos[i][j].setBackground(new Color(50, 200, 50));
                            }
                        } else {
                            botonesAsientos[i][j].setBackground(new Color(50, 200, 50));
                        }
                    }
                }
            }
        }
        
        // También resetear botones en paneles de dos pisos si existen
        resetearBotonesEnPanel(panelAsientos);
    }
    
    private void resetearBotonesEnPanel(Container container) {
        Component[] components = container.getComponents();
        for (Component comp : components) {
            if (comp instanceof JButton) {
                JButton btn = (JButton) comp;
                String codigo = btn.getText();
                if (codigo != null && codigo.matches("[A-D]\\d+")) {
                    Set<String> asientosOcupados = boletoDAO.obtenerAsientosOcupados(viaje.getId());
                    if (!asientosOcupados.contains(codigo) && btn.isEnabled() && !codigo.equals(asientoSeleccionado)) {
                        btn.setBackground(new Color(50, 200, 50));
                    }
                }
            } else if (comp instanceof Container) {
                resetearBotonesEnPanel((Container) comp);
            }
        }
    }
    
    private void cargarPasajeros() {
        if (modeloPasajeros == null) return;
        
        modeloPasajeros.setRowCount(0);
        List<Boleto> boletos = boletoDAO.listarPorViaje(viaje.getId());
        boolean esBusDosPisos = viaje.getVehiculo() instanceof BusDosPisos;
        BusDosPisos busDosPisos = esBusDosPisos ? (BusDosPisos) viaje.getVehiculo() : null;
        
        for (Boleto boleto : boletos) {
            Pasajero pasajero = boleto.getPasajero();
            String tipo = "NORMAL";
            if (esBusDosPisos && busDosPisos != null && busDosPisos.esAsientoVIP(boleto.getAsiento())) {
                tipo = "VIP";
            }
            
            modeloPasajeros.addRow(new Object[]{
                pasajero != null ? pasajero.getNombre() : "N/D",
                pasajero != null ? pasajero.getDni() : "",
                boleto.getAsiento(),
                tipo
            });
        }
    }
    
    private void actualizarCategoriaPasajero(int edad) {
        if (lblCategoriaPasajero == null) return;
        
        String categoria;
        Color color;
        
        if (edad < 18) {
            categoria = "Menor de Edad (Descuento 15%)";
            color = new Color(200, 100, 0); // Naranja
        } else if (edad >= 65) {
            categoria = "Adulto Mayor (Descuento 20%)";
            color = new Color(0, 150, 0); // Verde
        } else {
            categoria = "Adulto";
            color = new Color(0, 100, 200); // Azul
        }
        
        lblCategoriaPasajero.setText("Categoría: " + categoria);
        lblCategoriaPasajero.setForeground(color);
    }
    
    private void actualizarPrecio(JLabel lblPrecio, int edad) {
        if (asientoSeleccionado == null) {
            lblPrecio.setText("Precio: S/. 0.00");
            lblDetallePrecio.setText("Seleccione asiento para ver recargos/descuentos");
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
        
        StringBuilder detalle = new StringBuilder();
        detalle.append(String.format("Base S/. %.2f", precioBase));
        if ("VIP".equals(tipoAsiento)) {
            detalle.append(" +20% VIP");
        }
        if (edad < 18) {
            detalle.append(" -15% menor");
        } else if (edad >= 65) {
            detalle.append(" -20% adulto mayor");
        }
        lblDetallePrecio.setText(detalle.toString());
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
            
            // Crear boleto con tipo de asiento
            Boleto boleto = new Boleto(viaje.getId(), pasajero.getId(), asientoSeleccionado, tipoAsiento, precioFinal);
            
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

