import dominio.modelo.Usuario;
import infraestructura.persistencia.repository.RepositorioFactory;
import infraestructura.persistencia.repository.RepositorioUsuario;
import infraestructura.persistencia.repository.RepositorioScrim;
import presentacion.view.LoginView;
import presentacion.view.MenuView;
import presentacion.view.RegisterView;

/**
 * Clase principal de la aplicación eScrims.
 * Gestiona el flujo principal de la aplicación: Login, Registro y Menú de
 * Usuario.
 * 
 * @author eScrims Team
 */
public class Main {

    private static MenuView menuView;
    private static LoginView loginView;
    private static RegisterView registerView;
    private static aplicacion.services.AuthService authService;
    private static RepositorioUsuario repositorioUsuarios;
    private static RepositorioScrim repositorioScrims;

    public static void main(String[] args) {
        inicializarAplicacion();
        ejecutarMenuPrincipal();
        finalizarAplicacion();
    }

    /**
     * Inicializa todos los componentes necesarios de la aplicación.
     */
    private static void inicializarAplicacion() {
        // Cargar repositorios desde persistencia
        repositorioUsuarios = RepositorioFactory.getRepositorioUsuario();
        repositorioScrims = RepositorioFactory.getRepositorioScrim();

        // Inicializar servicio de autenticación
        authService = new aplicacion.services.AuthService(repositorioUsuarios);

        // Inicializar vistas
        menuView = new MenuView();
        loginView = new LoginView();
        registerView = new RegisterView();

        // Mostrar bienvenida
        menuView.mostrarBienvenida();
    }

    /**
     * Ejecuta el menú principal de la aplicación.
     */
    private static void ejecutarMenuPrincipal() {
        boolean salir = false;

        while (!salir) {
            menuView.mostrarMenuPrincipal();
            int opcion = menuView.leerOpcion();

            switch (opcion) {
                case 1:
                    manejarLogin();
                    break;
                case 2:
                    manejarRegistro();
                    break;
                case 3:
                    salir = true;
                    menuView.mostrarSalida();
                    break;
                default:
                    menuView.mostrarOpcionInvalida();
            }
        }
    }

    /**
     * Maneja el flujo de login.
     */
    private static void manejarLogin() {
        loginView.mostrarTituloLogin();

        String username = loginView.solicitarUsername();
        String password = loginView.solicitarPassword();

        try {
            Usuario usuario = authService.autenticar(username, password);
            if (usuario != null) {
                loginView.mostrarLoginExitoso(username);
                menuView.presionarEnterParaContinuar();

                // Ir al menú de usuario logueado
                ejecutarMenuUsuario();
            }
        } catch (RuntimeException e) {
            loginView.mostrarErrorLogin(e.getMessage());

            if (loginView.confirmarReintento()) {
                manejarLogin();
            }
        }
    }

    /**
     * Maneja el flujo de registro.
     */
    private static void manejarRegistro() {
        registerView.mostrarTituloRegistro();
        registerView.mostrarRequisitos();

        try {
            // Solicitar datos
            registerView.mostrarProgreso(1, 5);
            String username = registerView.solicitarUsername();

            registerView.mostrarProgreso(2, 5);
            String email = registerView.solicitarEmail();

            registerView.mostrarProgreso(3, 5);
            String password = registerView.solicitarPassword();
            String confirmPassword = registerView.confirmarPassword();

            if (!password.equals(confirmPassword)) {
                registerView.mostrarErrorConfirmacion();
                if (registerView.confirmarReintento()) {
                    manejarRegistro();
                }
                return;
            }

            registerView.mostrarProgreso(4, 5);
            String juegoPrincipal = registerView.solicitarJuegoPrincipal();

            registerView.mostrarProgreso(5, 5);
            int rango = registerView.solicitarRango();

            // Mostrar resumen y confirmar
            registerView.mostrarResumenDatos(username, email, juegoPrincipal, rango);

            if (!registerView.confirmarRegistro()) {
                registerView.mostrarMensaje("\n✗ Registro cancelado.");
                return;
            }

            // Registrar usuario usando el servicio de autenticación
            Usuario nuevoUsuario = authService.registrarUsuario(username, email, password, juegoPrincipal, rango);

            if (nuevoUsuario != null) {
                registerView.mostrarRegistroExitoso(username);
                menuView.presionarEnterParaContinuar();
            }

        } catch (RuntimeException e) {
            registerView.mostrarErrorRegistro(e.getMessage());

            if (registerView.confirmarReintento()) {
                manejarRegistro();
            }
        }
    }

