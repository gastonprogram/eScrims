package aplicacion.services;

import java.util.List;

import compartido.validators.PerfilValidator;
import dominio.juegos.Juego;
import dominio.modelo.Usuario;
import infraestructura.persistencia.repository.RepositorioUsuario;

/**
 * Servicio de aplicación para la gestión de usuarios.
 * 
 * Responsabilidades:
 * - Editar perfiles de usuario
 * - Buscar usuarios
 * - Validar datos de perfil
 * 
 * @author eScrims Team
 */
public class UsuarioService {

    private final RepositorioUsuario repositorioUsuario;

    public UsuarioService(RepositorioUsuario repositorioUsuario) {
        this.repositorioUsuario = repositorioUsuario;
    }

    /**
     * Edita el perfil de un usuario.
     * 
     * @param usuarioId             ID del usuario a editar
     * @param nuevoRango            El nuevo rango
     * @param nuevosRolesPreferidos Lista de roles preferidos para el juego
     *                              principal
     * @param nuevoJuegoPrincipal   Objeto Juego
     * @param nuevaRegion           Nueva región
     * @param nuevaDisponibilidad   Nueva disponibilidad horaria
     * @return El usuario actualizado
     * @throws IllegalArgumentException Si el usuario no existe o los datos son
     *                                  inválidos
     */
    public Usuario editarPerfil(
            String usuarioId,
            String nuevoRango,
            List<String> nuevosRolesPreferidos,
            Juego nuevoJuegoPrincipal,
            String nuevaRegion,
            String nuevaDisponibilidad) {

        // Buscar el usuario
        Usuario usuario = repositorioUsuario.buscarPorId(usuarioId);
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }

        // Validaciones
        PerfilValidator.validarRegion(nuevaRegion);
        PerfilValidator.validarDisponibilidad(nuevaDisponibilidad);
        PerfilValidator.validarRoles(nuevosRolesPreferidos);

        // Aplicar cambios al objeto Usuario
        usuario.setRangoPrincipal(nuevoRango);

        // Solo establecer roles si hay un juego principal configurado
        if (nuevoJuegoPrincipal != null) {
            usuario.setJuegoPrincipal(nuevoJuegoPrincipal);
            usuario.setRolesPreferidos(nuevosRolesPreferidos);
        }

        usuario.setRegion(nuevaRegion);
        usuario.setDisponibilidad(nuevaDisponibilidad);

        // Persistir cambios
        repositorioUsuario.guardar(usuario);

        return usuario;
    }

    /**
     * Busca un usuario por su ID.
     * 
     * @param usuarioId ID del usuario
     * @return El usuario encontrado
     * @throws IllegalArgumentException Si el usuario no existe
     */
    public Usuario buscarPorId(String usuarioId) {
        Usuario usuario = repositorioUsuario.buscarPorId(usuarioId);
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }
        return usuario;
    }

    /**
     * Obtiene todos los usuarios del sistema.
     * 
     * @return Lista de todos los usuarios
     */
    public List<Usuario> obtenerTodos() {
        return repositorioUsuario.listarTodos();
    }
}
