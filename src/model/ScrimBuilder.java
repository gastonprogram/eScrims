package model;

import model.juegos.Juego;
import model.utils.ScrimFormat;
import model.utils.ScrimValidator;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Builder para construir instancias de Scrim de forma fluida y segura.
 * 
 * Este patrón Builder facilita la creación de objetos complejos (Scrim)
 * que tienen muchos parámetros, algunos opcionales y otros obligatorios.
 * 
 * Aplica los principios:
 * - Single Responsibility: solo se encarga de construir Scrims
 * - Builder Pattern: construcción paso a paso con validaciones
 * 
 * @author eScrims Team
 */
public class ScrimBuilder {
    private Juego juego; // Cambiado de String a Juego
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

    /**
     * Establece el juego del scrim.
     * 
     * @param juego instancia del juego (ej: LeagueOfLegends.getInstance())
     * @return este builder para encadenamiento
     * @throws IllegalArgumentException si el juego es null
     */
    public ScrimBuilder withJuego(Juego juego) {
        if (juego == null) {
            throw new IllegalArgumentException("El juego es requerido");
        }
        this.juego = juego;
        return this;
    }

    /**
     * Establece el formato del scrim y calcula automáticamente las plazas.
     * También valida que el formato sea compatible con el juego.
     * 
     * @param formato el formato de la partida
     * @return este builder para encadenamiento
     * @throws IllegalArgumentException si el formato es inválido o incompatible
     */
    public ScrimBuilder withFormato(ScrimFormat formato) {
        if (formato == null || !formato.isValidFormat()) {
            throw new IllegalArgumentException("Formato inválido");
        }

        // Si ya se estableció el juego, validar compatibilidad
        if (this.juego != null && !this.juego.esFormatoValido(formato)) {
            throw new IllegalArgumentException(
                    "El formato '" + formato.getFormatName() +
                            "' no es compatible con el juego '" + this.juego.getNombre() + "'");
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
                plazas);
    }
}