package dominio.juegos.formatos;

import dominio.valueobjects.formatosScrims.ScrimFormat;

/**
 * Formato de scrim 5v5 Swift para Valorant.
 * Este es un formato más rápido con rondas más cortas y menos económica.
 * 
 * Formato Swift: 13 rondas máximo (primero en ganar 7 rondas gana).
 * Tiempo de ronda reducido y economía simplificada para partidas más rápidas.
 * 
 * @author eScrims Team
 */
public class Formato5v5SwiftValorant implements ScrimFormat {

    private static final int PLAYERS_PER_TEAM = 5;
    private static final String FORMAT_NAME = "5v5 Swift Valorant";

    /**
     * Obtiene la cantidad de jugadores por equipo.
     * En Valorant Swift son 5 jugadores por equipo.
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
     * Obtiene información específica del formato Swift.
     * 
     * @return descripción detallada del formato
     */
    public String getFormatDescription() {
        return "Formato Swift (13 rondas máximo). "
                + "Primero en ganar 7 rondas gana el mapa. "
                + "Tiempo de ronda: 80 segundos. Economía simplificada para partidas más rápidas.";
    }

    @Override
    public String toString() {
        return FORMAT_NAME + " (" + getTotalPlayers() + " jugadores)";
    }
}
