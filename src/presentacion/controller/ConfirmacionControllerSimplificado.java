package presentacion.controller;

import java.util.List;

import aplicacion.services.ConfirmacionService;
import dominio.modelo.Confirmacion;
import dominio.modelo.Scrim;
import presentacion.view.ConfirmacionViewSimplificada;

/**
 * Controller simplificado para confirmaciones (SOLO PARA USUARIO COMÚN).
 * 
 * Responsabilidades:
 * - Coordinar el flujo de confirmación/rechazo de asistencia
 * - Mostrar el estado de la confirmación del usuario
 * 
 * Las funcionalidades del organizador están en OrganizadorController.
 * 
 * @author eScrims Team
 */
public class ConfirmacionControllerSimplificado {

    private final ConfirmacionService confirmacionService;
    private final ConfirmacionViewSimplificada view;
    private final String usuarioActualId;

    public ConfirmacionControllerSimplificado(
            ConfirmacionService confirmacionService,
            ConfirmacionViewSimplificada view,
            String usuarioActualId) {
        this.confirmacionService = confirmacionService;
        this.view = view;
        this.usuarioActualId = usuarioActualId;
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
                case "0":
                    salir = true;
                    break;
                default:
                    view.mostrarError("Opción no válida");
            }
        }
    }

    /**
     * Inicia el flujo para confirmar asistencia a un scrim.
     */
    private void confirmarAsistencia() {
        try {
            // 1. Obtener y mostrar scrims con confirmaciones pendientes
            List<Scrim> scrimsConConfirmacion = confirmacionService
                    .obtenerScrimsConConfirmacionPendiente(usuarioActualId);
            view.mostrarScrimsConConfirmacionPendiente(scrimsConConfirmacion);

            // Si no hay scrims, salir
            if (scrimsConConfirmacion.isEmpty()) {
                return;
            }

            // 2. Solicitar número del scrim (1-based index)
            int numeroScrim = view.solicitarNumeroScrim(scrimsConConfirmacion.size());

            // Permitir cancelar
            if (numeroScrim == 0) {
                view.mostrarInfo("Operación cancelada");
                return;
            }

            // Validar que el número esté en rango
            if (numeroScrim < 1 || numeroScrim > scrimsConConfirmacion.size()) {
                view.mostrarError("Número inválido. Debe ser entre 1 y " + scrimsConConfirmacion.size());
                return;
            }

            // Obtener el scrim seleccionado (convertir de 1-based a 0-based index)
            Scrim scrimSeleccionado = scrimsConConfirmacion.get(numeroScrim - 1);
            String scrimId = scrimSeleccionado.getId();

            // Mostrar detalles de la confirmación
            Confirmacion confirmacion = confirmacionService.obtenerConfirmacion(scrimId, usuarioActualId);
            view.mostrarMiConfirmacion(confirmacion);

            // 3. Confirmar acción
            boolean confirmar = view.confirmarAsistenciaUsuario();

            if (!confirmar) {
                view.mostrarInfo("Confirmación cancelada");
                return;
            }

            // 4. Llamar al servicio
            confirmacion = confirmacionService.confirmarAsistencia(scrimId, usuarioActualId);

            // 5. Mostrar resultado
            view.mostrarExito(
                    "¡Asistencia confirmada!\n" +
                            "   Esperando confirmación de los demás jugadores...");

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
    private void rechazarAsistencia() {
        try {
            // 1. Obtener y mostrar scrims con confirmaciones pendientes
            List<Scrim> scrimsConConfirmacion = confirmacionService
                    .obtenerScrimsConConfirmacionPendiente(usuarioActualId);
            view.mostrarScrimsConConfirmacionPendiente(scrimsConConfirmacion);

            // Si no hay scrims, salir
            if (scrimsConConfirmacion.isEmpty()) {
                return;
            }

            // 2. Solicitar número del scrim (1-based index)
            int numeroScrim = view.solicitarNumeroScrim(scrimsConConfirmacion.size());

            // Permitir cancelar
            if (numeroScrim == 0) {
                view.mostrarInfo("Operación cancelada");
                return;
            }

            // Validar que el número esté en rango
            if (numeroScrim < 1 || numeroScrim > scrimsConConfirmacion.size()) {
                view.mostrarError("Número inválido. Debe ser entre 1 y " + scrimsConConfirmacion.size());
                return;
            }

            // Obtener el scrim seleccionado (convertir de 1-based a 0-based index)
            Scrim scrimSeleccionado = scrimsConConfirmacion.get(numeroScrim - 1);
            String scrimId = scrimSeleccionado.getId();

            // 3. Confirmar rechazo
            boolean confirmarRechazo = view.confirmarRechazoUsuario();

            if (!confirmarRechazo) {
                view.mostrarInfo("Operación cancelada");
                return;
            }

            // 4. Llamar al servicio
            confirmacionService.rechazarAsistencia(scrimId, usuarioActualId);

            // 5. Mostrar resultado
            view.mostrarExito(
                    "Has rechazado la asistencia al scrim.\n" +
                            "   Tu slot ha sido liberado.");

        } catch (IllegalArgumentException e) {
            view.mostrarError("Error: " + e.getMessage());
        } catch (IllegalStateException e) {
            view.mostrarError("Error de estado: " + e.getMessage());
        } catch (Exception e) {
            view.mostrarError("Error inesperado: " + e.getMessage());
        }
    }

    /**
     * Muestra la confirmación del usuario actual para un scrim.
     */
    private void verMiConfirmacion() {
        try {
            String scrimId = view.solicitarIdScrimParaVer();

            Confirmacion confirmacion = confirmacionService.obtenerConfirmacion(
                    scrimId, usuarioActualId);

            if (confirmacion == null) {
                view.mostrarInfo("No tienes una confirmación para este scrim");
            } else {
                view.mostrarMiConfirmacion(confirmacion);
            }

        } catch (Exception e) {
            view.mostrarError("Error inesperado: " + e.getMessage());
        }
    }
}
