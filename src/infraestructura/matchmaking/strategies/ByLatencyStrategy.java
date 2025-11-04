package infraestructura.matchmaking.strategies;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import dominio.modelo.Scrim;
import dominio.modelo.Usuario;
import infraestructura.matchmaking.MatchmakingStrategy;

/**
 * Estrategia de matchmaking basada en latencia/ping.
 * 
 * Esta estrategia prioriza jugadores con baja latencia para garantizar
 * una experiencia de juego fluida. Si no hay suficientes jugadores con
 * latencia óptima, expande progresivamente el umbral aceptable.
 * 
 * Algoritmo:
 * 1. Filtrar candidatos con latencia <= latenciaMax del scrim
 * 2. Ordenar por latencia ascendente (menor latencia = mayor prioridad)
 * 3. Si no hay suficientes, expandir umbral en pasos de 20ms
 * 4. Seleccionar hasta completar plazas disponibles
 * 
 * Criterios de selección:
 * - Latencia máxima permitida por el scrim
 * - Expansión progresiva del umbral si no se completan plazas
 * - Prioridad a jugadores con menor ping
 * - Límite de expansión: 300ms (threshold de juego competitivo)
 * 
 * Ventajas:
 * - Minimiza lag y problemas de conectividad
 * - Mejora experiencia de juego
 * - Flexible ante escasez de jugadores de baja latencia
 * 
 * Reglas de negocio:
 * - Latencia < 50ms: Excelente
 * - Latencia 50-100ms: Buena
 * - Latencia 100-150ms: Aceptable
 * - Latencia 150-300ms: Jugable
 * - Latencia > 300ms: No recomendado para competitivo
 * 
 * Ejemplo:
 * 
 * <pre>
 * Scrim con latenciaMax=100ms, plazas=5
 * Candidatos: [30ms, 80ms, 110ms, 150ms, 200ms]
 * 
 * Intento 1 (umbral 100ms): [30ms, 80ms] → Solo 2 jugadores
 * Intento 2 (umbral 120ms): [30ms, 80ms, 110ms] → Solo 3 jugadores
 * Intento 3 (umbral 140ms): [30ms, 80ms, 110ms] → Solo 3 jugadores
 * Intento 4 (umbral 160ms): [30ms, 80ms, 110ms, 150ms] → Solo 4 jugadores
 * Intento 5 (umbral 180ms): [30ms, 80ms, 110ms, 150ms] → Insuficientes
 * ...continúa expandiendo hasta encontrar 5 o alcanzar límite
 * </pre>
 * 
 * @author eScrims Team
 * @version 1.0
 * @see MatchmakingStrategy
 */
public class ByLatencyStrategy implements MatchmakingStrategy {

    private static final String NOMBRE = "Latency";
    private static final String DESCRIPCION = "Prioriza jugadores con baja latencia/ping. " +
            "Expande el umbral progresivamente si no hay suficientes candidatos.";

    // Constantes de configuración
    private static final int PASO_EXPANSION = 20; // Incremento en ms por intento
    private static final int LIMITE_LATENCIA = 300; // Latencia máxima absoluta en ms

    @Override
    public List<Usuario> seleccionar(List<Usuario> candidatos, Scrim scrim) {
        // Validaciones
        if (candidatos == null || scrim == null) {
            throw new IllegalArgumentException("Candidatos y scrim no pueden ser null");
        }

        if (candidatos.isEmpty()) {
            return new ArrayList<>();
        }

        int plazas = scrim.getPlazas();
        int latenciaMaxInicial = scrim.getLatenciaMax();

        // Intentar con la latencia máxima configurada en el scrim
        List<Usuario> seleccionados = intentarSeleccionPorLatencia(
                candidatos, plazas, latenciaMaxInicial);

        // Si encontramos suficientes jugadores, retornar
        if (seleccionados.size() >= plazas) {
            return seleccionados.subList(0, plazas);
        }

        // Si no hay suficientes, expandir progresivamente el umbral
        int umbralActual = latenciaMaxInicial + PASO_EXPANSION;

        while (umbralActual <= LIMITE_LATENCIA && seleccionados.size() < plazas) {
            seleccionados = intentarSeleccionPorLatencia(candidatos, plazas, umbralActual);

            if (seleccionados.size() >= plazas) {
                return seleccionados.subList(0, plazas);
            }

            umbralActual += PASO_EXPANSION;
        }

        // Retornar todos los candidatos encontrados (aunque sean menos que las plazas)
        return seleccionados;
    }

    /**
     * Intenta seleccionar jugadores con latencia máxima especificada.
     * 
     * @param candidatos       lista de candidatos
     * @param plazasRequeridas número de jugadores necesarios
     * @param latenciaMax      latencia máxima permitida
     * @return lista de usuarios seleccionados (ordenados por latencia)
     */
    private List<Usuario> intentarSeleccionPorLatencia(
            List<Usuario> candidatos,
            int plazasRequeridas,
            int latenciaMax) {

        // Filtrar por latencia
        List<Usuario> candidatosFiltrados = candidatos.stream()
                .filter(usuario -> usuario.getLatenciaPromedio() <= latenciaMax)
                .toList();

        // Ordenar por latencia ascendente (menor latencia primero)
        List<Usuario> candidatosOrdenados = new ArrayList<>(candidatosFiltrados);
        candidatosOrdenados.sort(Comparator.comparingInt(Usuario::getLatenciaPromedio));

        return candidatosOrdenados;
    }

    @Override
    public String getNombre() {
        return NOMBRE;
    }

    @Override
    public String getDescripcion() {
        return DESCRIPCION;
    }

    /**
     * Clasifica la calidad de latencia de un jugador.
     * 
     * @param latenciaMs latencia en milisegundos
     * @return calificación descriptiva
     */
    public static String clasificarLatencia(int latenciaMs) {
        if (latenciaMs < 0) {
            return "DESCONOCIDA";
        } else if (latenciaMs < 50) {
            return "EXCELENTE";
        } else if (latenciaMs < 100) {
            return "BUENA";
        } else if (latenciaMs < 150) {
            return "ACEPTABLE";
        } else if (latenciaMs < 300) {
            return "JUGABLE";
        } else {
            return "NO_RECOMENDADO";
        }
    }

    /**
     * Verifica si la latencia de un usuario es aceptable para el scrim.
     * 
     * @param usuario usuario a verificar
     * @param scrim   scrim con requisitos
     * @return true si la latencia es aceptable
     */
    public static boolean cumpleRequisitosLatencia(Usuario usuario, Scrim scrim) {
        return usuario.getLatenciaPromedio() <= scrim.getLatenciaMax();
    }

    /**
     * Obtiene la latencia promedio de una lista de usuarios.
     * 
     * @param usuarios lista de usuarios
     * @return latencia promedio en ms, o 0 si la lista está vacía
     */
    public static double calcularLatenciaPromedio(List<Usuario> usuarios) {
        if (usuarios == null || usuarios.isEmpty()) {
            return 0.0;
        }

        return usuarios.stream()
                .mapToInt(Usuario::getLatenciaPromedio)
                .average()
                .orElse(0.0);
    }
}
