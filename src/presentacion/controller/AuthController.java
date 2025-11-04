package presentacion.controller;

import aplicacion.services.AuthService;
import dominio.modelo.Usuario;
import presentacion.view.LoginView;
import presentacion.view.RegisterView;

/**
 * Controller de presentación para la gestión de autenticación.
 * 
 * Responsabilidades:
 * - Coordinar flujo de login y registro
 * - Manejar navegación post-autenticación
 * - Gestionar sesiones de usuario
 * - Formatear mensajes de bienvenida y error
 * 
 * @author eScrims Team
 */
public class AuthController {

    private final AuthService authService;
    private final LoginView loginView;
    private final RegisterView registerView;

    public AuthController(AuthService authService,
            LoginView loginView,
            RegisterView registerView) {
        this.authService = authService;
        this.loginView = loginView;
        this.registerView = registerView;
    }

    /**
     * Inicia el flujo de login.
     * 
     * @return true si el login fue exitoso, false en caso contrario
     */
    public boolean login() {
        try {
            // 1. Solicitar credenciales
            String username = loginView.solicitarUsername();
            String password = loginView.solicitarPassword();

            // 2. Llamar al servicio de autenticación
            Usuario usuario = authService.autenticar(username, password);

            // 3. Mostrar mensaje de bienvenida
            loginView.mostrarExito(
                    "¡Bienvenido " + usuario.getUsername() + "!\n" +
                            "Has iniciado sesión correctamente");

            return true;

        } catch (IllegalArgumentException e) {
            loginView.mostrarError("Error de login: " + e.getMessage());
            return false;
        } catch (Exception e) {
            loginView.mostrarError("Error inesperado: " + e.getMessage());
            return false;
        }
    }

    /**
     * Inicia el flujo de registro.
     * 
     * @return true si el registro fue exitoso, false en caso contrario
     */
    public boolean registrar() {
        try {
            // 1. Solicitar datos de registro
            String username = registerView.solicitarUsername();
            String email = registerView.solicitarEmail();
            String password = registerView.solicitarPassword();
            String confirmPassword = registerView.confirmarPassword();

            // Validar que las contraseñas coincidan
            if (!password.equals(confirmPassword)) {
                registerView.mostrarError("Las contraseñas no coinciden");
                return false;
            }

            String juegoPrincipal = registerView.solicitarJuegoPrincipal();
            int rango = registerView.solicitarRango();

            // 2. Llamar al servicio de registro
            Usuario nuevoUsuario = authService.registrarUsuario(
                    username, email, password, juegoPrincipal, rango);

            // 3. Mostrar mensaje de éxito
            registerView.mostrarExito(
                    "¡Registro exitoso!\n" +
                            "Usuario: " + nuevoUsuario.getUsername() + "\n" +
                            "Ahora puedes iniciar sesión");

            return true;

        } catch (IllegalArgumentException e) {
            registerView.mostrarError("Error de registro: " + e.getMessage());
            return false;
        } catch (Exception e) {
            registerView.mostrarError("Error inesperado: " + e.getMessage());
            return false;
        }
    }

    /**
     * Cierra la sesión del usuario actual.
     */
    public void logout() {
        try {
            if (!authService.estaLogueado()) {
                loginView.mostrarInfo("No hay sesión activa");
                return;
            }

            String username = authService.getUsuarioLogueado().getUsername();
            authService.logout();

            loginView.mostrarExito("Sesión cerrada. ¡Hasta pronto " + username + "!");

        } catch (Exception e) {
            loginView.mostrarError("Error al cerrar sesión: " + e.getMessage());
        }
    }

    /**
     * Verifica si hay un usuario logueado.
     * 
     * @return true si hay sesión activa, false en caso contrario
     */
    public boolean estaLogueado() {
        return authService.estaLogueado();
    }

    /**
     * Obtiene el usuario logueado actual.
     * 
     * @return El usuario logueado o null si no hay sesión
     */
    public Usuario getUsuarioLogueado() {
        try {
            return authService.getUsuarioLogueado();
        } catch (IllegalStateException e) {
            return null;
        }
    }

    /**
     * Muestra información de la sesión actual.
     */
    public void mostrarInfoSesion() {
        String info = authService.getInfoSesion();
        loginView.mostrarInfo(info);
    }

    /**
     * Verifica acceso (lanza excepción si no está logueado).
     * 
     * @throws IllegalStateException si no hay usuario logueado
     */
    public void verificarAcceso() {
        try {
            authService.verificarAcceso();
        } catch (IllegalStateException e) {
            loginView.mostrarError(e.getMessage());
            throw e;
        }
    }
}
