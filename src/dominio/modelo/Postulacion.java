package dominio.modelo;

import java.time.LocalDateTime;

/**
 * Representa una postulación de un usuario a un Scrim.
 * 
 * Una postulación pasa por diferentes estados:
 * - PENDIENTE: recién creada, esperando revisión del organizador
 * - ACEPTADA: el organizador aceptó la postulación
 * - RECHAZADA: el organizador rechazó la postulación
 * 
 * Contiene información adicional:
 * - Rango y latencia del usuario al momento de postular
 * - Motivo de rechazo si aplica
 * - Timestamps de creación y actualización
 * 
 * @author eScrims Team
 */
public class Postulacion {

    /**
     * Estados posibles de una postulación.
     */
    public enum EstadoPostulacion {
        PENDIENTE,
        ACEPTADA,
        RECHAZADA
    }

    private String id;
    private String scrimId;
    private String userId;
    private int rangoUsuario;
    private int latenciaUsuario;
    private EstadoPostulacion estado;
    private String motivoRechazo;
    private LocalDateTime fechaPostulacion;
    private LocalDateTime fechaActualizacion;

    /**
     * Constructor para crear una nueva postulación.
     * 
     * @param scrimId         ID del scrim al que se postula
     * @param userId          ID del usuario que se postula
     * @param rangoUsuario    Rango del usuario
     * @param latenciaUsuario Latencia del usuario
     */
    public Postulacion(String scrimId, String userId, int rangoUsuario, int latenciaUsuario) {
        this.id = java.util.UUID.randomUUID().toString();
        this.scrimId = scrimId;
        this.userId = userId;
        this.rangoUsuario = rangoUsuario;
        this.latenciaUsuario = latenciaUsuario;
        this.estado = EstadoPostulacion.PENDIENTE;
        this.fechaPostulacion = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
    }

    /**
     * Acepta la postulación.
     */
    public void aceptar() {
        System.out.println(this.estado);
        if (estado != EstadoPostulacion.PENDIENTE) {
            throw new IllegalStateException("Solo se pueden aceptar postulaciones pendientes");
        }
        this.estado = EstadoPostulacion.ACEPTADA;
        this.fechaActualizacion = LocalDateTime.now();
        System.out.println(this.estado);
    }

    /**
     * Rechaza la postulación con un motivo.
     * 
     * @param motivo El motivo del rechazo
     */
    public void rechazar(String motivo) {
        if (estado != EstadoPostulacion.PENDIENTE) {
            throw new IllegalStateException("Solo se pueden rechazar postulaciones pendientes");
        }
        this.estado = EstadoPostulacion.RECHAZADA;
        this.motivoRechazo = motivo;
        this.fechaActualizacion = LocalDateTime.now();
    }

    /**
     * Valida si el usuario cumple con los requisitos del scrim.
     * 
     * @param rangoMin    Rango mínimo requerido
     * @param rangoMax    Rango máximo requerido
     * @param latenciaMax Latencia máxima permitida
     * @return null si cumple todos los requisitos, mensaje de error si no
     */
    public String validarRequisitos(int rangoMin, int rangoMax, int latenciaMax) {
        if (rangoUsuario < rangoMin) {
            return "Rango insuficiente. Mínimo requerido: " + rangoMin;
        }

        if (rangoUsuario > rangoMax) {
            return "Rango demasiado alto. Máximo permitido: " + rangoMax;
        }

        if (latenciaMax != -1 && latenciaUsuario > latenciaMax) {
            return "Latencia demasiado alta. Máxima permitida: " + latenciaMax + "ms";
        }

        return null; // Cumple todos los requisitos
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

    public int getRangoUsuario() {
        return rangoUsuario;
    }

    public int getLatenciaUsuario() {
        return latenciaUsuario;
    }

    public EstadoPostulacion getEstado() {
        return estado;
    }

    public String getMotivoRechazo() {
        return motivoRechazo;
    }

    public LocalDateTime getFechaPostulacion() {
        return fechaPostulacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public boolean isPendiente() {
        return estado == EstadoPostulacion.PENDIENTE;
    }

    public boolean isAceptada() {
        return estado == EstadoPostulacion.ACEPTADA;
    }

    public boolean isRechazada() {
        return estado == EstadoPostulacion.RECHAZADA;
    }
}
