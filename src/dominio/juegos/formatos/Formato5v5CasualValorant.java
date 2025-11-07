package dominio.juegos.formatos;

import dominio.valueobjects.formatosScrims.ScrimFormat;

/**
 * Formato de scrim 5v5 Casual (Unrated) para Valorant.
 * Este es el formato estándar no competitivo con reglas más relajadas.
 * 
 * Formato Unrated: 25 rondas máximo (primero en ganar 13 rondas gana).
 * Sin restricciones de ranking y con ambiente más casual para práctica.
 * 
 * @author eScrims Team
 */
public class Formato5v5CasualValorant implements ScrimFormat {

    private static final int PLAYERS_PER_TEAM = 5;
    private static final String FORMAT_NAME = "5v5 Casual";

    /**
     * Obtiene la cantidad de jugadores por equipo.
     * En Valorant Casual son 5 jugadores por equipo.
     * 
     * @return 5 jugadores por equipo
     */
    @Override
    public int getPlayersPerTeam() {
        return PLAYERS_PER_TEAM;
    }

    /**
     * Obtiene el nombre descriptivo del formato.
     * 
     * @return nombre del formato
     */
    @Override
    public String getFormatName() {
        return FORMAT_NAME;
    }

    /**
     * Valida que el formato esté correctamente configurado.
     * Este formato siempre es válido ya que tiene valores predefinidos.
     * 
     * @return siempre true
     */
    @Override
    public boolean isValidFormat() {
        return true;
    }

    /**
     * Obtiene el total de jugadores en el scrim (ambos equipos).
     * 
     * @return 10 jugadores en total
     */
    public int getTotalPlayers() {
        return PLAYERS_PER_TEAM * 2;
    }

    /**
     * Obtiene información específica del formato Casual.
     * 
     * @return descripción detallada del formato
     */
    public String getFormatDescription() {
        return "Formato Casual/Unrated (25 rondas máximo). "
                + "Primero en ganar 13 rondas gana el mapa. "
                + "Tiempo de ronda: 100 segundos. Sin restricciones de ranking.";
    }

    @Override
    public String toString() {
        return FORMAT_NAME + " (" + getTotalPlayers() + " jugadores)";
    }
}
