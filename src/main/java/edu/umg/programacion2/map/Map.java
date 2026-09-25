package edu.umg.programacion2.map;

import edu.umg.programacion2.dao.CitaDAO;
import edu.umg.programacion2.modelo.Cita;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CitaFrame extends JFrame {
    private final CitaDAO citaDAO = new CitaDAO();
    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField txtId;
    private JTextField txtCliente;
    private JTextField txtFecha;
    private JTextField txtHora;
    private JTextField txtServicio;
    private JTextField txtDuracion;
    private JComboBox<String> cbEstado;
    private JCheckBox chkconfirmacion;

    public CitaFrame() {
        setTitle("Gestión de citas");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        initComponents();
        cargarDatosTabla();
    }

    private void initComponents() {
        JPanel panelForm = new JPanel(new GridLayout(9, 2, 5, 5));
        panelForm.setBorder(BorderFactory.createTitledBorder("Datos de la Cita"));

        panelForm.add(new JLabel("ID:"));
        txtId = new JTextField();
        txtId.setEditable(false);
        panelForm.add(txtId);

        panelForm.add(new JLabel("Cliente (máx 35):"));
        txtCliente = new JTextField();
        panelForm.add(txtCliente);

        panelForm.add(new JLabel("Fecha (YYYY-MM-DD):"));
        txtFecha = new JTextField();
        panelForm.add(txtFecha);

        panelForm.add(new JLabel("Hora (HH:MM):"));
        txtHora = new JTextField();
        panelForm.add(txtHora);

        panelForm.add(new JLabel("Servicio (máx 35):"));
        txtServicio = new JTextField();
        panelForm.add(txtServicio);

        panelForm.add(new JLabel("Duración (min):"));
        txtDuracion = new JTextField();
        panelForm.add(txtDuracion);

        panelForm.add(new JLabel("Estado:"));
        cbEstado = new JComboBox<>(new String[]{"pendiente", "confirmada", "cancelada"});
        panelForm.add(cbEstado);

        panelForm.add(new JLabel("Llamada de Confirmación:"));
        chkconfirmacion = new JCheckBox("Requiere confirmación por llamada");
        panelForm.add(chkconfirmacion);

        JButton btnLimpiar = new JButton("Limpiar / Nuevo");
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        panelForm.add(btnLimpiar);

        add(panelForm, BorderLayout.WEST);

        String[] columnas = {"ID", "Cliente", "Fecha", "Hora", "Servicio", "Duración", "Estado", "Confirmación"};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.getSelectionModel().addListSelectionListener(e -> seleccionarFilaTabla());
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel panelBotones = new JPanel();

        JButton btnGuardar = new JButton("Guardar Cita");
        btnGuardar.addActionListener(e -> guardarCita());
        panelBotones.add(btnGuardar);

        JButton btnEditar = new JButton("Actualizar Cita");
        btnEditar.addActionListener(e -> actualizarCita());
        panelBotones.add(btnEditar);

        JButton btnEliminar = new JButton("Eliminar Cita");
        btnEliminar.addActionListener(e -> eliminarCita());
        panelBotones.add(btnEliminar);

        JButton btnVerConteoServicios = new JButton("Ver Citas por Servicio");
        btnVerConteoServicios.addActionListener(e -> mostrarConteoPorServicio());
        panelBotones.add(btnVerConteoServicios);

        add(panelBotones, BorderLayout.SOUTH);
    }

    private void cargarDatosTabla() {
        tableModel.setRowCount(0);
        List<Cita> lista = citaDAO.listar();
        for (Cita c : lista) {
            tableModel.addRow(new Object[]{
                    c.getId(),
                    c.getCliente(),
                    c.getFecha(),
                    c.getHora(),
                    c.getServicio(),
                    c.getDuracionMinutos() + " min",
                    c.getEstado(),
                    c.getconfirmacion() ? "Sí" : "No"
            });
        }
    }

    private void guardarCita() {
        try {
            String cliente = txtCliente.getText().trim();
            String servicio = txtServicio.getText().trim();

            if (cliente.isEmpty() || servicio.isEmpty()) {
                JOptionPane.showMessageDialog(this, "El cliente y el servicio no pueden estar vacíos.");
                return;
            }
            if (cliente.length() > 35 || servicio.length() > 35) {
                JOptionPane.showMessageDialog(this, "El cliente y el servicio no deben superar los 35 caracteres.");
                return;
            }

            LocalDate fecha = LocalDate.parse(txtFecha.getText().trim());
            LocalTime hora = LocalTime.parse(txtHora.getText().trim());

            if (LocalDateTime.of(fecha, hora).isBefore(LocalDateTime.now())) {
                JOptionPane.showMessageDialog(this, "La fecha y hora no pueden ser pasadas.");
                return;
            }

            int duracion = Integer.parseInt(txtDuracion.getText().trim());
            if (duracion <= 0) {
                JOptionPane.showMessageDialog(this, "La duración debe ser mayor a 0 minutos.");
                return;
            }

            String estado = (String) cbEstado.getSelectedItem();
            boolean confirmacion = chkconfirmacion.isSelected();

            Cita nuevaCita = new Cita(0, cliente, fecha, hora, servicio, duracion, estado, confirmacion);
            if (citaDAO.agregar(nuevaCita)) {
                JOptionPane.showMessageDialog(this, "Cita guardada correctamente.");
                cargarDatosTabla();
                limpiarFormulario();
            } else {
                JOptionPane.showMessageDialog(this, "Error al guardar la cita en la base de datos.");
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error de validación: Revise los formatos ingresados.");
        }
    }

    private void actualizarCita() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione una cita de la tabla para editar.");
            return;
        }

        try {
            int id = Integer.parseInt(txtId.getText());
            String cliente = txtCliente.getText().trim();
            String servicio = txtServicio.getText().trim();

            if (cliente.isEmpty() || servicio.isEmpty() || cliente.length() > 35 || servicio.length() > 35) {
                JOptionPane.showMessageDialog(this, "Valide que los campos no estén vacíos ni superen 35 caracteres.");
                return;
            }

            LocalDate fecha = LocalDate.parse(txtFecha.getText().trim());
            LocalTime hora = LocalTime.parse(txtHora.getText().trim());
            int duracion = Integer.parseInt(txtDuracion.getText().trim());
            String estado = (String) cbEstado.getSelectedItem();
            boolean confirmacion = chkconfirmacion.isSelected();

            Cita citaEdit = new Cita(id, cliente, fecha, hora, servicio, duracion, estado, confirmacion);
            if (citaDAO.actualizar(citaEdit)) {
                JOptionPane.showMessageDialog(this, "Cita actualizada correctamente.");
                cargarDatosTabla();
                limpiarFormulario();
            } else {
                JOptionPane.showMessageDialog(this, "Error al actualizar la cita.");
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error de datos: Verifique el formato de los campos.");
        }
    }

    private void eliminarCita() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione una cita de la tabla.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "¿Está seguro de eliminar esta cita?", "Confirmación de borrado", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int id = Integer.parseInt(txtId.getText());
            if (citaDAO.eliminar(id)) {
                JOptionPane.showMessageDialog(this, "Cita eliminada correctamente.");
                cargarDatosTabla();
                limpiarFormulario();
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar la cita.");
            }
        }
    }

    private void mostrarConteoPorServicio() {
        List<Cita> lista = citaDAO.listar();
        if (lista.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay citas registradas para agrupar.");
            return;
        }

        Map<String, Integer> conteoPorServicio = new HashMap<>();
        for (Cita c : lista) {
            String claveServicio = c.getServicio().trim().toLowerCase();
            conteoPorServicio.put(claveServicio, conteoPorServicio.getOrDefault(claveServicio, 0) + 1);
        }

        StringBuilder sb = new StringBuilder("Citas por Servicio\n\n");
        for (Map.Entry<String, Integer> entry : conteoPorServicio.entrySet()) {
            String nombreServicio = entry.getKey().substring(0, 1).toUpperCase() + entry.getKey().substring(1);
            sb.append("• Servicio: ").append(nombreServicio)
              .append(" => ").append(entry.getValue()).append(" cita(s)\n");
        }

        JOptionPane.showMessageDialog(this, sb.toString(), "Resumen de Categorías", JOptionPane.INFORMATION_MESSAGE);
    }

    private void seleccionarFilaTabla() {
        int fila = table.getSelectedRow();
        if (fila >= 0) {
            txtId.setText(tableModel.getValueAt(fila, 0).toString());
            txtCliente.setText(tableModel.getValueAt(fila, 1).toString());
            txtFecha.setText(tableModel.getValueAt(fila, 2).toString());
            txtHora.setText(tableModel.getValueAt(fila, 3).toString());
            txtServicio.setText(tableModel.getValueAt(fila, 4).toString());

            String duracionTexto = tableModel.getValueAt(fila, 5).toString().replace(" min", "").trim();
            txtDuracion.setText(duracionTexto);

            cbEstado.setSelectedItem(tableModel.getValueAt(fila, 6).toString());

            boolean requiere = "Sí".equalsIgnoreCase(tableModel.getValueAt(fila, 7).toString());
            chkconfirmacion.setSelected(requiere);
        }
    }

    private void limpiarFormulario() {
        txtId.setText("");
        txtCliente.setText("");
        txtFecha.setText("");
        txtHora.setText("");
        txtServicio.setText("");
        txtDuracion.setText("");
        cbEstado.setSelectedIndex(0);
        chkconfirmacion.setSelected(false);
        table.clearSelection();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CitaFrame().setVisible(true));
    }
}