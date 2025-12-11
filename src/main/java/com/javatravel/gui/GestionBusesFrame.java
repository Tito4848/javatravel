package com.javatravel.gui;

import com.javatravel.dao.BusDAO;
import com.javatravel.model.BusDosPisos;
import com.javatravel.model.BusEstandar;
import com.javatravel.model.Vehiculo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class GestionBusesFrame extends JFrame {
    private JTable tablaBuses;
    private DefaultTableModel modeloTabla;
    private BusDAO busDAO;
    private JFrame parent;
    
    public GestionBusesFrame(JFrame parent) {
        this.parent = parent;
        busDAO = new BusDAO();
        initComponents();
        cargarBuses();
    }
    
    private void initComponents() {
        setTitle("Gestión de Buses");
        setSize(700, 500);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Tabla
        String[] columnas = {"ID", "Placa", "Marca", "Tipo", "Capacidad"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaBuses = new JTable(modeloTabla);
        JScrollPane scrollTabla = new JScrollPane(tablaBuses);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout());
        
        JButton btnNuevo = new JButton("Nuevo Bus");
        btnNuevo.addActionListener(e -> mostrarDialogoNuevoBus());
        
        JButton btnEditar = new JButton("Editar");
        btnEditar.addActionListener(e -> editarBus());
        
        JButton btnEliminar = new JButton("Eliminar");
        btnEliminar.addActionListener(e -> eliminarBus());
        
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
    
    private void cargarBuses() {
        modeloTabla.setRowCount(0);
        List<Vehiculo> buses = busDAO.listarTodos();
        
        for (Vehiculo bus : buses) {
            Object[] fila = {
                bus.getId(),
                bus.getPlaca(),
                bus.getMarca(),
                bus.getTipoVehiculo(),
                bus.getCapacidad()
            };
            modeloTabla.addRow(fila);
        }
    }
    
    private void mostrarDialogoNuevoBus() {
        JDialog dialog = new JDialog(this, "Nuevo Bus", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JTextField txtPlaca = new JTextField();
        JTextField txtMarca = new JTextField();
        JSpinner spnCapacidad = new JSpinner(new SpinnerNumberModel(40, 20, 100, 1));
        JComboBox<String> cmbTipo = new JComboBox<>(new String[]{"ESTANDAR", "DOS_PISOS"});
        
        panel.add(new JLabel("Placa:"));
        panel.add(txtPlaca);
        panel.add(new JLabel("Marca:"));
        panel.add(txtMarca);
        panel.add(new JLabel("Capacidad:"));
        panel.add(spnCapacidad);
        panel.add(new JLabel("Tipo:"));
        panel.add(cmbTipo);
        
        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.addActionListener(e -> {
            try {
                Vehiculo bus;
                String tipo = (String) cmbTipo.getSelectedItem();
                int capacidad = (Integer) spnCapacidad.getValue();
                
                if ("DOS_PISOS".equals(tipo)) {
                    bus = new BusDosPisos(txtPlaca.getText(), txtMarca.getText(), capacidad);
                } else {
                    bus = new BusEstandar(txtPlaca.getText(), txtMarca.getText(), capacidad);
                }
                
                if (busDAO.guardar(bus)) {
                    JOptionPane.showMessageDialog(this, "Bus registrado correctamente");
                    dialog.dispose();
                    cargarBuses();
                } else {
                    JOptionPane.showMessageDialog(this, "Error al registrar bus", "Error", JOptionPane.ERROR_MESSAGE);
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
    
    private void editarBus() {
        int fila = tablaBuses.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un bus para editar");
            return;
        }
        
        int id = (Integer) modeloTabla.getValueAt(fila, 0);
        Vehiculo bus = busDAO.buscarPorId(id);
        
        if (bus != null) {
            JDialog dialog = new JDialog(this, "Editar Bus", true);
            dialog.setSize(400, 250);
            dialog.setLocationRelativeTo(this);
            
            JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
            panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            JTextField txtPlaca = new JTextField(bus.getPlaca());
            JTextField txtMarca = new JTextField(bus.getMarca());
            JSpinner spnCapacidad = new JSpinner(new SpinnerNumberModel(bus.getCapacidad(), 20, 100, 1));
            
            panel.add(new JLabel("Placa:"));
            panel.add(txtPlaca);
            panel.add(new JLabel("Marca:"));
            panel.add(txtMarca);
            panel.add(new JLabel("Capacidad:"));
            panel.add(spnCapacidad);
            
            JButton btnGuardar = new JButton("Guardar");
            btnGuardar.addActionListener(e -> {
                bus.setPlaca(txtPlaca.getText());
                bus.setMarca(txtMarca.getText());
                bus.setCapacidad((Integer) spnCapacidad.getValue());
                
                if (busDAO.actualizar(bus)) {
                    JOptionPane.showMessageDialog(this, "Bus actualizado correctamente");
                    dialog.dispose();
                    cargarBuses();
                } else {
                    JOptionPane.showMessageDialog(this, "Error al actualizar bus", "Error", JOptionPane.ERROR_MESSAGE);
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
    }
    
    private void eliminarBus() {
        int fila = tablaBuses.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un bus para eliminar");
            return;
        }
        
        int id = (Integer) modeloTabla.getValueAt(fila, 0);
        int confirm = JOptionPane.showConfirmDialog(this, 
            "¿Está seguro de eliminar este bus?", 
            "Confirmar", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (busDAO.eliminar(id)) {
                JOptionPane.showMessageDialog(this, "Bus eliminado correctamente");
                cargarBuses();
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar bus", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}


