package dominio.estadisticas;

public class EstadisticasJugador {
    private Long jugadorId;
    private int kills;
    private int assists;
    private int deaths;
    private int puntuacion;
    private boolean esMVP;

    public EstadisticasJugador(Long jugadorId) {
        this.jugadorId = jugadorId;
        this.kills = 0;
        this.assists = 0;
        this.deaths = 0;
        this.puntuacion = 0;
        this.esMVP = false;
    }

    public double getKDA() {
        if (deaths == 0) {
            return kills + assists;
        }
        return (double) (kills + assists) / deaths;
    }

    // Getters y setters
    public Long getJugadorId() {
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
