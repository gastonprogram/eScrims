package dominio.roles.cs;

import dominio.juegos.CounterStrike;
import dominio.juegos.Juego;
import dominio.roles.RolJuego;

/**
 * Rol AWPer para Counter-Strike.
 * El AWPer es el francotirador del equipo que utiliza el rifle AWP
 * para eliminar enemigos a larga distancia y controlar ángulos clave.
 * 
 * @author eScrims Team
 */
public class RolAWPerCS implements RolJuego {

    private static final String NOMBRE = "AWPer";
    private static final String DESCRIPCION = "Francotirador del equipo. Se especializa en usar el AWP "
            + "para eliminar enemigos a larga distancia, controlar ángulos importantes y dar información clave.";

    private final Juego juego;

    public RolAWPerCS() {
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
        RolAWPerCS that = (RolAWPerCS) obj;
        return NOMBRE.equals(that.getNombre()) && juego.equals(that.getJuego());
    }

    @Override
    public int hashCode() {
        return NOMBRE.hashCode() + juego.hashCode();
    }
}
