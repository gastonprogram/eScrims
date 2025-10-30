package controller;

import model.Scrim;
import model.Persistencia.FiltrosScrim;
import model.Persistencia.RepositorioScrim;
import model.Persistencia.impl.RepositorioScrimMemoria;
import java.util.List;

/**
 * Controlador para buscar scrims con filtros.
 * NO depende de las vistas, solo proporciona lógica de negocio pura.
 * Siguiendo el patrón de Login y Register.
 * 
 * @author eScrims Team
 */
public class BuscarScrimController {

    private RepositorioScrim repositorio;

    public BuscarScrimController() {
        this.repositorio = RepositorioScrimMemoria.getInstance();
    }

    /**
     * Busca scrims según los filtros proporcionados.
     * 
     * @param filtros Los filtros de búsqueda
     * @return Lista de scrims que cumplen con los filtros
     * @throws RuntimeException Si ocurre un error durante la búsqueda
     */
    public List<Scrim> buscarScrims(FiltrosScrim filtros) {
        try {
            if (filtros == null) {
                throw new IllegalArgumentException("Los filtros no pueden ser nulos");
            }

            return repositorio.buscarConFiltros(filtros);

        } catch (Exception e) {
            throw new RuntimeException("Error al buscar scrims: " + e.getMessage(), e);
        }
    }

    /**
     * Obtiene todos los scrims disponibles sin aplicar filtros.
     * 
     * @return Lista con todos los scrims
     * @throws RuntimeException Si ocurre un error al obtener los scrims
     */
    public List<Scrim> obtenerTodos() {
        try {
            return repositorio.obtenerTodos();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener todos los scrims: " + e.getMessage(), e);
        }
    }

    /**
     * Busca un scrim por su ID.
     * 
     * @param id El ID del scrim a buscar
     * @return El scrim encontrado o null si no existe
     * @throws IllegalArgumentException Si el ID es nulo o vacío
     * @throws RuntimeException         Si ocurre un error durante la búsqueda
     */
    public Scrim buscarPorId(String id) {
        try {
            if (id == null || id.trim().isEmpty()) {
                throw new IllegalArgumentException("El ID es requerido");
            }

            return repositorio.buscarPorId(id.trim());

        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar scrim por ID: " + e.getMessage(), e);
        }
    }

    /**
     * Cuenta el total de scrims en el repositorio.
     * 
     * @return El número total de scrims
     * @throws RuntimeException Si ocurre un error al contar
     */
    public int contarScrims() {
        try {
            return repositorio.contar();
        } catch (Exception e) {
            throw new RuntimeException("Error al contar scrims: " + e.getMessage(), e);
        }
    }

    /**
     * Elimina un scrim por su ID.
     * 
     * @param id El ID del scrim a eliminar
     * @return true si se eliminó correctamente, false si no se encontró
     * @throws IllegalArgumentException Si el ID es nulo o vacío
     * @throws RuntimeException         Si ocurre un error durante la eliminación
     */
    public boolean eliminarScrim(String id) {
        try {
            if (id == null || id.trim().isEmpty()) {
                throw new IllegalArgumentException("El ID es requerido");
            }

            return repositorio.eliminar(id.trim());

        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar scrim: " + e.getMessage(), e);
        }
    }
}
