package dominio.juegos.formatos;

import dominio.valueobjects.formatosScrims.ScrimFormat;

/**
 * Formato de scrim 5v5 Competitive para Valorant.
 * Este es el formato competitivo estándar con ranking y reglas oficiales.
 * 
 * Formato Competitive: 25 rondas máximo (primero en ganar 13 rondas gana).
 * Incluye selección de agentes, economía completa y ranking competitivo.
 * 
 * @author eScrims Team
 */
public class Formato5v5CompetitiveValorant implements ScrimFormat {

    private static final int PLAYERS_PER_TEAM = 5;
    private static final String FORMAT_NAME = "5v5 Competitive";

    /**
     * Obtiene la cantidad de jugadores por equipo.
     * En Valorant Competitive son 5 jugadores por equipo.
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
     * Obtiene información específica del formato Competitive.
     * 
     * @return descripción detallada del formato
     */
    public String getFormatDescription() {
        return "Formato Competitive oficial (25 rondas máximo). "
                + "Primero en ganar 13 rondas gana el mapa. "
                + "Tiempo de ronda: 100 segundos. Selección de agentes y ranking competitivo.";
    }

    @Override
    public String toString() {
        return FORMAT_NAME + " (" + getTotalPlayers() + " jugadores)";
    }
}