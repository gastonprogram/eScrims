package infraestructura.persistencia.repository;

import dominio.estadisticas.EstadisticasScrim;
import dominio.estadisticas.EstadisticasJugador;
import dominio.estadisticas.ReporteConducta;
import dominio.estadisticas.Comentario;
import java.util.List;
import java.util.Map;

/**
 * Interfaz para el repositorio de estadísticas, reportes y moderación.
 * Maneja la persistencia de todas las estadísticas del sistema.
 * 
 * @author eScrims Team
 */
public interface RepositorioEstadisticas {

    // ========== ESTADÍSTICAS DE SCRIM ==========

    /**
     * Guarda las estadísticas de un scrim.
     * 
     * @param estadisticas Estadísticas del scrim a guardar
     */
    void guardarEstadisticasScrim(EstadisticasScrim estadisticas);

    /**
     * Obtiene las estadísticas de un scrim por su ID.
     * 
     * @param scrimId ID del scrim
     * @return Estadísticas del scrim o null si no existen
     */
    EstadisticasScrim obtenerEstadisticasScrim(String scrimId);

    /**
     * Obtiene todas las estadísticas de scrims.
     * 
     * @return Lista de todas las estadísticas de scrims
     */
    List<EstadisticasScrim> obtenerTodasLasEstadisticasScrims();

    /**
     * Actualiza las estadísticas de un scrim existente.
     * 
     * @param estadisticas Estadísticas actualizadas
     */
    void actualizarEstadisticasScrim(EstadisticasScrim estadisticas);

    /**
     * Elimina las estadísticas de un scrim.
     * 
     * @param scrimId ID del scrim
     */
    void eliminarEstadisticasScrim(String scrimId);

    // ========== ESTADÍSTICAS DE JUGADOR ==========

    /**
     * Guarda las estadísticas de un jugador para un scrim específico.
     * 
     * @param scrimId      ID del scrim
     * @param jugadorId    ID del jugador
     * @param estadisticas Estadísticas del jugador
     */
    void guardarEstadisticasJugador(String scrimId, String jugadorId, EstadisticasJugador estadisticas);

    /**
     * Obtiene las estadísticas de un jugador para un scrim específico.
     * 
     * @param scrimId   ID del scrim
     * @param jugadorId ID del jugador
     * @return Estadísticas del jugador o null si no existen
     */
    EstadisticasJugador obtenerEstadisticasJugador(String scrimId, String jugadorId);

    /**
     * Obtiene todas las estadísticas de un jugador en todos los scrims.
     * 
     * @param jugadorId ID del jugador
     * @return Mapa de scrimId -> EstadisticasJugador
     */
    Map<String, EstadisticasJugador> obtenerEstadisticasJugadorGeneral(String jugadorId);

    // ========== REPORTES DE CONDUCTA ==========

    /**
     * Guarda un reporte de conducta.
     * 
     * @param reporte Reporte de conducta a guardar
     */
    void guardarReporteConducta(ReporteConducta reporte);

    /**
     * Obtiene todos los reportes de conducta de un scrim.
     * 
     * @param scrimId ID del scrim
     * @return Lista de reportes del scrim
     */
    List<ReporteConducta> obtenerReportesScrim(String scrimId);

    /**
     * Obtiene todos los reportes de conducta pendientes.
     * 
     * @return Lista de reportes pendientes de moderación
     */
    List<ReporteConducta> obtenerReportesPendientes();

    /**
     * Obtiene todos los reportes de conducta de un usuario.
     * 
     * @param usuarioId ID del usuario reportado
     * @return Lista de reportes del usuario
     */
    List<ReporteConducta> obtenerReportesUsuario(String usuarioId);

    /**
     * Actualiza el estado de un reporte de conducta.
     * 
     * @param reporteId   ID del reporte
     * @param nuevoEstado Nuevo estado del reporte
     */
    void actualizarEstadoReporte(String reporteId, String nuevoEstado);

    // ========== COMENTARIOS ==========

    /**
     * Guarda un comentario.
     * 
     * @param comentario Comentario a guardar
     */
    void guardarComentario(Comentario comentario);

    /**
     * Obtiene todos los comentarios de un scrim.
     * 
     * @param scrimId ID del scrim
     * @return Lista de comentarios del scrim
     */
    List<Comentario> obtenerComentariosScrim(String scrimId);

    /**
     * Obtiene comentarios pendientes de moderación.
     * 
     * @return Lista de comentarios flaggeados
     */
    List<Comentario> obtenerComentariosPendientes();

    /**
     * Actualiza el estado de moderación de un comentario.
     * 
     * @param comentarioId ID del comentario
     * @param aprobado     true si se aprueba, false si se rechaza
     */
    void moderarComentario(String comentarioId, boolean aprobado);

    // ========== UTILIDADES ==========

    /**
     * Obtiene el número total de estadísticas guardadas.
     * 
     * @return Número total de estadísticas
     */
    int contarEstadisticas();

    /**
     * Limpia todas las estadísticas (útil para testing).
     */
    void limpiarTodas();
}