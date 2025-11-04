package dominio.roles.lol;

import dominio.juegos.Juego;
import dominio.juegos.LeagueOfLegends;
import dominio.roles.RolJuego;

/**
 * Rol Mid Lane para League of Legends.
 * El Mid Laner es el jugador de la línea central, generalmente
 * con campeones magos o asesinos con alto daño.
 * 
 * @author eScrims Team
 */
public class RolMidLoL implements RolJuego {

    private static final String NOMBRE = "Mid";
    private static final String DESCRIPCION = "Jugador de la línea central. Generalmente usa magos o asesinos. " +
            "Responsable de hacer daño masivo y controlar el centro del mapa.";

    private final Juego juego;

    public RolMidLoL() {
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
        RolMidLoL that = (RolMidLoL) obj;
        return NOMBRE.equals(that.getNombre()) && juego.equals(that.getJuego());
    }

    @Override
    public int hashCode() {
        return NOMBRE.hashCode() + juego.hashCode();
    }
}
