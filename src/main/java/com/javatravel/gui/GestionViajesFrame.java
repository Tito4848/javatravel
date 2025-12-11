package com.javatravel.gui;

import com.javatravel.dao.BusDAO;
import com.javatravel.dao.RutaDAO;
import com.javatravel.dao.ViajeDAO;
import com.javatravel.model.Ruta;
import com.javatravel.model.Vehiculo;
import com.javatravel.model.Viaje;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Calendar;

public class GestionViajesFrame extends JFrame {
    private JTable tablaViajes;
    private DefaultTableModel modeloTabla;
    private ViajeDAO viajeDAO;
    private BusDAO busDAO;
    private RutaDAO rutaDAO;
    private JFrame parent;
    
    public GestionViajesFrame(JFrame parent) {
        this.parent = parent;
        viajeDAO = new ViajeDAO();
        busDAO = new BusDAO();
        rutaDAO = new RutaDAO();
        initComponents();
        cargarViajes();
    }
    
    private void initComponents() {
        setTitle("Gestión de Viajes");
        setSize(900, 600);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Tabla
        String[] columnas = {"ID", "Ruta", "Bus", "Fecha", "Hora", "Tipo Bus"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaViajes = new JTable(modeloTabla);
        JScrollPane scrollTabla = new JScrollPane(tablaViajes);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout());
        
        JButton btnNuevo = new JButton("Nuevo Viaje");
        btnNuevo.addActionListener(e -> mostrarDialogoNuevoViaje());
        
        JButton btnEditar = new JButton("Editar");
        btnEditar.addActionListener(e -> editarViaje());
        
        JButton btnEliminar = new JButton("Eliminar");
        btnEliminar.addActionListener(e -> eliminarViaje());
        
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dispose());
        
        panelBotones.add(btnNuevo);
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnCerrar);
        
        mainPanel.add(scrollTabla, BorderLayout.CENTER);
        mainPanel.add(panelBotones, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private void cargarViajes() {
        modeloTabla.setRowCount(0);
        List<Viaje> viajes = viajeDAO.listarTodos();
        
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        for (Viaje viaje : viajes) {
            Object[] fila = {
                viaje.getId(),
                viaje.getRuta().toString(),
                viaje.getVehiculo().getPlaca(),
                viaje.getFechaSalida().format(dateFormatter),
                viaje.getHoraSalida().toString(),
                viaje.getVehiculo().getTipoVehiculo()
            };
            modeloTabla.addRow(fila);
        }
    }
    
    private void mostrarDialogoNuevoViaje() {
        JDialog dialog = new JDialog(this, "Nuevo Viaje", true);
        dialog.setSize(450, 350);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Combo de buses
        JComboBox<Vehiculo> cmbBus = new JComboBox<>();
        List<Vehiculo> buses = busDAO.listarTodos();
        for (Vehiculo bus : buses) {
            cmbBus.addItem(bus);
        }
        
        // Combo de rutas
        JComboBox<Ruta> cmbRuta = new JComboBox<>();
        List<Ruta> rutas = rutaDAO.listarTodos();
        for (Ruta ruta : rutas) {
            cmbRuta.addItem(ruta);
        }
        
        // Fecha
        Calendar calFecha = Calendar.getInstance();
        calFecha.set(Calendar.HOUR_OF_DAY, 0);
        calFecha.set(Calendar.MINUTE, 0);
        calFecha.set(Calendar.SECOND, 0);
        calFecha.set(Calendar.MILLISECOND, 0);
        JSpinner spnFecha = new JSpinner(new SpinnerDateModel(calFecha.getTime(), null, null, Calendar.DAY_OF_MONTH));
        JSpinner.DateEditor editorFecha = new JSpinner.DateEditor(spnFecha, "dd/MM/yyyy");
        spnFecha.setEditor(editorFecha);
        
        // Hora
        Calendar calHora = Calendar.getInstance();
        calHora.set(Calendar.HOUR_OF_DAY, 8);
        calHora.set(Calendar.MINUTE, 0);
        calHora.set(Calendar.SECOND, 0);
        calHora.set(Calendar.MILLISECOND, 0);
        JSpinner spnHora = new JSpinner(new SpinnerDateModel(calHora.getTime(), null, null, Calendar.HOUR_OF_DAY));
        JSpinner.DateEditor editorHora = new JSpinner.DateEditor(spnHora, "HH:mm");
        spnHora.setEditor(editorHora);
        
        panel.add(new JLabel("Bus:"));
        panel.add(cmbBus);
        panel.add(new JLabel("Ruta:"));
        panel.add(cmbRuta);
        panel.add(new JLabel("Fecha Salida:"));
        panel.add(spnFecha);
        panel.add(new JLabel("Hora Salida:"));
        panel.add(spnHora);
        
        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.addActionListener(e -> {
            try {
                Vehiculo bus = (Vehiculo) cmbBus.getSelectedItem();
                Ruta ruta = (Ruta) cmbRuta.getSelectedItem();
                java.util.Date fecha = (java.util.Date) spnFecha.getValue();
                java.util.Date hora = (java.util.Date) spnHora.getValue();
                
                LocalDate fechaSalida = new java.sql.Date(fecha.getTime()).toLocalDate();
                LocalTime horaSalida = new java.sql.Time(hora.getTime()).toLocalTime();
                
                Viaje viaje = new Viaje(bus.getId(), ruta.getId(), fechaSalida, horaSalida);
                
                if (viajeDAO.guardar(viaje)) {
                    JOptionPane.showMessageDialog(this, "Viaje programado correctamente");
                    dialog.dispose();
                    cargarViajes();
                } else {
                    JOptionPane.showMessageDialog(this, "Error al programar viaje", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.addActionListener(e -> dialog.dispose());
        
        JPanel panelBotones = new JPanel(new FlowLayout());
        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);
        
        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(panelBotones, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private void editarViaje() {
        int fila = tablaViajes.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un viaje para editar");
            return;
        }
        
        int id = (Integer) modeloTabla.getValueAt(fila, 0);
        Viaje viaje = viajeDAO.buscarPorId(id);
        
        if (viaje != null) {
            JOptionPane.showMessageDialog(this, "Funcionalidad de edición en desarrollo");
        }
    }
    
    private void eliminarViaje() {
        int fila = tablaViajes.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un viaje para eliminar");
            return;
        }
        
        int id = (Integer) modeloTabla.getValueAt(fila, 0);
        int confirm = JOptionPane.showConfirmDialog(this, 
            "¿Está seguro de eliminar este viaje?", 
            "Confirmar", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (viajeDAO.eliminar(id)) {
                JOptionPane.showMessageDialog(this, "Viaje eliminado correctamente");
                cargarViajes();
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar viaje", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

