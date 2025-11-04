package infraestructura.matchmaking.strategies;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dominio.modelo.HistorialUsuario;
import dominio.modelo.Scrim;
import dominio.modelo.Usuario;
import infraestructura.matchmaking.MatchmakingStrategy;

/**
 * Estrategia de matchmaking basada en historial y compatibilidad.
 * 
 * Esta es la estrategia más sofisticada, considera múltiples factores
 * para crear equipos balanceados y confiables:
 * - Historial de comportamiento (abandono, fair play)
 * - Compatibilidad de roles (evitar equipos desbalanceados)
 * - Confiabilidad y experiencia
 * 
 * Algoritmo:
 * 1. Filtrar candidatos con buen comportamiento (fair play > 0.5)
 * 2. Calcular score de compatibilidad para cada candidato
 * 3. Penalizar jugadores con alto abandono
 * 4. Priorizar diversidad de roles
 * 5. Ordenar por score total descendente
 * 6. Seleccionar top N hasta completar plazas
 * 
 * Score de compatibilidad (máximo 100 puntos):
 * - Fair play: 40 puntos (tasaFairPlay * 40)
 * - Baja tasa de abandono: 30 puntos ((1 - tasaAbandono) * 30)
 * - Experiencia: 20 puntos (min(20, partidasJugadas / 5))
 * - Diversidad de roles: 10 puntos (si rol no repetido)
 * 
 * Ventajas:
 * - Minimiza toxicidad y abandonos
 * - Crea equipos balanceados en roles
 * - Premia jugadores confiables
 * - Mejora calidad de la experiencia
 * 
 * Reglas de fair play:
 * - Fair play >= 0.9: Jugador ejemplar
 * - Fair play 0.7-0.9: Buen jugador
 * - Fair play 0.5-0.7: Jugador promedio
 * - Fair play < 0.5: Jugador problemático (rechazado)
 * 
 * Ejemplo de scoring:
 * 
 * <pre>
 * Usuario A: fairPlay=0.95, abandono=0.05, partidas=50, rol=TOP
 *   → Score: (0.95*40) + (0.95*30) + (10) + (10) = 88.5 pts
 * 
 * Usuario B: fairPlay=0.70, abandono=0.20, partidas=20, rol=TOP
 *   → Score: (0.70*40) + (0.80*30) + (4) + (0) = 60 pts (penalizado por rol repetido)
 * 
 * Usuario C: fairPlay=0.85, abandono=0.10, partidas=100, rol=JUNGLE
 *   → Score: (0.85*40) + (0.90*30) + (20) + (10) = 91 pts
 * </pre>
 * 
 * @author eScrims Team
 * @version 1.0
 * @see MatchmakingStrategy
 * @see dominio.modelo.HistorialUsuario
 */
public class ByHistoryStrategy implements MatchmakingStrategy {

    private static final String NOMBRE = "History";
    private static final String DESCRIPCION = "Selecciona jugadores por historial de comportamiento, fair play y compatibilidad de roles. "
            +
            "Penaliza alto abandono y premia diversidad de roles.";

    // Constantes de scoring
    private static final double PESO_FAIR_PLAY = 40.0;
    private static final double PESO_ABANDONO = 30.0;
    private static final double PESO_EXPERIENCIA = 20.0;
    private static final double PESO_DIVERSIDAD_ROL = 10.0;

    // Umbrales
    private static final double UMBRAL_FAIR_PLAY_MINIMO = 0.5;
    private static final double UMBRAL_ABANDONO_MAXIMO = 0.30;

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
        String nombreJuego = scrim.getJuego().getNombre();

        // Paso 1: Filtrar candidatos con comportamiento aceptable
        List<Usuario> candidatosFiltrados = candidatos.stream()
                .filter(usuario -> {
                    HistorialUsuario historial = usuario.getHistorial();
                    // Rechazar si no hay historial o si el comportamiento es inaceptable
                    return historial != null
                            && historial.getTasaFairPlay() >= UMBRAL_FAIR_PLAY_MINIMO
                            && historial.getTasaAbandono() <= UMBRAL_ABANDONO_MAXIMO;
                })
                .toList();

        // Si no hay candidatos válidos, retornar lista vacía
        if (candidatosFiltrados.isEmpty()) {
            return new ArrayList<>();
        }

        // Paso 2: Calcular score de cada candidato
        Map<Usuario, Double> scoresPorUsuario = new HashMap<>();
        Map<String, Integer> contadorRoles = new HashMap<>();

        for (Usuario usuario : candidatosFiltrados) {
            double score = calcularScoreCompatibilidad(
                    usuario,
                    nombreJuego,
                    contadorRoles);
            scoresPorUsuario.put(usuario, score);
        }

        // Paso 3: Ordenar por score descendente
        List<Usuario> candidatosOrdenados = new ArrayList<>(candidatosFiltrados);
        candidatosOrdenados.sort(Comparator.comparingDouble(
                usuario -> -scoresPorUsuario.get(usuario) // Negativo para orden descendente
        ));

        // Paso 4: Seleccionar top N con balance de roles
        List<Usuario> seleccionados = seleccionarConBalanceRoles(
                candidatosOrdenados,
                plazas,
                nombreJuego);

