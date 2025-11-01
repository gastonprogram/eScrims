package model.formatos;

import model.utils.ScrimFormat;

/**
 * Formato ARAM (All Random All Mid) para League of Legends.
 * Modo de juego casual donde todos los jugadores pelean en una sola línea
 * con campeones aleatorios.
 * 
 * @author eScrims Team
 */
public class FormatoARAMLoL implements ScrimFormat {

    private static final int PLAYERS_PER_TEAM = 5;
    private static final String FORMAT_NAME = "5v5 ARAM";

    @Override
    public int getPlayersPerTeam() {
        return PLAYERS_PER_TEAM;
    }

    @Override
    public String getFormatName() {
        return FORMAT_NAME;
    }

    @Override
    public boolean isValidFormat() {
        return true;
    }

    public int getTotalPlayers() {
        return PLAYERS_PER_TEAM * 2;
    }

    @Override
    public String toString() {
        return FORMAT_NAME + " (" + getTotalPlayers() + " jugadores)";
    }
}
