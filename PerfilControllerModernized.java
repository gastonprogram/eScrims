package presentacion.controller;

import java.util.List;

import aplicacion.services.UsuarioService;
import dominio.juegos.Juego;
import dominio.modelo.Usuario;
import presentacion.view.PerfilView;

/**
 * EJEMPLO: Controller modernizado para la gestión de perfiles de usuario.
 * 
 * Funcionalidades actualizadas:
 * - Gestión de rangos por juego específico
 * - Roles preferidos organizados por juego
 * - Edición de latencia promedio
 * - Selección enumerada para mejor UX
 * 
 * Este es un ejemplo de cómo adaptar el controller existente
 * para trabajar con la nueva estructura del Usuario y PerfilView.
 * 
 * @author eScrims Team
 */
public class PerfilControllerModernized {

    private final UsuarioService usuarioService;
    private final PerfilView view;
    private final String usuarioActualId;

    public PerfilControllerModernized(UsuarioService usuarioService,
            PerfilView view,
            String usuarioActualId) {
        this.usuarioService = usuarioService;
        this.view = view;
        this.usuarioActualId = usuarioActualId;
    }

    /**
     * Gestiona el menú principal de perfil.
     */
    public void gestionarPerfil() {
        boolean salir = false;

        while (!salir) {
            String opcion = view.mostrarMenuPerfil();

            switch (opcion) {
                case "1":
                    verPerfilCompleto();
                    break;
                case "2":
                    editarRangoPorJuego();
                    break;
                case "3":
                    gestionarRolesPorJuego();
                    break;
                case "4":
                    cambiarRegion();
                    break;
                case "5":
                    ajustarLatencia();
                    break;
                case "6":
                    verPerfilOtroUsuario();
                    break;
                case "0":
                    salir = true;
                    break;
                default:
                    view.mostrarError("Opción no válida");
            }
        }
    }

    /**
     * Muestra el perfil completo del usuario actual.
     */
    private void verPerfilCompleto() {
        try {
            Usuario usuario = usuarioService.buscarPorId(usuarioActualId);
            view.mostrarPerfilCompleto(usuario);
            view.pausaParaContinuar();

        } catch (IllegalArgumentException e) {
            view.mostrarError("Error: " + e.getMessage());
        } catch (Exception e) {
            view.mostrarError("Error inesperado: " + e.getMessage());
        }
    }

    /**
     * Permite editar el rango para un juego específico.
     */
    private void editarRangoPorJuego() {
        try {
            Usuario usuario = usuarioService.buscarPorId(usuarioActualId);

            // 1. Seleccionar juego
            Juego juegoSeleccionado = view.solicitarJuegoParaRango();
            if (juegoSeleccionado == null) {
                view.mostrarInfo("Operación cancelada");
                return;
            }

            String nombreJuego = juegoSeleccionado.getNombre();
            Integer rangoActual = usuario.getRangoPorJuego().get(nombreJuego);

            // 2. Solicitar nuevo rango
            int nuevoRango = view.solicitarNuevoRango(nombreJuego, rangoActual);
            if (nuevoRango == -1) {
                view.mostrarInfo("Operación cancelada");
                return;
            }

            // 3. Confirmar cambios
            String mensaje = String.format("Cambiar rango en %s de %s a %d",
                    nombreJuego,
                    rangoActual != null ? rangoActual.toString() : "No configurado",
                    nuevoRango);

            if (!view.confirmarCambios(mensaje)) {
                view.mostrarInfo("Cambios cancelados");
                return;
            }

            // 4. Actualizar en el modelo
            usuario.setRangoParaJuego(nombreJuego, nuevoRango);
            usuarioService.actualizar(usuario);

            view.mostrarExito("Rango actualizado exitosamente");

        } catch (IllegalArgumentException e) {
            view.mostrarError("Error: " + e.getMessage());
        } catch (Exception e) {
            view.mostrarError("Error inesperado: " + e.getMessage());
        }
    }

