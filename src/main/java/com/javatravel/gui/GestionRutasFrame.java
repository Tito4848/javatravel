package com.javatravel.gui;

import com.javatravel.dao.RutaDAO;
import com.javatravel.model.Ruta;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class GestionRutasFrame extends JFrame {
    private JTable tablaRutas;
    private DefaultTableModel modeloTabla;
    private RutaDAO rutaDAO;
    private JFrame parent;
    
    public GestionRutasFrame(JFrame parent) {
        this.parent = parent;
        rutaDAO = new RutaDAO();
        initComponents();
        cargarRutas();
    }
    
    private void initComponents() {
        setTitle("Gestión de Rutas");
        setSize(700, 500);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Tabla
        String[] columnas = {"ID", "Origen", "Destino", "Precio Base", "Duración (horas)"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaRutas = new JTable(modeloTabla);
        JScrollPane scrollTabla = new JScrollPane(tablaRutas);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout());
        
        JButton btnNuevo = new JButton("Nueva Ruta");
        btnNuevo.addActionListener(e -> mostrarDialogoNuevaRuta());
        
        JButton btnEditar = new JButton("Editar");
        btnEditar.addActionListener(e -> editarRuta());
        
        JButton btnEliminar = new JButton("Eliminar");
        btnEliminar.addActionListener(e -> eliminarRuta());
        
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
    
    private void cargarRutas() {
        modeloTabla.setRowCount(0);
        List<Ruta> rutas = rutaDAO.listarTodos();
        
        for (Ruta ruta : rutas) {
            Object[] fila = {
                ruta.getId(),
                ruta.getOrigen(),
                ruta.getDestino(),
                String.format("S/. %.2f", ruta.getPrecioBase()),
                ruta.getDuracionHoras()
            };
            modeloTabla.addRow(fila);
        }
    }
    
    private void mostrarDialogoNuevaRuta() {
        JDialog dialog = new JDialog(this, "Nueva Ruta", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JTextField txtOrigen = new JTextField();
        JTextField txtDestino = new JTextField();
        JSpinner spnPrecio = new JSpinner(new SpinnerNumberModel(50.0, 10.0, 1000.0, 5.0));
        JSpinner spnDuracion = new JSpinner(new SpinnerNumberModel(4, 1, 24, 1));
        
        panel.add(new JLabel("Origen:"));
        panel.add(txtOrigen);
        panel.add(new JLabel("Destino:"));
        panel.add(txtDestino);
        panel.add(new JLabel("Precio Base:"));
        panel.add(spnPrecio);
        panel.add(new JLabel("Duración (horas):"));
        panel.add(spnDuracion);
        
        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.addActionListener(e -> {
            try {
                Ruta ruta = new Ruta(
                    txtOrigen.getText(),
                    txtDestino.getText(),
                    ((Double) spnPrecio.getValue()),
                    (Integer) spnDuracion.getValue()
                );
                
                if (rutaDAO.guardar(ruta)) {
                    JOptionPane.showMessageDialog(this, "Ruta registrada correctamente");
                    dialog.dispose();
                    cargarRutas();
                } else {
                    JOptionPane.showMessageDialog(this, "Error al registrar ruta", "Error", JOptionPane.ERROR_MESSAGE);
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
    
    private void editarRuta() {
        int fila = tablaRutas.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una ruta para editar");
            return;
        }
        
        int id = (Integer) modeloTabla.getValueAt(fila, 0);
        Ruta ruta = rutaDAO.buscarPorId(id);
        
        if (ruta != null) {
            JDialog dialog = new JDialog(this, "Editar Ruta", true);
            dialog.setSize(400, 300);
            dialog.setLocationRelativeTo(this);
            
            JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5));
            panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            JTextField txtOrigen = new JTextField(ruta.getOrigen());
            JTextField txtDestino = new JTextField(ruta.getDestino());
            JSpinner spnPrecio = new JSpinner(new SpinnerNumberModel(ruta.getPrecioBase(), 10.0, 1000.0, 5.0));
            JSpinner spnDuracion = new JSpinner(new SpinnerNumberModel(ruta.getDuracionHoras(), 1, 24, 1));
            
            panel.add(new JLabel("Origen:"));
            panel.add(txtOrigen);
            panel.add(new JLabel("Destino:"));
            panel.add(txtDestino);
            panel.add(new JLabel("Precio Base:"));
            panel.add(spnPrecio);
            panel.add(new JLabel("Duración (horas):"));
            panel.add(spnDuracion);
            
            JButton btnGuardar = new JButton("Guardar");
            btnGuardar.addActionListener(e -> {
                ruta.setOrigen(txtOrigen.getText());
                ruta.setDestino(txtDestino.getText());
                ruta.setPrecioBase((Double) spnPrecio.getValue());
                ruta.setDuracionHoras((Integer) spnDuracion.getValue());
                
                if (rutaDAO.actualizar(ruta)) {
                    JOptionPane.showMessageDialog(this, "Ruta actualizada correctamente");
                    dialog.dispose();
                    cargarRutas();
                } else {
                    JOptionPane.showMessageDialog(this, "Error al actualizar ruta", "Error", JOptionPane.ERROR_MESSAGE);
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
    
    private void eliminarRuta() {
        int fila = tablaRutas.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una ruta para eliminar");
            return;
        }
        
        int id = (Integer) modeloTabla.getValueAt(fila, 0);
        int confirm = JOptionPane.showConfirmDialog(this, 
            "¿Está seguro de eliminar esta ruta?", 
            "Confirmar", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (rutaDAO.eliminar(id)) {
                JOptionPane.showMessageDialog(this, "Ruta eliminada correctamente");
                cargarRutas();
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar ruta", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}


