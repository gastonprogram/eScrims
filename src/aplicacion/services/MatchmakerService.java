package aplicacion.services;

import java.util.List;

import dominio.modelo.Scrim;
import dominio.modelo.Usuario;
import infraestructura.matchmaking.MatchmakingStrategy;

/**
 * Servicio central de emparejamiento (matchmaking) que aplica el patrón
 * Strategy.
 * 
 * Este servicio actúa como contexto en el patrón Strategy, permitiendo cambiar
 * dinámicamente el algoritmo de selección de jugadores. Cada scrim puede tener
 * su propia estrategia configurada, proporcionando flexibilidad en el
 * matchmaking.
 * 
 * Flujo de uso:
 * 1. Crear servicio con estrategia deseada
 * 2. Proporcionar lista de candidatos y scrim
 * 3. Obtener lista filtrada y ordenada de jugadores seleccionados
 * 
 * Ejemplo:
 * 
 * <pre>
 * MatchmakingStrategy estrategia = new ByMMRStrategy();
 * MatchmakerService service = new MatchmakerService(estrategia);
 * List<Usuario> seleccionados = service.seleccionarJugadores(candidatos, scrim);
 * </pre>
 * 
 * Características:
 * - Inyección de dependencias simple (Strategy pattern)
 * - Estrategia intercambiable en tiempo de ejecución
 * - Validaciones de entrada
 * - Manejo de casos edge (lista vacía, scrim completo, etc.)
 * 
 * @author eScrims Team
 * @version 1.0
 * @see MatchmakingStrategy
 * @see infraestructura.matchmaking.strategies.ByMMRStrategy
 * @see infraestructura.matchmaking.strategies.ByLatencyStrategy
 * @see infraestructura.matchmaking.strategies.ByHistoryStrategy
 */
public class MatchmakerService {

    private MatchmakingStrategy estrategia;

    /**
     * Constructor con inyección de estrategia.
     * 
     * @param estrategia estrategia de matchmaking a utilizar
     * @throws IllegalArgumentException si estrategia es null
     */
    public MatchmakerService(MatchmakingStrategy estrategia) {
        if (estrategia == null) {
            throw new IllegalArgumentException("La estrategia de matchmaking no puede ser null");
        }
        this.estrategia = estrategia;
    }

    /**
     * Selecciona los mejores jugadores para un scrim según la estrategia
     * configurada.
     * 
     * Este método delega la lógica de selección a la estrategia actual,
     * aplicando validaciones previas y posteriores.
     * 
     * @param candidatos lista de usuarios candidatos
     * @param scrim      scrim que necesita jugadores
     * @return lista de usuarios seleccionados (ordenados por prioridad)
     * @throws IllegalArgumentException si candidatos o scrim son null
     */
    public List<Usuario> seleccionarJugadores(List<Usuario> candidatos, Scrim scrim) {
        // Validaciones de entrada
        if (candidatos == null) {
            throw new IllegalArgumentException("La lista de candidatos no puede ser null");
        }
        if (scrim == null) {
            throw new IllegalArgumentException("El scrim no puede ser null");
        }

        // Casos edge
        if (candidatos.isEmpty()) {
            return List.of();
        }

        if (scrim.getPlazas() <= 0) {
            return List.of();
        }

        // Delegar a la estrategia
        List<Usuario> seleccionados = estrategia.seleccionar(candidatos, scrim);

        // Validación posterior: no exceder plazas disponibles
        int plazasDisponibles = scrim.getPlazas();
        if (seleccionados.size() > plazasDisponibles) {
            return seleccionados.subList(0, plazasDisponibles);
        }

        return seleccionados;
    }

    /**
     * Cambia la estrategia de matchmaking en tiempo de ejecución.
     * 
     * @param nuevaEstrategia nueva estrategia a utilizar
     * @throws IllegalArgumentException si nuevaEstrategia es null
     */
    public void setEstrategia(MatchmakingStrategy nuevaEstrategia) {
        if (nuevaEstrategia == null) {
            throw new IllegalArgumentException("La estrategia de matchmaking no puede ser null");
        }
        this.estrategia = nuevaEstrategia;
    }

    /**
     * Obtiene la estrategia actual.
     * 
     * @return estrategia de matchmaking configurada
     */
    public MatchmakingStrategy getEstrategia() {
        return estrategia;
    }

    /**
     * Obtiene el nombre de la estrategia actual.
     * 
     * @return nombre de la estrategia
     */
    public String getNombreEstrategia() {
        return estrategia.getNombre();
    }

    /**
     * Obtiene la descripción de la estrategia actual.
     * 
     * @return descripción de la estrategia
     */
    public String getDescripcionEstrategia() {
        return estrategia.getDescripcion();
    }
}
