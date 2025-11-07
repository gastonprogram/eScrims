package dominio.roles.valorant;

import dominio.juegos.Juego;
import dominio.juegos.Valorant;
import dominio.roles.RolJuego;

/**
 * Rol Centinela para Valorant.
 * Los Centinelas son expertos defensivos que pueden asegurar áreas
 * y vigilar flancos tanto para ellos como para su equipo.
 * 
 * Ejemplos: Sage, Cypher, Killjoy, Chamber, Deadlock.
 * 
 * @author eScrims Team
 */
public class RolCentinelaValorant implements RolJuego {

    private static final String NOMBRE = "Centinela";
    private static final String DESCRIPCION = "Agentes defensivos que aseguran áreas y vigilan flancos. "
            + "Especialistas en defensa, proporcionan información y apoyo al equipo desde retaguardia. "
            + "Incluye agentes como Sage, Cypher, Killjoy, Chamber y Deadlock.";

    /**
     * Instancia compartida del juego Valorant.
     * Se usa el patrón Singleton implícito para mantener una única
     * referencia al juego.
     */
    private final Juego juego;

    public RolCentinelaValorant() {
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
        RolCentinelaValorant that = (RolCentinelaValorant) obj;
        return NOMBRE.equals(that.getNombre()) && juego.equals(that.getJuego());
    }

    @Override
    public int hashCode() {
        return NOMBRE.hashCode() + juego.hashCode();
    }
}
