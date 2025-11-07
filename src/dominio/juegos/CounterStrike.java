package dominio.juegos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dominio.juegos.formatos.Formato5v5CompetitiveCS;
import dominio.juegos.formatos.Formato2v2WingmanCS;
import dominio.roles.RolJuego;
import dominio.roles.cs.RolAWPerCS;
import dominio.roles.cs.RolEntryFraggerCS;
import dominio.roles.cs.RolIglCS;
import dominio.roles.cs.RolLurkerCS;
import dominio.roles.cs.RolSupportCS;
import dominio.valueobjects.formatosScrims.ScrimFormat;

/**
 * Implementación concreta del juego Counter-Strike.
 * Define los roles específicos del juego (AWPer, IGL, Entry Fragger, Lurker,
 * Support)
 * y los formatos disponibles (5v5 Competitive, 2v2 Wingman, 5v5 Retake).
 * 
 * Esta clase implementa el patrón Singleton para asegurar que solo
 * exista una instancia del juego en toda la aplicación, evitando
 * duplicación innecesaria de datos.
 * 
 * También aplica el patrón Template Method heredado de Juego,
 * donde define los métodos abstractos específicos para CS.
 * 
 * @author eScrims Team
 */
public class CounterStrike extends Juego {

    // Singleton instance
    private static CounterStrike instance;

    private static final String NOMBRE_JUEGO = "Counter-Strike";

    /**
     * Lista de roles disponibles en Counter-Strike.
     * Se inicializa de forma lazy (cuando se solicita por primera vez).
     */
    private List<RolJuego> rolesDisponibles;

    /**
     * Lista de formatos disponibles en Counter-Strike.
     * Se inicializa de forma lazy (cuando se solicita por primera vez).
     */
    private List<ScrimFormat> formatosDisponibles;

    /**
     * Constructor privado para implementar el patrón Singleton.
     * Esto previene la creación de múltiples instancias del juego.
     */
    private CounterStrike() {
        // Inicialización lazy de roles y formatos
    }

    /**
     * Obtiene la instancia única de CounterStrike (Singleton).
     * Si no existe, la crea; si ya existe, retorna la existente.
     * 
     * Este método es thread-safe mediante sincronización lazy.
     * 
     * @return instancia única de CounterStrike
     */
    public static synchronized CounterStrike getInstance() {
        if (instance == null) {
            instance = new CounterStrike();
        }
        return instance;
    }

    @Override
    public String getNombre() {
        return NOMBRE_JUEGO;
    }

    /**
     * Obtiene la lista de roles disponibles en Counter-Strike.
     * Los roles son: AWPer, Lurker, IGL, Entry Fragger y Support.
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
                    new RolAWPerCS(),
                    new RolLurkerCS(),
                    new RolIglCS(),
                    new RolEntryFraggerCS(),
                    new RolSupportCS());
        }
        // Retorna una copia para prevenir modificaciones externas
        return new ArrayList<>(rolesDisponibles);
    }

    /**
     * Obtiene la lista de formatos disponibles en Counter-Strike.
     * Los formatos incluyen: 5v5 Competitive, 2v2 Wingman y 5v5 Retake.
     * 
     * Implementa inicialización lazy similar a los roles.
     * 
     * @return lista inmutable de formatos disponibles
     */
    @Override
    public List<ScrimFormat> getFormatosDisponibles() {
        if (formatosDisponibles == null) {
            formatosDisponibles = Arrays.asList(
                    new Formato5v5CompetitiveCS(),
                    new Formato2v2WingmanCS());
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
     * Obtiene el formato estándar por defecto (5v5 Competitive).
     * 
     * @return formato 5v5 competitivo estándar
     */
    public ScrimFormat getFormatoEstandar() {
        return new Formato5v5CompetitiveCS();
    }
}
