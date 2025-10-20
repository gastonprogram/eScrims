package controller;

import model.Usuario;
import model.Persistencia.RepositorioUsuario;
import model.utils.PerfilValidator;

import java.util.List;

/**
 * Controlador para la gestión de perfiles de usuario.
 */
public class PerfilController {

    private final RepositorioUsuario repositorioUsuario;

    /**
     * Crea un nuevo controlador de perfil con el repositorio especificado.
     * @param repositorioUsuario El repositorio de usuarios a utilizar
     */
    public PerfilController(RepositorioUsuario repositorioUsuario) {
        this.repositorioUsuario = repositorioUsuario;
    }

    /**
     * Edita y persiste el perfil del usuario.
     */
    public boolean editarPerfil(
            Usuario usuario,
            String nuevoRango,
            List<String> nuevosRolesPreferidos,
            String nuevoJuegoPrincipal,
            String nuevaRegion,
            String nuevaDisponibilidad) {

        try {
            // 1. Validaciones
            // Solo validamos los campos que tienen validación específica (región y horario)
            PerfilValidator.validarRegion(nuevaRegion);
            PerfilValidator.validarDisponibilidad(nuevaDisponibilidad);
            PerfilValidator.validarRoles(nuevosRolesPreferidos);

            // Note: Aquí podría añadir validación para rango y juego principal si las necesitara

            // 2. Aplicar cambios al objeto Usuario
            usuario.setRangoPrincipal(nuevoRango);
            usuario.setRolesPreferidos(nuevosRolesPreferidos);
            usuario.setJuegoPrincipal(nuevoJuegoPrincipal);
            usuario.setRegion(nuevaRegion);
            usuario.setDisponibilidad(nuevaDisponibilidad);

            // 3. Persistencia: Guardar los cambios en el repositorio
            repositorioUsuario.guardar(usuario);

            return true;
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Error de validación al editar perfil: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Error inesperado al editar perfil: " + e.getMessage());
        }
    }
}