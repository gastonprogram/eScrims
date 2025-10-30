package model.Persistencia;

import model.Scrim;
import java.util.List;

/**
 * Interfaz para el repositorio de Scrims.
 * Define las operaciones básicas para persistencia y búsqueda de scrims.
 * 
 * Aplica el principio de inversión de dependencias (DIP):
 * Los controllers dependen de esta interfaz, no de implementaciones concretas.
 * 
 * @author eScrims Team
 */
public interface RepositorioScrim {

    /**
     * Guarda un nuevo scrim en el repositorio.
     * 
     * @param scrim el scrim a guardar
     * @return true si se guardó exitosamente
     */
    boolean guardar(Scrim scrim);

    /**
     * Busca un scrim por su ID.
     * 
     * @param id el ID del scrim
     * @return el scrim encontrado, o null si no existe
     */
    Scrim buscarPorId(String id);

    /**
     * Obtiene todos los scrims disponibles.
     * 
     * @return lista de todos los scrims
     */
    List<Scrim> obtenerTodos();

    /**
     * Busca scrims con filtros específicos.
     * Los parámetros null se ignoran en la búsqueda.
     * 
     * @param filtros objeto con los criterios de búsqueda
     * @return lista de scrims que coinciden con los filtros
     */
    List<Scrim> buscarConFiltros(FiltrosScrim filtros);

    /**
     * Actualiza un scrim existente en el repositorio.
     * 
     * @param scrim el scrim con los datos actualizados
     * @return true si se actualizó exitosamente
     */
    boolean actualizar(Scrim scrim);

    /**
     * Elimina un scrim del repositorio.
     * 
     * @param id el ID del scrim a eliminar
     * @return true si se eliminó exitosamente
     */
    boolean eliminar(String id);

    /**
     * Obtiene la cantidad total de scrims.
     * 
     * @return número de scrims en el repositorio
     */
    int contar();
}
