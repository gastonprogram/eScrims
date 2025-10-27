package model.roles.lol;

import model.juegos.Juego;
import model.juegos.LeagueOfLegends;
import model.roles.RolJuego;

/**
 * Rol Support para League of Legends.
 * El Support acompaña al ADC en la línea inferior, proporcionando
 * utilidad, visión, control de masas y protección al equipo.
 * 
 * @author eScrims Team
 */
public class RolSupportLoL implements RolJuego {

    private static final String NOMBRE = "Support";
    private static final String DESCRIPCION = "Jugador de soporte en la línea inferior. Protege al ADC, proporciona " +
            "visión del mapa, control de masas y utilidad para el equipo.";

    private final Juego juego;

    public RolSupportLoL() {
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
        RolSupportLoL that = (RolSupportLoL) obj;
        return NOMBRE.equals(that.getNombre()) && juego.equals(that.getJuego());
    }

    @Override
    public int hashCode() {
        return NOMBRE.hashCode() + juego.hashCode();
    }
}
