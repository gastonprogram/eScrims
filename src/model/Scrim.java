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
    private List<String> listaPostulaciones;
    private List<String> listaConfirmaciones;
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
        this.listaPostulaciones = new ArrayList<>();
        this.listaConfirmaciones = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.state = new BuscandoState();
    }

    // Métodos para el State Pattern
    public void postular(String userId) {
        state.postular(this, userId);
    }

    public void confirmar(String userId) {
        state.confirmar(this, userId);
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

    // Getters necesarios para los estados
    public List<String> getListaPostulaciones() {
        return listaPostulaciones;
    }

    public List<String> getListaConfirmaciones() {
        return listaConfirmaciones;
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