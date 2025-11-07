package dominio.estadisticas;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Reporte de conducta dentro del módulo de estadísticas.
 */
public class ReporteConducta {
    public enum TipoReporte {
        ABUSO_VERBAL,
        FEED_INTENCIONAL,
        ABANDONO,
        TRAMPA,
        OTRO
    }

    public enum Gravedad {
        LEVE,
        MODERADO,
        GRAVE
    }

    private String id;
    private String scrimId;
    private String usuarioReportadoId;
    private String usuarioReportadorId;
    private TipoReporte tipo;
    private Gravedad gravedad;
    private String descripcion;
    private LocalDateTime fechaHora;
    private boolean revisado;
    private boolean sancionado;
    private String comentariosModerador;

    public ReporteConducta(String scrimId, String usuarioReportadoId, String usuarioReportadorId,
                           TipoReporte tipo, Gravedad gravedad, String descripcion) {
        this.id = UUID.randomUUID().toString();
        this.scrimId = scrimId;
        this.usuarioReportadoId = usuarioReportadoId;
        this.usuarioReportadorId = usuarioReportadorId;
        this.tipo = tipo;
        this.gravedad = gravedad;
        this.descripcion = descripcion;
        this.fechaHora = LocalDateTime.now();
        this.revisado = false;
        this.sancionado = false;
    }

    // Getters y Setters
    public String getId() { return id; }
    public String getScrimId() { return scrimId; }
    public String getUsuarioReportadoId() { return usuarioReportadoId; }
    public String getUsuarioReportadorId() { return usuarioReportadorId; }
    public TipoReporte getTipo() { return tipo; }
    public Gravedad getGravedad() { return gravedad; }
    public String getDescripcion() { return descripcion; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public boolean isRevisado() { return revisado; }
    public boolean isSancionado() { return sancionado; }
    public String getComentariosModerador() { return comentariosModerador; }

    public void setRevisado(boolean revisado) { this.revisado = revisado; }
    public void setSancionado(boolean sancionado) { this.sancionado = sancionado; }
    public void setComentariosModerador(String comentariosModerador) { this.comentariosModerador = comentariosModerador; }

    @Override
    public String toString() {
        return String.format("Reporte [%s] - %s - %s - %s - %s - %s",
                id.substring(0, 8),
                tipo,
                gravedad,
                fechaHora,
                revisado ? "Revisado" : "Pendiente",
                sancionado ? "Sancionado" : "Sin sanción");
    }
}
