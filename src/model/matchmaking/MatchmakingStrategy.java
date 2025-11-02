package model.matchmaking;

import model.Usuario;
import model.Scrim;
import java.util.List;

/**
 * Interfaz que define el contrato para estrategias de emparejamiento
 * (matchmaking).
 * 
 * El patrón Strategy permite intercambiar algoritmos de selección de jugadores
 * sin modificar el código cliente. Cada implementación aplica criterios
 * diferentes
 * para seleccionar los mejores candidatos para un scrim.
 * 
 * Estrategias disponibles:
 * - ByMMRStrategy: Selecciona por rango/MMR dentro de diferencia configurable
 * - ByLatencyStrategy: Prioriza jugadores con latencia baja
 * - ByHistoryStrategy: Considera historial, compatibilidad de roles y fair play
 * 
 * Responsabilidades de las implementaciones:
 * 1. Filtrar candidatos según criterios específicos
 * 2. Ordenar candidatos por prioridad/relevancia
 * 3. Seleccionar exactamente el número de plazas requeridas
 * 4. Retornar lista vacía si no hay suficientes candidatos válidos
 * 
 * Reglas de negocio:
 * - NO modificar el objeto Scrim ni los objetos Usuario
 * - Respetar el número de plazas del scrim
 * - Garantizar que todos los seleccionados cumplen requisitos mínimos
 * 
 * @author eScrims Team
 * @version 1.0
 * @see model.matchmaking.MatchmakerService
 * @see model.matchmaking.strategies.ByMMRStrategy
 * @see model.matchmaking.strategies.ByLatencyStrategy
 * @see model.matchmaking.strategies.ByHistoryStrategy
 */
public interface MatchmakingStrategy {

    /**
     * Selecciona los mejores candidatos para un scrim según los criterios
     * específicos de la estrategia.
     * 
     * @param candidatos Lista de usuarios candidatos a postularse
     * @param scrim      Scrim que requiere jugadores
     * @return Lista de usuarios seleccionados (máximo igual a plazas disponibles)
     *         o lista vacía si no hay suficientes candidatos válidos
     * @throws IllegalArgumentException si candidatos o scrim son null
     */
    List<Usuario> seleccionar(List<Usuario> candidatos, Scrim scrim);

    /**
     * Obtiene el nombre descriptivo de la estrategia.
     * 
     * @return nombre de la estrategia (ej: "MMR", "Latency", "History")
     */
    String getNombre();

    /**
     * Obtiene una descripción detallada de los criterios de selección.
     * 
     * @return descripción de la estrategia
     */
    String getDescripcion();
}
