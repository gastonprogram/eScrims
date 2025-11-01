package controller;

import model.Postulacion;
import model.Scrim;
import model.Usuario;
import model.Persistencia.RepositorioFactory;
import model.Persistencia.RepositorioScrim;
import model.Persistencia.RepositorioUsuario;

import java.util.List;

/**
 * Controlador para gestionar las postulaciones a scrims.
 * Maneja la lógica de negocio relacionada con postulaciones:
 * - Crear postulaciones
 * - Aceptar/rechazar postulaciones (organizador)
 * - Listar postulaciones pendientes
 * 
 * Siguiendo el patrón de desacoplamiento: NO depende de las vistas.
 * 
 * @author eScrims Team
 */
public class PostulacionController {
    private final RepositorioScrim repositorioScrim;
    private final RepositorioUsuario repositorioUsuario;

    public PostulacionController() {
        this.repositorioScrim = RepositorioFactory.getRepositorioScrim();
        this.repositorioUsuario = RepositorioFactory.getRepositorioUsuario();
    }

    /**
     * Un usuario se postula a un scrim.
     * 
     * @param scrimId         ID del scrim
     * @param userId          ID del usuario que se postula
     * @param rangoUsuario    Rango del usuario (valor numérico, ej: 1000 para Gold)
     * @param latenciaUsuario Latencia del usuario en ms
     * @return Mensaje de éxito o error
     */
    public String postularAScrim(String scrimId, String userId, int rangoUsuario, int latenciaUsuario) {
        try {
            // Validar que el scrim existe
            Scrim scrim = repositorioScrim.buscarPorId(scrimId);
            if (scrim == null) {
                return "Error: El scrim no existe";
            }

            // Validar que el usuario existe
            Usuario usuario = repositorioUsuario.buscarPorId(userId);
            if (usuario == null) {
                return "Error: El usuario no existe";
            }

            // Validar que el usuario no es el organizador
            if (scrim.getCreatedBy().equals(userId)) {
                return "Error: El organizador no puede postularse a su propio scrim";
            }

            // Validar que el usuario no se haya postulado antes
            if (scrim.yaSePostulo(userId)) {
                return "Error: Ya te has postulado a este scrim";
            }

            // Crear la postulación
            Postulacion postulacion = new Postulacion(scrimId, userId, rangoUsuario, latenciaUsuario);

            // El estado del scrim se encarga de validar y procesar la postulación
            scrim.postular(postulacion);

            // Actualizar en el repositorio
            repositorioScrim.actualizar(scrim);

            // Verificar el estado de la postulación para dar feedback
            if (postulacion.getEstado() == Postulacion.EstadoPostulacion.RECHAZADA) {
                return "Postulación rechazada: " + postulacion.getMotivoRechazo();
            } else if (postulacion.getEstado() == Postulacion.EstadoPostulacion.ACEPTADA) {
                // Verificar si el scrim pasó a LOBBY_ARMADO
                if ("LOBBY_ARMADO".equals(scrim.getState().getEstado())) {
                    return "Postulación aceptada. ¡El lobby está completo! Estado: LOBBY_ARMADO";
                }
                return "Postulación enviada exitosamente. Estado: ACEPTADA";
            } else {
                return "Postulación enviada exitosamente. Estado: PENDIENTE";
            }

        } catch (IllegalStateException e) {
            return "Error: " + e.getMessage();
        } catch (Exception e) {
            return "Error inesperado: " + e.getMessage();
        }
    }

    /**
     * El organizador acepta una postulación pendiente.
     * 
     * @param scrimId       ID del scrim
     * @param userId        ID del usuario postulante
     * @param organizadorId ID del organizador (para validar permisos)
     * @return Mensaje de éxito o error
     */
    public String aceptarPostulacion(String scrimId, String userId, String organizadorId) {
        try {
            Scrim scrim = repositorioScrim.buscarPorId(scrimId);
            if (scrim == null) {
                return "Error: El scrim no existe";
            }

            // Validar que quien acepta es el organizador
            if (!scrim.getCreatedBy().equals(organizadorId)) {
                return "Error: Solo el organizador puede aceptar postulaciones";
            }

            // Buscar la postulación
            Postulacion postulacion = scrim.getPostulaciones().stream()
                    .filter(p -> p.getUserId().equals(userId))
                    .findFirst()
                    .orElse(null);

            if (postulacion == null) {
                return "Error: No se encontró la postulación";
            }

            if (postulacion.getEstado() != Postulacion.EstadoPostulacion.PENDIENTE) {
                return "Error: Solo se pueden aceptar postulaciones pendientes";
            }

            // Aceptar la postulación
            postulacion.aceptar();
            repositorioScrim.actualizar(scrim);

            // Verificar si el scrim cambió de estado (a LOBBY_ARMADO)
            String estadoScrim = scrim.getState().getEstado();
            if ("LOBBY_ARMADO".equals(estadoScrim)) {
                return "Postulación aceptada. El lobby está completo! Se generaron las confirmaciones.";
            }

            return "Postulación aceptada exitosamente";

        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    /**
     * El organizador rechaza una postulación pendiente.
     * 
     * @param scrimId       ID del scrim
     * @param userId        ID del usuario postulante
     * @param organizadorId ID del organizador (para validar permisos)
     * @param motivo        Motivo del rechazo
     * @return Mensaje de éxito o error
     */
    public String rechazarPostulacion(String scrimId, String userId, String organizadorId, String motivo) {
        try {
            Scrim scrim = repositorioScrim.buscarPorId(scrimId);
            if (scrim == null) {
                return "Error: El scrim no existe";
            }

            // Validar que quien rechaza es el organizador
            if (!scrim.getCreatedBy().equals(organizadorId)) {
                return "Error: Solo el organizador puede rechazar postulaciones";
            }

            // Buscar la postulación
            Postulacion postulacion = scrim.getPostulaciones().stream()
                    .filter(p -> p.getUserId().equals(userId))
                    .findFirst()
                    .orElse(null);

            if (postulacion == null) {
                return "Error: No se encontró la postulación";
            }

            if (postulacion.getEstado() != Postulacion.EstadoPostulacion.PENDIENTE) {
                return "Error: Solo se pueden rechazar postulaciones pendientes";
            }

            // Rechazar la postulación
            postulacion.rechazar(motivo);
            repositorioScrim.actualizar(scrim);

            return "Postulación rechazada";

        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    /**
     * Lista todas las postulaciones pendientes de un scrim.
     * Solo el organizador puede ver esto.
     * 
     * @param scrimId       ID del scrim
     * @param organizadorId ID del organizador (para validar permisos)
     * @return Lista de postulaciones pendientes
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
     * Solo el organizador puede ver esto.
     * 
     * @param scrimId       ID del scrim
     * @param organizadorId ID del organizador (para validar permisos)
     * @return Lista de todas las postulaciones
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
