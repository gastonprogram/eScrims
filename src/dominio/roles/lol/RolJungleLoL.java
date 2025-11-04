package dominio.roles.lol;

import dominio.juegos.Juego;
import dominio.juegos.LeagueOfLegends;
import dominio.roles.RolJuego;

/**
 * Rol Jungle para League of Legends.
 * El Jungler es el jugador que recorre la jungla del mapa,
 * farmea campamentos neutrales y gankea las líneas para ayudar al equipo.
 * 
 * @author eScrims Team
 */
public class RolJungleLoL implements RolJuego {

    private static final String NOMBRE = "Jungle";
    private static final String DESCRIPCION = "Jugador de la jungla. Farmea campamentos neutrales, gankea las líneas, "
            +
            "controla objetivos (dragones, barones) y proporciona visión del mapa.";

    private final Juego juego;

    public RolJungleLoL() {
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
        RolJungleLoL that = (RolJungleLoL) obj;
        return NOMBRE.equals(that.getNombre()) && juego.equals(that.getJuego());
    }

    @Override
    public int hashCode() {
        return NOMBRE.hashCode() + juego.hashCode();
    }
}
