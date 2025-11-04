package dominio.roles.lol;

import dominio.juegos.Juego;
import dominio.juegos.LeagueOfLegends;
import dominio.roles.RolJuego;

/**
 * Rol ADC (Attack Damage Carry) para League of Legends.
 * El ADC es el jugador que va a la línea inferior con campeones de
 * daño físico a distancia, enfocados en hacer daño sostenido.
 * 
 * @author eScrims Team
 */
public class RolADCLoL implements RolJuego {

    private static final String NOMBRE = "ADC";
    private static final String DESCRIPCION = "Attack Damage Carry. Jugador de la línea inferior que usa tiradores. " +
            "Responsable de hacer daño sostenido en peleas de equipo.";

    private final Juego juego;

    public RolADCLoL() {
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
        RolADCLoL that = (RolADCLoL) obj;
        return NOMBRE.equals(that.getNombre()) && juego.equals(that.getJuego());
    }

    @Override
    public int hashCode() {
        return NOMBRE.hashCode() + juego.hashCode();
    }
}
