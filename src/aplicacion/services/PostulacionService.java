package aplicacion.services;

import java.util.List;

import dominio.modelo.Postulacion;
import dominio.modelo.Scrim;
import dominio.modelo.Usuario;
import infraestructura.persistencia.repository.RepositorioScrim;
import infraestructura.persistencia.repository.RepositorioUsuario;

/**
 * Servicio de aplicación para la gestión de postulaciones a scrims.
 * 
 * Responsabilidades:
 * - Crear postulaciones validando reglas de negocio
 * - Aceptar/rechazar postulaciones por parte del organizador
 * - Listar postulaciones pendientes
 * - Validar permisos y estados
 * 
 * @author eScrims Team
 */
public class PostulacionService {

    private final RepositorioScrim repositorioScrim;
    private final RepositorioUsuario repositorioUsuario;

    public PostulacionService(RepositorioScrim repositorioScrim, RepositorioUsuario repositorioUsuario) {
        this.repositorioScrim = repositorioScrim;
        this.repositorioUsuario = repositorioUsuario;
    }

    /**
     * Un usuario se postula a un scrim.
     * 
     * @param scrimId         ID del scrim
     * @param userId          ID del usuario que se postula
     * @param rangoUsuario    Rango del usuario (valor numérico)
     * @param latenciaUsuario Latencia del usuario en ms
     * @return La postulación creada
     * @throws IllegalArgumentException Si los datos son inválidos
     * @throws IllegalStateException    Si el scrim no acepta postulaciones
     */
    public Postulacion postularAScrim(String scrimId, String userId, int rangoUsuario, int latenciaUsuario) {
        // Validar que el scrim existe
        Scrim scrim = repositorioScrim.buscarPorId(scrimId);
        if (scrim == null) {
            throw new IllegalArgumentException("El scrim no existe");
        }

        // Validar que el usuario existe
        Usuario usuario = repositorioUsuario.buscarPorId(userId);
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no existe");
        }

        // Validar que el usuario no es el organizador
        if (scrim.getCreatedBy().equals(userId)) {
            throw new IllegalArgumentException("El organizador no puede postularse a su propio scrim");
        }

        // Validar que el usuario no se haya postulado antes
        if (scrim.yaSePostulo(userId)) {
            throw new IllegalArgumentException("Ya te has postulado a este scrim");
        }

        // Crear la postulación
        Postulacion postulacion = new Postulacion(scrimId, userId, rangoUsuario, latenciaUsuario);

        // El estado del scrim se encarga de validar y procesar la postulación
        scrim.postular(postulacion);

        // Actualizar en el repositorio
        repositorioScrim.actualizar(scrim);

