package dominio.juegos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Registro centralizado de todos los juegos disponibles en el sistema.
 * Implementado como Singleton para mantener una única fuente de verdad.
 */
public class JuegosRegistry {
    private static JuegosRegistry instance;
    private final List<Juego> juegosDisponibles;

    private JuegosRegistry() {
        juegosDisponibles = new ArrayList<>();
        inicializarJuegos();
    }

    public static synchronized JuegosRegistry getInstance() {
        if (instance == null) {
            instance = new JuegosRegistry();
        }
        return instance;
    }

    /**
     * Inicializa el catálogo de juegos disponibles.
     * Aquí se registran todos los juegos que el sistema soporta.
     */
    private void inicializarJuegos() {
        // Por ahora solo League of Legends
        juegosDisponibles.add(LeagueOfLegends.getInstance());

        // En el futuro se pueden agregar más juegos:
        // juegosDisponibles.add(Valorant.getInstance());
        // juegosDisponibles.add(CounterStrike.getInstance());
    }

    /**
     * Obtiene todos los juegos disponibles en el sistema.
     * 
     * @return Lista inmutable de juegos disponibles
     */
    public List<Juego> getJuegosDisponibles() {
        return Collections.unmodifiableList(juegosDisponibles);
    }

    /**
     * Alias para getJuegosDisponibles() - mantiene compatibilidad.
     * 
     * @return Lista inmutable de juegos disponibles
     */
    public List<Juego> getJuegos() {
        return getJuegosDisponibles();
    }

    /**
     * Busca un juego por su nombre (case-insensitive).
     * 
     * @param nombre Nombre del juego a buscar
     * @return El juego si se encuentra, null en caso contrario
     */
    public Juego buscarPorNombre(String nombre) {
        return juegosDisponibles.stream()
                .filter(j -> j.getNombre().equalsIgnoreCase(nombre))
                .findFirst()
                .orElse(null);
    }

    /**
     * Obtiene un juego por su índice en la lista (base 1).
     * 
     * @param numero Número del juego (1-based index)
     * @return El juego correspondiente o null si el índice es inválido
     */
    public Juego obtenerPorNumero(int numero) {
        if (numero < 1 || numero > juegosDisponibles.size()) {
            return null;
        }
        return juegosDisponibles.get(numero - 1);
    }

    /**
     * Obtiene la cantidad de juegos disponibles.
     * 
     * @return Cantidad de juegos registrados
     */
    public int getCantidadJuegos() {
        return juegosDisponibles.size();
    }
}
