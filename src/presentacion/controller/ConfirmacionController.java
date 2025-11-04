package presentacion.controller;

import java.util.List;

import aplicacion.services.ConfirmacionService;
import dominio.modelo.Confirmacion;
import presentacion.view.ConfirmacionView;

/**
 * Controller de presentación para la gestión de confirmaciones.
 * 
 * Responsabilidades:
 * - Coordinar flujo de confirmaciones de asistencia
 * - Manejar confirmación/rechazo de jugadores
 * - Mostrar estado de confirmaciones al organizador
 * - Formatear mensajes según el estado del scrim
 * 
 * @author eScrims Team
 */
public class ConfirmacionController {

    private final ConfirmacionService confirmacionService;
    private final ConfirmacionView view;
    private final String usuarioActualId;

    public ConfirmacionController(ConfirmacionService confirmacionService,
            ConfirmacionView view,
            String usuarioActualId) {
        this.confirmacionService = confirmacionService;
        this.view = view;
        this.usuarioActualId = usuarioActualId;
    }

    /**
     * Inicia el flujo para confirmar asistencia a un scrim.
     */
    public void confirmarAsistencia() {
        try {
            // 1. Solicitar ID del scrim
            String scrimId = view.solicitarIdScrim();

            // Verificar si tiene confirmación pendiente
            if (!confirmacionService.tieneConfirmacionPendiente(scrimId, usuarioActualId)) {
                view.mostrarError("No tienes una confirmación pendiente para este scrim");
                return;
            }

            // Mostrar detalles de la confirmación
            Confirmacion confirmacion = confirmacionService.obtenerConfirmacion(scrimId, usuarioActualId);
            view.mostrarDetalleConfirmacion(confirmacion);

            // 2. Confirmar acción
            boolean confirmar = view.confirmarAsistenciaUsuario();

            if (!confirmar) {
                view.mostrarInfo("Confirmación cancelada");
                return;
            }

            // 3. Llamar al servicio
            confirmacion = confirmacionService.confirmarAsistencia(scrimId, usuarioActualId);

            // 4. Mostrar resultado
            view.mostrarExito(
                    "¡Asistencia confirmada!\n" +
                            "Esperando confirmación de los demás jugadores...");

        } catch (IllegalArgumentException e) {
            view.mostrarError("Error: " + e.getMessage());
        } catch (IllegalStateException e) {
            view.mostrarError("Error de estado: " + e.getMessage());
        } catch (Exception e) {
            view.mostrarError("Error inesperado: " + e.getMessage());
        }
    }

    /**
     * Inicia el flujo para rechazar asistencia a un scrim.
     */
    public void rechazarAsistencia() {
        try {
            // 1. Solicitar ID del scrim
            String scrimId = view.solicitarIdScrim();

            // Verificar si tiene confirmación pendiente
            if (!confirmacionService.tieneConfirmacionPendiente(scrimId, usuarioActualId)) {
                view.mostrarError("No tienes una confirmación pendiente para este scrim");
                return;
            }

            // 2. Confirmar rechazo
            boolean confirmarRechazo = view.confirmarRechazoUsuario();

            if (!confirmarRechazo) {
                view.mostrarInfo("Operación cancelada");
                return;
            }

            // 3. Llamar al servicio
            confirmacionService.rechazarAsistencia(scrimId, usuarioActualId);

            // 4. Mostrar resultado
            view.mostrarExito(
                    "Has rechazado la asistencia al scrim.\n" +
                            "Tu slot ha sido liberado.");

        } catch (IllegalArgumentException e) {
            view.mostrarError("Error: " + e.getMessage());
        } catch (IllegalStateException e) {
            view.mostrarError("Error de estado: " + e.getMessage());
        } catch (Exception e) {
            view.mostrarError("Error inesperado: " + e.getMessage());
        }
    }

