package model;

import model.juegos.Juego;
import model.utils.ScrimFormat;
import model.states.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Representa un Scrim (partida de práctica) en la plataforma eScrims.
 * 
 * Un Scrim contiene toda la información necesaria para organizar una partida:
 * - Juego y formato de la partida
 * - Requisitos (rangos, latencia, roles)
 * - Participantes (postulaciones y confirmaciones)
 * - Estado actual (usando State Pattern)
 * 
 * Esta clase colabora con:
 * - Juego: define qué juego se jugará y valida roles
 * - ScrimFormat: define el formato de la partida (5v5, etc.)
 * - ScrimState: maneja las transiciones de estado del scrim
 * - ScrimOrganizador: gestiona las acciones del organizador
 * 
 * @author eScrims Team
 */
public class Scrim {

    private String id;
    private Juego juego; // Cambiado de String a Juego para mejor diseño
    private ScrimFormat formato;
    private int rangoMin;
    private int rangoMax;
    private int latenciaMax;
    private LocalDateTime fechaHora;
    private int plazas;
    private List<String> rolesRequeridos;
    private List<Postulacion> postulaciones;
    private List<Confirmacion> confirmaciones;
    private String createdBy;
    private LocalDateTime createdAt;
    private ScrimState state;

    /**
     * Constructor protegido porque usa ScrimBuilder para instanciarse.
     * Este constructor aplica el patrón Builder para facilitar la creación
     * de scrims con múltiples parámetros opcionales.
     * 
     * @param juego           el juego del scrim
     * @param formato         el formato de la partida
     * @param fechaHora       fecha y hora programada
     * @param rangoMin        rango mínimo requerido
     * @param rangoMax        rango máximo permitido
     * @param rolesRequeridos lista de roles necesarios
     * @param latenciaMax     latencia máxima permitida
     * @param plazas          cantidad de plazas disponibles
     */
    protected Scrim(Juego juego, ScrimFormat formato, LocalDateTime fechaHora,
            int rangoMin, int rangoMax, List<String> rolesRequeridos,
            int latenciaMax, int plazas) {
        this.id = UUID.randomUUID().toString();
        this.juego = juego;
        this.formato = formato;
        this.fechaHora = fechaHora;
        this.rangoMin = rangoMin;
        this.rangoMax = rangoMax;
        this.rolesRequeridos = rolesRequeridos;
        this.latenciaMax = latenciaMax;
        this.plazas = plazas;
        this.postulaciones = new ArrayList<>();
        this.confirmaciones = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.state = new BuscandoState();
    }

    // Métodos para el State Pattern
    public void postular(Postulacion postulacion) {
        state.postular(this, postulacion);
    }

    public void confirmar(Confirmacion confirmacion) {
        state.confirmar(this, confirmacion);
    }

    public void iniciar() {
        state.iniciar(this);
    }

    public void finalizar() {
        state.finalizar(this);
    }

    public void cancelar() {
        state.cancelar(this);
    }

    // Getters necesarios para los estados y lógica de negocio
    public List<Postulacion> getPostulaciones() {
        return postulaciones;
    }

    public List<Confirmacion> getConfirmaciones() {
        return confirmaciones;
    }

    /**
     * Retorna solo las postulaciones aceptadas.
     */
    public List<Postulacion> getPostulacionesAceptadas() {
        return postulaciones.stream()
                .filter(Postulacion::isAceptada)
                .toList();
    }

    /**
     * Retorna solo las postulaciones pendientes.
     */
    public List<Postulacion> getPostulacionesPendientes() {
        return postulaciones.stream()
                .filter(Postulacion::isPendiente)
                .toList();
    }

    /**
     * Retorna solo las confirmaciones confirmadas.
     */
    public List<Confirmacion> getConfirmacionesConfirmadas() {
        return confirmaciones.stream()
                .filter(Confirmacion::isConfirmada)
                .toList();
    }

    /**
     * Verifica si un usuario ya se postuló a este scrim.
     */
    public boolean yaSePostulo(String userId) {
        return postulaciones.stream()
                .anyMatch(p -> p.getUserId().equals(userId));
    }

    /**
     * Obtiene las postulaciones aceptadas para mantener compatibilidad con vistas.
     * 
     * @deprecated Usar getPostulacionesAceptadas() en su lugar
     */
    @Deprecated
    public List<String> getListaPostulaciones() {
        return postulaciones.stream()
                .filter(Postulacion::isAceptada)
                .map(Postulacion::getUserId)
                .toList();
    }

    /**
     * Obtiene las confirmaciones confirmadas para mantener compatibilidad con
     * vistas.
     * 
     * @deprecated Usar getConfirmacionesConfirmadas() en su lugar
     */
    @Deprecated
    public List<String> getListaConfirmaciones() {
        return confirmaciones.stream()
                .filter(Confirmacion::isConfirmada)
                .map(Confirmacion::getUserId)
                .toList();
    }

    public int getPlazas() {
        return plazas;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    // Setter protegido para el state
    public void setState(ScrimState newState) {
        this.state = newState;
    }

    public String getEstado() {
        return state.getEstado();
    }

    // Getters adicionales necesarios para el organizador y validaciones

    public String getId() {
        return id;
    }

    public Juego getJuego() {
        return juego;
    }

    public ScrimFormat getFormato() {
        return formato;
    }

    public List<String> getRolesRequeridos() {
        return rolesRequeridos;
    }

    public int getRangoMin() {
        return rangoMin;
    }

    public int getRangoMax() {
        return rangoMax;
    }

    public int getLatenciaMax() {
        return latenciaMax;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public ScrimState getState() {
        return state;
    }
}