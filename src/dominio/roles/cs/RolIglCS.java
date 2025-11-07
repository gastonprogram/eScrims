package dominio.roles.cs;

import dominio.juegos.CounterStrike;
import dominio.juegos.Juego;
import dominio.roles.RolJuego;

/**
 * Rol IGL (In-Game Leader) para Counter-Strike.
 * El IGL es el líder del equipo que toma las decisiones estratégicas,
 * coordina las tácticas y dirige a su equipo durante las rondas.
 * 
 * @author eScrims Team
 */
public class RolIglCS implements RolJuego {

    private static final String NOMBRE = "IGL";
    private static final String DESCRIPCION = "In-Game Leader. Líder del equipo que coordina estrategias, "
            + "toma decisiones tácticas durante las rondas y dirige las jugadas del equipo.";

    private final Juego juego;

    public RolIglCS() {
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
        RolIglCS that = (RolIglCS) obj;
        return NOMBRE.equals(that.getNombre()) && juego.equals(that.getJuego());
    }

    @Override
    public int hashCode() {
        return NOMBRE.hashCode() + juego.hashCode();
    }
}
