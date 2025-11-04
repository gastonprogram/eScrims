package aplicacion.builders;

import java.time.LocalDateTime;

/**
 * Clase que encapsula los filtros para buscar scrims.
 * Todos los campos son opcionales (pueden ser null).
 * 
 * Esta clase aplica el patrón Builder para facilitar la construcción
 * de filtros complejos de forma fluida.
 * 
 * @author eScrims Team
 */
public class FiltrosScrim {

    private String juego; // Nombre del juego (ej: "League of Legends")
    private String formato; // Nombre del formato (ej: "5v5 Summoner's Rift")
    private Integer rangoMin; // Rango mínimo
    private Integer rangoMax; // Rango máximo
    private Integer latenciaMax; // Latencia máxima
    private LocalDateTime fechaDesde; // Buscar desde esta fecha
    private LocalDateTime fechaHasta; // Buscar hasta esta fecha
    private String estado; // Estado del scrim (ej: "BUSCANDO")

    /**
     * Constructor privado. Usar el Builder para crear instancias.
     */
    private FiltrosScrim() {
    }

    // Getters
    public String getJuego() {
        return juego;
    }

    public String getFormato() {
        return formato;
    }

    public Integer getRangoMin() {
        return rangoMin;
    }

    public Integer getRangoMax() {
        return rangoMax;
    }

    public Integer getLatenciaMax() {
        return latenciaMax;
    }

    public LocalDateTime getFechaDesde() {
        return fechaDesde;
    }

    public LocalDateTime getFechaHasta() {
        return fechaHasta;
    }

    public String getEstado() {
        return estado;
    }

    /**
     * Builder para construir filtros de forma fluida.
     */
    public static class Builder {
        private FiltrosScrim filtros;

        public Builder() {
            this.filtros = new FiltrosScrim();
        }

        public Builder conJuego(String juego) {
            filtros.juego = juego;
            return this;
        }

        public Builder conFormato(String formato) {
            filtros.formato = formato;
            return this;
        }

        public Builder conRangoMin(Integer rangoMin) {
            filtros.rangoMin = rangoMin;
            return this;
        }

        public Builder conRangoMax(Integer rangoMax) {
            filtros.rangoMax = rangoMax;
            return this;
        }

        public Builder conLatenciaMax(Integer latenciaMax) {
            filtros.latenciaMax = latenciaMax;
            return this;
        }

        public Builder conFechaDesde(LocalDateTime fechaDesde) {
            filtros.fechaDesde = fechaDesde;
            return this;
        }

        public Builder conFechaHasta(LocalDateTime fechaHasta) {
            filtros.fechaHasta = fechaHasta;
            return this;
        }

        public Builder conEstado(String estado) {
            filtros.estado = estado;
            return this;
        }

        public FiltrosScrim build() {
            return filtros;
        }
    }
}
