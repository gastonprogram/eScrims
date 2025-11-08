package aplicacion.services;

import dominio.estadisticas.Comentario;
import dominio.estadisticas.EstadisticasScrim;
import dominio.estadisticas.EstadisticasJugador;
import dominio.estadisticas.ReporteConducta;
import dominio.estadisticas.SistemaModeracion;
import dominio.modelo.Scrim;
import infraestructura.persistencia.repository.RepositorioEstadisticas;
import infraestructura.persistencia.repository.RepositorioFactory;

import java.util.*;

/**
 * Servicio para manejar las estadísticas, comentarios y moderación de scrims.
 * Integra persistencia para mantener los datos entre sesiones.
 */
public class EstadisticasService {

    private final RepositorioEstadisticas repositorioEstadisticas;
    private final SistemaModeracion sistemaModeracion;
    private Long siguienteIdComentario;

    public EstadisticasService() {
        this.repositorioEstadisticas = RepositorioFactory.getRepositorioEstadisticas();
        this.sistemaModeracion = new SistemaModeracion();
        this.siguienteIdComentario = 1L;

        // Cargar reportes existentes en el sistema de moderación
        cargarReportesEnSistemaModeracion();
    }

    /**
     * Constructor con inyección de dependencia para testing.
     */
    public EstadisticasService(RepositorioEstadisticas repositorioEstadisticas) {
        this.repositorioEstadisticas = repositorioEstadisticas;
        this.sistemaModeracion = new SistemaModeracion();
        this.siguienteIdComentario = 1L;
        cargarReportesEnSistemaModeracion();
    }

    /**
     * Carga los reportes pendientes en el sistema de moderación al inicializar.
     */
    private void cargarReportesEnSistemaModeracion() {
        List<ReporteConducta> reportesPendientes = repositorioEstadisticas.obtenerReportesPendientes();
        for (ReporteConducta reporte : reportesPendientes) {
            sistemaModeracion.registrarReporte(reporte);
        }
    }

    // ========== ESTADÍSTICAS DE SCRIM ==========

    /**
     * Crea o obtiene las estadísticas asociadas a un scrim con persistencia.
     */
    public EstadisticasScrim obtenerEstadisticasParaScrim(Scrim scrim) {
        EstadisticasScrim estadisticas = repositorioEstadisticas.obtenerEstadisticasScrim(scrim.getId());

        if (estadisticas == null) {
            estadisticas = new EstadisticasScrim(scrim);
            repositorioEstadisticas.guardarEstadisticasScrim(estadisticas);
        }

        return estadisticas;
    }

    /**
     * Registra el resultado final (equipo ganador) y finaliza el registro.
     */
    public void registrarResultado(Scrim scrim, String equipoGanador) {
        EstadisticasScrim estadisticas = obtenerEstadisticasParaScrim(scrim);
        estadisticas.finalizarPartida(equipoGanador);
        repositorioEstadisticas.actualizarEstadisticasScrim(estadisticas);
    }

    /**
     * Obtiene todas las estadísticas de scrims guardadas.
     */
    public List<EstadisticasScrim> obtenerTodasLasEstadisticasScrims() {
        return repositorioEstadisticas.obtenerTodasLasEstadisticasScrims();
    }

    // ========== REPORTES DE CONDUCTA ==========

    /**
     * Registra un reporte de conducta con persistencia y lo envía al sistema de
     * moderación.
     */
    public void reportarConducta(String scrimId, ReporteConducta.TipoReporte tipo, ReporteConducta.Gravedad gravedad,
            String usuarioReportadoId, String usuarioReportadorId, String descripcion) {

        ReporteConducta reporte = new ReporteConducta(scrimId, usuarioReportadoId, usuarioReportadorId, tipo, gravedad,
                descripcion);

        // Guardar en persistencia
        repositorioEstadisticas.guardarReporteConducta(reporte);

        // Añadir a las estadísticas del scrim
        EstadisticasScrim estadisticas = repositorioEstadisticas.obtenerEstadisticasScrim(scrimId);
        if (estadisticas == null) {
            estadisticas = new EstadisticasScrim(scrimId);
        }
        estadisticas.agregarReporte(reporte);
        repositorioEstadisticas.actualizarEstadisticasScrim(estadisticas);

        // Registrar en el sistema de moderación
        sistemaModeracion.registrarReporte(reporte);
    }

    /**
     * Obtiene reportes de un scrim específico.
     */
    public List<ReporteConducta> obtenerReportesScrim(String scrimId) {
        return repositorioEstadisticas.obtenerReportesScrim(scrimId);
    }

    /**
     * Obtiene reportes de un usuario específico.
     */
    public List<ReporteConducta> obtenerReportesUsuario(String usuarioId) {
        return repositorioEstadisticas.obtenerReportesUsuario(usuarioId);
    }

    /**
     * Obtiene reportes pendientes de moderación.
     */
    public List<ReporteConducta> obtenerReportesPendientes() {
        return repositorioEstadisticas.obtenerReportesPendientes();
    }

    /**
     * Actualiza el estado de un reporte.
     */
    public void actualizarEstadoReporte(String reporteId, String nuevoEstado) {
        repositorioEstadisticas.actualizarEstadoReporte(reporteId, nuevoEstado);
    }

    public SistemaModeracion getSistemaModeracion() {
        return sistemaModeracion;
    }

