package presentacion.controller;

import java.util.List;

import aplicacion.services.PostulacionService;
import aplicacion.services.ScrimService;
import dominio.modelo.Postulacion;
import dominio.modelo.Scrim;
import dominio.modelo.Usuario;
import presentacion.view.PostulacionViewSimplificada;

/**
 * Controller simplificado para postulaciones (SOLO PARA USUARIO COMÚN).
 * 
 * Responsabilidades:
 * - Mostrar scrims disponibles para postularse
 * - Coordinar el flujo de postulación del usuario
 * - Mostrar el estado de la postulación del usuario
 * - Usar datos del perfil del usuario (rango y latencia)
 * 
 * Las funcionalidades del organizador están en OrganizadorController.
 * 
 * @author eScrims Team
 */
public class PostulacionControllerSimplificado {

    private final PostulacionService postulacionService;
    private final ScrimService scrimService;
    private final PostulacionViewSimplificada view;
    private final Usuario usuario;

    public PostulacionControllerSimplificado(
            PostulacionService postulacionService,
            ScrimService scrimService,
            PostulacionViewSimplificada view,
            Usuario usuario) {
        this.postulacionService = postulacionService;
        this.scrimService = scrimService;
        this.view = view;
        this.usuario = usuario;
    }

    /**
     * Inicia el flujo para postularse a un scrim.
     * Primero muestra los scrims disponibles, luego usa los datos del perfil del
     * usuario.
     */
    public void postularseAScrim() {
        try {
            // 1. Obtener y mostrar scrims disponibles
            List<Scrim> scrimsDisponibles = scrimService.obtenerScrimsDisponibles(usuario.getId());
            view.mostrarScrimsDisponibles(scrimsDisponibles);

            // Si no hay scrims disponibles, salir
            if (scrimsDisponibles.isEmpty()) {
                return;
            }

            // 2. Solicitar número del scrim (1-based index)
            int numeroScrim = view.solicitarNumeroScrim(scrimsDisponibles.size());

            // Permitir cancelar
            if (numeroScrim == 0) {
                view.mostrarInfo("Postulación cancelada");
                return;
            }

            // Validar que el número esté en rango
            if (numeroScrim < 1 || numeroScrim > scrimsDisponibles.size()) {
                view.mostrarError("Número inválido. Debe ser entre 1 y " + scrimsDisponibles.size());
                return;
            }

            // Obtener el scrim seleccionado (convertir de 1-based a 0-based index)
            Scrim scrimSeleccionado = scrimsDisponibles.get(numeroScrim - 1);
            String scrimId = scrimSeleccionado.getId();

            // 3. Obtener rango y latencia del perfil del usuario
            String nombreJuego = scrimSeleccionado.getJuego().getNombre();
            Integer rango = usuario.getRangoPorJuego().get(nombreJuego);
            int latencia = usuario.getLatenciaPromedio();

            // Validar que el usuario tenga rango para ese juego
            if (rango == null) {
                view.mostrarError(
                        "No tienes un rango configurado para " + nombreJuego + ".\n" +
                                "   Por favor, actualiza tu perfil antes de postularte.");
                return;
            }

            // 4. Mostrar datos y pedir confirmación
            view.mostrarDatosPostulacion(rango, latencia, nombreJuego);

            if (!view.confirmarPostulacion()) {
                view.mostrarInfo("Postulación cancelada");
                return;
            }

            // 5. Llamar al servicio con los datos del perfil
            Postulacion postulacion = postulacionService.postularAScrim(
                    scrimId, usuario.getId(), rango, latencia);

            // 6. Mostrar resultado formateado según estado
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
     * Ver el estado de la postulación del usuario actual.
     */
    public void verMiPostulacion() {
        try {
            String scrimId = view.solicitarIdScrimParaVer();

            Postulacion postulacion = postulacionService.obtenerPostulacion(
                    scrimId, usuario.getId());

            if (postulacion == null) {
                view.mostrarInfo("No te has postulado a este scrim");
            } else {
                view.mostrarMiPostulacion(postulacion);
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
                return "¡Postulación aceptada automáticamente! Has sido agregado al scrim.\n" +
                        "   Recibirás una notificación cuando todos los jugadores se unan.";
            case RECHAZADA:
                return "Postulación rechazada automáticamente.\n" +
                        "   Motivo: " + postulacion.getMotivoRechazo();
            case PENDIENTE:
                return "Postulación enviada exitosamente.\n" +
                        "   Esperando respuesta del organizador...";
            default:
                return "Postulación procesada";
        }
    }
}
