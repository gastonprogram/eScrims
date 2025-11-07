package dominio.juegos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dominio.juegos.formatos.Formato5v5CompetitiveValorant;
import dominio.juegos.formatos.Formato5v5CasualValorant;
import dominio.juegos.formatos.Formato5v5SwiftValorant;
import dominio.roles.RolJuego;
import dominio.roles.valorant.*;
import dominio.valueobjects.formatosScrims.ScrimFormat;

/**
 * Implementación concreta del juego Valorant.
 * Define los roles específicos del juego (Duelista, Iniciador, Controlador,
 * Centinela)
 * y los formatos disponibles (5v5 Unrated, 5v5 Competitive, etc.).
 * 
 * Esta clase implementa el patrón Singleton para asegurar que solo
 * exista una instancia del juego en toda la aplicación, evitando
 * duplicación innecesaria de datos.
 * 
 * También aplica el patrón Template Method heredado de Juego,
 * donde define los métodos abstractos específicos para Valorant.
 * 
 * @author eScrims Team
 */
public class Valorant extends Juego {

    // Singleton instance
    private static Valorant instance;

    private static final String NOMBRE_JUEGO = "Valorant";

    /**
     * Lista de roles disponibles en Valorant.
     * Se inicializa de forma lazy (cuando se solicita por primera vez).
     */
    private List<RolJuego> rolesDisponibles;

    /**
     * Lista de formatos disponibles en Valorant.
     * Se inicializa de forma lazy (cuando se solicita por primera vez).
     */
    private List<ScrimFormat> formatosDisponibles;

    /**
     * Constructor privado para implementar el patrón Singleton.
     * Esto previene la creación de múltiples instancias del juego.
     */
    private Valorant() {
        // Inicialización lazy de roles y formatos
    }

    /**
     * Obtiene la instancia única de Valorant (Singleton).
     * Si no existe, la crea; si ya existe, retorna la existente.
     * 
     * Este método es thread-safe mediante sincronización lazy.
     * 
     * @return instancia única de Valorant
     */
    public static synchronized Valorant getInstance() {
        if (instance == null) {
            instance = new Valorant();
        }
        return instance;
    }

    @Override
    public String getNombre() {
        return NOMBRE_JUEGO;
    }

    /**
     * Obtiene la lista de roles disponibles en Valorant.
     * Los roles son: Duelista, Iniciador, Controlador y Centinela.
     * 
     * Implementa inicialización lazy: solo crea los roles cuando
     * son solicitados por primera vez.
     * 
     * @return lista inmutable de roles disponibles
     */
    @Override
    public List<RolJuego> getRolesDisponibles() {
        if (rolesDisponibles == null) {
            rolesDisponibles = Arrays.asList(
                    new RolDuelistaValorant(),
                    new RolIniciadorValorant(),
                    new RolControladorValorant(),
                    new RolCentinelaValorant());
        }
        // Retorna una copia para prevenir modificaciones externas
        return new ArrayList<>(rolesDisponibles);
    }

    /**
     * Obtiene la lista de formatos disponibles en Valorant.
     * Los formatos incluyen: 5v5 Competitive, 5v5 Casual y 5v5 Swift.
     * 
     * Implementa inicialización lazy similar a los roles.
     * 
     * @return lista inmutable de formatos disponibles
     */
    @Override
    public List<ScrimFormat> getFormatosDisponibles() {
        if (formatosDisponibles == null) {
            formatosDisponibles = Arrays.asList(
                    new Formato5v5CompetitiveValorant(),
                    new Formato5v5CasualValorant(),
                    new Formato5v5SwiftValorant());
        }
        // Retorna una copia para prevenir modificaciones externas
        return new ArrayList<>(formatosDisponibles);
    }

    /**
     * Método helper para crear un rol por su nombre.
     * Facilita la creación de roles sin necesidad de conocer
     * las clases concretas.
     * 
     * @param nombreRol nombre del rol ("Duelista", "Iniciador", "Controlador",
     *                  "Centinela")
     * @return instancia del rol solicitado, o null si no existe
     */
    public RolJuego crearRol(String nombreRol) {
        return buscarRolPorNombre(nombreRol);
    }

    /**
     * Obtiene el formato estándar por defecto (5v5 Casual).
     * 
     * @return formato 5v5 Casual estándar
     */
    public ScrimFormat getFormatoEstandar() {
        return new Formato5v5CasualValorant();
    }
}
