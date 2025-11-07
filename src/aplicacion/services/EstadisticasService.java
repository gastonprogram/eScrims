package aplicacion.services;

import dominio.estadisticas.EstadisticasScrim;
import dominio.estadisticas.ReporteConducta;
import dominio.estadisticas.SistemaModeracion;
import dominio.modelo.Scrim;

import java.util.*;

/**
 * Servicio para manejar las estadísticas y moderación de scrims.
 */
public class EstadisticasService {

    private final Map<String, EstadisticasScrim> estadisticasPorScrim;
    private final SistemaModeracion sistemaModeracion;

    public EstadisticasService() {
        this.estadisticasPorScrim = new HashMap<>();
        this.sistemaModeracion = new SistemaModeracion();
    }

    /**
     * Crea o obtiene las estadísticas asociadas a un scrim
     */
    public EstadisticasScrim obtenerEstadisticasParaScrim(Scrim scrim) {
        return estadisticasPorScrim.computeIfAbsent(scrim.getId(), id -> new EstadisticasScrim(scrim));
    }

    /**
     * Registra el resultado final (equipo ganador) y finaliza el registro.
     */
    public void registrarResultado(Scrim scrim, String equipoGanador) {
        EstadisticasScrim e = obtenerEstadisticasParaScrim(scrim);
        e.finalizarPartida(equipoGanador);
    }

    /**
     * Registra un reporte de conducta y lo envía al sistema de moderación.
     */
    public void reportarConducta(String scrimId, ReporteConducta.TipoReporte tipo, ReporteConducta.Gravedad gravedad,
                                 String usuarioReportadoId, String usuarioReportadorId, String descripcion) {
        ReporteConducta reporte = new ReporteConducta(scrimId, usuarioReportadoId, usuarioReportadorId, tipo, gravedad, descripcion);
        // almacenar en estadisticas
        EstadisticasScrim e = estadisticasPorScrim.computeIfAbsent(scrimId, id -> new EstadisticasScrim(id));
        e.agregarReporte(reporte);
        // pasar al sistema de moderacion
        sistemaModeracion.registrarReporte(reporte);
    }

    public SistemaModeracion getSistemaModeracion() {
        return sistemaModeracion;
    }

    public Optional<EstadisticasScrim> buscarEstadisticas(String scrimId) {
        return Optional.ofNullable(estadisticasPorScrim.get(scrimId));
    }
}
