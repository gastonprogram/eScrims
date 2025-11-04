package dominio.valueobjects.formatosScrims;

public interface ScrimFormat {
    int getPlayersPerTeam();

    String getFormatName();

    boolean isValidFormat();
}