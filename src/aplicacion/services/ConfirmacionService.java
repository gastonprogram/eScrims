package aplicacion.services;

import java.util.List;
import java.util.stream.Collectors;

import dominio.modelo.Confirmacion;
import dominio.modelo.Scrim;
import infraestructura.persistencia.repository.RepositorioScrim;

/**
 * Servicio de aplicación para la gestión de confirmaciones de asistencia a
 * scrims.
 * 
 * Responsabilidades:
 * - Confirmar asistencia de jugadores
 * - Rechazar asistencia
 * - Listar confirmaciones pendientes
 * - Validar estados y permisos
 * 
 * Las confirmaciones se generan automáticamente cuando un scrim
 * pasa al estado LOBBY_ARMADO (todos los slots llenos).
 * 
 * @author eScrims Team
 */
public class ConfirmacionService {

    private final RepositorioScrim repositorioScrim;

    public ConfirmacionService(RepositorioScrim repositorioScrim) {
        this.repositorioScrim = repositorioScrim;
    }

    /**
     * Un jugador confirma su asistencia al scrim.
     * 
     * @param scrimId ID del scrim
     * @param userId  ID del usuario que confirma
     * @return La confirmación confirmada
     * @throws IllegalArgumentException Si el scrim no existe o no hay confirmación
     * @throws IllegalStateException    Si la confirmación ya fue respondida
     */
    public Confirmacion confirmarAsistencia(String scrimId, String userId) {
        Scrim scrim = repositorioScrim.buscarPorId(scrimId);
        if (scrim == null) {
            throw new IllegalArgumentException("El scrim no existe");
        }

        // Buscar la confirmación del usuario
        Confirmacion confirmacion = scrim.getConfirmaciones().stream()
                .filter(c -> c.getUserId().equals(userId))
                .findFirst()
                .orElseThrow(
                        () -> new IllegalArgumentException("No tienes una confirmación pendiente para este scrim"));

        if (confirmacion.getEstado() != Confirmacion.EstadoConfirmacion.PENDIENTE) {
            throw new IllegalStateException("Ya has respondido a esta confirmación");
        }

        // Confirmar la confirmación
        confirmacion.confirmar();

        // El estado se encarga de procesar y verificar transiciones
        scrim.confirmar(confirmacion);

        repositorioScrim.actualizar(scrim);

        return confirmacion;
    }

    /**
     * Un jugador rechaza su asistencia al scrim.
     * Esto libera el slot para que otro jugador pueda unirse.
     * 
     * @param scrimId ID del scrim
     * @param userId  ID del usuario que rechaza
     * @return La confirmación rechazada
     * @throws IllegalArgumentException Si el scrim no existe o no hay confirmación
     * @throws IllegalStateException    Si la confirmación ya fue respondida
     */
    public Confirmacion rechazarAsistencia(String scrimId, String userId) {
        Scrim scrim = repositorioScrim.buscarPorId(scrimId);
        if (scrim == null) {
            throw new IllegalArgumentException("El scrim no existe");
        }

        // Buscar la confirmación del usuario
        Confirmacion confirmacion = scrim.getConfirmaciones().stream()
                .filter(c -> c.getUserId().equals(userId))
                .findFirst()
                .orElseThrow(
                        () -> new IllegalArgumentException("No tienes una confirmación pendiente para este scrim"));

        if (confirmacion.getEstado() != Confirmacion.EstadoConfirmacion.PENDIENTE) {
            throw new IllegalStateException("Ya has respondido a esta confirmación");
        }

        // Rechazar la confirmación
        confirmacion.rechazar();

        // El estado se encarga de procesar y cambiar el estado del scrim
        scrim.confirmar(confirmacion);

        repositorioScrim.actualizar(scrim);

        return confirmacion;
    }

    /**
     * Lista todas las confirmaciones de un scrim.
     * Útil para el organizador para ver quién confirmó.
     * 
     * @param scrimId       ID del scrim
     * @param organizadorId ID del organizador (para validar permisos)
     * @return Lista de confirmaciones
     * @throws IllegalArgumentException Si el scrim no existe o el usuario no es el
     *                                  organizador
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
     * @throws IllegalArgumentException Si el scrim no existe o el usuario no es el
     *                                  organizador
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
                .collect(Collectors.toList());
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

    /**
     * Obtiene todos los scrims donde el usuario tiene una confirmación pendiente.
     * 
     * @param userId ID del usuario
     * @return Lista de scrims con confirmaciones pendientes
     */
    public List<Scrim> obtenerScrimsConConfirmacionPendiente(String userId) {
        return repositorioScrim.obtenerTodos().stream()
                .filter(scrim -> scrim.getConfirmaciones().stream()
                        .anyMatch(c -> c.getUserId().equals(userId) &&
                                c.getEstado() == Confirmacion.EstadoConfirmacion.PENDIENTE))
                .collect(Collectors.toList());
    }
}
