package presentacion.view;

import dominio.modelo.Confirmacion;

import java.util.List;
import java.util.Scanner;

/**
 * Vista para gestionar las confirmaciones de asistencia a scrims.
 * 
 * Vista simple que solo solicita datos y muestra resultados.
 * El controller maneja toda la lógica.
 * 
 * @author eScrims Team
 */
public class ConfirmacionView {
    private final Scanner scanner;

    public ConfirmacionView() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Solicita el ID de un scrim al usuario.
     */
    public String solicitarIdScrim() {
        System.out.print("\nIngrese el ID del scrim: ");
        return scanner.nextLine().trim();
    }

    /**
     * Confirma si el usuario realmente desea confirmar su asistencia.
     */
    public boolean confirmarAsistenciaUsuario() {
        System.out.println("\n=== CONFIRMAR ASISTENCIA ===");
        System.out.println("¿Confirma que asistirá a este scrim?");
        System.out.print("Confirmar (S/N): ");
        String respuesta = scanner.nextLine().trim().toUpperCase();
        return respuesta.equals("S") || respuesta.equals("SI") || respuesta.equals("YES");
    }

    /**
     * Confirma si el usuario realmente desea rechazar su asistencia.
     */
    public boolean confirmarRechazoUsuario() {
        System.out.println("\n=== RECHAZAR ASISTENCIA ===");
        System.out.println(
                "⚠️  ADVERTENCIA: Si rechazas la asistencia, el slot quedará disponible para otros jugadores.");
        System.out.print("¿Estás seguro? (S/N): ");
        String respuesta = scanner.nextLine().trim().toUpperCase();
        return respuesta.equals("S") || respuesta.equals("SI") || respuesta.equals("YES");
    }

    /**
     * Muestra el detalle de una confirmación específica.
     */
    public void mostrarDetalleConfirmacion(Confirmacion confirmacion) {
        System.out.println("\n=== DETALLE DE CONFIRMACIÓN ===");
        System.out.println("Usuario:         " + confirmacion.getUserId());
        System.out.println("Estado:          " + confirmacion.getEstado());
        System.out.println("Fecha solicitud: " + confirmacion.getFechaSolicitud());

        if (confirmacion.getFechaRespuesta() != null) {
            System.out.println("Fecha respuesta: " + confirmacion.getFechaRespuesta());
        }
    }

    /**
     * Muestra una lista de confirmaciones con estadísticas.
     */
    public void mostrarListaConfirmaciones(List<Confirmacion> confirmaciones) {
        if (confirmaciones.isEmpty()) {
            System.out.println("\nNo hay confirmaciones generadas todavía");
            System.out.println("Las confirmaciones se generan automáticamente cuando el lobby está completo");
            return;
        }

        long confirmadas = confirmaciones.stream()
                .filter(c -> c.getEstado() == Confirmacion.EstadoConfirmacion.CONFIRMADA)
                .count();
        long pendientes = confirmaciones.stream()
                .filter(c -> c.getEstado() == Confirmacion.EstadoConfirmacion.PENDIENTE)
                .count();
        long rechazadas = confirmaciones.stream()
                .filter(c -> c.getEstado() == Confirmacion.EstadoConfirmacion.RECHAZADA)
                .count();

        System.out.println("\n=== ESTADO DE CONFIRMACIONES ===");
        System.out.println("\nResumen:");
        System.out.println("- Confirmadas: " + confirmadas);
        System.out.println("- Pendientes:  " + pendientes);
        System.out.println("- Rechazadas:  " + rechazadas);
        System.out.println("- Total:       " + confirmaciones.size());

        System.out.println("\nDetalle:");
        for (int i = 0; i < confirmaciones.size(); i++) {
            Confirmacion c = confirmaciones.get(i);
            System.out.println("\n" + (i + 1) + ". Usuario: " + c.getUserId());
            System.out.println("   Estado: " + c.getEstado());
            System.out.println("   Fecha solicitud: " + c.getFechaSolicitud());

            if (c.getEstado() == Confirmacion.EstadoConfirmacion.CONFIRMADA) {
                System.out.println("   ✓ Confirmó el: " + c.getFechaRespuesta());
            } else if (c.getEstado() == Confirmacion.EstadoConfirmacion.RECHAZADA) {
                System.out.println("   ✗ Rechazó el: " + c.getFechaRespuesta());
            } else {
                System.out.println("   ⏳ Esperando respuesta...");
            }
        }

        if (confirmadas == confirmaciones.size()) {
            System.out.println("\n✅ ¡Todos los jugadores confirmaron! El scrim está listo para comenzar.");
        } else if (pendientes > 0) {
            System.out.println("\n⏳ Esperando confirmación de " + pendientes + " jugador(es)...");
        }
    }

    /**
     * Muestra solo las confirmaciones pendientes.
     */
    public void mostrarConfirmacionesPendientes(List<Confirmacion> pendientes) {
        if (pendientes.isEmpty()) {
            System.out.println("\nNo hay confirmaciones pendientes");
            return;
        }

        System.out.println("\n=== CONFIRMACIONES PENDIENTES ===");
        for (int i = 0; i < pendientes.size(); i++) {
            Confirmacion c = pendientes.get(i);
            System.out.println("\n" + (i + 1) + ". Usuario: " + c.getUserId());
            System.out.println("   Estado: " + c.getEstado());
            System.out.println("   Fecha solicitud: " + c.getFechaSolicitud());
        }
    }

    /**
     * Muestra el menú de confirmaciones y solicita una opción.
     */
    public String mostrarMenuConfirmaciones() {
        System.out.println("\n=== CONFIRMACIONES DE ASISTENCIA ===");
        System.out.println("1. Confirmar asistencia a un scrim");
        System.out.println("2. Rechazar asistencia a un scrim");
        System.out.println("3. Ver mi confirmación");
        System.out.println("4. Ver estado de confirmaciones (organizador)");
        System.out.println("5. Ver confirmaciones pendientes (organizador)");
        System.out.println("0. Volver");
        System.out.print("Seleccione una opción: ");
        return scanner.nextLine().trim();
    }

    /**
     * Muestra un mensaje de éxito.
     */
    public void mostrarExito(String mensaje) {
        System.out.println("\n✓ " + mensaje);
    }

    /**
     * Muestra un mensaje de error.
     */
    public void mostrarError(String mensaje) {
        System.err.println("\n✗ Error: " + mensaje);
    }

    /**
     * Muestra un mensaje informativo.
     */
    public void mostrarInfo(String mensaje) {
        System.out.println("\nℹ " + mensaje);
    }
}