        return seleccionados;
    }

    /**
     * Calcula el score de compatibilidad de un usuario.
     * 
     * @param usuario       usuario a evaluar
     * @param nombreJuego   nombre del juego
     * @param contadorRoles contador de roles ya seleccionados
     * @return score total (0-100)
     */
    private double calcularScoreCompatibilidad(
            Usuario usuario,
            String nombreJuego,
            Map<String, Integer> contadorRoles) {

        HistorialUsuario historial = usuario.getHistorial();

        // Component 1: Fair play (40 puntos)
        double scoreFairPlay = historial.getTasaFairPlay() * PESO_FAIR_PLAY;

        // Component 2: Baja tasa de abandono (30 puntos)
        double scoreAbandono = (1.0 - historial.getTasaAbandono()) * PESO_ABANDONO;

        // Component 3: Experiencia (20 puntos)
        int partidasJugadas = historial.getPartidasJugadas();
        double scoreExperiencia = Math.min(PESO_EXPERIENCIA, partidasJugadas / 5.0);

        // Component 4: Diversidad de roles (10 puntos)
        double scoreDiversidad = calcularScoreDiversidadRoles(
                usuario,
                nombreJuego,
                contadorRoles);

        return scoreFairPlay + scoreAbandono + scoreExperiencia + scoreDiversidad;
    }

    /**
     * Calcula el score de diversidad de roles.
     * Penaliza roles ya seleccionados múltiples veces.
     * 
     * @param usuario       usuario a evaluar
     * @param nombreJuego   nombre del juego
     * @param contadorRoles contador de roles seleccionados
     * @return score de diversidad (0-10)
     */
    private double calcularScoreDiversidadRoles(
            Usuario usuario,
            String nombreJuego,
            Map<String, Integer> contadorRoles) {

        List<String> rolesUsuario = usuario.getRolesPreferidosParaJuego(nombreJuego);

        if (rolesUsuario.isEmpty()) {
            return 0.0; // Sin roles configurados
        }

        // Tomar el rol principal (primer rol de la lista)
        String rolPrincipal = rolesUsuario.get(0);
        int vecesSeleccionado = contadorRoles.getOrDefault(rolPrincipal, 0);

        // Penalizar roles ya seleccionados
        // 0 veces = 10 puntos, 1 vez = 7 puntos, 2+ veces = 3 puntos
        if (vecesSeleccionado == 0) {
            return PESO_DIVERSIDAD_ROL;
        } else if (vecesSeleccionado == 1) {
            return PESO_DIVERSIDAD_ROL * 0.7;
        } else {
            return PESO_DIVERSIDAD_ROL * 0.3;
        }
    }

    /**
     * Selecciona usuarios con balance de roles.
     * Evita seleccionar demasiados jugadores del mismo rol.
     * 
     * @param candidatos  lista ordenada por score
     * @param plazas      número de jugadores a seleccionar
     * @param nombreJuego nombre del juego
     * @return lista de usuarios seleccionados
     */
    private List<Usuario> seleccionarConBalanceRoles(
            List<Usuario> candidatos,
            int plazas,
            String nombreJuego) {

        List<Usuario> seleccionados = new ArrayList<>();
        Map<String, Integer> contadorRoles = new HashMap<>();

        // Límite máximo de jugadores por rol (para evitar 5 del mismo rol)
        int limiteRol = Math.max(2, plazas / 3);

        for (Usuario candidato : candidatos) {
            if (seleccionados.size() >= plazas) {
                break;
            }

            List<String> roles = candidato.getRolesPreferidosParaJuego(nombreJuego);

            if (roles.isEmpty()) {
                // Sin roles, pero aún puede ser seleccionado si hay espacio
                seleccionados.add(candidato);
                continue;
            }

            String rolPrincipal = roles.get(0);
            int cantidadRol = contadorRoles.getOrDefault(rolPrincipal, 0);

            // Verificar si ya alcanzamos el límite de este rol
            if (cantidadRol < limiteRol) {
                seleccionados.add(candidato);
                contadorRoles.put(rolPrincipal, cantidadRol + 1);
            }
        }

        // Si no completamos las plazas por restricciones de rol,
        // agregar los mejores candidatos restantes sin restricción
        if (seleccionados.size() < plazas) {
            for (Usuario candidato : candidatos) {
                if (seleccionados.size() >= plazas) {
                    break;
                }
                if (!seleccionados.contains(candidato)) {
                    seleccionados.add(candidato);
                }
            }
        }

        return seleccionados;
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
     * Evalúa la compatibilidad entre un grupo de usuarios.
     * 
     * @param usuarios lista de usuarios
     * @return score de compatibilidad grupal (0-100)
     */
    public static double evaluarCompatibilidadGrupal(List<Usuario> usuarios) {
        if (usuarios == null || usuarios.isEmpty()) {
            return 0.0;
        }

        double sumaScores = 0.0;

        for (Usuario usuario : usuarios) {
            HistorialUsuario historial = usuario.getHistorial();
            if (historial != null) {
                sumaScores += historial.getScoreConfiabilidad();
            }
        }

        return sumaScores / usuarios.size();
    }

    /**
     * Calcula la varianza de roles en un equipo.
     * Mayor varianza = mejor balance.
     * 
     * @param usuarios    lista de usuarios
     * @param nombreJuego nombre del juego
     * @return índice de varianza (0-1, donde 1 es máxima diversidad)
     */
    public static double calcularVarianzaRoles(List<Usuario> usuarios, String nombreJuego) {
        if (usuarios == null || usuarios.isEmpty()) {
            return 0.0;
        }

        Map<String, Integer> contadorRoles = new HashMap<>();

        for (Usuario usuario : usuarios) {
            List<String> roles = usuario.getRolesPreferidosParaJuego(nombreJuego);
            if (!roles.isEmpty()) {
                String rolPrincipal = roles.get(0);
                contadorRoles.put(rolPrincipal, contadorRoles.getOrDefault(rolPrincipal, 0) + 1);
            }
        }

        if (contadorRoles.isEmpty()) {
            return 0.0;
        }

        // Calcular diversidad: roles únicos / total roles
        int rolesUnicos = contadorRoles.size();
        int totalRoles = usuarios.size();

        return (double) rolesUnicos / totalRoles;
    }
}
