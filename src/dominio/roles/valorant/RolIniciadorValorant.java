package dominio.roles.valorant;

import dominio.juegos.Juego;
import dominio.juegos.Valorant;
import dominio.roles.RolJuego;

/**
 * Rol Iniciador para Valorant.
 * Los Iniciadores configuran a su equipo para entrar al territorio enemigo
 * y conseguir información vital para el equipo.
 * 
 * Ejemplos: Sova, Breach, Skye, KAY/O, Fade, Gekko.
 * 
 * @author eScrims Team
 */
public class RolIniciadorValorant implements RolJuego {

    private static final String NOMBRE = "Iniciador";
    private static final String DESCRIPCION = "Agentes que configuran a su equipo para entrar al territorio enemigo. "
            + "Proporcionan información vital, despejan ángulos y apoyan las entradas del equipo. "
            + "Incluye agentes como Sova, Breach, Skye, KAY/O, Fade y Gekko.";

    /**
     * Instancia compartida del juego Valorant.
     * Se usa el patrón Singleton implícito para mantener una única
     * referencia al juego.
     */
    private final Juego juego;

    public RolIniciadorValorant() {
        this.juego = Valorant.getInstance();
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
        RolIniciadorValorant that = (RolIniciadorValorant) obj;
        return NOMBRE.equals(that.getNombre()) && juego.equals(that.getJuego());
    }

    @Override
    public int hashCode() {
        return NOMBRE.hashCode() + juego.hashCode();
    }
}
