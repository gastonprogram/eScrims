package presentacion.controller;

import java.util.List;

import aplicacion.services.ConfirmacionService;
import aplicacion.services.PostulacionService;
import aplicacion.services.ScrimService;
import dominio.modelo.Confirmacion;
import dominio.modelo.Postulacion;
import presentacion.controller.GestionMatchmakingController;
import presentacion.view.GestionMatchmakingView;
import presentacion.view.OrganizadorView;

/**
 * Controller para la gestión de scrims del ORGANIZADOR.
 * 
 * Responsabilidades:
 * - Coordinar flujo de gestión de postulaciones
 * - Coordinar flujo de gestión de confirmaciones
 * - Manejar acciones del organizador sobre sus scrims
 * 
 * @author eScrims Team
 */
public class OrganizadorController {

    private final PostulacionService postulacionService;
    private final ConfirmacionService confirmacionService;
    private final ScrimService scrimService;
    private final OrganizadorView view;
    private final String organizadorId;

    public OrganizadorController(
            PostulacionService postulacionService,
            ConfirmacionService confirmacionService,
            ScrimService scrimService,
            OrganizadorView view,
            String organizadorId) {
        this.postulacionService = postulacionService;
        this.confirmacionService = confirmacionService;
        this.scrimService = scrimService;
        this.view = view;
        this.organizadorId = organizadorId;
    }

    /**
     * Gestiona el menú completo del organizador.
     */
    public void gestionarScrims() {
        boolean salir = false;

        while (!salir) {
            String opcion = view.mostrarMenuOrganizador();

            switch (opcion) {
                case "1":
                    verPostulacionesPendientes();
                    break;
                case "2":
                    gestionarPostulaciones();
                    break;
                case "3":
                    verEstadoConfirmaciones();
                    break;
                case "4":
                    verTodasLasPostulaciones();
                    break;
                case "5":
                    gestionarEstrategiasMatchmaking();
                    break;
                case "0":
                    salir = true;
                    break;
                default:
                    view.mostrarError("Opción no válida");
            }
        }
    }

    // ========== GESTIÓN DE POSTULACIONES ==========

    /**
     * Muestra las postulaciones pendientes de un scrim.
     */
    private void verPostulacionesPendientes() {
        try {
            String scrimId = view.solicitarIdScrim();

            List<Postulacion> pendientes = postulacionService.listarPostulacionesPendientes(
                    scrimId, organizadorId);

            if (pendientes.isEmpty()) {
                view.mostrarInfo("No hay postulaciones pendientes para este scrim");
            } else {
                view.mostrarPostulacionesPendientes(pendientes);
            }

        } catch (IllegalArgumentException e) {
            view.mostrarError("Error: " + e.getMessage());
        } catch (Exception e) {
            view.mostrarError("Error inesperado: " + e.getMessage());
        }
    }

    /**
     * Gestiona las postulaciones pendientes (aceptar/rechazar).
     */
    private void gestionarPostulaciones() {
        try {
            // 1. Solicitar ID del scrim
            String scrimId = view.solicitarIdScrim();

            // 2. Obtener postulaciones pendientes
            List<Postulacion> pendientes = postulacionService.listarPostulacionesPendientes(
                    scrimId, organizadorId);

            if (pendientes.isEmpty()) {
                view.mostrarInfo("No hay postulaciones pendientes para este scrim");
                return;
            }

            // 3. Mostrar postulaciones
            view.mostrarPostulacionesPendientes(pendientes);

            // 4. Solicitar acción
            String accion = view.solicitarAccion();

            if ("salir".equalsIgnoreCase(accion)) {
                return;
            }

            if ("invalida".equalsIgnoreCase(accion)) {
                view.mostrarError("Acción no válida");
                return;
            }

            String userId = view.seleccionarPostulante(pendientes);

            // 5. Ejecutar acción
            if ("aceptar".equalsIgnoreCase(accion)) {
                aceptarPostulacion(scrimId, userId);
            } else if ("rechazar".equalsIgnoreCase(accion)) {
                rechazarPostulacion(scrimId, userId);
            }

        } catch (IllegalArgumentException e) {
            view.mostrarError("Error: " + e.getMessage());
        } catch (Exception e) {
            view.mostrarError("Error inesperado: " + e.getMessage());
        }
    }

