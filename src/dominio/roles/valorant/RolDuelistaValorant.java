package dominio.roles.valorant;

import dominio.juegos.Juego;
import dominio.juegos.Valorant;
import dominio.roles.RolJuego;

/**
 * Rol Duelista para Valorant.
 * Los Duelistas son agentes diseñados para la entrada agresiva y el fragging.
 * Se especializan en conseguir eliminations y abrir sitios para el equipo.
 * 
 * Ejemplos: Jett, Reyna, Raze, Phoenix, Yoru, Neon.
 * 
 * @author eScrims Team
 */
public class RolDuelistaValorant implements RolJuego {

    private static final String NOMBRE = "Duelista";
    private static final String DESCRIPCION = "Agentes especializados en entrada agresiva y fragging. "
            + "Responsables de conseguir eliminations y abrir sitios para el equipo. "
            + "Incluye agentes como Jett, Reyna, Raze, Phoenix, Yoru y Neon.";

    /**
     * Instancia compartida del juego Valorant.
     * Se usa el patrón Singleton implícito para mantener una única
     * referencia al juego.
     */
    private final Juego juego;

    public RolDuelistaValorant() {
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
        RolDuelistaValorant that = (RolDuelistaValorant) obj;
        return NOMBRE.equals(that.getNombre()) && juego.equals(that.getJuego());
    }

    @Override
    public int hashCode() {
        return NOMBRE.hashCode() + juego.hashCode();
    }
}
