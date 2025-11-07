package aplicacion.services;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import aplicacion.builders.FiltrosScrim;
import aplicacion.builders.ScrimBuilder;
import dominio.juegos.Juego;
import dominio.modelo.Scrim;
import dominio.valueobjects.formatosScrims.ScrimFormat;
import infraestructura.persistencia.repository.RepositorioScrim;

/**
 * Servicio de aplicación para la gestión de Scrims.
 * 
 * Responsabilidades:
 * - Crear scrims con validaciones
 * - Buscar scrims con filtros
 * - Eliminar scrims
 * - Validar reglas de negocio relacionadas con scrims
 * 
 * @author eScrims Team
 */
public class ScrimService {

    private final RepositorioScrim repositorioScrim;

    public ScrimService(RepositorioScrim repositorioScrim) {
        this.repositorioScrim = repositorioScrim;
    }

    /**
     * Crea un scrim con los parámetros proporcionados.
     * 
     * @param juego         El juego del scrim
     * @param formato       El formato del scrim
     * @param fechaHora     La fecha y hora del scrim
     * @param rangoMin      El rango mínimo requerido
     * @param rangoMax      El rango máximo requerido
     * @param latenciaMax   La latencia máxima permitida
     * @param organizadorId El ID del usuario que organiza el scrim
     * @return El scrim creado con su ID asignado
     * @throws IllegalArgumentException Si algún parámetro es inválido
     */
    public Scrim crearScrim(Juego juego, ScrimFormat formato, LocalDateTime fechaHora,
            int rangoMin, int rangoMax, int latenciaMax, String organizadorId) {

        // Validaciones de negocio
        validarParametrosCreacion(juego, formato, fechaHora, rangoMin, rangoMax, latenciaMax);

        // Obtener roles del juego
        var roles = juego.getRolesDisponibles().stream()
                .map(rol -> rol.getNombre())
                .toList();

        // Construir el scrim usando el builder
        ScrimBuilder builder = new ScrimBuilder();
        Scrim scrim = builder
                .withJuego(juego)
                .withFormato(formato)
                .withFechaHora(fechaHora)
                .withRango(rangoMin, rangoMax)
                .withLatenciaMaxima(latenciaMax)
                .withRolesRequeridos(Arrays.asList(roles.toArray(new String[0])))
                .build();

        // Establecer el organizador
        scrim.setCreatedBy(organizadorId);

        // Persistir
        boolean guardado = repositorioScrim.guardar(scrim);
        if (!guardado) {
            throw new RuntimeException("No se pudo guardar el scrim en el repositorio");
        }

        return scrim;
    }

    /**
     * Crea un scrim con estrategia de matchmaking específica.
     * 
     * @param juego                 El juego del scrim
     * @param formato               El formato del scrim
     * @param fechaHora             La fecha y hora del scrim
     * @param rangoMin              El rango mínimo requerido
     * @param rangoMax              El rango máximo requerido
     * @param latenciaMax           La latencia máxima permitida
     * @param estrategiaMatchmaking La estrategia de matchmaking ("MMR", "Latency",
     *                              "History")
     * @param organizadorId         El ID del usuario que organiza el scrim
     * @return El scrim creado con su ID asignado
     * @throws IllegalArgumentException Si algún parámetro es inválido
     */
    public Scrim crearScrimConEstrategia(Juego juego, ScrimFormat formato, LocalDateTime fechaHora,
            int rangoMin, int rangoMax, int latenciaMax, String estrategiaMatchmaking, String organizadorId) {

        // Validaciones de negocio
        validarParametrosCreacion(juego, formato, fechaHora, rangoMin, rangoMax, latenciaMax);

        if (estrategiaMatchmaking == null || estrategiaMatchmaking.trim().isEmpty()) {
            throw new IllegalArgumentException("La estrategia de matchmaking no puede ser null o vacía");
        }

        // Obtener roles del juego
        var roles = juego.getRolesDisponibles().stream()
                .map(rol -> rol.getNombre())
                .toList();

        // Construir el scrim usando el builder
        ScrimBuilder builder = new ScrimBuilder();
        Scrim scrim = builder
                .withJuego(juego)
                .withFormato(formato)
                .withFechaHora(fechaHora)
                .withRango(rangoMin, rangoMax)
                .withLatenciaMaxima(latenciaMax)
                .withRolesRequeridos(Arrays.asList(roles.toArray(new String[0])))
                .withEstrategiaMatchmaking(estrategiaMatchmaking)
                .build();

        // Establecer el organizador
        scrim.setCreatedBy(organizadorId);

        // Persistir
        boolean guardado = repositorioScrim.guardar(scrim);
        if (!guardado) {
            throw new RuntimeException("No se pudo guardar el scrim en el repositorio");
        }

        return scrim;
    }

    /**
     * Busca scrims según los filtros proporcionados.
     * 
     * @param filtros Los filtros de búsqueda
     * @return Lista de scrims que cumplen con los filtros
     */
    public List<Scrim> buscarScrims(FiltrosScrim filtros) {
        if (filtros == null) {
            throw new IllegalArgumentException("Los filtros no pueden ser nulos");
        }
        return repositorioScrim.buscarConFiltros(filtros);
    }

    /**
     * Obtiene todos los scrims disponibles sin aplicar filtros.
     * 
     * @return Lista con todos los scrims
     */
    public List<Scrim> obtenerTodos() {
        return repositorioScrim.obtenerTodos();
    }