    /**
     * Muestra el estado de las confirmaciones de un scrim (para organizador).
     */
    public void verEstadoConfirmaciones() {
        try {
            // 1. Solicitar ID del scrim
            String scrimId = view.solicitarIdScrim();

            // 2. Obtener confirmaciones
            List<Confirmacion> confirmaciones = confirmacionService.listarConfirmaciones(
                    scrimId, usuarioActualId);

            if (confirmaciones.isEmpty()) {
                view.mostrarInfo("No hay confirmaciones para este scrim");
                return;
            }

            // 3. Mostrar lista de confirmaciones
            view.mostrarListaConfirmaciones(confirmaciones);

            // Mostrar estadísticas
            long confirmadas = confirmaciones.stream()
                    .filter(c -> c.getEstado() == Confirmacion.EstadoConfirmacion.CONFIRMADA)
                    .count();
            long pendientes = confirmaciones.stream()
                    .filter(c -> c.getEstado() == Confirmacion.EstadoConfirmacion.PENDIENTE)
                    .count();
            long rechazadas = confirmaciones.stream()
                    .filter(c -> c.getEstado() == Confirmacion.EstadoConfirmacion.RECHAZADA)
                    .count();

            view.mostrarInfo(
                    String.format("\nEstadísticas:\nConfirmadas: %d\nPendientes: %d\nRechazadas: %d",
                            confirmadas, pendientes, rechazadas));

        } catch (IllegalArgumentException e) {
            view.mostrarError("Error: " + e.getMessage());
        } catch (Exception e) {
            view.mostrarError("Error inesperado: " + e.getMessage());
        }
    }

    /**
     * Muestra solo las confirmaciones pendientes de un scrim (para organizador).
     */
    public void verConfirmacionesPendientes() {
        try {
            String scrimId = view.solicitarIdScrim();

            List<Confirmacion> pendientes = confirmacionService.listarConfirmacionesPendientes(
                    scrimId, usuarioActualId);

            if (pendientes.isEmpty()) {
                view.mostrarExito("¡Todos los jugadores han confirmado su asistencia!");
            } else {
                view.mostrarConfirmacionesPendientes(pendientes);
                view.mostrarInfo("Esperando confirmación de " + pendientes.size() + " jugador(es)");
            }

        } catch (IllegalArgumentException e) {
            view.mostrarError("Error: " + e.getMessage());
        } catch (Exception e) {
            view.mostrarError("Error inesperado: " + e.getMessage());
        }
    }

    /**
     * Muestra la confirmación del usuario actual para un scrim.
     */
    public void verMiConfirmacion() {
        try {
            String scrimId = view.solicitarIdScrim();

            Confirmacion confirmacion = confirmacionService.obtenerConfirmacion(
                    scrimId, usuarioActualId);

            if (confirmacion == null) {
                view.mostrarInfo("No tienes una confirmación para este scrim");
            } else {
                view.mostrarDetalleConfirmacion(confirmacion);

                // Mostrar mensaje según estado
                switch (confirmacion.getEstado()) {
                    case PENDIENTE:
                        view.mostrarInfo("Debes confirmar tu asistencia");
                        break;
                    case CONFIRMADA:
                        view.mostrarExito("Ya confirmaste tu asistencia");
                        break;
                    case RECHAZADA:
                        view.mostrarInfo("Rechazaste la asistencia a este scrim");
                        break;
                }
            }

        } catch (Exception e) {
            view.mostrarError("Error inesperado: " + e.getMessage());
        }
    }

    /**
     * Gestiona el flujo completo de confirmaciones (menú interactivo).
     */
    public void gestionarConfirmaciones() {
        boolean salir = false;

        while (!salir) {
            String opcion = view.mostrarMenuConfirmaciones();

            switch (opcion) {
                case "1":
                    confirmarAsistencia();
                    break;
                case "2":
                    rechazarAsistencia();
                    break;
                case "3":
                    verMiConfirmacion();
                    break;
                case "4":
                    verEstadoConfirmaciones();
                    break;
                case "5":
                    verConfirmacionesPendientes();
                    break;
                case "0":
                    salir = true;
                    break;
                default:
                    view.mostrarError("Opción no válida");
            }
        }
    }
}
