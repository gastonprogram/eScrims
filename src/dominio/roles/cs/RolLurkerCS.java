package dominio.roles.cs;

import dominio.juegos.CounterStrike;
import dominio.juegos.Juego;
import dominio.roles.RolJuego;

/**
 * Rol Support para Counter-Strike.
 * El Support es el jugador que proporciona apoyo al equipo,
 * controlando áreas del mapa y ayudando a sus compañeros de equipo.
 * 
 * @author eScrims Team
 */

public class RolLurkerCS implements RolJuego {

    private static final String NOMBRE = "Lurker";
    private static final String DESCRIPCION = "Jugador que se especializa en flanquear al equipo enemigo, "
            +
            "controla objetivos (bombas, áreas) y proporciona visión del mapa.";

    private final Juego juego;

    public RolLurkerCS() {
        this.juego = CounterStrike.getInstance();
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
        RolLurkerCS that = (RolLurkerCS) obj;
        return NOMBRE.equals(that.getNombre()) && juego.equals(that.getJuego());
    }

    @Override
    public int hashCode() {
        return NOMBRE.hashCode() + juego.hashCode();
    }
}
