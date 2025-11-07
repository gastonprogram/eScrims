package aplicacion.services;

import dominio.juegos.Juego;
import dominio.juegos.JuegosRegistry;
import dominio.modelo.Usuario;
import infraestructura.persistencia.repository.RepositorioUsuario;

/**
 * Servicio de aplicación para la gestión de autenticación.
 * 
 * Responsabilidades:
 * - Autenticar usuarios (login)
 * - Registrar nuevos usuarios
 * - Gestionar sesiones (login/logout)
 * - Validar credenciales
 * 
 * @author eScrims Team
 */
public class AuthService {

    private final RepositorioUsuario repositorioUsuario;
    private Usuario usuarioLogueado;

    public AuthService(RepositorioUsuario repositorioUsuario) {
        this.repositorioUsuario = repositorioUsuario;
        this.usuarioLogueado = null;
    }

    /**
     * Autentica un usuario con sus credenciales.
     * 
     * @param username El nombre de usuario
     * @param password La contraseña
     * @return El usuario autenticado
     * @throws IllegalArgumentException Si las credenciales son incorrectas
     */
    public Usuario autenticar(String username, String password) {
        // Validación de entrada
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("El username es requerido");
        }

        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("La contraseña es requerida");
        }

        // Buscar usuario en el repositorio
        Usuario usuario = repositorioUsuario.buscarPorUsername(username.trim());
        if (usuario == null) {
            throw new IllegalArgumentException("Credenciales incorrectas");
        }

        // Verificar contraseña hasheada
        if (!usuario.verifyPassword(password)) {
            throw new IllegalArgumentException("Credenciales incorrectas");
        }

        // Usuario autenticado exitosamente
        usuarioLogueado = usuario;
        return usuario;
    }

    /**
     * Registra un nuevo usuario en el sistema.
     * 
     * @param username       Nombre de usuario
     * @param email          Email del usuario
     * @param password       Contraseña
     * @param juegoPrincipal Juego principal (opcional)
     * @param rango          Rango del usuario (opcional)
     * @return El usuario creado
     * @throws IllegalArgumentException Si los datos son inválidos o el usuario ya
     *                                  existe
     */
    public Usuario registrarUsuario(String username, String email, String password,
            String juegoPrincipal, int rango) {

        // Validar datos de registro (con validaciones manuales)
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("El username es requerido");
        }

        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("El email es requerido");
        }

        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("La contraseña es requerida");
        }

        // Validar que no exista el username
        if (repositorioUsuario.existeUsername(username.trim())) {
            throw new IllegalArgumentException("El username ya está en uso");
        }

        // Validar que no exista el email
        if (repositorioUsuario.existeEmail(email.trim())) {
            throw new IllegalArgumentException("El email ya está registrado");
        }

        // Crear el nuevo usuario
        Usuario nuevoUsuario = new Usuario(username.trim(), email.trim(), password);

        // Configurar juego principal y rango si se proporcionaron
        if (juegoPrincipal != null && !juegoPrincipal.trim().isEmpty()) {
            // Obtener el objeto Juego usando JuegosRegistry
            JuegosRegistry juegosRegistry = JuegosRegistry.getInstance();
            Juego juegoObj = juegosRegistry.buscarPorNombre(juegoPrincipal.trim());

            if (juegoObj != null) {
                // Establecer el juego principal
                nuevoUsuario.setJuegoPrincipal(juegoObj);

                // Establecer el rango para ese juego si es válido
                if (rango > 0 && rango <= 100) {
                    nuevoUsuario.setRangoParaJuego(juegoPrincipal.trim(), rango);
                }
            }
        }

        // Guardar en el repositorio
        repositorioUsuario.guardar(nuevoUsuario);

        return nuevoUsuario;
    }

    /**
     * Cierra la sesión del usuario actual.
     */
    public void logout() {
        usuarioLogueado = null;
    }

    /**
     * Verifica si hay un usuario logueado.
     * 
     * @return true si hay un usuario logueado, false en caso contrario
     */
    public boolean estaLogueado() {
        return usuarioLogueado != null;
    }

    /**
     * Obtiene el usuario actualmente logueado.
     * 
     * @return El usuario logueado
     * @throws IllegalStateException Si no hay usuario logueado
     */
    public Usuario getUsuarioLogueado() {
        if (!estaLogueado()) {
            throw new IllegalStateException("No hay usuario logueado en el sistema");
        }
        return usuarioLogueado;
    }

    /**
     * Verifica que haya acceso (usuario logueado).
     * 
     * @throws IllegalStateException Si no hay usuario logueado
     */
    public void verificarAcceso() {
        if (!estaLogueado()) {
            throw new IllegalStateException("Acceso denegado. Debe iniciar sesión para continuar");
        }
    }

    /**
     * Obtiene información de la sesión actual.
     * 
     * @return String con información de la sesión
     */
    public String getInfoSesion() {
        if (!estaLogueado()) {
            return "No hay sesión activa";
        }

        return "Sesión activa - Usuario: " + usuarioLogueado.getUsername();
    }
}