    /**
     * Acepta una postulación específica.
     */
    private void aceptarPostulacion(String scrimId, String userId) {
        try {
            postulacionService.aceptarPostulacion(scrimId, userId, organizadorId);
            view.mostrarExito("Postulación de " + userId + " aceptada exitosamente");

        } catch (IllegalStateException e) {
            view.mostrarError("Error: " + e.getMessage());
        }
    }

    /**
     * Rechaza una postulación específica.
     */
    private void rechazarPostulacion(String scrimId, String userId) {
        try {
            String motivo = view.solicitarMotivo();

            postulacionService.rechazarPostulacion(scrimId, userId, organizadorId, motivo);
            view.mostrarExito("Postulación de " + userId + " rechazada");

        } catch (IllegalStateException e) {
            view.mostrarError("Error: " + e.getMessage());
        }
    }

    /**
     * Muestra todas las postulaciones de un scrim.
     */
    private void verTodasLasPostulaciones() {
        try {
            String scrimId = view.solicitarIdScrim();

            List<Postulacion> postulaciones = postulacionService.listarTodasLasPostulaciones(
                    scrimId, organizadorId);

            if (postulaciones.isEmpty()) {
                view.mostrarInfo("No hay postulaciones para este scrim");
            } else {
                view.mostrarTodasLasPostulaciones(postulaciones);
            }

        } catch (IllegalArgumentException e) {
            view.mostrarError("Error: " + e.getMessage());
        } catch (Exception e) {
            view.mostrarError("Error inesperado: " + e.getMessage());
        }
    }

    // ========== GESTIÓN DE CONFIRMACIONES ==========

    /**
     * Muestra el estado de las confirmaciones de un scrim.
     */
    private void verEstadoConfirmaciones() {
        try {
            // 1. Solicitar ID del scrim
            String scrimId = view.solicitarIdScrim();

            // 2. Obtener confirmaciones
            List<Confirmacion> confirmaciones = confirmacionService.listarConfirmaciones(
                    scrimId, organizadorId);

            if (confirmaciones.isEmpty()) {
                view.mostrarInfo("No hay confirmaciones para este scrim");
                return;
            }

            // 3. Mostrar lista de confirmaciones
            view.mostrarListaConfirmaciones(confirmaciones);

            // 4. Mostrar estadísticas
            long confirmadas = confirmaciones.stream()
                    .filter(c -> c.getEstado() == Confirmacion.EstadoConfirmacion.CONFIRMADA)
                    .count();
            long pendientes = confirmaciones.stream()
                    .filter(c -> c.getEstado() == Confirmacion.EstadoConfirmacion.PENDIENTE)
                    .count();
            long rechazadas = confirmaciones.stream()
                    .filter(c -> c.getEstado() == Confirmacion.EstadoConfirmacion.RECHAZADA)
                    .count();

            view.mostrarEstadisticas(confirmadas, pendientes, rechazadas);

            // 5. Mostrar confirmaciones pendientes si las hay
            if (pendientes > 0) {
                List<Confirmacion> confirmacionesPendientes = confirmacionService
                        .listarConfirmacionesPendientes(scrimId, organizadorId);
                view.mostrarConfirmacionesPendientes(confirmacionesPendientes);
            }

        } catch (IllegalArgumentException e) {
            view.mostrarError("Error: " + e.getMessage());
        } catch (Exception e) {
            view.mostrarError("Error inesperado: " + e.getMessage());
        }
    }

    /**
     * Gestiona las estrategias de matchmaking de los scrims del organizador.
     */
    private void gestionarEstrategiasMatchmaking() {
        try {
            // Crear vista específica para gestión de matchmaking
            GestionMatchmakingView matchmakingView = new GestionMatchmakingView();

            // Crear controller específico para gestión de matchmaking
            GestionMatchmakingController matchmakingController = new GestionMatchmakingController(
                    scrimService, matchmakingView, organizadorId);

            // Ejecutar la gestión de estrategias
            matchmakingController.gestionarEstrategias();

        } catch (Exception e) {
            view.mostrarError("Error al gestionar estrategias de matchmaking: " + e.getMessage());
        }
    }
}
