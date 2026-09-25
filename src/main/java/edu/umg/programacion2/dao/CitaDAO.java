package edu.umg.programacion2.dao;
import edu.umg.programacion2.conexion.Conexion;
import edu.umg.programacion2.modelo.Cita;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CitaDAO {

    public List<Cita> listar() {
        List<Cita> citas = new ArrayList<>();
        String sql = "SELECT id, cliente, fecha, hora, servicio, duracion_minutos, estado, confirmacion FROM citas";

        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Cita cita = new Cita(
                    rs.getInt("id"),
                    rs.getString("cliente"),
                    rs.getDate("fecha").toLocalDate(),
                    rs.getTime("hora").toLocalTime(),
                    rs.getString("servicio"),
                    rs.getInt("duracion_minutos"),
                    rs.getString("estado"),
                    rs.getBoolean("confirmacion")
                );
                citas.add(cita);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar citas: " + e.getMessage());
        }
        return citas;
    }

    public Cita obtenerPorId(int id) {
        String sql = "SELECT id, cliente, fecha, hora, servicio, duracion_minutos, estado, confirmacion FROM citas WHERE id = ?";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Cita(
                        rs.getInt("id"),
                        rs.getString("cliente"),
                        rs.getDate("fecha").toLocalDate(),
                        rs.getTime("hora").toLocalTime(),
                        rs.getString("servicio"),
                        rs.getInt("duracion_minutos"),
                        rs.getString("estado"),
                        rs.getBoolean("confirmacion")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener cita: " + e.getMessage());
        }
        return null;
    }

    public boolean agregar(Cita cita) {
        String sql = "INSERT INTO citas (cliente, fecha, hora, servicio, duracion_minutos, estado, confirmacion) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, cita.getCliente());
            ps.setDate(2, Date.valueOf(cita.getFecha()));
            ps.setTime(3, Time.valueOf(cita.getHora()));
            ps.setString(4, cita.getServicio());
            ps.setInt(5, cita.getDuracionMinutos());
            ps.setString(6, cita.getEstado());
            ps.setBoolean(7, cita.getconfirmacion());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al agregar cita: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizar(Cita cita) {
        String sql = "UPDATE citas SET cliente = ?, fecha = ?, hora = ?, servicio = ?, duracion_minutos = ?, estado = ?, confirmacion = ? WHERE id = ?";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, cita.getCliente());
            ps.setDate(2, Date.valueOf(cita.getFecha()));
            ps.setTime(3, Time.valueOf(cita.getHora()));
            ps.setString(4, cita.getServicio());
            ps.setInt(5, cita.getDuracionMinutos());
            ps.setString(6, cita.getEstado());
            ps.setBoolean(7, cita.getconfirmacion());
            ps.setInt(8, cita.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar cita: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM citas WHERE id = ?";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar cita: " + e.getMessage());
            return false;
        }
    }
}