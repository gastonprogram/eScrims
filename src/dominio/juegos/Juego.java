package dominio.juegos;

import java.util.List;
import java.util.Objects;

import dominio.roles.RolJuego;
import dominio.valueobjects.formatosScrims.ScrimFormat;

/**
 * Clase abstracta que representa un videojuego en la plataforma eScrims.
 * Cada juego define sus propios roles, formatos de partida y reglas
 * específicas.
 * 
 * Esta clase implementa el patrón Template Method, donde cada subclase
 * define los roles y formatos específicos del juego, mientras que esta
 * clase base proporciona la lógica común de validación.
 * 
 * También aplica el principio Open/Closed: podemos agregar nuevos juegos
 * sin modificar el código existente.
 * 
 * @author eScrims Team
 */
public abstract class Juego {

    /**
     * Obtiene el nombre del juego.
     * 
     * @return nombre del juego (ej: "League of Legends", "Valorant")
     */
    public abstract String getNombre();

    /**
     * Obtiene la lista de roles disponibles para este juego.
     * Cada juego define sus propios roles específicos.
     * 
     * @return lista de roles disponibles
     */
    public abstract List<RolJuego> getRolesDisponibles();

    /**
     * Obtiene la lista de formatos de partida disponibles para este juego.
     * Por ejemplo: 5v5, 1v1, 3v3, etc.
     * 
     * @return lista de formatos disponibles
     */
    public abstract List<ScrimFormat> getFormatosDisponibles();

    /**
     * Verifica si un rol es válido para este juego.
     * Un rol es válido si pertenece a este juego y está en la lista
     * de roles disponibles.
     * 
     * @param rol el rol a validar
     * @return true si el rol es válido para este juego
     */
    public boolean esRolValido(RolJuego rol) {
        if (rol == null) {
            return false;
        }

        // Verificar que el rol pertenezca a este juego
        if (!this.equals(rol.getJuego())) {
            return false;
        }

        // Verificar que el rol esté en la lista de roles disponibles
        return getRolesDisponibles().stream()
                .anyMatch(r -> r.getNombre().equals(rol.getNombre()));
    }

    /**
     * Verifica si un formato es válido para este juego.
     * 
     * @param formato el formato a validar
     * @return true si el formato es válido para este juego
     */
    public boolean esFormatoValido(ScrimFormat formato) {
        if (formato == null || !formato.isValidFormat()) {
            return false;
        }

        // Verificar que el formato esté en la lista de formatos disponibles
        return getFormatosDisponibles().stream()
                .anyMatch(f -> f.getFormatName().equals(formato.getFormatName()));
    }

    /**
     * Busca un rol por su nombre dentro de los roles disponibles del juego.
     * 
     * @param nombreRol nombre del rol a buscar
     * @return el rol encontrado, o null si no existe
     */
    public RolJuego buscarRolPorNombre(String nombreRol) {
        if (nombreRol == null || nombreRol.trim().isEmpty()) {
            return null;
        }

        return getRolesDisponibles().stream()
                .filter(r -> r.getNombre().equalsIgnoreCase(nombreRol.trim()))
                .findFirst()
                .orElse(null);
    }

    /**
     * Obtiene la cantidad de roles requeridos según el formato.
     * Por defecto, es el total de jugadores del formato.
     * 
     * @param formato el formato de scrim
     * @return cantidad de roles requeridos
     */
    public int getCantidadRolesRequeridos(ScrimFormat formato) {
        return formato.getPlayersPerTeam() * 2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Juego juego = (Juego) o;
        return getNombre().equals(juego.getNombre());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getNombre());
    }

    @Override
    public String toString() {
        return getNombre();
    }
}
