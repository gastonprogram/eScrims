package presentacion.controller;

import java.util.List;

import aplicacion.services.UsuarioService;
import dominio.juegos.Juego;
import dominio.modelo.Usuario;
import presentacion.view.PerfilView;

/**
 * Controller de presentación para la gestión de perfiles de usuario.
 * 
 * Responsabilidades:
 * - Coordinar flujo de edición de perfil
 * - Mostrar información del perfil
 * - Validar y enviar datos al servicio
 * - Formatear información para el usuario
 * 
 * @author eScrims Team
 */
public class PerfilController {

    private final UsuarioService usuarioService;
    private final PerfilView view;
    private final String usuarioActualId;

    public PerfilController(UsuarioService usuarioService,
            PerfilView view,
            String usuarioActualId) {
        this.usuarioService = usuarioService;
        this.view = view;
        this.usuarioActualId = usuarioActualId;
    }

    /**
     * Muestra el perfil del usuario actual.
     */
    public void verPerfil() {
        try {
            Usuario usuario = usuarioService.buscarPorId(usuarioActualId);
            view.mostrarPerfil(usuario);

        } catch (IllegalArgumentException e) {
            view.mostrarError("Error: " + e.getMessage());
        } catch (Exception e) {
            view.mostrarError("Error inesperado: " + e.getMessage());
        }
    }

    /**
     * Inicia el flujo de edición de perfil.
     */
    public void editarPerfil() {
        try {
            // 1. Obtener usuario actual
            Usuario usuario = usuarioService.buscarPorId(usuarioActualId);

            // 2. Mostrar perfil actual
            view.mostrarPerfilActual(usuario);

            // 3. Solicitar nuevos datos
            String nuevoRango = view.solicitarRango();
            Juego nuevoJuegoPrincipal = view.solicitarJuegoPrincipal();
            List<String> nuevosRolesPreferidos = view.solicitarRolesPreferidos(nuevoJuegoPrincipal);
            String nuevaRegion = view.solicitarRegion();
            String nuevaDisponibilidad = view.solicitarDisponibilidad();

            // 4. Confirmar cambios
            boolean confirmar = view.confirmarCambios(
                    nuevoRango,
                    nuevoJuegoPrincipal,
                    nuevosRolesPreferidos,
                    nuevaRegion,
                    nuevaDisponibilidad);

            if (!confirmar) {
                view.mostrarInfo("Edición cancelada");
                return;
            }

            // 5. Llamar al servicio
            Usuario usuarioActualizado = usuarioService.editarPerfil(
                    usuarioActualId,
                    nuevoRango,
                    nuevosRolesPreferidos,
                    nuevoJuegoPrincipal,
                    nuevaRegion,
                    nuevaDisponibilidad);

            // 6. Mostrar resultado
            view.mostrarExito("Perfil actualizado exitosamente");
            view.mostrarPerfil(usuarioActualizado);

        } catch (IllegalArgumentException e) {
            view.mostrarError("Error de validación: " + e.getMessage());
        } catch (Exception e) {
            view.mostrarError("Error inesperado: " + e.getMessage());
        }
    }

    /**
     * Ver perfil de otro usuario.
     */
    public void verPerfilDeUsuario() {
        try {
            String userId = view.solicitarIdUsuario();
            Usuario usuario = usuarioService.buscarPorId(userId);

            view.mostrarPerfilOtroUsuario(usuario);

        } catch (IllegalArgumentException e) {
            view.mostrarError("Usuario no encontrado");
        } catch (Exception e) {
            view.mostrarError("Error inesperado: " + e.getMessage());
        }
    }

    /**
     * Gestiona el menú de perfil.
     */
    public void gestionarPerfil() {
        boolean salir = false;

        while (!salir) {
            String opcion = view.mostrarMenuPerfil();

            switch (opcion) {
                case "1":
                    verPerfil();
                    break;
                case "2":
                    editarPerfil();
                    break;
                case "3":
                    verPerfilDeUsuario();
                    break;
                case "0":
                    salir = true;
                    break;
                default:
                    view.mostrarError("Opción no válida");
            }
        }
    }
}
