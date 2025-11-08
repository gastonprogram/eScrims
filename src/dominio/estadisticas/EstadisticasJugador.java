package dominio.estadisticas;

/**
 * Estadísticas individuales de un jugador en un scrim específico.
 * Contiene métricas como kills, assists, deaths, puntuación y estado de MVP.
 */
public class EstadisticasJugador {
    private String jugadorId; // Cambiado de Long a String para consistencia
    private int kills;
    private int assists;
    private int deaths;
    private int puntuacion;
    private boolean esMVP;

    public EstadisticasJugador(String jugadorId) {
        this.jugadorId = jugadorId;
        this.kills = 0;
        this.assists = 0;
        this.deaths = 0;
        this.puntuacion = 0;
        this.esMVP = false;
    }

    /**
     * Constructor completo para establecer todas las estadísticas de una vez.
     */
    public EstadisticasJugador(String jugadorId, int kills, int assists, int deaths, int puntuacion) {
        this.jugadorId = jugadorId;
        this.kills = kills;
        this.assists = assists;
        this.deaths = deaths;
        this.puntuacion = puntuacion;
        this.esMVP = false;
    }

    public double getKDA() {
        if (deaths == 0) {
            return kills + assists;
        }
        return (double) (kills + assists) / deaths;
    }

    // Getters y setters
    public String getJugadorId() { // Cambiado retorno de Long a String
        return jugadorId;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public int getAssists() {
        return assists;
    }

    public void setAssists(int assists) {
        this.assists = assists;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public int getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(int puntuacion) {
        this.puntuacion = puntuacion;
    }

    public boolean isEsMVP() {
        return esMVP;
    }

    public void setEsMVP(boolean esMVP) {
        this.esMVP = esMVP;
    }
}
