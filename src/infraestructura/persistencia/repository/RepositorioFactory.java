package infraestructura.persistencia.repository;

import infraestructura.persistencia.implementacion.RepositorioScrimJson;
import infraestructura.persistencia.implementacion.RepositorioUsuarioJSON;
import infraestructura.persistencia.implementacion.RepositorioEstadisticasJSON;

/**
 * Factory para crear instancias de los repositorios de la aplicación.
 * Centraliza la creación de instancias para facilitar el cambio de
 * implementación.
 */
public class RepositorioFactory {

    private static RepositorioUsuario repositorioUsuario;
    private static RepositorioScrim repositorioScrim;
    private static RepositorioEstadisticas repositorioEstadisticas;

    /**
     * Obtiene una instancia del repositorio de usuarios.
     * 
     * @return Una implementación de RepositorioUsuario
     */
    public static synchronized RepositorioUsuario getRepositorioUsuario() {
        if (repositorioUsuario == null) {
            repositorioUsuario = new RepositorioUsuarioJSON();
        }
        return repositorioUsuario;
    }

    /**
     * Obtiene una instancia del repositorio de scrims.
     * 
     * @return Una implementación de RepositorioScrim
     */
    public static synchronized RepositorioScrim getRepositorioScrim() {
        if (repositorioScrim == null) {
            repositorioScrim = RepositorioScrimJson.getInstance();
        }
        return repositorioScrim;
    }

    /**
     * Obtiene una instancia del repositorio de estadísticas.
     * 
     * @return Una implementación de RepositorioEstadisticas
     */
    public static synchronized RepositorioEstadisticas getRepositorioEstadisticas() {
        if (repositorioEstadisticas == null) {
            repositorioEstadisticas = new RepositorioEstadisticasJSON();
        }
        return repositorioEstadisticas;
    }

    /**
     * Permite inyectar una implementación personalizada de RepositorioUsuario.
     * Útil para pruebas unitarias.
     * 
     * @param repositorio La implementación de RepositorioUsuario a utilizar
     */
    public static synchronized void setRepositorioUsuario(RepositorioUsuario repositorio) {
        repositorioUsuario = repositorio;
    }

    /**
     * Permite inyectar una implementación personalizada de RepositorioScrim.
     * Útil para pruebas unitarias.
     * 
     * @param repositorio La implementación de RepositorioScrim a utilizar
     */
    public static synchronized void setRepositorioScrim(RepositorioScrim repositorio) {
        repositorioScrim = repositorio;
    }

    /**
     * Permite inyectar una implementación personalizada de RepositorioEstadisticas.
     * Útil para pruebas unitarias.
     * 
     * @param repositorio La implementación de RepositorioEstadisticas a utilizar
     */
    public static synchronized void setRepositorioEstadisticas(RepositorioEstadisticas repositorio) {
        repositorioEstadisticas = repositorio;
    }
}