        return postulacion;
    }

    /**
     * El organizador acepta una postulación pendiente.
     * 
     * @param scrimId       ID del scrim
     * @param userId        ID del usuario postulante
     * @param organizadorId ID del organizador
     * @return La postulación aceptada
     * @throws IllegalArgumentException Si no se encuentra la postulación
     * @throws IllegalStateException    Si la postulación no está pendiente
     */
    public Postulacion aceptarPostulacion(String scrimId, String userId, String organizadorId) {
        Scrim scrim = repositorioScrim.buscarPorId(scrimId);
        if (scrim == null) {
            throw new IllegalArgumentException("El scrim no existe");
        }

        // Validar que quien acepta es el organizador
        if (!scrim.getCreatedBy().equals(organizadorId)) {
            throw new IllegalArgumentException("Solo el organizador puede aceptar postulaciones");
        }

        // Buscar la postulación
        Postulacion postulacion = scrim.getPostulaciones().stream()
                .filter(p -> p.getUserId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No se encontró la postulación"));

        if (postulacion.getEstado() != Postulacion.EstadoPostulacion.PENDIENTE) {
            throw new IllegalStateException("Solo se pueden aceptar postulaciones pendientes");
        }

        // Aceptar la postulación
        postulacion.aceptar();
        repositorioScrim.actualizar(scrim);

        return postulacion;
    }

    /**
     * El organizador rechaza una postulación pendiente.
     * 
     * @param scrimId       ID del scrim
     * @param userId        ID del usuario postulante
     * @param organizadorId ID del organizador
     * @param motivo        Motivo del rechazo
     * @return La postulación rechazada
     * @throws IllegalArgumentException Si no se encuentra la postulación
     * @throws IllegalStateException    Si la postulación no está pendiente
     */
    public Postulacion rechazarPostulacion(String scrimId, String userId, String organizadorId, String motivo) {
        Scrim scrim = repositorioScrim.buscarPorId(scrimId);
        if (scrim == null) {
            throw new IllegalArgumentException("El scrim no existe");
        }

        // Validar que quien rechaza es el organizador
        if (!scrim.getCreatedBy().equals(organizadorId)) {
            throw new IllegalArgumentException("Solo el organizador puede rechazar postulaciones");
        }

        // Buscar la postulación
        Postulacion postulacion = scrim.getPostulaciones().stream()
                .filter(p -> p.getUserId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No se encontró la postulación"));

        if (postulacion.getEstado() != Postulacion.EstadoPostulacion.PENDIENTE) {
            throw new IllegalStateException("Solo se pueden rechazar postulaciones pendientes");
        }

        // Rechazar la postulación
        postulacion.rechazar(motivo);
        repositorioScrim.actualizar(scrim);

        return postulacion;
    }

    /**
     * Lista todas las postulaciones pendientes de un scrim.
     * 
     * @param scrimId       ID del scrim
     * @param organizadorId ID del organizador
     * @return Lista de postulaciones pendientes
     * @throws IllegalArgumentException Si el scrim no existe o el usuario no es el
     *                                  organizador
     */
    public List<Postulacion> listarPostulacionesPendientes(String scrimId, String organizadorId) {
        Scrim scrim = repositorioScrim.buscarPorId(scrimId);
        if (scrim == null) {
            throw new IllegalArgumentException("El scrim no existe");
        }

        if (!scrim.getCreatedBy().equals(organizadorId)) {
            throw new IllegalArgumentException("Solo el organizador puede ver las postulaciones");
        }

        return scrim.getPostulacionesPendientes();
    }

    /**
     * Lista todas las postulaciones de un scrim (todas, no solo pendientes).
     * 
     * @param scrimId       ID del scrim
     * @param organizadorId ID del organizador
     * @return Lista de todas las postulaciones
     * @throws IllegalArgumentException Si el scrim no existe o el usuario no es el
     *                                  organizador
     */
    public List<Postulacion> listarTodasLasPostulaciones(String scrimId, String organizadorId) {
        Scrim scrim = repositorioScrim.buscarPorId(scrimId);
        if (scrim == null) {
            throw new IllegalArgumentException("El scrim no existe");
        }

        if (!scrim.getCreatedBy().equals(organizadorId)) {
            throw new IllegalArgumentException("Solo el organizador puede ver las postulaciones");
        }

        return scrim.getPostulaciones();
    }

    /**
     * Verifica si un usuario ya se postuló a un scrim.
     * 
     * @param scrimId ID del scrim
     * @param userId  ID del usuario
     * @return true si ya se postuló, false en caso contrario
     */
    public boolean yaSePostulo(String scrimId, String userId) {
        Scrim scrim = repositorioScrim.buscarPorId(scrimId);
        if (scrim == null) {
            return false;
        }
        return scrim.yaSePostulo(userId);
    }

    /**
     * Obtiene la postulación de un usuario en un scrim específico.
     * 
     * @param scrimId ID del scrim
     * @param userId  ID del usuario
     * @return La postulación o null si no existe
     */
    public Postulacion obtenerPostulacion(String scrimId, String userId) {
        Scrim scrim = repositorioScrim.buscarPorId(scrimId);
        if (scrim == null) {
            return null;
        }

        return scrim.getPostulaciones().stream()
                .filter(p -> p.getUserId().equals(userId))
                .findFirst()
                .orElse(null);
    }
}
