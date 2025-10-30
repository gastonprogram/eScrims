package view;

import controller.ConfirmacionController;
import model.Confirmacion;
import model.Usuario;

import java.util.List;
import java.util.Scanner;

/**
 * Vista para gestionar las confirmaciones de asistencia a scrims.
 * 
 * Funcionalidades:
 * - Confirmar o rechazar asistencia (jugador)
 * - Ver estado de confirmaciones (organizador)
 * 
 * Las confirmaciones se generan automáticamente cuando un scrim
 * alcanza el estado LOBBY_ARMADO (todos los slots llenos).
 * 
 * Siguiendo el patrón de desacoplamiento:
 * - La vista NO contiene lógica de negocio
 * - Toda la lógica está en ConfirmacionController
 * - La vista solo orquesta la interacción
 * 
 * @author eScrims Team
 */
public class ConfirmacionView {
    private final ConfirmacionController controller;
    private final Scanner scanner;

    public ConfirmacionView() {
        this.controller = new ConfirmacionController();
        this.scanner = new Scanner(System.in);
    }

    /**
     * Muestra el menú de confirmaciones para un jugador.
     * 
     * @param usuario Usuario actual
     */
    public void mostrarMenuConfirmaciones(Usuario usuario) {
        boolean continuar = true;

        while (continuar) {
            System.out.println("\n=== CONFIRMACIONES DE ASISTENCIA ===");
            System.out.println("1. Confirmar asistencia a un scrim");
            System.out.println("2. Rechazar asistencia a un scrim");
            System.out.println("3. Ver mis confirmaciones pendientes");
            System.out.println("0. Volver");
            System.out.print("Seleccione una opción: ");

            String opcion = scanner.nextLine().trim();

            switch (opcion) {
                case "1":
                    confirmarAsistencia(usuario);
                    break;
                case "2":
                    rechazarAsistencia(usuario);
                    break;
                case "3":
                    verMisConfirmaciones(usuario);
                    break;
                case "0":
                    continuar = false;
                    break;
                default:
                    System.out.println("Opción inválida");
            }
        }
    }

    /**
     * Permite a un jugador confirmar su asistencia.
     * 
     * @param usuario Usuario que confirma
     */
    private void confirmarAsistencia(Usuario usuario) {
        System.out.println("\n=== CONFIRMAR ASISTENCIA ===");
        System.out.print("ID del scrim: ");
        String scrimId = scanner.nextLine().trim();

        if (scrimId.isEmpty()) {
            System.out.println("El ID del scrim no puede estar vacío");
            return;
        }

        String resultado = controller.confirmarAsistencia(scrimId, usuario.getId());
        System.out.println("\n" + resultado);
    }

    /**
     * Permite a un jugador rechazar su asistencia.
     * 
     * @param usuario Usuario que rechaza
     */
    private void rechazarAsistencia(Usuario usuario) {
        System.out.println("\n=== RECHAZAR ASISTENCIA ===");
        System.out.print("ID del scrim: ");
        String scrimId = scanner.nextLine().trim();

        if (scrimId.isEmpty()) {
            System.out.println("El ID del scrim no puede estar vacío");
            return;
        }

        System.out.println(
                "\n⚠️  ADVERTENCIA: Si rechazas la asistencia, el slot quedará disponible para otros jugadores.");
        System.out.print("¿Estás seguro? (s/n): ");
        String confirmacion = scanner.nextLine().trim().toLowerCase();

        if (!confirmacion.equals("s")) {
            System.out.println("Operación cancelada");
            return;
        }

        String resultado = controller.rechazarAsistencia(scrimId, usuario.getId());
        System.out.println("\n" + resultado);
    }

    /**
     * Muestra las confirmaciones pendientes del usuario.
     * 
     * @param usuario Usuario actual
     */
    private void verMisConfirmaciones(Usuario usuario) {
        System.out.println("\n=== MIS CONFIRMACIONES PENDIENTES ===");
        System.out.println("(Funcionalidad pendiente de implementación)");
        System.out.println("Requiere un método para buscar scrims donde el usuario tiene confirmaciones pendientes");
    }

    /**
     * Muestra el estado de las confirmaciones de un scrim (vista del organizador).
     * 
     * @param scrimId       ID del scrim
     * @param organizadorId ID del organizador
     */
    public void verEstadoConfirmaciones(String scrimId, String organizadorId) {
        try {
            List<Confirmacion> confirmaciones = controller.listarConfirmaciones(scrimId, organizadorId);

            if (confirmaciones.isEmpty()) {
                System.out.println("\nNo hay confirmaciones generadas todavía");
                System.out.println("Las confirmaciones se generan automáticamente cuando el lobby está completo");
                return;
            }

            System.out.println("\n=== ESTADO DE CONFIRMACIONES ===");
            System.out.println("Scrim ID: " + scrimId);

            long confirmadas = confirmaciones.stream()
                    .filter(c -> c.getEstado() == Confirmacion.EstadoConfirmacion.CONFIRMADA)
                    .count();
            long pendientes = confirmaciones.stream()
                    .filter(c -> c.getEstado() == Confirmacion.EstadoConfirmacion.PENDIENTE)
                    .count();
            long rechazadas = confirmaciones.stream()
                    .filter(c -> c.getEstado() == Confirmacion.EstadoConfirmacion.RECHAZADA)
                    .count();

            System.out.println("\nResumen:");
            System.out.println("- Confirmadas: " + confirmadas);
            System.out.println("- Pendientes: " + pendientes);
            System.out.println("- Rechazadas: " + rechazadas);
            System.out.println("- Total: " + confirmaciones.size());

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

        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Menú de gestión de confirmaciones para el organizador.
     * 
     * @param scrimId       ID del scrim
     * @param organizadorId ID del organizador
     */
    public void gestionarConfirmaciones(String scrimId, String organizadorId) {
        boolean continuar = true;

        while (continuar) {
            System.out.println("\n=== GESTIÓN DE CONFIRMACIONES (ORGANIZADOR) ===");
            System.out.println("Scrim ID: " + scrimId);
            System.out.println("1. Ver estado de confirmaciones");
            System.out.println("2. Ver solo confirmaciones pendientes");
            System.out.println("0. Volver");
            System.out.print("Seleccione una opción: ");

            String opcion = scanner.nextLine().trim();

            switch (opcion) {
                case "1":
                    verEstadoConfirmaciones(scrimId, organizadorId);
                    break;
                case "2":
                    verConfirmacionesPendientes(scrimId, organizadorId);
                    break;
                case "0":
                    continuar = false;
                    break;
                default:
                    System.out.println("Opción inválida");
            }
        }
    }

    /**
     * Muestra solo las confirmaciones pendientes.
     * 
     * @param scrimId       ID del scrim
     * @param organizadorId ID del organizador
     */
    private void verConfirmacionesPendientes(String scrimId, String organizadorId) {
        try {
            List<Confirmacion> pendientes = controller.listarConfirmacionesPendientes(scrimId, organizadorId);

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

        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
