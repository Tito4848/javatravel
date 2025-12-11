package com.javatravel.gui;

import com.javatravel.dao.BoletoDAO;
import com.javatravel.dao.ViajeDAO;
import com.javatravel.model.Viaje;

import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DashboardFrame extends JFrame {
    private JLabel lblTotalVentas;
    private JPanel panelProximosViajes;
    private BoletoDAO boletoDAO;
    private ViajeDAO viajeDAO;
    
    public DashboardFrame() {
        boletoDAO = new BoletoDAO();
        viajeDAO = new ViajeDAO();
        initComponents();
        
        // Cargar datos después de que la ventana esté visible
        SwingUtilities.invokeLater(() -> {
            cargarDatos();
        });
    }
    
    private void initComponents() {
        setTitle("JavaTravel - Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Panel superior - Total de ventas
        JPanel panelVentas = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelVentas.setBorder(BorderFactory.createTitledBorder("Ventas del Día"));
        panelVentas.setPreferredSize(new Dimension(0, 100));
        
        lblTotalVentas = new JLabel("S/. 0.00", JLabel.CENTER);
        lblTotalVentas.setFont(new Font("Arial", Font.BOLD, 32));
        lblTotalVentas.setForeground(new Color(0, 128, 0));
        panelVentas.add(lblTotalVentas);
        
        // Panel central - Próximos viajes
        panelProximosViajes = new JPanel();
        panelProximosViajes.setBorder(BorderFactory.createTitledBorder("Próximos 3 Buses por Salir"));
        panelProximosViajes.setLayout(new BoxLayout(panelProximosViajes, BoxLayout.Y_AXIS));
        
        JScrollPane scrollViajes = new JScrollPane(panelProximosViajes);
        scrollViajes.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        // Panel inferior - Botones de acceso rápido
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        
        JButton btnNuevaVenta = new JButton("Nueva Venta");
        btnNuevaVenta.setFont(new Font("Arial", Font.BOLD, 16));
        btnNuevaVenta.setPreferredSize(new Dimension(200, 50));
        btnNuevaVenta.addActionListener(e -> abrirVentaPasajes());
        
        JButton btnGestionBuses = new JButton("Gestión de Buses");
        btnGestionBuses.setFont(new Font("Arial", Font.BOLD, 16));
        btnGestionBuses.setPreferredSize(new Dimension(200, 50));
        btnGestionBuses.addActionListener(e -> abrirGestionBuses());
        
        JButton btnGestionRutas = new JButton("Gestión de Rutas");
        btnGestionRutas.setFont(new Font("Arial", Font.BOLD, 16));
        btnGestionRutas.setPreferredSize(new Dimension(200, 50));
        btnGestionRutas.addActionListener(e -> abrirGestionRutas());
        
        JButton btnGestionViajes = new JButton("Gestión de Viajes");
        btnGestionViajes.setFont(new Font("Arial", Font.BOLD, 16));
        btnGestionViajes.setPreferredSize(new Dimension(200, 50));
        btnGestionViajes.addActionListener(e -> abrirGestionViajes());
        
        JButton btnGestionPasajeros = new JButton("Gestión de Pasajeros");
        btnGestionPasajeros.setFont(new Font("Arial", Font.BOLD, 16));
        btnGestionPasajeros.setPreferredSize(new Dimension(200, 50));
        btnGestionPasajeros.addActionListener(e -> abrirGestionPasajeros());
        
        JButton btnActualizar = new JButton("Actualizar");
        btnActualizar.addActionListener(e -> cargarDatos());
        
        panelBotones.add(btnNuevaVenta);
        panelBotones.add(btnGestionBuses);
        panelBotones.add(btnGestionRutas);
        panelBotones.add(btnGestionViajes);
        panelBotones.add(btnGestionPasajeros);
        panelBotones.add(btnActualizar);
        
        mainPanel.add(panelVentas, BorderLayout.NORTH);
        mainPanel.add(scrollViajes, BorderLayout.CENTER);
        mainPanel.add(panelBotones, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private void cargarDatos() {
        try {
            System.out.println("Cargando datos del dashboard...");
            
            // Cargar total de ventas
            double totalVentas = boletoDAO.obtenerTotalVentasDelDia();
            System.out.println("Total ventas del día: S/. " + totalVentas);
            lblTotalVentas.setText(String.format("S/. %.2f", totalVentas));
            
            // Cargar próximos viajes
            panelProximosViajes.removeAll();
            List<Viaje> proximosViajes = viajeDAO.obtenerProximosViajes(3);
            System.out.println("Viajes encontrados: " + proximosViajes.size());
            
            if (proximosViajes.isEmpty()) {
                JLabel lblSinViajes = new JLabel("No hay viajes programados", JLabel.CENTER);
                lblSinViajes.setFont(new Font("Arial", Font.ITALIC, 14));
                panelProximosViajes.add(lblSinViajes);
                System.out.println("No se encontraron viajes programados");
            } else {
                for (Viaje viaje : proximosViajes) {
                    System.out.println("  - Viaje: " + viaje.getRuta() + " - " + viaje.getFechaSalida());
                    panelProximosViajes.add(crearPanelViaje(viaje));
                }
            }
            
            panelProximosViajes.revalidate();
            panelProximosViajes.repaint();
            System.out.println("Datos del dashboard cargados correctamente");
        } catch (Exception e) {
            System.err.println("Error al cargar datos del dashboard: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error al cargar datos: " + e.getMessage() + 
                "\n\nVerifique la consola para más detalles.", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private JPanel crearPanelViaje(Viaje viaje) {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        
        // Calcular asientos libres
        BoletoDAO boletoDAO = new BoletoDAO();
        int capacidad = viaje.getVehiculo().getCapacidad();
        int ocupados = boletoDAO.obtenerAsientosOcupados(viaje.getId()).size();
        int libres = capacidad - ocupados;
        
        String info = String.format(
            "<html><b>%s</b><br/>" +
            "Fecha: %s | Hora: %s<br/>" +
            "Bus: %s (%s)<br/>" +
            "Asientos libres: %d / %d</html>",
            viaje.getRuta().toString(),
            viaje.getFechaSalida().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
            viaje.getHoraSalida().toString(),
            viaje.getVehiculo().getPlaca(),
            viaje.getVehiculo().getTipoVehiculo(),
            libres,
            capacidad
        );
        
        JLabel lblInfo = new JLabel(info);
        lblInfo.setFont(new Font("Arial", Font.PLAIN, 12));
        
        // Alerta visual si quedan menos de 5 asientos
        if (libres < 5 && libres > 0) {
            panel.setBackground(new Color(255, 255, 200));
            JLabel lblAlerta = new JLabel("POCOS ASIENTOS", JLabel.RIGHT);
            lblAlerta.setFont(new Font("Arial", Font.BOLD, 12));
            lblAlerta.setForeground(Color.RED);
            panel.add(lblAlerta, BorderLayout.EAST);
        } else if (libres == 0) {
            panel.setBackground(new Color(255, 200, 200));
            JLabel lblCompleto = new JLabel("COMPLETO", JLabel.RIGHT);
            lblCompleto.setFont(new Font("Arial", Font.BOLD, 12));
            lblCompleto.setForeground(Color.RED);
            panel.add(lblCompleto, BorderLayout.EAST);
        }
        
        panel.add(lblInfo, BorderLayout.WEST);
        
        return panel;
    }
    
    private void abrirVentaPasajes() {
        new VentaPasajesFrame(this).setVisible(true);
    }
    
    private void abrirGestionBuses() {
        new GestionBusesFrame(this).setVisible(true);
    }
    
    private void abrirGestionRutas() {
        new GestionRutasFrame(this).setVisible(true);
    }
    
    private void abrirGestionViajes() {
        new GestionViajesFrame(this).setVisible(true);
    }
    
    private void abrirGestionPasajeros() {
        new GestionPasajerosFrame(this).setVisible(true);
    }
    
    public void actualizarDashboard() {
        cargarDatos();
    }
}


