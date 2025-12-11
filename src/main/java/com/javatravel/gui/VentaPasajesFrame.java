package com.javatravel.gui;

import com.javatravel.dao.*;
import com.javatravel.model.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class VentaPasajesFrame extends JFrame {
    private JComboBox<String> cmbOrigen;
    private JComboBox<String> cmbDestino;
    private JSpinner spnFecha;
    private JTable tablaViajes;
    private DefaultTableModel modeloTabla;
    private ViajeDAO viajeDAO;
    private RutaDAO rutaDAO;
    private JFrame parent;
    
    public VentaPasajesFrame(JFrame parent) {
        this.parent = parent;
        viajeDAO = new ViajeDAO();
        rutaDAO = new RutaDAO();
        initComponents();
        cargarOrigenesDestinos();
    }
    
    private void initComponents() {
        setTitle("Venta de Pasajes");
        setSize(1000, 700);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panel de búsqueda
        JPanel panelBusqueda = new JPanel(new GridLayout(2, 4, 5, 5));
        panelBusqueda.setBorder(BorderFactory.createTitledBorder("Buscar Viajes"));
        
        panelBusqueda.add(new JLabel("Origen:"));
        cmbOrigen = new JComboBox<>();
        panelBusqueda.add(cmbOrigen);
        
        panelBusqueda.add(new JLabel("Destino:"));
        cmbDestino = new JComboBox<>();
        panelBusqueda.add(cmbDestino);
        
        panelBusqueda.add(new JLabel("Fecha:"));
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
        cal.set(java.util.Calendar.MINUTE, 0);
        cal.set(java.util.Calendar.SECOND, 0);
        cal.set(java.util.Calendar.MILLISECOND, 0);
        spnFecha = new JSpinner(new SpinnerDateModel(cal.getTime(), null, null, java.util.Calendar.DAY_OF_MONTH));
        JSpinner.DateEditor editor = new JSpinner.DateEditor(spnFecha, "dd/MM/yyyy");
        spnFecha.setEditor(editor);
        panelBusqueda.add(spnFecha);
        
        JButton btnBuscar = new JButton("Buscar Viajes");
        btnBuscar.addActionListener(e -> buscarViajes());
        panelBusqueda.add(btnBuscar);
        
        // Tabla de viajes
        String[] columnas = {"ID", "Ruta", "Hora", "Bus", "Tipo", "Precio Base", "Asientos Libres"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaViajes = new JTable(modeloTabla);
        tablaViajes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaViajes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int fila = tablaViajes.getSelectedRow();
                if (fila != -1) {
                    abrirSeleccionAsientos();
                }
            }
        });
        
        JScrollPane scrollTabla = new JScrollPane(tablaViajes);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout());
        JButton btnSeleccionar = new JButton("Seleccionar Viaje y Asiento");
        btnSeleccionar.addActionListener(e -> abrirSeleccionAsientos());
        
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dispose());
        
        panelBotones.add(btnSeleccionar);
        panelBotones.add(btnCerrar);
        
        mainPanel.add(panelBusqueda, BorderLayout.NORTH);
        mainPanel.add(scrollTabla, BorderLayout.CENTER);
        mainPanel.add(panelBotones, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private void cargarOrigenesDestinos() {
        try {
            List<Ruta> rutas = rutaDAO.listarTodos();
            java.util.Set<String> origenes = new java.util.HashSet<>();
            java.util.Set<String> destinos = new java.util.HashSet<>();
            
            System.out.println("Cargando orígenes y destinos. Rutas encontradas: " + rutas.size());
            
            for (Ruta ruta : rutas) {
                origenes.add(ruta.getOrigen());
                destinos.add(ruta.getDestino());
            }
            
            cmbOrigen.removeAllItems();
            cmbDestino.removeAllItems();
            
            // Ordenar alfabéticamente
            java.util.List<String> origenesList = new java.util.ArrayList<>(origenes);
            java.util.List<String> destinosList = new java.util.ArrayList<>(destinos);
            java.util.Collections.sort(origenesList);
            java.util.Collections.sort(destinosList);
            
            for (String origen : origenesList) {
                cmbOrigen.addItem(origen);
            }
            
            for (String destino : destinosList) {
                cmbDestino.addItem(destino);
            }
            
            System.out.println("Orígenes cargados: " + origenesList.size());
            System.out.println("Destinos cargados: " + destinosList.size());
        } catch (Exception e) {
            System.err.println("Error al cargar orígenes y destinos: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void buscarViajes() {
        String origen = (String) cmbOrigen.getSelectedItem();
        String destino = (String) cmbDestino.getSelectedItem();
        java.util.Date fecha = (java.util.Date) spnFecha.getValue();
        LocalDate fechaLocal = new java.sql.Date(fecha.getTime()).toLocalDate();
        
        if (origen == null || destino == null) {
            JOptionPane.showMessageDialog(this, "Seleccione origen y destino");
            return;
        }
        
        if (origen.equals(destino)) {
            JOptionPane.showMessageDialog(this, "El origen y destino no pueden ser iguales");
            return;
        }
        
        modeloTabla.setRowCount(0);
        
        try {
            List<Viaje> viajes = viajeDAO.buscarPorOrigenDestinoFecha(origen, destino, fechaLocal);
            BoletoDAO boletoDAO = new BoletoDAO();
            
            System.out.println("Buscando viajes: " + origen + " -> " + destino + " (" + fechaLocal + ")");
            System.out.println("Viajes encontrados: " + viajes.size());
            
            for (Viaje viaje : viajes) {
                if (viaje.getVehiculo() == null || viaje.getRuta() == null) {
                    System.err.println("Viaje " + viaje.getId() + " tiene relaciones nulas");
                    continue;
                }
                
                int capacidad = viaje.getVehiculo().getCapacidad();
                int ocupados = boletoDAO.obtenerAsientosOcupados(viaje.getId()).size();
                int libres = capacidad - ocupados;
                
                Object[] fila = {
                    viaje.getId(),
                    viaje.getRuta().toString(),
                    viaje.getHoraSalida().toString(),
                    viaje.getVehiculo().getPlaca(),
                    viaje.getVehiculo().getTipoVehiculo(),
                    String.format("S/. %.2f", viaje.getRuta().getPrecioBase()),
                    libres + " / " + capacidad
                };
                modeloTabla.addRow(fila);
            }
            
            if (viajes.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "No se encontraron viajes para:\n" +
                    "Origen: " + origen + "\n" +
                    "Destino: " + destino + "\n" +
                    "Fecha: " + fechaLocal.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")) +
                    "\n\nVerifique que haya viajes programados para esta ruta y fecha.",
                    "Sin resultados", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            System.err.println("Error al buscar viajes: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error al buscar viajes: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void abrirSeleccionAsientos() {
        int fila = tablaViajes.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un viaje de la tabla");
            return;
        }
        
        int idViaje = (Integer) modeloTabla.getValueAt(fila, 0);
        Viaje viaje = viajeDAO.buscarPorId(idViaje);
        
        if (viaje != null) {
            new SeleccionAsientosFrame(this, viaje).setVisible(true);
        }
    }
    
    public void actualizarVentas() {
        if (parent instanceof DashboardFrame) {
            ((DashboardFrame) parent).actualizarDashboard();
        }
    }
}

