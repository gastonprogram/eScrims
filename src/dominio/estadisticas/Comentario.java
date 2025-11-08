package dominio.estadisticas;

import java.time.LocalDateTime;

public class Comentario {
    public enum EstadoModeracion {
        PENDIENTE, APROBADO, RECHAZADO
    }

    private Long id;
    private String jugadorId; // Cambiado de Long a String para usar IDs directos
    private String scrimId; // Cambiado de partidoId (Long) a scrimId (String) para consistencia
    private String contenido;
    private int rating; // 1-5 estrellas
    private EstadoModeracion estado;
    private LocalDateTime fechaCreacion;
    private String motivoRechazo;

    public Comentario(String jugadorId, String scrimId, String contenido, int rating) {
        this.jugadorId = jugadorId;
        this.scrimId = scrimId;
        this.contenido = contenido;
        this.rating = Math.max(1, Math.min(5, rating));
        this.estado = EstadoModeracion.PENDIENTE;
        this.fechaCreacion = LocalDateTime.now();
    }

    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getJugadorId() {
        return jugadorId;
    }

    public String getScrimId() {
        return scrimId;
    }

    public String getContenido() {
        return contenido;
    }

    public int getRating() {
        return rating;
    }

    public EstadoModeracion getEstado() {
        return estado;
    }

    public void setEstado(EstadoModeracion estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public String getMotivoRechazo() {
        return motivoRechazo;
    }

    public void setMotivoRechazo(String motivoRechazo) {
        this.motivoRechazo = motivoRechazo;
    }
}
