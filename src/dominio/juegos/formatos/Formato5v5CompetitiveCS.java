package dominio.juegos.formatos;

import dominio.valueobjects.formatosScrims.ScrimFormat;

/**
 * Formato de scrim 5v5 Competitive para Counter-Strike.
 * Este es el formato estándar competitivo con 5 jugadores por equipo.
 * 
 * Formato MR30 (30 rondas máximo, primero en ganar 16 rondas gana).
 * Incluye tiempo de compra, dinero inicial y configuración competitiva
 * estándar.
 * 
 * @author eScrims Team
 */
public class Formato5v5CompetitiveCS implements ScrimFormat {

    private static final int PLAYERS_PER_TEAM = 5;
    private static final String FORMAT_NAME = "5v5 Competitive";

    /**
     * Obtiene la cantidad de jugadores por equipo.
     * En CS competitivo estándar son 5 jugadores por equipo.
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
     * Obtiene información específica del formato competitivo.
     * 
     * @return descripción detallada del formato
     */
    public String getFormatDescription() {
        return "Formato competitivo estándar MR30 (Max Rounds 30). "
                + "Primero en ganar 16 rondas gana el mapa. "
                + "Tiempo de compra: 20 segundos. Tiempo de ronda: 1:55.";
    }

    @Override
    public String toString() {
        return FORMAT_NAME + " (" + getTotalPlayers() + " jugadores)";
    }
}