    /**
     * Permite gestionar roles preferidos para un juego específico.
     */
    private void gestionarRolesPorJuego() {
        try {
            Usuario usuario = usuarioService.buscarPorId(usuarioActualId);

            // 1. Seleccionar juego
            Juego juegoSeleccionado = view.solicitarJuegoParaRoles();
            if (juegoSeleccionado == null) {
                view.mostrarInfo("Operación cancelada");
                return;
            }

            String nombreJuego = juegoSeleccionado.getNombre();
            List<String> rolesActuales = usuario.getRolesPreferidosParaJuego(nombreJuego);

            // 2. Solicitar nuevos roles
            List<String> nuevosRoles = view.solicitarNuevosRoles(juegoSeleccionado, rolesActuales);

            // 3. Verificar si hubo cambios
            if (nuevosRoles.equals(rolesActuales)) {
                view.mostrarInfo("No se realizaron cambios");
                return;
            }

            // 4. Confirmar cambios
            String mensaje = String.format("Cambiar roles en %s de [%s] a [%s]",
                    nombreJuego,
                    String.join(", ", rolesActuales),
                    String.join(", ", nuevosRoles));

            if (!view.confirmarCambios(mensaje)) {
                view.mostrarInfo("Cambios cancelados");
                return;
            }

            // 5. Actualizar en el modelo
            // Primero limpiar roles actuales para este juego
            usuario.getRolesPorJuego().put(nombreJuego, nuevosRoles);
            usuarioService.actualizar(usuario);

            view.mostrarExito("Roles actualizados exitosamente");

        } catch (IllegalArgumentException e) {
            view.mostrarError("Error: " + e.getMessage());
        } catch (Exception e) {
            view.mostrarError("Error inesperado: " + e.getMessage());
        }
    }

    /**
     * Permite cambiar la región del usuario.
     */
    private void cambiarRegion() {
        try {
            Usuario usuario = usuarioService.buscarPorId(usuarioActualId);

            String regionActual = usuario.getRegion();
            String nuevaRegion = view.solicitarNuevaRegion(regionActual);

            if (nuevaRegion == null) {
                view.mostrarInfo("Operación cancelada");
                return;
            }

            if (nuevaRegion.equals(regionActual)) {
                view.mostrarInfo("No se realizaron cambios");
                return;
            }

            // Confirmar cambios
            String mensaje = String.format("Cambiar región de '%s' a '%s'",
                    regionActual.isEmpty() ? "No configurada" : regionActual,
                    nuevaRegion);

            if (!view.confirmarCambios(mensaje)) {
                view.mostrarInfo("Cambios cancelados");
                return;
            }

            // Actualizar
            usuario.setRegion(nuevaRegion);
            usuarioService.actualizar(usuario);

            view.mostrarExito("Región actualizada exitosamente");

        } catch (IllegalArgumentException e) {
            view.mostrarError("Error: " + e.getMessage());
        } catch (Exception e) {
            view.mostrarError("Error inesperado: " + e.getMessage());
        }
    }

    /**
     * Permite ajustar la latencia promedio del usuario.
     */
    private void ajustarLatencia() {
        try {
            Usuario usuario = usuarioService.buscarPorId(usuarioActualId);

            int latenciaActual = usuario.getLatenciaPromedio();
            int nuevaLatencia = view.solicitarNuevaLatencia(latenciaActual);

            if (nuevaLatencia == -1) {
                view.mostrarInfo("Operación cancelada");
                return;
            }

            if (nuevaLatencia == latenciaActual) {
                view.mostrarInfo("No se realizaron cambios");
                return;
            }

            // Confirmar cambios
            String mensaje = String.format("Cambiar latencia de %d ms a %d ms",
                    latenciaActual, nuevaLatencia);

            if (!view.confirmarCambios(mensaje)) {
                view.mostrarInfo("Cambios cancelados");
                return;
            }

            // Actualizar
            usuario.setLatenciaPromedio(nuevaLatencia);
            usuarioService.actualizar(usuario);

            view.mostrarExito("Latencia actualizada exitosamente");

        } catch (IllegalArgumentException e) {
            view.mostrarError("Error: " + e.getMessage());
        } catch (Exception e) {
            view.mostrarError("Error inesperado: " + e.getMessage());
        }
    }

    /**
     * Permite ver el perfil de otro usuario.
     */
    private void verPerfilOtroUsuario() {
        try {
            String username = view.solicitarUsernameUsuario();

            if (username.isEmpty()) {
                view.mostrarInfo("Operación cancelada");
                return;
            }

            Usuario usuario = usuarioService.buscarPorUsername(username);
            if (usuario == null) {
                view.mostrarError("Usuario no encontrado: " + username);
                return;
            }

            view.mostrarPerfilOtroUsuario(usuario);
            view.pausaParaContinuar();

        } catch (IllegalArgumentException e) {
            view.mostrarError("Error: " + e.getMessage());
        } catch (Exception e) {
            view.mostrarError("Error inesperado: " + e.getMessage());
        }
    }
}