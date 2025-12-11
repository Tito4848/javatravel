package com.javatravel.gui;

import com.javatravel.dao.PasajeroDAO;
import com.javatravel.model.Pasajero;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class GestionPasajerosFrame extends JFrame {
    private JTable tablaPasajeros;
    private DefaultTableModel modeloTabla;
    private PasajeroDAO pasajeroDAO;
    private JFrame parent;
    
    public GestionPasajerosFrame(JFrame parent) {
        this.parent = parent;
        pasajeroDAO = new PasajeroDAO();
        initComponents();
        cargarPasajeros();
    }
    
    private void initComponents() {
        setTitle("Gestión de Pasajeros");
        setSize(700, 500);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Tabla
        String[] columnas = {"ID", "DNI", "Nombre", "Edad", "Tipo"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaPasajeros = new JTable(modeloTabla);
        JScrollPane scrollTabla = new JScrollPane(tablaPasajeros);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout());
        
        JButton btnNuevo = new JButton("Nuevo Pasajero");
        btnNuevo.addActionListener(e -> mostrarDialogoNuevoPasajero());
        
        JButton btnEditar = new JButton("Editar");
        btnEditar.addActionListener(e -> editarPasajero());
        
        JButton btnEliminar = new JButton("Eliminar");
        btnEliminar.addActionListener(e -> eliminarPasajero());
        
        JButton btnBuscar = new JButton("Buscar por DNI");
        btnBuscar.addActionListener(e -> buscarPorDni());
        
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dispose());
        
        panelBotones.add(btnNuevo);
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnBuscar);
        panelBotones.add(btnCerrar);
        
        mainPanel.add(scrollTabla, BorderLayout.CENTER);
        mainPanel.add(panelBotones, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private void cargarPasajeros() {
        modeloTabla.setRowCount(0);
        List<Pasajero> pasajeros = pasajeroDAO.listarTodos();
        
        for (Pasajero pasajero : pasajeros) {
            String tipo = "";
            if (pasajero.esMenorDeEdad()) {
                tipo = "Menor de Edad";
            } else if (pasajero.esAdultoMayor()) {
                tipo = "Adulto Mayor";
            } else {
                tipo = "Adulto";
            }
            
            Object[] fila = {
                pasajero.getId(),
                pasajero.getDni(),
                pasajero.getNombre(),
                pasajero.getEdad(),
                tipo
            };
            modeloTabla.addRow(fila);
        }
    }
    
    private void mostrarDialogoNuevoPasajero() {
        JDialog dialog = new JDialog(this, "Nuevo Pasajero", true);
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JTextField txtDni = new JTextField();
        JTextField txtNombre = new JTextField();
        JSpinner spnEdad = new JSpinner(new SpinnerNumberModel(25, 1, 120, 1));
        
        panel.add(new JLabel("DNI (8 dígitos):"));
        panel.add(txtDni);
        panel.add(new JLabel("Nombre:"));
        panel.add(txtNombre);
        panel.add(new JLabel("Edad:"));
        panel.add(spnEdad);
        
        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.addActionListener(e -> {
            try {
                String dni = txtDni.getText().trim();
                String nombre = txtNombre.getText().trim();
                int edad = (Integer) spnEdad.getValue();
                
                if (dni.isEmpty() || nombre.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Complete todos los campos");
                    return;
                }
                
                if (dni.length() != 8 || !dni.matches("\\d+")) {
                    JOptionPane.showMessageDialog(this, "El DNI debe tener 8 dígitos numéricos");
                    return;
                }
                
                // Verificar si el DNI ya existe
                Pasajero existente = pasajeroDAO.buscarPorDni(dni);
                if (existente != null) {
                    JOptionPane.showMessageDialog(this, 
                        "El DNI " + dni + " ya está registrado.\n" +
                        "Pasajero: " + existente.getNombre(),
                        "DNI Duplicado", 
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                Pasajero pasajero = new Pasajero(dni, nombre, edad);
                
                if (pasajeroDAO.guardar(pasajero)) {
                    JOptionPane.showMessageDialog(this, "Pasajero registrado correctamente");
                    dialog.dispose();
                    cargarPasajeros();
                } else {
                    JOptionPane.showMessageDialog(this, "Error al registrar pasajero", "Error", JOptionPane.ERROR_MESSAGE);
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
    
    private void editarPasajero() {
        int fila = tablaPasajeros.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un pasajero para editar");
            return;
        }
        
        int id = (Integer) modeloTabla.getValueAt(fila, 0);
        Pasajero pasajero = pasajeroDAO.buscarPorId(id);
        
        if (pasajero != null) {
            JDialog dialog = new JDialog(this, "Editar Pasajero", true);
            dialog.setSize(400, 250);
            dialog.setLocationRelativeTo(this);
            
            JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
            panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            JTextField txtDni = new JTextField(pasajero.getDni());
            txtDni.setEditable(false); // DNI no se puede cambiar
            JTextField txtNombre = new JTextField(pasajero.getNombre());
            JSpinner spnEdad = new JSpinner(new SpinnerNumberModel(pasajero.getEdad(), 1, 120, 1));
            
            panel.add(new JLabel("DNI:"));
            panel.add(txtDni);
            panel.add(new JLabel("Nombre:"));
            panel.add(txtNombre);
            panel.add(new JLabel("Edad:"));
            panel.add(spnEdad);
            
            JButton btnGuardar = new JButton("Guardar");
            btnGuardar.addActionListener(e -> {
                pasajero.setNombre(txtNombre.getText().trim());
                pasajero.setEdad((Integer) spnEdad.getValue());
                
                if (pasajero.getNombre().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "El nombre no puede estar vacío");
                    return;
                }
                
                if (pasajeroDAO.actualizar(pasajero)) {
                    JOptionPane.showMessageDialog(this, "Pasajero actualizado correctamente");
                    dialog.dispose();
                    cargarPasajeros();
                } else {
                    JOptionPane.showMessageDialog(this, "Error al actualizar pasajero", "Error", JOptionPane.ERROR_MESSAGE);
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
    
    private void eliminarPasajero() {
        int fila = tablaPasajeros.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un pasajero para eliminar");
            return;
        }
        
        int id = (Integer) modeloTabla.getValueAt(fila, 0);
        Pasajero pasajero = pasajeroDAO.buscarPorId(id);
        
        if (pasajero != null) {
            int confirm = JOptionPane.showConfirmDialog(this, 
                "¿Está seguro de eliminar al pasajero:\n" +
                pasajero.getNombre() + " (DNI: " + pasajero.getDni() + ")?\n\n" +
                "NOTA: Si tiene boletos asociados, la eliminación puede fallar.", 
                "Confirmar Eliminación", 
                JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                if (pasajeroDAO.eliminar(id)) {
                    JOptionPane.showMessageDialog(this, "Pasajero eliminado correctamente");
                    cargarPasajeros();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Error al eliminar pasajero.\n" +
                        "Puede tener boletos asociados.", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    private void buscarPorDni() {
        String dni = JOptionPane.showInputDialog(this, 
            "Ingrese el DNI a buscar (8 dígitos):", 
            "Buscar Pasajero", 
            JOptionPane.QUESTION_MESSAGE);
        
        if (dni != null && !dni.trim().isEmpty()) {
            Pasajero pasajero = pasajeroDAO.buscarPorDni(dni.trim());
            if (pasajero != null) {
                // Seleccionar la fila en la tabla
                for (int i = 0; i < modeloTabla.getRowCount(); i++) {
                    if (modeloTabla.getValueAt(i, 0).equals(pasajero.getId())) {
                        tablaPasajeros.setRowSelectionInterval(i, i);
                        tablaPasajeros.scrollRectToVisible(tablaPasajeros.getCellRect(i, 0, true));
                        break;
                    }
                }
                JOptionPane.showMessageDialog(this, 
                    "Pasajero encontrado:\n" +
                    "Nombre: " + pasajero.getNombre() + "\n" +
                    "DNI: " + pasajero.getDni() + "\n" +
                    "Edad: " + pasajero.getEdad(),
                    "Resultado de Búsqueda",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "No se encontró ningún pasajero con DNI: " + dni,
                    "No Encontrado",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
}

