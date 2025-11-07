package dominio.juegos.formatos;

import dominio.valueobjects.formatosScrims.ScrimFormat;

/**
 * Formato de scrim 2v2 Wingman para Counter-Strike.
 * Formato más rápido y casual con 2 jugadores por equipo.
 * 
 * Formato MR16 (16 rondas máximo, primero en ganar 9 rondas gana).
 * Se juega en mapas específicos de Wingman más pequeños.
 * 
 * @author eScrims Team
 */
public class Formato2v2WingmanCS implements ScrimFormat {

    private static final int PLAYERS_PER_TEAM = 2;
    private static final String FORMAT_NAME = "2v2 Wingman";

    /**
     * Obtiene la cantidad de jugadores por equipo.
     * En Wingman son 2 jugadores por equipo.
     * 
     * @return 2 jugadores por equipo
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
     * @return 4 jugadores en total
     */
    public int getTotalPlayers() {
        return PLAYERS_PER_TEAM * 2;
    }

    /**
     * Obtiene información específica del formato Wingman.
     * 
     * @return descripción detallada del formato
     */
    public String getFormatDescription() {
        return "Formato Wingman MR16 (Max Rounds 16). "
                + "Primero en ganar 9 rondas gana el mapa. "
                + "Mapas específicos: Lake, Shortdust, Rialto, etc.";
    }

    @Override
    public String toString() {
        return FORMAT_NAME + " (" + getTotalPlayers() + " jugadores)";
    }
}