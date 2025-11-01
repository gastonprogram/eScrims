package model.roles.lol;

import model.juegos.Juego;
import model.juegos.LeagueOfLegends;
import model.roles.RolJuego;

/**
 * Rol Top Lane para League of Legends.
 * El Top Laner es un jugador que va a la línea superior del mapa,
 * generalmente con campeones tanques, luchadores o split-pushers.
 * 
 * Esta clase es una implementación concreta de RolJuego específica
 * para League of Legends, aplicando el principio Open/Closed.
 * 
 * @author eScrims Team
 */
public class RolTopLoL implements RolJuego {

    private static final String NOMBRE = "Top";
    private static final String DESCRIPCION = "Jugador de la línea superior. Generalmente usa campeones tanques, " +
            "luchadores o duelistas. Responsable del split-push y peleas 1v1.";

    /**
     * Instancia compartida del juego League of Legends.
     * Se usa el patrón Singleton implícito para mantener una única
     * referencia al juego.
     */
    private final Juego juego;

    public RolTopLoL() {
        this.juego = LeagueOfLegends.getInstance();
    }

    @Override
    public String getNombre() {
        return NOMBRE;
    }

    @Override
    public String getDescripcion() {
        return DESCRIPCION;
    }

    @Override
    public Juego getJuego() {
        return juego;
    }

    @Override
    public String toString() {
        return NOMBRE + " - " + juego.getNombre();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        RolTopLoL that = (RolTopLoL) obj;
        return NOMBRE.equals(that.getNombre()) && juego.equals(that.getJuego());
    }

    @Override
    public int hashCode() {
        return NOMBRE.hashCode() + juego.hashCode();
    }
}
