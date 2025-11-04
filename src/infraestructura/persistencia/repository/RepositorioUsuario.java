package infraestructura.persistencia.repository;

import java.util.List;

import dominio.modelo.Usuario;

/**
 * Interfaz que define las operaciones CRUD para la gestión de usuarios
 */
public interface RepositorioUsuario {

    /**
     * Guarda un usuario en el repositorio. Si ya existe un usuario con el mismo
     * email,
     * actualiza sus datos.
     * 
     * @param usuario El usuario a guardar o actualizar
     */
    void guardar(Usuario usuario);

    /**
     * Busca un usuario por su ID.
     * 
     * @param id El ID del usuario a buscar
     * @return El usuario encontrado o null si no existe
     */
    Usuario buscarPorId(String id);

    /**
     * Busca un usuario por su email.
     * 
     * @param email El email del usuario a buscar
     * @return El usuario encontrado o null si no existe
     */
    Usuario buscarPorEmail(String email);

    /**
     * Busca un usuario por su nombre de usuario.
     * 
     * @param username El nombre de usuario a buscar
     * @return El usuario encontrado o null si no existe
     */
    Usuario buscarPorUsername(String username);

    /**
     * Obtiene todos los usuarios registrados.
     * 
     * @return Una lista con todos los usuarios
     */
    List<Usuario> listarTodos();

    /**
     * Elimina un usuario por su email.
     * 
     * @param email El email del usuario a eliminar
     * @return true si se eliminó correctamente, false si no se encontró el usuario
     */
    boolean eliminar(String email);

    /**
     * Verifica si existe un usuario con el email proporcionado.
     * 
     * @param email El email a verificar
     * @return true si existe, false en caso contrario
     */
    boolean existeEmail(String email);

    /**
     * Verifica si existe un usuario con el nombre de usuario proporcionado.
     * 
     * @param username El nombre de usuario a verificar
     * @return true si existe, false en caso contrario
     */
    boolean existeUsername(String username);
}