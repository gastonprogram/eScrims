package model;

import java.time.LocalDateTime;

/**
 * Representa la confirmación de asistencia de un jugador a un Scrim.
 * 
 * Una vez que el scrim está en estado "Lobby Armado", cada jugador aceptado
 * debe confirmar su asistencia. Esta clase registra esa confirmación.
 * 
 * Estados:
 * - PENDIENTE: esperando que el jugador confirme
 * - CONFIRMADA: el jugador confirmó su asistencia
 * - RECHAZADA: el jugador rechazó su asistencia
 * 
 * @author eScrims Team
 */
public class Confirmacion {

    /**
     * Estados posibles de una confirmación.
     */
    public enum EstadoConfirmacion {
        PENDIENTE,
        CONFIRMADA,
        RECHAZADA
    }

    private String id;
    private String scrimId;
    private String userId;
    private EstadoConfirmacion estado;
    private LocalDateTime fechaSolicitud;
    private LocalDateTime fechaRespuesta;

    /**
     * Constructor para crear una nueva confirmación.
     * 
     * @param scrimId ID del scrim
     * @param userId  ID del usuario que debe confirmar
     */
    public Confirmacion(String scrimId, String userId) {
        this.id = java.util.UUID.randomUUID().toString();
        this.scrimId = scrimId;
        this.userId = userId;
        this.estado = EstadoConfirmacion.PENDIENTE;
        this.fechaSolicitud = LocalDateTime.now();
    }

    /**
     * Marca la confirmación como confirmada.
     */
    public void confirmar() {
        if (estado != EstadoConfirmacion.PENDIENTE) {
            throw new IllegalStateException("Solo se pueden confirmar confirmaciones pendientes");
        }
        this.estado = EstadoConfirmacion.CONFIRMADA;
        this.fechaRespuesta = LocalDateTime.now();
    }

    /**
     * Marca la confirmación como rechazada.
     */
    public void rechazar() {
        if (estado != EstadoConfirmacion.PENDIENTE) {
            throw new IllegalStateException("Solo se pueden rechazar confirmaciones pendientes");
        }
        this.estado = EstadoConfirmacion.RECHAZADA;
        this.fechaRespuesta = LocalDateTime.now();
    }

    // Getters

    public String getId() {
        return id;
    }

    public String getScrimId() {
        return scrimId;
    }

    public String getUserId() {
        return userId;
    }

    public EstadoConfirmacion getEstado() {
        return estado;
    }

    public LocalDateTime getFechaSolicitud() {
        return fechaSolicitud;
    }

    public LocalDateTime getFechaRespuesta() {
        return fechaRespuesta;
    }

    public boolean isPendiente() {
        return estado == EstadoConfirmacion.PENDIENTE;
    }

    public boolean isConfirmada() {
        return estado == EstadoConfirmacion.CONFIRMADA;
    }

    public boolean isRechazada() {
        return estado == EstadoConfirmacion.RECHAZADA;
    }
}
