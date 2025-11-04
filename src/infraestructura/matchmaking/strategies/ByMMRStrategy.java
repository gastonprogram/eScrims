package infraestructura.matchmaking.strategies;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import dominio.modelo.Scrim;
import dominio.modelo.Usuario;
import infraestructura.matchmaking.MatchmakingStrategy;

/**
 * Estrategia de matchmaking basada en MMR/Rango.
 * 
 * Esta estrategia selecciona jugadores que se encuentren dentro del rango de
 * MMR
 * permitido por el scrim, priorizando aquellos más cercanos al rango objetivo.
 * 
 * Algoritmo:
 * 1. Filtrar candidatos dentro del rango [rangoMin, rangoMax] del scrim
 * 2. Calcular rango promedio objetivo (media entre min y max)
 * 3. Ordenar candidatos por cercanía al rango objetivo
 * 4. Seleccionar hasta completar plazas disponibles
 * 
 * Criterios de selección:
 * - Cumplimiento de requisitos de rango mínimo y máximo
 * - Diferencia máxima de MMR configurable (por defecto: rangoMax - rangoMin)
 * - Prioridad a jugadores con rango más cercano al promedio
 * 
 * Ventajas:
 * - Garantiza partidas balanceadas en skill
 * - Evita disparidad de nivel entre jugadores
 * - Configurable por scrim
 * 
 * Ejemplo:
 * 
 * <pre>
 * Scrim con rangoMin=1500, rangoMax=2000
 * Candidatos: [1400, 1550, 1700, 1800, 2100]
 * Rango objetivo: 1750
 * Resultado filtrado: [1550, 1700, 1800] (1400 y 2100 fuera de rango)
 * Resultado ordenado: [1700, 1800, 1550] (por cercanía a 1750)
 * </pre>
 * 
 * @author eScrims Team
 * @version 1.0
 * @see MatchmakingStrategy
 */
public class ByMMRStrategy implements MatchmakingStrategy {

    private static final String NOMBRE = "MMR";
    private static final String DESCRIPCION = "Selecciona jugadores por rango/MMR dentro del umbral configurado. " +
            "Prioriza jugadores con rango similar para partidas balanceadas.";

    @Override
    public List<Usuario> seleccionar(List<Usuario> candidatos, Scrim scrim) {
        // Validaciones
        if (candidatos == null || scrim == null) {
            throw new IllegalArgumentException("Candidatos y scrim no pueden ser null");
        }

        if (candidatos.isEmpty()) {
            return new ArrayList<>();
        }

        // Obtener requisitos del scrim
        int rangoMin = scrim.getRangoMin();
        int rangoMax = scrim.getRangoMax();
        int plazas = scrim.getPlazas();
        String nombreJuego = scrim.getJuego().getNombre();

        // Calcular rango objetivo (promedio)
        double rangoObjetivo = (rangoMin + rangoMax) / 2.0;

        // Filtrar candidatos que cumplan con el rango requerido
        List<Usuario> candidatosFiltrados = candidatos.stream()
                .filter(usuario -> {
                    // Obtener el rango del usuario para este juego
                    Integer rangoUsuario = usuario.getRangoPorJuego().get(nombreJuego);

                    // Si el usuario no tiene rango para este juego, rechazar
                    if (rangoUsuario == null) {
                        return false;
                    }

                    // Verificar que esté dentro del rango permitido
                    return rangoUsuario >= rangoMin && rangoUsuario <= rangoMax;
                })
                .toList();

        // Si no hay candidatos válidos, retornar lista vacía
        if (candidatosFiltrados.isEmpty()) {
            return new ArrayList<>();
        }

        // Ordenar por cercanía al rango objetivo
        List<Usuario> candidatosOrdenados = new ArrayList<>(candidatosFiltrados);
        candidatosOrdenados.sort(Comparator.comparingDouble(usuario -> {
            Integer rangoUsuario = usuario.getRangoPorJuego().get(nombreJuego);
            return Math.abs(rangoUsuario - rangoObjetivo);
        }));

        // Seleccionar hasta completar las plazas disponibles (o todos si hay menos)
        return candidatosOrdenados.subList(0, Math.min(plazas, candidatosOrdenados.size()));
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
     * Calcula la diferencia de MMR entre dos usuarios.
     * 
     * @param usuario1    primer usuario
     * @param usuario2    segundo usuario
     * @param nombreJuego nombre del juego
     * @return diferencia absoluta de rango, o -1 si alguno no tiene rango
     */
    public static int calcularDiferenciaMMR(Usuario usuario1, Usuario usuario2, String nombreJuego) {
        Integer rango1 = usuario1.getRangoPorJuego().get(nombreJuego);
        Integer rango2 = usuario2.getRangoPorJuego().get(nombreJuego);

        if (rango1 == null || rango2 == null) {
            return -1;
        }

        return Math.abs(rango1 - rango2);
    }

    /**
     * Verifica si un usuario cumple con los requisitos de rango del scrim.
     * 
     * @param usuario usuario a verificar
     * @param scrim   scrim con requisitos
     * @return true si el usuario cumple los requisitos
     */
    public static boolean cumpleRequisitosRango(Usuario usuario, Scrim scrim) {
        String nombreJuego = scrim.getJuego().getNombre();
        Integer rangoUsuario = usuario.getRangoPorJuego().get(nombreJuego);

        if (rangoUsuario == null) {
            return false;
        }

        return rangoUsuario >= scrim.getRangoMin() && rangoUsuario <= scrim.getRangoMax();
    }
}
