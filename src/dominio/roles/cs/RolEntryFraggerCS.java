package dominio.roles.cs;

import dominio.juegos.CounterStrike;
import dominio.juegos.Juego;
import dominio.roles.RolJuego;

/**
 * Rol Entry Fragger para Counter-Strike.
 * El Entry Fragger es el primer jugador en entrar a los sitios,
 * busca eliminar enemigos y abrir el camino para su equipo.
 * 
 * @author eScrims Team
 */
public class RolEntryFraggerCS implements RolJuego {

    private static final String NOMBRE = "Entry Fragger";
    private static final String DESCRIPCION = "Primer jugador en entrar a los sitios. Se especializa en "
            + "eliminar enemigos clave, abrir el camino para el equipo y crear oportunidades de entrada.";

    private final Juego juego;

    public RolEntryFraggerCS() {
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
        RolEntryFraggerCS that = (RolEntryFraggerCS) obj;
        return NOMBRE.equals(that.getNombre()) && juego.equals(that.getJuego());
    }

    @Override
    public int hashCode() {
        return NOMBRE.hashCode() + juego.hashCode();
    }
}
