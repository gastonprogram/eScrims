package model;

import model.utils.ScrimFormat;
import model.states.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Scrim {

    private String id;
    private String juego;
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

    // constructor protegido porque usa ScrimBuilder para instanciarse
    protected Scrim(String juego, ScrimFormat formato, LocalDateTime fechaHora,
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
}