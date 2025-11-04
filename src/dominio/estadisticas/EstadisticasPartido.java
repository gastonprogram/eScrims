package dominio.estadisticas;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class EstadisticasPartido {
    private Long id;
    private Long partidoId;
    private Map<Long, EstadisticasJugador> estadisticasPorJugador;
    private Long mvpJugadorId;
    private LocalDateTime fechaRegistro;

    public EstadisticasPartido(Long partidoId) {
        this.partidoId = partidoId;
        this.estadisticasPorJugador = new HashMap<>();
        this.fechaRegistro = LocalDateTime.now();
    }

    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPartidoId() {
        return partidoId;
    }

    public Map<Long, EstadisticasJugador> getEstadisticasPorJugador() {
        return estadisticasPorJugador;
    }

    public Long getMvpJugadorId() {
        return mvpJugadorId;
    }

    public void setMvpJugadorId(Long mvpJugadorId) {
        this.mvpJugadorId = mvpJugadorId;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }
}
