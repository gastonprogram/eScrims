package controller;

import model.Confirmacion;
import model.Scrim;
import model.Persistencia.RepositorioFactory;
import model.Persistencia.RepositorioScrim;

import java.util.List;

/**
 * Controlador para gestionar las confirmaciones de asistencia a scrims.
 * 
 * Maneja la lógica de negocio relacionada con confirmaciones:
 * - Confirmar asistencia de un jugador
 * - Rechazar asistencia
 * - Listar confirmaciones pendientes
 * 
 * Las confirmaciones se generan automáticamente cuando un scrim
 * pasa al estado LOBBY_ARMADO (todos los slots llenos).
 * 
 * Siguiendo el patrón de desacoplamiento: NO depende de las vistas.
 * 
 * @author eScrims Team
 */
public class ConfirmacionController {
    private final RepositorioScrim repositorioScrim;

    public ConfirmacionController() {
        this.repositorioScrim = RepositorioFactory.getRepositorioScrim();
    }

    /**
     * Un jugador confirma su asistencia al scrim.
     * 
     * @param scrimId ID del scrim
     * @param userId  ID del usuario que confirma
     * @return Mensaje de éxito o error
     */
    public String confirmarAsistencia(String scrimId, String userId) {
        try {
            Scrim scrim = repositorioScrim.buscarPorId(scrimId);
            if (scrim == null) {
                return "Error: El scrim no existe";
            }

            // Buscar la confirmación del usuario
            Confirmacion confirmacion = scrim.getConfirmaciones().stream()
                    .filter(c -> c.getUserId().equals(userId))
                    .findFirst()
                    .orElse(null);

            if (confirmacion == null) {
                return "Error: No tienes una confirmación pendiente para este scrim";
            }

            if (confirmacion.getEstado() != Confirmacion.EstadoConfirmacion.PENDIENTE) {
                return "Error: Ya has respondido a esta confirmación";
            }

            // Confirmar la confirmación
            confirmacion.confirmar();

            // El estado se encarga de procesar y verificar transiciones
            scrim.confirmar(confirmacion);

            repositorioScrim.actualizar(scrim);

            // Verificar si el scrim cambió a CONFIRMADO
            String estadoScrim = scrim.getState().getEstado();
            if ("CONFIRMADO".equals(estadoScrim)) {
                return "¡Confirmación exitosa! Todos los jugadores confirmaron. El scrim está listo para comenzar.";
            }

            return "Confirmación exitosa. Esperando a los demás jugadores...";

        } catch (IllegalStateException e) {
            return "Error: " + e.getMessage();
        } catch (Exception e) {
            return "Error inesperado: " + e.getMessage();
        }
    }

    /**
     * Un jugador rechaza su asistencia al scrim.
     * Esto libera el slot para que otro jugador pueda unirse.
     * 
     * @param scrimId ID del scrim
     * @param userId  ID del usuario que rechaza
     * @return Mensaje de éxito o error
     */
    public String rechazarAsistencia(String scrimId, String userId) {
        try {
            Scrim scrim = repositorioScrim.buscarPorId(scrimId);
            if (scrim == null) {
                return "Error: El scrim no existe";
            }

            // Buscar la confirmación del usuario
            Confirmacion confirmacion = scrim.getConfirmaciones().stream()
                    .filter(c -> c.getUserId().equals(userId))
                    .findFirst()
                    .orElse(null);

            if (confirmacion == null) {
                return "Error: No tienes una confirmación pendiente para este scrim";
            }

            if (confirmacion.getEstado() != Confirmacion.EstadoConfirmacion.PENDIENTE) {
                return "Error: Ya has respondido a esta confirmación";
            }

            // Rechazar la confirmación
            confirmacion.rechazar();

            // El estado se encarga de procesar y cambiar el estado del scrim
            scrim.confirmar(confirmacion);

            repositorioScrim.actualizar(scrim);

            return "Has rechazado la asistencia. El slot quedó disponible.";

        } catch (IllegalStateException e) {
            return "Error: " + e.getMessage();
        } catch (Exception e) {
            return "Error inesperado: " + e.getMessage();
        }
    }

    /**
     * Lista todas las confirmaciones de un scrim.
     * Útil para el organizador para ver quién confirmó.
     * 
     * @param scrimId       ID del scrim
     * @param organizadorId ID del organizador (para validar permisos)
     * @return Lista de confirmaciones
     */
    public List<Confirmacion> listarConfirmaciones(String scrimId, String organizadorId) {
        Scrim scrim = repositorioScrim.buscarPorId(scrimId);
        if (scrim == null) {
            throw new IllegalArgumentException("El scrim no existe");
        }

        if (!scrim.getCreatedBy().equals(organizadorId)) {
            throw new IllegalArgumentException("Solo el organizador puede ver las confirmaciones");
        }

        return scrim.getConfirmaciones();
    }

    /**
     * Lista las confirmaciones pendientes de un scrim.
     * 
     * @param scrimId       ID del scrim
     * @param organizadorId ID del organizador (para validar permisos)
     * @return Lista de confirmaciones pendientes
     */
    public List<Confirmacion> listarConfirmacionesPendientes(String scrimId, String organizadorId) {
        Scrim scrim = repositorioScrim.buscarPorId(scrimId);
        if (scrim == null) {
            throw new IllegalArgumentException("El scrim no existe");
        }

        if (!scrim.getCreatedBy().equals(organizadorId)) {
            throw new IllegalArgumentException("Solo el organizador puede ver las confirmaciones");
        }

        return scrim.getConfirmaciones().stream()
                .filter(c -> c.getEstado() == Confirmacion.EstadoConfirmacion.PENDIENTE)
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Obtiene la confirmación de un usuario específico.
     * 
     * @param scrimId ID del scrim
     * @param userId  ID del usuario
     * @return La confirmación o null si no existe
     */
    public Confirmacion obtenerConfirmacion(String scrimId, String userId) {
        Scrim scrim = repositorioScrim.buscarPorId(scrimId);
        if (scrim == null) {
            return null;
        }

        return scrim.getConfirmaciones().stream()
                .filter(c -> c.getUserId().equals(userId))
                .findFirst()
                .orElse(null);
    }

    /**
     * Verifica si un usuario tiene una confirmación pendiente.
     * 
     * @param scrimId ID del scrim
     * @param userId  ID del usuario
     * @return true si tiene confirmación pendiente, false en caso contrario
     */
    public boolean tieneConfirmacionPendiente(String scrimId, String userId) {
        Confirmacion confirmacion = obtenerConfirmacion(scrimId, userId);
        return confirmacion != null &&
                confirmacion.getEstado() == Confirmacion.EstadoConfirmacion.PENDIENTE;
    }
}
