package presentacion.controller;

import presentacion.view.MenuView;

/**
 * Controller principal que coordina la navegación del menú.
 * 
 * Responsabilidades:
 * - Mostrar menú principal
 * - Delegar acciones a controllers específicos
 * - Manejar flujo de navegación entre funcionalidades
 * - Controlar el ciclo de vida de la aplicación
 * 
 * @author eScrims Team
 */
public class MenuController {

    private final MenuView view;
    private final AuthController authController;
    private final ScrimController scrimController;
    private final PostulacionController postulacionController;
    private final ConfirmacionController confirmacionController;
    private final PerfilController perfilController;

    public MenuController(MenuView view,
            AuthController authController,
            ScrimController scrimController,
            PostulacionController postulacionController,
            ConfirmacionController confirmacionController,
            PerfilController perfilController) {
        this.view = view;
        this.authController = authController;
        this.scrimController = scrimController;
        this.postulacionController = postulacionController;
        this.confirmacionController = confirmacionController;
        this.perfilController = perfilController;
    }

    /**
     * Muestra el menú inicial (Login/Registro).
     */
    public void mostrarMenuInicial() {
        boolean salir = false;

        while (!salir) {
            String opcion = view.mostrarMenuInicial();

            switch (opcion) {
                case "1":
                    if (authController.login()) {
                        // Si login exitoso, ir al menú principal
                        mostrarMenuPrincipal();
                    }
                    break;
                case "2":
                    authController.registrar();
                    break;
                case "0":
                    view.mostrarInfo("¡Hasta pronto!");
                    salir = true;
                    break;
                default:
                    view.mostrarError("Opción no válida");
            }
        }
    }

    /**
     * Muestra el menú principal de la aplicación (requiere login).
     */
    public void mostrarMenuPrincipal() {
        try {
            // Verificar que haya usuario logueado
            authController.verificarAcceso();

            boolean salir = false;

            while (!salir) {
                String usuario = authController.getUsuarioLogueado().getUsername();
                String opcion = view.mostrarMenuPrincipal(usuario);

                switch (opcion) {
                    case "1":
                        menuScrims();
                        break;
                    case "2":
                        menuPostulaciones();
                        break;
                    case "3":
                        menuConfirmaciones();
                        break;
                    case "4":
                        perfilController.gestionarPerfil();
                        break;
                    case "5":
                        authController.mostrarInfoSesion();
                        break;
                    case "0":
                        authController.logout();
                        salir = true;
                        break;
                    default:
                        view.mostrarError("Opción no válida");
                }
            }

        } catch (IllegalStateException e) {
            view.mostrarError("Debes iniciar sesión primero");
        }
    }

    /**
     * Menú de gestión de scrims.
     */
    private void menuScrims() {
        boolean volver = false;

        while (!volver) {
            String opcion = view.mostrarMenuScrims();

            switch (opcion) {
                case "1":
                    scrimController.crearScrim();
                    break;
                case "2":
                    scrimController.buscarScrims();
                    break;
                case "3":
                    scrimController.listarTodosScrims();
                    break;
                case "4":
                    scrimController.verDetalleScrim();
                    break;
                case "5":
                    scrimController.eliminarScrim();
                    break;
                case "6":
                    scrimController.mostrarEstadisticas();
                    break;
                case "0":
                    volver = true;
                    break;
                default:
                    view.mostrarError("Opción no válida");
            }
        }
    }

    /**
     * Menú de gestión de postulaciones.
     */
    private void menuPostulaciones() {
        boolean volver = false;

        while (!volver) {
            String opcion = view.mostrarMenuPostulaciones();

            switch (opcion) {
                case "1":
                    postulacionController.postularseAScrim();
                    break;
                case "2":
                    postulacionController.gestionarPostulaciones();
                    break;
                case "3":
                    postulacionController.verTodasLasPostulaciones();
                    break;
                case "4":
                    postulacionController.verMiPostulacion();
                    break;
                case "0":
                    volver = true;
                    break;
                default:
                    view.mostrarError("Opción no válida");
            }
        }
    }

    /**
     * Menú de gestión de confirmaciones.
     */
    private void menuConfirmaciones() {
        confirmacionController.gestionarConfirmaciones();
    }

    /**
     * Inicia la aplicación.
     */
    public void iniciar() {
        view.mostrarBienvenida();
        mostrarMenuInicial();
    }
}
