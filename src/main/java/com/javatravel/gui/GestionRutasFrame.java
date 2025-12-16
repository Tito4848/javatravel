package com.javatravel.gui;

import com.javatravel.dao.RutaDAO;
import com.javatravel.model.Ruta;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
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
        
        
        String[] columnas = {"ID", "Origen", "Destino", "Precio Base", "Duración (horas)", "Fecha", "Hora"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaRutas = new JTable(modeloTabla);
        JScrollPane scrollTabla = new JScrollPane(tablaRutas);
        
        
        JPanel panelBotones = new JPanel(new FlowLayout());
        
        JButton btnNuevo = new JButton("Nueva Ruta");
        btnNuevo.addActionListener(e -> mostrarDialogoNuevaRuta());
        
        JButton btnEditar = new JButton("Editar");
        btnEditar.addActionListener(e -> editarRuta());
        
        JButton btnEliminar = new JButton("Eliminar");
        btnEliminar.addActionListener(e -> eliminarRuta());

        JButton btnProgramarViaje = new JButton("Programar Viaje");
        btnProgramarViaje.addActionListener(e -> programarViaje());
        
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dispose());
        
        panelBotones.add(btnNuevo);
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnProgramarViaje);
        panelBotones.add(btnCerrar);
        
        mainPanel.add(scrollTabla, BorderLayout.CENTER);
        mainPanel.add(panelBotones, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private void cargarRutas() {
        modeloTabla.setRowCount(0);
        List<Ruta> rutas = rutaDAO.listarTodos();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        
        for (Ruta ruta : rutas) {
            Object[] fila = {
                ruta.getId(),
                ruta.getOrigen(),
                ruta.getDestino(),
                String.format("S/. %.2f", ruta.getPrecioBase()),
                ruta.getDuracionHoras(),
                ruta.getFecha().format(dateFormatter),
                ruta.getHoraSalida().format(timeFormatter)
            };
            modeloTabla.addRow(fila);
        }
    }
    
    private void mostrarDialogoNuevaRuta() {
        JDialog dialog = new JDialog(this, "Nueva Ruta", true);
        dialog.setSize(420, 360);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridLayout(7, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JTextField txtOrigen = new JTextField();
        JTextField txtDestino = new JTextField();
        JSpinner spnPrecio = new JSpinner(new SpinnerNumberModel(50.0, 10.0, 1000.0, 5.0));
        JSpinner spnDuracion = new JSpinner(new SpinnerNumberModel(4, 1, 24, 1));
        
        
        Calendar calFecha = Calendar.getInstance();
        calFecha.set(Calendar.HOUR_OF_DAY, 0);
        calFecha.set(Calendar.MINUTE, 0);
        calFecha.set(Calendar.SECOND, 0);
        calFecha.set(Calendar.MILLISECOND, 0);
        JSpinner spnFecha = new JSpinner(new SpinnerDateModel(calFecha.getTime(), null, null, Calendar.DAY_OF_MONTH));
        JSpinner.DateEditor editorFecha = new JSpinner.DateEditor(spnFecha, "dd/MM/yyyy");
        spnFecha.setEditor(editorFecha);
        
        
        Calendar calHora = Calendar.getInstance();
        calHora.set(Calendar.HOUR_OF_DAY, 8);
        calHora.set(Calendar.MINUTE, 0);
        calHora.set(Calendar.SECOND, 0);
        calHora.set(Calendar.MILLISECOND, 0);
        JSpinner spnHora = new JSpinner(new SpinnerDateModel(calHora.getTime(), null, null, Calendar.HOUR_OF_DAY));
        JSpinner.DateEditor editorHora = new JSpinner.DateEditor(spnHora, "HH:mm");
        spnHora.setEditor(editorHora);
        
        panel.add(new JLabel("Origen:"));
        panel.add(txtOrigen);
        panel.add(new JLabel("Destino:"));
        panel.add(txtDestino);
        panel.add(new JLabel("Precio Base:"));
        panel.add(spnPrecio);
        panel.add(new JLabel("Duración (horas):"));
        panel.add(spnDuracion);
        panel.add(new JLabel("Fecha:"));
        panel.add(spnFecha);
        panel.add(new JLabel("Hora:"));
        panel.add(spnHora);
        
        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.addActionListener(e -> {
            try {
                java.util.Date fecha = (java.util.Date) spnFecha.getValue();
                java.util.Date hora = (java.util.Date) spnHora.getValue();
                
                Ruta ruta = new Ruta(
                    txtOrigen.getText(),
                    txtDestino.getText(),
                    ((Double) spnPrecio.getValue()),
                    (Integer) spnDuracion.getValue(),
                    new java.sql.Date(fecha.getTime()).toLocalDate(),
                    new java.sql.Time(hora.getTime()).toLocalTime()
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
            dialog.setSize(420, 360);
            dialog.setLocationRelativeTo(this);
            
            JPanel panel = new JPanel(new GridLayout(7, 2, 5, 5));
            panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            JTextField txtOrigen = new JTextField(ruta.getOrigen());
            JTextField txtDestino = new JTextField(ruta.getDestino());
            JSpinner spnPrecio = new JSpinner(new SpinnerNumberModel(ruta.getPrecioBase(), 10.0, 1000.0, 5.0));
            JSpinner spnDuracion = new JSpinner(new SpinnerNumberModel(ruta.getDuracionHoras(), 1, 24, 1));
            
            Calendar calFecha = Calendar.getInstance();
            calFecha.set(ruta.getFecha().getYear(), ruta.getFecha().getMonthValue() - 1, ruta.getFecha().getDayOfMonth());
            calFecha.set(Calendar.HOUR_OF_DAY, 0);
            calFecha.set(Calendar.MINUTE, 0);
            calFecha.set(Calendar.SECOND, 0);
            calFecha.set(Calendar.MILLISECOND, 0);
            JSpinner spnFecha = new JSpinner(new SpinnerDateModel(calFecha.getTime(), null, null, Calendar.DAY_OF_MONTH));
            JSpinner.DateEditor editorFecha = new JSpinner.DateEditor(spnFecha, "dd/MM/yyyy");
            spnFecha.setEditor(editorFecha);
            
            Calendar calHora = Calendar.getInstance();
            calHora.set(Calendar.HOUR_OF_DAY, ruta.getHoraSalida().getHour());
            calHora.set(Calendar.MINUTE, ruta.getHoraSalida().getMinute());
            calHora.set(Calendar.SECOND, 0);
            calHora.set(Calendar.MILLISECOND, 0);
            JSpinner spnHora = new JSpinner(new SpinnerDateModel(calHora.getTime(), null, null, Calendar.HOUR_OF_DAY));
            JSpinner.DateEditor editorHora = new JSpinner.DateEditor(spnHora, "HH:mm");
            spnHora.setEditor(editorHora);
            
            panel.add(new JLabel("Origen:"));
            panel.add(txtOrigen);
            panel.add(new JLabel("Destino:"));
            panel.add(txtDestino);
            panel.add(new JLabel("Precio Base:"));
            panel.add(spnPrecio);
            panel.add(new JLabel("Duración (horas):"));
            panel.add(spnDuracion);
            panel.add(new JLabel("Fecha:"));
            panel.add(spnFecha);
            panel.add(new JLabel("Hora:"));
            panel.add(spnHora);
            
            JButton btnGuardar = new JButton("Guardar");
            btnGuardar.addActionListener(e -> {
                ruta.setOrigen(txtOrigen.getText());
                ruta.setDestino(txtDestino.getText());
                ruta.setPrecioBase((Double) spnPrecio.getValue());
                ruta.setDuracionHoras((Integer) spnDuracion.getValue());
                
                java.util.Date fecha = (java.util.Date) spnFecha.getValue();
                java.util.Date hora = (java.util.Date) spnHora.getValue();
                ruta.setFecha(new java.sql.Date(fecha.getTime()).toLocalDate());
                ruta.setHoraSalida(new java.sql.Time(hora.getTime()).toLocalTime());
                
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

    private void programarViaje() {
        int fila = tablaRutas.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una ruta para programar un viaje");
            return;
        }

        int id = (Integer) modeloTabla.getValueAt(fila, 0);
        Ruta ruta = rutaDAO.buscarPorId(id);
        if (ruta == null) {
            JOptionPane.showMessageDialog(this, "No se pudo cargar la ruta seleccionada");
            return;
        }

        
        GestionViajesFrame viajesFrame = new GestionViajesFrame(this);
        viajesFrame.setVisible(true);
        viajesFrame.programarViajeParaRuta(ruta);
    }
}


