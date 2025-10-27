package model.roles;

import model.juegos.Juego;

/**
 * Interfaz que define el contrato para los roles dentro de un juego.
 * Cada rol representa una posición o función específica que un jugador
 * puede desempeñar en una partida (ej: Top, Jungle, Duelist, etc.).
 * 
 * Esta interfaz permite aplicar el principio Open/Closed: podemos agregar
 * nuevos roles sin modificar el código existente.
 * 
 * @author eScrims Team
 */
public interface RolJuego {

    /**
     * Obtiene el nombre del rol.
     * 
     * @return nombre del rol (ej: "Top", "Jungle", "Duelist")
     */
    String getNombre();

    /**
     * Obtiene una descripción detallada del rol y sus responsabilidades.
     * 
     * @return descripción del rol
     */
    String getDescripcion();

    /**
     * Obtiene el juego al que pertenece este rol.
     * Esto permite validar que el rol sea compatible con el scrim.
     * 
     * @return instancia del juego al que pertenece
     */
    Juego getJuego();

    /**
     * Verifica si este rol es compatible con otro rol dado.
     * Por ejemplo, en algunos juegos ciertos roles no pueden estar
     * en el mismo equipo o tienen restricciones especiales.
     * 
     * @param otroRol el rol a comparar
     * @return true si los roles son compatibles, false en caso contrario
     */
    default boolean esCompatibleCon(RolJuego otroRol) {
        // Por defecto, todos los roles del mismo juego son compatibles
        return this.getJuego().equals(otroRol.getJuego());
    }
}
