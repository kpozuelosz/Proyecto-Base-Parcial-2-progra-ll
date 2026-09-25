package edu.umg.programacion2.modelo;
import java.time.LocalDate;
import java.time.LocalTime;

public class Cita {
    private int id;
    private String cliente;
    private LocalDate fecha;
    private LocalTime hora;
    private String servicio;
    private int duracionMinutos;
    private String estado; 
    private boolean confirmacion;

    public Cita() {}

    public Cita(int id, String cliente, LocalDate fecha, LocalTime hora, String servicio, int duracionMinutos, String estado, boolean confirmacion) {
        this.id = id;
        this.cliente = cliente;
        this.fecha = fecha;
        this.hora = hora;
        this.servicio = servicio;
        this.duracionMinutos = duracionMinutos;
        this.estado = estado;
        this.confirmacion = confirmacion;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCliente() { return cliente; }
    public void setCliente(String cliente) { this.cliente = cliente; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public LocalTime getHora() { return hora; }
    public void setHora(LocalTime hora) { this.hora = hora; }

    public String getServicio() { return servicio; }
    public void setServicio(String servicio) { this.servicio = servicio; }

    public int getDuracionMinutos() { return duracionMinutos; }
    public void setDuracionMinutos(int duracionMinutos) { this.duracionMinutos = duracionMinutos; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    
    public boolean getconfirmacion() { return confirmacion; }
    public void setconfirmacion(boolean confirmacion) { this.confirmacion = confirmacion; }
}