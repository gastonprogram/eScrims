package model.utils;

import model.Usuario;

public class AuthUtils {

    // Verifica que haya sesión iniciada
    public static void verificarSesionActiva(Usuario usuario) {
        if (usuario == null) {
            throw new SecurityException("Debes iniciar sesión para realizar esta acción.");
        }
    }

    // Verifica que el usuario sea ADMIN
    public static void verificarAdmin(Usuario usuario) {
        verificarSesionActiva(usuario);
        if (usuario.getRol() != Rol.ADMIN) {
            throw new SecurityException("Solo los administradores pueden realizar esta acción.");
        }
    }

    // Verifica que el usuario sea MOD o ADMIN
    public static void verificarModerador(Usuario usuario) {
        verificarSesionActiva(usuario);
        if (usuario.getRol() != Rol.MOD && usuario.getRol() != Rol.ADMIN) {
            throw new SecurityException("Solo moderadores o administradores pueden realizar esta acción.");
        }
    }

    // Verifica que sea usuario normal o superior (MOD o ADMIN)
    public static void verificarUsuario(Usuario usuario) {
        verificarSesionActiva(usuario);
        if (usuario.getRol() == null) {
            throw new SecurityException("Usuario sin rol asignado.");
        }
    }

    // Verifica si un usuario tiene un rol específico
    public static boolean tieneRol(Usuario usuario, Rol rol) {
        if (usuario == null || usuario.getRol() == null)
            return false;
        return usuario.getRol() == rol;
    }

}
