package model.Persistencia;

import model.Persistencia.impl.RepositorioUsuarioJSON;

/**
 * Factory para crear instancias de los repositorios de la aplicación.
 * Centraliza la creación de instancias para facilitar el cambio de implementación.
 */
public class RepositorioFactory {
    
    private static RepositorioUsuario repositorioUsuario;
    
    /**
     * Obtiene una instancia del repositorio de usuarios.
     * @return Una implementación de RepositorioUsuario
     */
    public static synchronized RepositorioUsuario getRepositorioUsuario() {
        if (repositorioUsuario == null) {
            repositorioUsuario = new RepositorioUsuarioJSON();
        }
        return repositorioUsuario;
    }
    
    /**
     * Permite inyectar una implementación personalizada de RepositorioUsuario.
     * Útil para pruebas unitarias.
     * @param repositorio La implementación de RepositorioUsuario a utilizar
     */
    public static synchronized void setRepositorioUsuario(RepositorioUsuario repositorio) {
        repositorioUsuario = repositorio;
    }
}
