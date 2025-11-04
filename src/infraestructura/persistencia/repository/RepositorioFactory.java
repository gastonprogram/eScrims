package infraestructura.persistencia.repository;

import infraestructura.persistencia.implementacion.RepositorioScrimMemoria;
import infraestructura.persistencia.implementacion.RepositorioUsuarioJSON;

/**
 * Factory para crear instancias de los repositorios de la aplicación.
 * Centraliza la creación de instancias para facilitar el cambio de
 * implementación.
 */
public class RepositorioFactory {

    private static RepositorioUsuario repositorioUsuario;
    private static RepositorioScrim repositorioScrim;

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
            repositorioScrim = RepositorioScrimMemoria.getInstance();
        }
        return repositorioScrim;
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
}
