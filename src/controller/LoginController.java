package controller;

import model.Usuario;
import java.util.Map;

public class LoginController {
    private Map<String, Usuario> usuarios;
    private Usuario usuarioLogueado;

    public LoginController(Map<String, Usuario> usuarios) {
        this.usuarios = usuarios;
        this.usuarioLogueado = null;
    }

    // Método principal para autenticar usuario
    public boolean autenticar(String username, String password) {
        try {
            // Validación de entrada
            if (username == null || username.trim().isEmpty()) {
                throw new IllegalArgumentException("El username es requerido");
            }

            if (password == null || password.isEmpty()) {
                throw new IllegalArgumentException("La contraseña es requerida");
            }

            // Buscar usuario en el sistema
            Usuario usuario = usuarios.get(username.trim());
            if (usuario == null) {
                throw new IllegalArgumentException("Credenciales incorrectas");
            }

            // Verificar contraseña hasheada
            if (!usuario.verifyPassword(password)) {
                throw new IllegalArgumentException("Credenciales incorrectas");
            }

            // Usuario autenticado exitosamente
            usuarioLogueado = usuario;
            return true;

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // Cerrar sesión del usuario actual
    public void logout() {
        usuarioLogueado = null;
    }

    // Verificar si el usuario está logueado
    public boolean estaLogueado() {
        return usuarioLogueado != null;
    }

    // Obtener el usuario actualmente logueado
    public Usuario getUsuarioLogueado() {
        if (!estaLogueado()) {
            throw new IllegalStateException("No hay usuario logueado en el sistema");
        }
        return usuarioLogueado;
    }

    // Verificar acceso al sistema
    public void verificarAcceso() {
        if (!estaLogueado()) {
            throw new IllegalStateException("Acceso denegado. Debe iniciar sesión para continuar");
        }
    }

    // Obtener información de sesión actual
    public String getInfoSesion() {
        if (!estaLogueado()) {
            return "No hay sesión activa";
        }

        return "Sesión activa - Usuario: " + usuarioLogueado.getUsername();
    }
}