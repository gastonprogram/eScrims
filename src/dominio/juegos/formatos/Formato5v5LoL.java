package dominio.juegos.formatos;

import dominio.valueobjects.formatosScrims.ScrimFormat;

/**
 * Formato de scrim 5v5 para League of Legends.
 * Este es el formato estándar competitivo con 5 jugadores por equipo.
 * 
 * Implementa ScrimFormat siguiendo el principio de sustitución de Liskov:
 * puede ser usado en cualquier lugar donde se espere un ScrimFormat.
 * 
 * @author eScrims Team
 */
public class Formato5v5LoL implements ScrimFormat {

    private static final int PLAYERS_PER_TEAM = 5;
    private static final String FORMAT_NAME = "5v5 Summoner's Rift";

    /**
     * Obtiene la cantidad de jugadores por equipo.
     * En LoL competitivo estándar son 5 jugadores por equipo.
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

    @Override
    public String toString() {
        return FORMAT_NAME + " (" + getTotalPlayers() + " jugadores)";
    }
}
