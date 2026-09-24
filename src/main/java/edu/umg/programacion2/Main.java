package edu.umg.programacion2;
import edu.umg.programacion2.dao.CitaDAO;
import edu.umg.programacion2.modelo.Cita;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final CitaDAO citaDAO = new CitaDAO();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        int opcion;
        do {
            System.out.println("\n=== AGENDA DE CITAS ===");
            System.out.println("1. Ver listado de citas");
            System.out.println("2. Agendar nueva cita");
            System.out.println("3. Editar cita existente");
            System.out.println("4. Eliminar cita");
            System.out.println("5. Salir");
            System.out.print("Seleccione una opción: ");

            try {
                opcion = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                opcion = 0;
            }

            switch (opcion) {
                case 1 -> listarCitas();
                case 2 -> agendarCita();
                case 3 -> editarCita();
                case 4 -> eliminarCita();
                case 5 -> System.out.println("¡Hasta luego!");
                default -> System.out.println("Opción no válida.");
            }
        } while (opcion != 5);
    }

    private static void listarCitas() {
        List<Cita> citas = citaDAO.listar();
        System.out.println("\nListado:");
        if (citas.isEmpty()) {
            System.out.println("No hay citas registradas.");
            return;
        }
        for (Cita c : citas) {
            System.out.printf("[%d] %-25s | %s %s | %-20s | %2d min | %s%n",
                    c.getId(),
                    c.getCliente(),
                    c.getFecha(),
                    c.getHora(),
                    c.getServicio(),
                    c.getDuracionMinutos(),
                    capitalizar(c.getEstado()));
        }
    }

    private static void agendarCita() {
        System.out.println("\n--- Nueva Cita ---");

        String cliente = solicitarTexto("Cliente (máx 35 chars): ");
        LocalDate fecha = solicitarFecha();
        LocalTime hora = solicitarHora();

        if (LocalDateTime.of(fecha, hora).isBefore(LocalDateTime.now())) {
            System.out.println("Error: La fecha y hora no pueden ser anteriores al momento actual.");
            return;
        }

        String servicio = solicitarTexto("Servicio (máx 35 chars): ");
        int duracion = solicitarEnteroPositivo("Duración estimada (min): ");

        Cita nuevaCita = new Cita(0, cliente, fecha, hora, servicio, duracion, "pendiente");
        if (citaDAO.agregar(nuevaCita)) {
            System.out.println("Cita agendada con estado: Pendiente");
        } else {
            System.out.println("Error al agendar la cita.");
        }
    }

    private static void editarCita() {
        System.out.println("\n--- Editar Cita ---");
        System.out.print("Ingrese el ID de la cita a editar: ");
        int id;
        try {
            id = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("ID no válido.");
            return;
        }

        Cita cita = citaDAO.obtenerPorId(id);
        if (cita == null) {
            System.out.println("No se encontró la cita con ID " + id);
            return;
        }

        String cliente = solicitarTexto("Cliente (" + cita.getCliente() + "): ");
        LocalDate fecha = solicitarFecha();
        LocalTime hora = solicitarHora();

        if (LocalDateTime.of(fecha, hora).isBefore(LocalDateTime.now())) {
            System.out.println("Error: La fecha y hora no pueden ser anteriores al momento actual.");
            return;
        }

        String servicio = solicitarTexto("Servicio (" + cita.getServicio() + "): ");
        int duracion = solicitarEnteroPositivo("Duración estimada min (" + cita.getDuracionMinutos() + "): ");
        String estado = solicitarEstado("Estado (1: pendiente, 2: confirmada, 3: cancelada): ");

        cita.setCliente(cliente);
        cita.setFecha(fecha);
        cita.setHora(hora);
        cita.setServicio(servicio);
        cita.setDuracionMinutos(duracion);
        cita.setEstado(estado);

        if (citaDAO.actualizar(cita)) {
            System.out.println("Cita actualizada exitosamente.");
        } else {
            System.out.println("Error al actualizar la cita.");
        }
    }

    private static void eliminarCita() {
        System.out.println("\n--- Eliminar Cita ---");
        System.out.print("Ingrese el ID de la cita a eliminar: ");
        int id;
        try {
            id = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("ID no válido.");
            return;
        }

        Cita cita = citaDAO.obtenerPorId(id);
        if (cita == null) {
            System.out.println("No se encontró la cita con ID " + id);
            return;
        }

        System.out.printf("¿Está seguro de eliminar la cita de %s programada para %s %s? (s/n): ",
                cita.getCliente(), cita.getFecha(), cita.getHora());
        String confirmacion = scanner.nextLine().trim().toLowerCase();

        if (confirmacion.equals("s")) {
            if (citaDAO.eliminar(id)) {
                System.out.println("Cita eliminada correctamente.");
            } else {
                System.out.println("Error al eliminar la cita.");
            }
        } else {
            System.out.println("Operación cancelada.");
        }
    }

    private static String solicitarTexto(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String entrada = scanner.nextLine().trim();
            if (entrada.isEmpty()) {
                System.out.println("Este campo no puede quedar vacío.");
            } else if (entrada.length() > 35) {
                System.out.println("El texto no puede exceder los 35 caracteres.");
            } else {
                return entrada;
            }
        }
    }

    private static LocalDate solicitarFecha() {
        while (true) {
            System.out.print("Fecha (YYYY-MM-DD): ");
            try {
                return LocalDate.parse(scanner.nextLine().trim());
            } catch (DateTimeParseException e) {
                System.out.println("Formato de fecha inválido. Use YYYY-MM-DD.");
            }
        }
    }

    private static LocalTime solicitarHora() {
        while (true) {
            System.out.print("Hora (HH:MM): ");
            try {
                return LocalTime.parse(scanner.nextLine().trim());
            } catch (DateTimeParseException e) {
                System.out.println("Formato de hora inválido. Use HH:MM.");
            }
        }
    }

    private static int solicitarEnteroPositivo(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            try {
                int valor = Integer.parseInt(scanner.nextLine().trim());
                if (valor > 0) return valor;
                System.out.println("La duración debe ser mayor a 0 minutos.");
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un número entero válido.");
            }
        }
    }

    private static String solicitarEstado(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String op = scanner.nextLine().trim();
            switch (op) {
                case "1" -> { return "pendiente"; }
                case "2" -> { return "confirmada"; }
                case "3" -> { return "cancelada"; }
                default -> System.out.println("Opción inválida. Seleccione 1, 2 o 3.");
            }
        }
    }

    private static String capitalizar(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}