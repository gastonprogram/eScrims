package presentacion.controller;

import java.util.List;

import aplicacion.services.ConfirmacionService;
import aplicacion.services.PostulacionService;
import aplicacion.services.ScrimService;
import dominio.modelo.Confirmacion;
import dominio.modelo.Postulacion;
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
            // 1. Obtener mis scrims como organizador
            List<dominio.modelo.Scrim> misScrims = scrimService.obtenerScrimsPorOrganizador(organizadorId);
            
            // 2. Mostrar lista numerada de mis scrims
            view.mostrarMisScrims(misScrims);
            
            // Si no hay scrims, salir
            if (misScrims.isEmpty()) {
                return;
            }

            // 3. Solicitar número del scrim (1-based index)
            int numeroScrim = view.solicitarNumeroScrim(misScrims.size());

            // Permitir cancelar
            if (numeroScrim == 0) {
                view.mostrarInfo("Operación cancelada");
                return;
            }

            // Validar que el número esté en rango
            if (numeroScrim < 1 || numeroScrim > misScrims.size()) {
                view.mostrarError("Número inválido. Debe ser entre 1 y " + misScrims.size());
                return;
            }

            // 4. Obtener el scrim seleccionado (convertir de 1-based a 0-based index)
            dominio.modelo.Scrim scrimSeleccionado = misScrims.get(numeroScrim - 1);
            String scrimId = scrimSeleccionado.getId();

            // 5. Obtener y mostrar postulaciones pendientes
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
            // 1. Obtener mis scrims como organizador
            List<dominio.modelo.Scrim> misScrims = scrimService.obtenerScrimsPorOrganizador(organizadorId);
            
            // 2. Mostrar lista numerada de mis scrims
            view.mostrarMisScrims(misScrims);
            
            // Si no hay scrims, salir
            if (misScrims.isEmpty()) {
                return;
            }

            // 3. Solicitar número del scrim (1-based index)
            int numeroScrim = view.solicitarNumeroScrim(misScrims.size());

            // Permitir cancelar
            if (numeroScrim == 0) {
                view.mostrarInfo("Operación cancelada");
                return;
            }

            // Validar que el número esté en rango
            if (numeroScrim < 1 || numeroScrim > misScrims.size()) {
                view.mostrarError("Número inválido. Debe ser entre 1 y " + misScrims.size());
                return;
            }

            // 4. Obtener el scrim seleccionado (convertir de 1-based a 0-based index)
            dominio.modelo.Scrim scrimSeleccionado = misScrims.get(numeroScrim - 1);
            String scrimId = scrimSeleccionado.getId();

            // 5. Obtener postulaciones pendientes
            List<Postulacion> pendientes = postulacionService.listarPostulacionesPendientes(
                    scrimId, organizadorId);

            if (pendientes.isEmpty()) {
                view.mostrarInfo("No hay postulaciones pendientes para este scrim");
                return;
            }

            // 6. Mostrar postulaciones
            view.mostrarPostulacionesPendientes(pendientes);

            // 7. Solicitar acción
            String accion = view.solicitarAccion();

            if ("salir".equalsIgnoreCase(accion)) {
                return;
            }

            if ("invalida".equalsIgnoreCase(accion)) {
                view.mostrarError("Acción no válida");
                return;
            }

            String userId = view.seleccionarPostulante(pendientes);

            // 8. Ejecutar acción
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
            // 1. Obtener mis scrims como organizador
            List<dominio.modelo.Scrim> misScrims = scrimService.obtenerScrimsPorOrganizador(organizadorId);
            
            // 2. Mostrar lista numerada de mis scrims
            view.mostrarMisScrims(misScrims);
            
            // Si no hay scrims, salir
            if (misScrims.isEmpty()) {
                return;
            }

            // 3. Solicitar número del scrim (1-based index)
            int numeroScrim = view.solicitarNumeroScrim(misScrims.size());

            // Permitir cancelar
            if (numeroScrim == 0) {
                view.mostrarInfo("Operación cancelada");
                return;
            }

            // Validar que el número esté en rango
            if (numeroScrim < 1 || numeroScrim > misScrims.size()) {
                view.mostrarError("Número inválido. Debe ser entre 1 y " + misScrims.size());
                return;
            }

            // 4. Obtener el scrim seleccionado (convertir de 1-based a 0-based index)
            dominio.modelo.Scrim scrimSeleccionado = misScrims.get(numeroScrim - 1);
            String scrimId = scrimSeleccionado.getId();

            // 5. Obtener todas las postulaciones
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
            // 1. Obtener mis scrims como organizador
            List<dominio.modelo.Scrim> misScrims = scrimService.obtenerScrimsPorOrganizador(organizadorId);
            
            // 2. Mostrar lista numerada de mis scrims
            view.mostrarMisScrims(misScrims);
            
            // Si no hay scrims, salir
            if (misScrims.isEmpty()) {
                return;
            }

            // 3. Solicitar número del scrim (1-based index)
            int numeroScrim = view.solicitarNumeroScrim(misScrims.size());

            // Permitir cancelar
            if (numeroScrim == 0) {
                view.mostrarInfo("Operación cancelada");
                return;
            }

            // Validar que el número esté en rango
            if (numeroScrim < 1 || numeroScrim > misScrims.size()) {
                view.mostrarError("Número inválido. Debe ser entre 1 y " + misScrims.size());
                return;
            }

            // 4. Obtener el scrim seleccionado (convertir de 1-based a 0-based index)
            dominio.modelo.Scrim scrimSeleccionado = misScrims.get(numeroScrim - 1);
            String scrimId = scrimSeleccionado.getId();

            // 5. Obtener confirmaciones
            List<Confirmacion> confirmaciones = confirmacionService.listarConfirmaciones(
                    scrimId, organizadorId);

            if (confirmaciones.isEmpty()) {
                view.mostrarInfo("No hay confirmaciones para este scrim");
                return;
            }

            // 6. Mostrar lista de confirmaciones
            view.mostrarListaConfirmaciones(confirmaciones);

            // 7. Mostrar estadísticas
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

            // 8. Mostrar confirmaciones pendientes si las hay
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