    /**
     * Ejecuta el menú del usuario logueado.
     */
    private static void ejecutarMenuUsuario() {
        boolean salir = false;

        while (!salir) {
            mostrarMenuUsuario();
            int opcion = menuView.leerOpcion();

            switch (opcion) {
                case 1:
                    manejarCrearScrim();
                    break;
                case 2:
                    manejarBuscarScrims();
                    break;
                case 3:
                    manejarEditarPerfil();
                    break;
                case 4:
                    salir = true;
                    authService.logout();
                    loginView.mostrarLogoutExitoso();
                    menuView.presionarEnterParaContinuar();
                    break;
                default:
                    menuView.mostrarOpcionInvalida();
            }
        }
    }

    /**
     * Muestra el menú de opciones para usuario logueado.
     */
    private static void mostrarMenuUsuario() {
        Usuario usuario = authService.getUsuarioLogueado();

        System.out.println("\n" + "=".repeat(50));
        System.out.println("           MENÚ DE USUARIO - " + usuario.getUsername());
        System.out.println("=".repeat(50));
        System.out.println("1. Crear Scrim");
        System.out.println("2. Buscar Scrims");
        System.out.println("3. Editar Perfil");
        System.out.println("4. Cerrar Sesión");
        System.out.println("=".repeat(50));
        System.out.print("Seleccione una opción: ");
    }

    /**
     * Maneja la creación de un scrim.
     * El controller coordina el flujo entre vista y servicio.
     */
    private static void manejarCrearScrim() {
        try {
            Usuario usuario = authService.getUsuarioLogueado();

            // Crear vista y controller
            presentacion.view.CrearScrimView vista = new presentacion.view.CrearScrimView();
            aplicacion.services.ScrimService scrimService = new aplicacion.services.ScrimService(repositorioScrims);
            presentacion.controller.ScrimController controller = new presentacion.controller.ScrimController(
                    scrimService, vista, null, usuario.getId());

            // El controller coordina el flujo
            controller.crearScrim();

            menuView.presionarEnterParaContinuar();
        } catch (Exception e) {
            System.err.println("\n✗ Error al crear scrim: " + e.getMessage());
            e.printStackTrace();
            menuView.presionarEnterParaContinuar();
        }
    }

    /**
     * Maneja la búsqueda de scrims.
     * El controller coordina el flujo entre vista y servicio.
     */
    private static void manejarBuscarScrims() {
        try {
            Usuario usuario = authService.getUsuarioLogueado();

            // Crear vista y controller
            presentacion.view.BuscarScrimView vista = new presentacion.view.BuscarScrimView();
            aplicacion.services.ScrimService scrimService = new aplicacion.services.ScrimService(repositorioScrims);
            presentacion.controller.ScrimController controller = new presentacion.controller.ScrimController(
                    scrimService, null, vista, usuario.getId());

            // El controller coordina el flujo
            controller.buscarScrims();

            menuView.presionarEnterParaContinuar();
        } catch (Exception e) {
            System.err.println("\n✗ Error al buscar scrims: " + e.getMessage());
            e.printStackTrace();
            menuView.presionarEnterParaContinuar();
        }
    }

    /**
     * Maneja la edición del perfil del usuario.
     * TODO: Implementar cuando la clase Usuario tenga los getters necesarios
     */
    private static void manejarEditarPerfil() {
        System.out.println("\n✗ Función en desarrollo. Próximamente disponible.");
        menuView.presionarEnterParaContinuar();
    }

    /**
     * Finaliza la aplicación y libera recursos.
     */
    private static void finalizarAplicacion() {
        menuView.cerrar();
    }
}
