import controller.LoginController;
import controller.RegisterController;
import controller.CrearScrimController;
import controller.BuscarScrimController;
import model.Usuario;
import model.Persistencia.RepositorioUsuario;
import model.Persistencia.RepositorioFactory;
import view.MenuView;
import view.LoginView;
import view.RegisterView;
import java.util.Map;

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
    private static LoginController loginController;
    private static RegisterController registerController;
    private static RepositorioUsuario repositorioUsuarios;

    public static void main(String[] args) {
        inicializarAplicacion();
        ejecutarMenuPrincipal();
        finalizarAplicacion();
    }

    /**
     * Inicializa todos los componentes necesarios de la aplicación.
     */
    private static void inicializarAplicacion() {
        // Cargar usuarios desde persistencia
        repositorioUsuarios = RepositorioFactory.getRepositorioUsuario();
        Map<String, Usuario> usuarios = repositorioUsuarios.listarTodos()
                .stream()
                .collect(java.util.stream.Collectors.toMap(Usuario::getUsername, u -> u));

        // Inicializar vistas
        menuView = new MenuView();
        loginView = new LoginView();
        registerView = new RegisterView();

        // Inicializar controladores
        loginController = new LoginController(usuarios);
        registerController = new RegisterController(usuarios);

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
            if (loginController.autenticar(username, password)) {
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

            // Registrar usuario
            if (registerController.registrarUsuario(username, email, password, juegoPrincipal, rango)) {
                // Persistir cambios
                repositorioUsuarios.guardar(new Usuario(username, email, password));

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
                    loginController.logout();
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
        Usuario usuario = loginController.getUsuarioLogueado();

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
     * La vista orquesta el flujo usando el controller.
     */
    private static void manejarCrearScrim() {
        try {
            Usuario usuario = loginController.getUsuarioLogueado();

            // Crear controller desacoplado
            CrearScrimController controller = new CrearScrimController(usuario.getUsername());

            // La vista orquesta el flujo
            view.CrearScrimView vista = new view.CrearScrimView();
            vista.iniciarCreacion(controller);

            menuView.presionarEnterParaContinuar();
        } catch (Exception e) {
            System.err.println("\n✗ Error al crear scrim: " + e.getMessage());
            e.printStackTrace();
            menuView.presionarEnterParaContinuar();
        }
    }

    /**
     * Maneja la búsqueda de scrims.
     * La vista orquesta el flujo usando el controller.
     */
    private static void manejarBuscarScrims() {
        try {
            // Crear controller desacoplado
            BuscarScrimController controller = new BuscarScrimController();

            // La vista orquesta el flujo
            view.BuscarScrimView vista = new view.BuscarScrimView();
            vista.iniciarBusqueda(controller);

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
