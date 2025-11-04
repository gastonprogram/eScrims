package presentacion.controller;

import java.util.List;

import aplicacion.services.PostulacionService;
import dominio.modelo.Postulacion;
import presentacion.view.PostulacionView;

/**
 * Controller de presentación para la gestión de postulaciones.
 * 
 * Responsabilidades:
 * - Coordinar flujo de postulaciones
 * - Manejar postulaciones de jugadores
 * - Gestionar aceptación/rechazo de postulaciones (organizador)
 * - Formatear mensajes según el estado de las postulaciones
 * 
 * @author eScrims Team
 */
public class PostulacionController {

    private final PostulacionService postulacionService;
    private final PostulacionView view;
    private final String usuarioActualId;

    public PostulacionController(PostulacionService postulacionService,
            PostulacionView view,
            String usuarioActualId) {
        this.postulacionService = postulacionService;
        this.view = view;
        this.usuarioActualId = usuarioActualId;
    }

    /**
     * Inicia el flujo para postularse a un scrim.
     */
    public void postularseAScrim() {
        try {
            // 1. Solicitar datos a la vista
            String scrimId = view.solicitarIdScrim();

            // Verificar si ya se postuló
            if (postulacionService.yaSePostulo(scrimId, usuarioActualId)) {
                view.mostrarError("Ya te has postulado a este scrim");
                return;
            }

            int rango = view.solicitarRango();
            int latencia = view.solicitarLatencia();

            // 2. Llamar al servicio
            Postulacion postulacion = postulacionService.postularAScrim(
                    scrimId, usuarioActualId, rango, latencia);

            // 3. Mostrar resultado formateado según estado
            String mensaje = formatearMensajePostulacion(postulacion);
            view.mostrarExito(mensaje);

        } catch (IllegalArgumentException e) {
            view.mostrarError("Error: " + e.getMessage());
        } catch (IllegalStateException e) {
            view.mostrarError("El scrim no acepta postulaciones: " + e.getMessage());
        } catch (Exception e) {
            view.mostrarError("Error inesperado: " + e.getMessage());
        }
    }

    /**
     * Gestiona las postulaciones pendientes (solo para organizadores).
     */
    public void gestionarPostulaciones() {
        try {
            // 1. Solicitar ID del scrim
            String scrimId = view.solicitarIdScrim();

            // 2. Obtener postulaciones pendientes
            List<Postulacion> pendientes = postulacionService.listarPostulacionesPendientes(
                    scrimId, usuarioActualId);

            if (pendientes.isEmpty()) {
                view.mostrarInfo("No hay postulaciones pendientes para este scrim");
                return;
            }

            // 3. Mostrar postulaciones
            view.mostrarPostulacionesPendientes(pendientes);

            // 4. Solicitar acción
            String accion = view.solicitarAccion(); // "aceptar", "rechazar" o "salir"

            if ("salir".equalsIgnoreCase(accion)) {
                return;
            }

            String userId = view.seleccionarPostulante(pendientes);

            // 5. Ejecutar acción
            if ("aceptar".equalsIgnoreCase(accion)) {
                aceptarPostulacion(scrimId, userId);
            } else if ("rechazar".equalsIgnoreCase(accion)) {
                rechazarPostulacion(scrimId, userId);
            } else {
                view.mostrarError("Acción no válida");
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
            postulacionService.aceptarPostulacion(scrimId, userId, usuarioActualId);

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

            postulacionService.rechazarPostulacion(scrimId, userId, usuarioActualId, motivo);

            view.mostrarExito("Postulación de " + userId + " rechazada");

        } catch (IllegalStateException e) {
            view.mostrarError("Error: " + e.getMessage());
        }
    }

    /**
     * Ver todas las postulaciones de un scrim.
     */
    public void verTodasLasPostulaciones() {
        try {
            String scrimId = view.solicitarIdScrim();

            List<Postulacion> postulaciones = postulacionService.listarTodasLasPostulaciones(
                    scrimId, usuarioActualId);

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

    /**
     * Ver el estado de la postulación del usuario actual.
     */
    public void verMiPostulacion() {
        try {
            String scrimId = view.solicitarIdScrim();

            Postulacion postulacion = postulacionService.obtenerPostulacion(
                    scrimId, usuarioActualId);

            if (postulacion == null) {
                view.mostrarInfo("No te has postulado a este scrim");
            } else {
                view.mostrarDetallePostulacion(postulacion);
            }

        } catch (Exception e) {
            view.mostrarError("Error inesperado: " + e.getMessage());
        }
    }

    /**
     * Formatea el mensaje según el estado de la postulación.
     */
    private String formatearMensajePostulacion(Postulacion postulacion) {
        switch (postulacion.getEstado()) {
            case ACEPTADA:
                return "¡Postulación aceptada automáticamente! Has sido agregado al scrim.";
            case RECHAZADA:
                return "Postulación rechazada automáticamente.\nMotivo: " +
                        postulacion.getMotivoRechazo();
            case PENDIENTE:
                return "Postulación enviada exitosamente.\n" +
                        "Esperando respuesta del organizador...";
            default:
                return "Postulación procesada";
        }
    }
}
