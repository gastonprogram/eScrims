package infraestructura.matchmaking;

import java.util.ArrayList;
import java.util.List;

import infraestructura.matchmaking.strategies.ByHistoryStrategy;
import infraestructura.matchmaking.strategies.ByLatencyStrategy;
import infraestructura.matchmaking.strategies.ByMMRStrategy;

/**
 * Registry (Singleton) que centraliza todas las estrategias de matchmaking
 * disponibles.
 * 
 * Proporciona acceso a las diferentes implementaciones de MatchmakingStrategy
 * y permite obtenerlas por número o nombre para su uso en la interfaz de
 * usuario.
 * 
 * @author eScrims Team
 * @version 1.0
 */
public class MatchmakingRegistry {
    private static MatchmakingRegistry instance;
    private final List<MatchmakingStrategy> estrategias;

    private MatchmakingRegistry() {
        estrategias = new ArrayList<>();
        cargarEstrategias();
    }

    /**
     * Obtiene la única instancia del registry (Singleton).
     */
    public static MatchmakingRegistry getInstance() {
        if (instance == null) {
            instance = new MatchmakingRegistry();
        }
        return instance;
    }

    /**
     * Carga todas las estrategias disponibles.
     */
    private void cargarEstrategias() {
        estrategias.add(new ByMMRStrategy());
        estrategias.add(new ByLatencyStrategy());
        estrategias.add(new ByHistoryStrategy());
    }

    /**
     * Obtiene todas las estrategias disponibles.
     */
    public List<MatchmakingStrategy> getEstrategiasDisponibles() {
        return new ArrayList<>(estrategias);
    }

    /**
     * Obtiene una estrategia por su número (1-indexed).
     */
    public MatchmakingStrategy obtenerPorNumero(int numero) {
        if (numero < 1 || numero > estrategias.size()) {
            throw new IllegalArgumentException("Número de estrategia inválido: " + numero);
        }
        return estrategias.get(numero - 1);
    }

    /**
     * Obtiene una estrategia por su nombre.
     */
    public MatchmakingStrategy obtenerPorNombre(String nombre) {
        return estrategias.stream()
                .filter(estrategia -> estrategia.getNombre().equalsIgnoreCase(nombre))
                .findFirst()
                .orElse(null);
    }

    /**
     * Obtiene el número de una estrategia específica.
     */
    public int obtenerNumero(MatchmakingStrategy estrategia) {
        for (int i = 0; i < estrategias.size(); i++) {
            if (estrategias.get(i).getClass().equals(estrategia.getClass())) {
                return i + 1;
            }
        }
        return -1;
    }

    /**
     * Obtiene la cantidad total de estrategias disponibles.
     */
    public int getCantidadEstrategias() {
        return estrategias.size();
    }
}