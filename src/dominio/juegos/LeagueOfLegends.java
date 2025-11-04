package dominio.juegos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dominio.juegos.formatos.Formato5v5LoL;
import dominio.juegos.formatos.FormatoARAMLoL;
import dominio.roles.RolJuego;
import dominio.roles.lol.*;
import dominio.valueobjects.formatosScrims.ScrimFormat;

/**
 * Implementación concreta del juego League of Legends.
 * Define los roles específicos del juego (Top, Jungle, Mid, ADC, Support)
 * y los formatos disponibles (5v5 Summoner's Rift, ARAM, etc.).
 * 
 * Esta clase implementa el patrón Singleton para asegurar que solo
 * exista una instancia del juego en toda la aplicación, evitando
 * duplicación innecesaria de datos.
 * 
 * También aplica el patrón Template Method heredado de Juego,
 * donde define los métodos abstractos específicos para LoL.
 * 
 * @author eScrims Team
 */
public class LeagueOfLegends extends Juego {

    // Singleton instance
    private static LeagueOfLegends instance;

    private static final String NOMBRE_JUEGO = "League of Legends";

    /**
     * Lista de roles disponibles en League of Legends.
     * Se inicializa de forma lazy (cuando se solicita por primera vez).
     */
    private List<RolJuego> rolesDisponibles;

    /**
     * Lista de formatos disponibles en League of Legends.
     * Se inicializa de forma lazy (cuando se solicita por primera vez).
     */
    private List<ScrimFormat> formatosDisponibles;

    /**
     * Constructor privado para implementar el patrón Singleton.
     * Esto previene la creación de múltiples instancias del juego.
     */
    private LeagueOfLegends() {
        // Inicialización lazy de roles y formatos
    }

    /**
     * Obtiene la instancia única de League of Legends (Singleton).
     * Si no existe, la crea; si ya existe, retorna la existente.
     * 
     * Este método es thread-safe mediante sincronización lazy.
     * 
     * @return instancia única de LeagueOfLegends
     */
    public static synchronized LeagueOfLegends getInstance() {
        if (instance == null) {
            instance = new LeagueOfLegends();
        }
        return instance;
    }

    @Override
    public String getNombre() {
        return NOMBRE_JUEGO;
    }

    /**
     * Obtiene la lista de roles disponibles en League of Legends.
     * Los roles son: Top, Jungle, Mid, ADC y Support.
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
                    new RolTopLoL(),
                    new RolJungleLoL(),
                    new RolMidLoL(),
                    new RolADCLoL(),
                    new RolSupportLoL());
        }
        // Retorna una copia para prevenir modificaciones externas
        return new ArrayList<>(rolesDisponibles);
    }

    /**
     * Obtiene la lista de formatos disponibles en League of Legends.
     * Los formatos incluyen: 5v5 Summoner's Rift y ARAM.
     * 
     * Implementa inicialización lazy similar a los roles.
     * 
     * @return lista inmutable de formatos disponibles
     */
    @Override
    public List<ScrimFormat> getFormatosDisponibles() {
        if (formatosDisponibles == null) {
            formatosDisponibles = Arrays.asList(
                    new Formato5v5LoL(),
                    new FormatoARAMLoL());
        }
        // Retorna una copia para prevenir modificaciones externas
        return new ArrayList<>(formatosDisponibles);
    }

    /**
     * Método helper para crear un rol por su nombre.
     * Facilita la creación de roles sin necesidad de conocer
     * las clases concretas.
     * 
     * @param nombreRol nombre del rol ("Top", "Jungle", "Mid", "ADC", "Support")
     * @return instancia del rol solicitado, o null si no existe
     */
    public RolJuego crearRol(String nombreRol) {
        return buscarRolPorNombre(nombreRol);
    }

    /**
     * Obtiene el formato estándar por defecto (5v5 Summoner's Rift).
     * 
     * @return formato 5v5 estándar
     */
    public ScrimFormat getFormatoEstandar() {
        return new Formato5v5LoL();
    }
}
