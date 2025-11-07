package dominio.roles.valorant;

import dominio.juegos.Juego;
import dominio.juegos.Valorant;
import dominio.roles.RolJuego;

/**
 * Rol Controlador para Valorant.
 * Los Controladores cortan la visión del enemigo, controlan territorio
 * y apoyan a su equipo bloqueando ángulos peligrosos.
 * 
 * Ejemplos: Brimstone, Omen, Viper, Astra, Harbor, Clove.
 * 
 * @author eScrims Team
 */
public class RolControladorValorant implements RolJuego {

    private static final String NOMBRE = "Controlador";
    private static final String DESCRIPCION = "Agentes que cortan la visión del enemigo y controlan territorio. "
            + "Responsables de bloquear ángulos peligrosos y dividir el campo de batalla. "
            + "Incluye agentes como Brimstone, Omen, Viper, Astra, Harbor y Clove.";

    /**
     * Instancia compartida del juego Valorant.
     * Se usa el patrón Singleton implícito para mantener una única
     * referencia al juego.
     */
    private final Juego juego;

    public RolControladorValorant() {
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
        RolControladorValorant that = (RolControladorValorant) obj;
        return NOMBRE.equals(that.getNombre()) && juego.equals(that.getJuego());
    }

    @Override
    public int hashCode() {
        return NOMBRE.hashCode() + juego.hashCode();
    }
}
