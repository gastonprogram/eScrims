package model;

import model.utils.ScrimFormat;
import model.utils.ScrimValidator;
import java.time.LocalDateTime;
import java.util.List;

public class ScrimBuilder {
    private String juego;
    private ScrimFormat formato;
    private LocalDateTime fechaHora;
    private int rangoMin;
    private int rangoMax;
    private List<String> rolesRequeridos;
    private int latenciaMaxima;
    private int plazas;

    public ScrimBuilder() {
        // Valores por defecto
        this.rangoMin = 0;
        this.rangoMax = Integer.MAX_VALUE;
        this.latenciaMaxima = -1; // -1 indica que no hay límite
    }

    public ScrimBuilder withJuego(String juego) {
        if (juego == null || juego.trim().isEmpty()) {
            throw new IllegalArgumentException("El juego es requerido");
        }
        this.juego = juego;
        return this;
    }

    public ScrimBuilder withFormato(ScrimFormat formato) {
        if (formato == null || !formato.isValidFormat()) {
            throw new IllegalArgumentException("Formato inválido");
        }
        this.formato = formato;
        this.plazas = formato.getPlayersPerTeam() * 2; // Actualiza plazas basado en el formato
        return this;
    }

    public ScrimBuilder withFechaHora(LocalDateTime fechaHora) {
        ScrimValidator.validateFechaHora(fechaHora);
        this.fechaHora = fechaHora;
        return this;
    }

    public ScrimBuilder withRango(int rangoMin, int rangoMax) {
        ScrimValidator.validateRangos(rangoMin, rangoMax);
        this.rangoMin = rangoMin;
        this.rangoMax = rangoMax;
        return this;
    }

    public ScrimBuilder withRolesRequeridos(List<String> roles) {
        this.rolesRequeridos = roles;
        return this;
    }

    public ScrimBuilder withLatenciaMaxima(int latencia) {
        if (latencia < 0) {
            throw new IllegalArgumentException("La latencia máxima no puede ser negativa");
        }
        this.latenciaMaxima = latencia;
        return this;
    }

    public Scrim build() {
        // Validaciones finales
        if (juego == null) {
            throw new IllegalStateException("El juego es requerido para crear una Scrim");
        }
        if (formato == null) {
            throw new IllegalStateException("El formato es requerido para crear una Scrim");
        }
        if (fechaHora == null) {
            throw new IllegalStateException("La fecha y hora son requeridas para crear una Scrim");
        }

        return new Scrim(
            juego,
            formato,
            fechaHora,
            rangoMin,
            rangoMax,
            rolesRequeridos,
            latenciaMaxima,
            plazas
        );
    }
}