    public Optional<EstadisticasScrim> buscarEstadisticas(String scrimId) {
        EstadisticasScrim estadisticas = repositorioEstadisticas.obtenerEstadisticasScrim(scrimId);
        return Optional.ofNullable(estadisticas);
    }

    // ========== ESTADÍSTICAS DE JUGADOR ==========

    /**
     * Registra las estadísticas individuales de un jugador en un scrim específico
     * con persistencia.
     */
    public void registrarEstadisticasJugador(String scrimId, String jugadorId, int kills, int deaths, int assists,
            int puntuacion) {
        EstadisticasScrim estadisticas = repositorioEstadisticas.obtenerEstadisticasScrim(scrimId);
        if (estadisticas == null) {
            estadisticas = new EstadisticasScrim(scrimId);
        }

        estadisticas.registrarEstadisticasJugador(jugadorId, kills, assists, deaths, puntuacion);
        repositorioEstadisticas.actualizarEstadisticasScrim(estadisticas);
    }

    /**
     * Obtiene las estadísticas de un jugador específico en un scrim.
     */
    public EstadisticasJugador obtenerEstadisticasJugador(String scrimId, String jugadorId) {
        return repositorioEstadisticas.obtenerEstadisticasJugador(scrimId, jugadorId);
    }

    /**
     * Obtiene todas las estadísticas de un jugador en todos los scrims.
     */
    public Map<String, EstadisticasJugador> obtenerEstadisticasJugadorGeneral(String jugadorId) {
        return repositorioEstadisticas.obtenerEstadisticasJugadorGeneral(jugadorId);
    }

    /**
     * Designa al MVP de un scrim específico.
     */
    public void designarMVP(String scrimId, String jugadorId) {
        EstadisticasScrim estadisticas = repositorioEstadisticas.obtenerEstadisticasScrim(scrimId);
        if (estadisticas != null) {
            estadisticas.designarMVP(jugadorId);
            repositorioEstadisticas.actualizarEstadisticasScrim(estadisticas);
        }
    }

    /**
     * Obtiene el ranking de jugadores por KDA en un scrim.
     */
    public List<EstadisticasJugador> obtenerRankingPorKDA(String scrimId) {
        EstadisticasScrim estadisticas = repositorioEstadisticas.obtenerEstadisticasScrim(scrimId);
        return estadisticas != null ? estadisticas.obtenerRankingPorKDA() : new ArrayList<>();
    }

    /**
     * Obtiene el ranking de jugadores por puntuación en un scrim.
     */
    public List<EstadisticasJugador> obtenerRankingPorPuntuacion(String scrimId) {
        EstadisticasScrim estadisticas = repositorioEstadisticas.obtenerEstadisticasScrim(scrimId);
        return estadisticas != null ? estadisticas.obtenerRankingPorPuntuacion() : new ArrayList<>();
    }

    /**
     * Obtiene todas las estadísticas de jugadores de un scrim.
     */
    public Collection<EstadisticasJugador> obtenerTodasLasEstadisticasJugadores(String scrimId) {
        EstadisticasScrim estadisticas = repositorioEstadisticas.obtenerEstadisticasScrim(scrimId);
        if (estadisticas != null) {
            return estadisticas.obtenerTodasLasEstadisticas();
        }
        return new ArrayList<>();
    }

    /**
     * Obtiene el MVP actual de un scrim.
     */
    public EstadisticasJugador obtenerMVP(String scrimId) {
        EstadisticasScrim estadisticas = repositorioEstadisticas.obtenerEstadisticasScrim(scrimId);
        return estadisticas != null ? estadisticas.obtenerMVP() : null;
    }

    // ========== GESTIÓN DE COMENTARIOS ==========

    /**
     * Crea un comentario para un scrim y jugador específico con persistencia.
     */
    public Comentario crearComentario(String jugadorId, String scrimId, String contenido, int rating) {
        Comentario comentario = new Comentario(jugadorId, scrimId, contenido, rating);
        comentario.setId(siguienteIdComentario++);

        // Guardar en persistencia
        repositorioEstadisticas.guardarComentario(comentario);

        return comentario;
    }

    /**
     * Obtiene todos los comentarios de un scrim específico.
     */
    public List<Comentario> obtenerComentariosDeScrim(String scrimId) {
        return repositorioEstadisticas.obtenerComentariosScrim(scrimId);
    }

    /**
     * Modera un comentario (aprueba o rechaza) con persistencia.
     */
    public void moderarComentario(Long comentarioId, Comentario.EstadoModeracion nuevoEstado, String motivoRechazo) {
        boolean aprobado = (nuevoEstado == Comentario.EstadoModeracion.APROBADO);
        repositorioEstadisticas.moderarComentario(String.valueOf(comentarioId), aprobado);
    }

    /**
     * Obtiene todos los comentarios pendientes de moderación.
     */
    public List<Comentario> obtenerComentariosPendientes() {
        return repositorioEstadisticas.obtenerComentariosPendientes();
    }

    /**
     * Obtiene un comentario por su ID (simulado con búsqueda en todos los
     * comentarios).
     */
    public Optional<Comentario> buscarComentario(Long comentarioId) {
        // Como no tenemos un método directo, buscamos en todos los comentarios
        // pendientes
        return obtenerComentariosPendientes().stream()
                .filter(comentario -> comentarioId.equals(comentario.getId()))
                .findFirst();
    }
}