    /**
     * Obtiene todos los scrims disponibles para postularse (en estado BUSCANDO).
     * Excluye los scrims del usuario actual si es organizador.
     * 
     * @param userId ID del usuario que quiere postularse (para excluir sus propios
     *               scrims)
     * @return Lista de scrims disponibles para postularse
     */
    public List<Scrim> obtenerScrimsDisponibles(String userId) {
        return repositorioScrim.obtenerTodos().stream()
                .filter(scrim -> {
                    // Debe estar en estado BUSCANDO
                    if (scrim.getState() == null) {
                        scrim.reconstruirEstado();
                    }
                    return "BUSCANDO".equals(scrim.getState().getEstado());
                })
                .filter(scrim -> !scrim.getCreatedBy().equals(userId)) // No puede postularse a su propio scrim
                .filter(scrim -> !scrim.yaSePostulo(userId)) // No se ha postulado antes
                .toList();
    }

    /**
     * Busca un scrim por su ID.
     * 
     * @param id El ID del scrim a buscar
     * @return El scrim encontrado
     * @throws IllegalArgumentException Si el ID es nulo o vacío o no se encuentra
     */
    public Scrim buscarPorId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID es requerido");
        }

        Scrim scrim = repositorioScrim.buscarPorId(id.trim());
        if (scrim == null) {
            throw new IllegalArgumentException("Scrim no encontrado con ID: " + id);
        }

        return scrim;
    }

    /**
     * Cuenta el total de scrims en el repositorio.
     * 
     * @return El número total de scrims
     */
    public int contarScrims() {
        return repositorioScrim.contar();
    }

    /**
     * Elimina un scrim por su ID.
     * 
     * @param id El ID del scrim a eliminar
     * @return true si se eliminó correctamente
     * @throws IllegalArgumentException Si el ID es nulo o vacío o no se encuentra
     */
    public boolean eliminarScrim(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID es requerido");
        }

        boolean eliminado = repositorioScrim.eliminar(id.trim());
        if (!eliminado) {
            throw new IllegalArgumentException("No se pudo eliminar el scrim con ID: " + id);
        }

        return true;
    }

    /**
     * Valida los parámetros de creación de un scrim.
     */
    private void validarParametrosCreacion(Juego juego, ScrimFormat formato, LocalDateTime fechaHora,
            int rangoMin, int rangoMax, int latenciaMax) {

        if (juego == null) {
            throw new IllegalArgumentException("El juego es requerido");
        }

        if (formato == null) {
            throw new IllegalArgumentException("El formato es requerido");
        }

        if (fechaHora == null) {
            throw new IllegalArgumentException("La fecha y hora son requeridas");
        }

        if (fechaHora.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("La fecha no puede ser anterior a la actual");
        }

        if (rangoMin < 1 || rangoMin > 100) {
            throw new IllegalArgumentException("El rango mínimo debe estar entre 1 y 100");
        }

        if (rangoMax < 1 || rangoMax > 100) {
            throw new IllegalArgumentException("El rango máximo debe estar entre 1 y 100");
        }

        if (rangoMin > rangoMax) {
            throw new IllegalArgumentException("El rango mínimo no puede ser mayor que el máximo");
        }

        if (latenciaMax < 0) {
            throw new IllegalArgumentException("La latencia no puede ser negativa");
        }

        if (latenciaMax > 1000) {
            throw new IllegalArgumentException("La latencia máxima no puede superar 1000ms");
        }
    }

    /**
     * Obtiene todos los scrims creados por un organizador específico.
     * 
     * @param organizadorId El ID del organizador
     * @return Lista de scrims del organizador
     * @throws IllegalArgumentException Si el organizadorId es null o vacío
     */
    public List<Scrim> obtenerScrimsPorOrganizador(String organizadorId) {
        if (organizadorId == null || organizadorId.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID del organizador es requerido");
        }

        return repositorioScrim.obtenerTodos().stream()
                .filter(scrim -> organizadorId.equals(scrim.getCreatedBy()))
                .toList();
    }

    /**
     * Cambia la estrategia de matchmaking de un scrim.
     * 
     * @param scrimId         El ID del scrim
     * @param nuevaEstrategia La nueva estrategia de matchmaking
     * @throws IllegalArgumentException Si los parámetros son inválidos o el scrim
     *                                  no existe
     */
    public void cambiarEstrategiaMatchmaking(String scrimId, String nuevaEstrategia) {
        if (scrimId == null || scrimId.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID del scrim es requerido");
        }

        if (nuevaEstrategia == null || nuevaEstrategia.trim().isEmpty()) {
            throw new IllegalArgumentException("La nueva estrategia es requerida");
        }

        // Buscar el scrim
        Scrim scrim = buscarPorId(scrimId);
        if (scrim == null) {
            throw new IllegalArgumentException("No se encontró el scrim con ID: " + scrimId);
        }

        // Cambiar la estrategia
        scrim.setEstrategiaMatchmaking(nuevaEstrategia.trim());

        // Guardar cambios usando actualizar() en lugar de guardar()
        boolean actualizado = repositorioScrim.actualizar(scrim);
        if (!actualizado) {
            throw new RuntimeException("No se pudo guardar el cambio de estrategia");
        }
    }
}
