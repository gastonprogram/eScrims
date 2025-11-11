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
            if (juegoPrincipal.isEmpty()) {
                registerView.mostrarError("Número de juego inválido. Por favor, intente nuevamente.");
                if (registerView.confirmarReintento()) {
                    manejarRegistro();
                }
                return;
            }

            registerView.mostrarProgreso(5, 5);
            int rango = registerView.solicitarRango();

            // Mostrar resumen y confirmar
            registerView.mostrarResumenDatos(username, email, juegoPrincipal, rango);

            if (!registerView.confirmarRegistro()) {
                registerView.mostrarMensaje("\n- Registro cancelado.");
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
                    manejarPostulacion();
                    break;
                case 4:
                    manejarConfirmaciones();
                    break;
                case 5:
                    manejarGestionOrganizador();
                    break;
                case 6:
                    manejarEstadisticasYModeracion();
                    break;
                case 7:
                    manejarEditarPerfil();
                    break;
                case 8:
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
        System.out.println("3. Postularse a un Scrim");
        System.out.println("4. Gestionar Confirmaciones");
        System.out.println("5. Gestionar mis Scrims (Organizador)");
        System.out.println("6. Estadísticas y Moderación");
        System.out.println("7. Editar Perfil");
        System.out.println("8. Cerrar Sesión");
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
            System.err.println("\n- Error al crear scrim: " + e.getMessage());
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
            System.err.println("\n- Error al buscar scrims: " + e.getMessage());
            e.printStackTrace();
            menuView.presionarEnterParaContinuar();
        }
    }

    /**
     * Maneja la edición del perfil del usuario.
     * Utiliza el PerfilController modernizado para gestionar todas las
     * funcionalidades.
     */
    private static void manejarEditarPerfil() {
        try {
            Usuario usuario = authService.getUsuarioLogueado();

            // Crear vista modernizada y servicio
            presentacion.view.PerfilView vista = new presentacion.view.PerfilView();
            aplicacion.services.UsuarioService usuarioService = new aplicacion.services.UsuarioService(
                    repositorioUsuarios);

            // Crear controller modernizado
            presentacion.controller.PerfilController controller = new presentacion.controller.PerfilController(
                    usuarioService, vista, usuario.getId(), repositorioUsuarios);

            // El controller gestiona todo el flujo de perfil
            controller.gestionarPerfil();

        } catch (Exception e) {
            System.err.println("\n- Error en la gestión de perfil: " + e.getMessage());
            e.printStackTrace();
            menuView.presionarEnterParaContinuar();
        }
    }

    /**
     * Maneja la postulación a un scrim.
     * El controller coordina el flujo entre vista y servicio.
     * Usa los datos del perfil del usuario (rango y latencia).
     */
    private static void manejarPostulacion() {
        try {
            Usuario usuario = authService.getUsuarioLogueado();

            // Crear vista simplificada y servicios
            presentacion.view.PostulacionViewSimplificada vista = new presentacion.view.PostulacionViewSimplificada();
            aplicacion.services.PostulacionService postulacionService = new aplicacion.services.PostulacionService(
                    repositorioScrims, repositorioUsuarios);
            aplicacion.services.ScrimService scrimService = new aplicacion.services.ScrimService(repositorioScrims);

            // Crear controller simplificado pasando el objeto Usuario completo
            presentacion.controller.PostulacionControllerSimplificado controller = new presentacion.controller.PostulacionControllerSimplificado(
                    postulacionService, scrimService, vista, usuario);

            // El controller coordina el flujo de postulación
            controller.postularseAScrim();

            menuView.presionarEnterParaContinuar();
        } catch (Exception e) {
            System.err.println("\n- Error en la postulación: " + e.getMessage());
            e.printStackTrace();
            menuView.presionarEnterParaContinuar();
        }
    }

    /**
     * Maneja el menú de confirmaciones (confirmar/rechazar asistencia).
     * El controller coordina el flujo entre vista y servicio.
     */
    private static void manejarConfirmaciones() {
        try {
            Usuario usuario = authService.getUsuarioLogueado();

            // Crear vista simplificada y servicio
            presentacion.view.ConfirmacionViewSimplificada vista = new presentacion.view.ConfirmacionViewSimplificada();
            aplicacion.services.ConfirmacionService confirmacionService = new aplicacion.services.ConfirmacionService(
                    repositorioScrims);

            // Crear controller simplificado
            presentacion.controller.ConfirmacionControllerSimplificado controller = new presentacion.controller.ConfirmacionControllerSimplificado(
                    confirmacionService, vista, usuario.getId());

            // El controller coordina el flujo completo de confirmaciones (menú interno)
            controller.gestionarConfirmaciones();

            menuView.presionarEnterParaContinuar();
        } catch (Exception e) {
            System.err.println("\n- Error en confirmaciones: " + e.getMessage());
            e.printStackTrace();
            menuView.presionarEnterParaContinuar();
        }
    }

    /**
     * Maneja la gestión de scrims como organizador.
     * El controller coordina el flujo entre vista y servicios.
     */
    private static void manejarGestionOrganizador() {
        try {
            Usuario usuario = authService.getUsuarioLogueado();
            boolean salir = false;

            while (!salir) {
                // Mostrar submenu de gestión del organizador
                System.out.println("\n" + "=".repeat(50));
                System.out.println("        GESTIÓN DEL ORGANIZADOR");
                System.out.println("=".repeat(50));
                System.out.println("1. Gestionar Postulaciones y Confirmaciones");
                System.out.println("2. Acciones del Organizador (Invitar, Asignar Roles, etc.)");
                System.out.println("0. Volver al menú anterior");
                System.out.println("=".repeat(50));
                System.out.print("Selecciona una opción: ");

                int opcion = menuView.leerOpcion();

                switch (opcion) {
                    case 1:
                        gestionarPostulacionesYConfirmaciones(usuario);
                        break;
                    case 2:
                        gestionarAccionesOrganizador(usuario);
                        break;
                    case 0:
                        salir = true;
                        break;
                    default:
                        menuView.mostrarOpcionInvalida();
                }
            }

        } catch (Exception e) {
            System.err.println("\n- Error en gestión de organizador: " + e.getMessage());
            e.printStackTrace();
            menuView.presionarEnterParaContinuar();
        }
    }

    /**
     * Gestiona las postulaciones y confirmaciones de los scrims del organizador.
     */
    private static void gestionarPostulacionesYConfirmaciones(Usuario usuario) {
        try {
            // Crear vista y servicios
            presentacion.view.OrganizadorView vista = new presentacion.view.OrganizadorView();
            aplicacion.services.PostulacionService postulacionService = new aplicacion.services.PostulacionService(
                    repositorioScrims, repositorioUsuarios);
            aplicacion.services.ConfirmacionService confirmacionService = new aplicacion.services.ConfirmacionService(
                    repositorioScrims);
            aplicacion.services.ScrimService scrimService = new aplicacion.services.ScrimService(
                    repositorioScrims);

            // Crear controller del organizador
            presentacion.controller.OrganizadorController controller = new presentacion.controller.OrganizadorController(
                    postulacionService, confirmacionService, scrimService, vista, usuario.getId());

            // El controller coordina el flujo completo del organizador (menú interno)
            controller.gestionarScrims();

            menuView.presionarEnterParaContinuar();
        } catch (Exception e) {
            System.err.println("\n- Error en gestión de postulaciones: " + e.getMessage());
            e.printStackTrace();
            menuView.presionarEnterParaContinuar();
        }
    }

    /**
     * Gestiona las acciones específicas del organizador (invitar, asignar roles,
     * etc.).
     */
    private static void gestionarAccionesOrganizador(Usuario usuario) {
        try {
            // Crear servicios
            aplicacion.services.ScrimService scrimService = new aplicacion.services.ScrimService(
                    repositorioScrims);
            aplicacion.services.OrganizadorService organizadorService = new aplicacion.services.OrganizadorService(
                    repositorioScrims, repositorioUsuarios, scrimService);

            // Crear controller de acciones del organizador
            presentacion.controller.AccionesOrganizadorController controller = new presentacion.controller.AccionesOrganizadorController(
                    organizadorService, scrimService, new java.util.Scanner(System.in));

            // El controller coordina el flujo de acciones del organizador
            controller.mostrarMenuAccionesOrganizador(usuario.getId());

            menuView.presionarEnterParaContinuar();
        } catch (Exception e) {
            System.err.println("\n- Error en acciones de organizador: " + e.getMessage());
            e.printStackTrace();
            menuView.presionarEnterParaContinuar();
        }
    }

    /**
     * Maneja las estadísticas y moderación.
     * Gestiona el sistema de reportes, moderación y estadísticas de scrims.
     */
    private static void manejarEstadisticasYModeracion() {
        try {
            Usuario usuario = authService.getUsuarioLogueado();

            // Crear vista y servicios necesarios
            presentacion.view.EstadisticasView vista = new presentacion.view.EstadisticasView();
            aplicacion.services.EstadisticasService estadisticasService = new aplicacion.services.EstadisticasService();
            aplicacion.services.ScrimService scrimService = new aplicacion.services.ScrimService(repositorioScrims);

            boolean salir = false;

            while (!salir) {
                vista.mostrarMenuCompleto();
                int opcion = vista.leerOpcion();

                switch (opcion) {
                    case 1:
                        // Ver estadísticas de mis scrims
                        manejarVerEstadisticasScrim(vista, estadisticasService, scrimService);
                        break;
                    case 2:
                        // Finalizar scrim en juego
                        manejarFinalizarScrimEnJuego(vista, estadisticasService, scrimService);
                        break;
                    case 3:
                        // Reportar conducta
                        manejarReporteConducata(vista, estadisticasService, usuario);
                        break;
                    case 4:
                        // Ver reportes de un usuario
                        manejarVerReportes(vista, estadisticasService);
                        break;
                    case 5:
                        // Ver estado de moderación de un usuario
                        manejarVerEstadoModeracion(vista, estadisticasService);
                        break;
                    case 6:
                        // Gestionar comentarios
                        manejarGestionComentarios(vista, estadisticasService, usuario);
                        break;
                    case 7:
                        salir = true;
                        break;
                    default:
                        vista.mostrarMensaje("Opción inválida. Por favor, seleccione una opción válida.");
                }
            }

        } catch (Exception e) {
            System.err.println("\n- Error en estadísticas y moderación: " + e.getMessage());
            e.printStackTrace();
            menuView.presionarEnterParaContinuar();
        }
    }

    private static void manejarVerEstadisticasScrim(presentacion.view.EstadisticasView vista,
            aplicacion.services.EstadisticasService estadisticasService,
            aplicacion.services.ScrimService scrimService) {

        try {
            Usuario usuario = authService.getUsuarioLogueado();

            // Obtener solo los scrims del usuario logueado que tengan estadísticas
            java.util.List<dominio.modelo.Scrim> misScrimsConEstadisticas = repositorioScrims.obtenerTodos().stream()
                    .filter(scrim -> scrim.getCreatedBy().equals(usuario.getId())) // Solo mis scrims
                    .filter(scrim -> "FINALIZADO".equals(scrim.getEstado())) // Solo finalizados
                    .filter(scrim -> estadisticasService.buscarEstadisticas(scrim.getId()).isPresent()) // Solo con
                                                                                                        // estadísticas
                    .toList();

            if (misScrimsConEstadisticas.isEmpty()) {
                vista.mostrarMensaje("No tienes scrims finalizados con estadísticas.");
                vista.mostrarMensaje("Las estadísticas se generan automáticamente al finalizar un scrim.");
                return;
            }

            // Permitir al usuario seleccionar por número en lugar de escribir ID
            String scrimId = vista.seleccionarScrimDeNumero(misScrimsConEstadisticas);

            if (scrimId == null) {
                vista.mostrarMensaje("Operación cancelada.");
                return;
            }

            // Buscar las estadísticas existentes
            java.util.Optional<dominio.estadisticas.EstadisticasScrim> estadisticasOpt = estadisticasService
                    .buscarEstadisticas(scrimId);
            if (estadisticasOpt.isPresent()) {
                vista.mostrarEstadisticasScrim(estadisticasOpt.get());
            } else {
                vista.mostrarMensaje("No se encontraron estadísticas para este scrim.");
            }
        } catch (Exception e) {
            vista.mostrarMensaje("Error al obtener estadísticas: " + e.getMessage());
        }
    }

    private static void manejarReporteConducata(presentacion.view.EstadisticasView vista,
            aplicacion.services.EstadisticasService estadisticasService, Usuario usuario) {

        try {
            // Seleccionar scrim de lista numerada
            java.util.List<dominio.modelo.Scrim> todosLosScrims = repositorioScrims.obtenerTodos();
            String scrimId = vista.seleccionarScrimDeNumero(todosLosScrims);

            if (scrimId == null) {
                vista.mostrarMensaje("Operación cancelada.");
                return;
            }

            // Seleccionar usuario de lista numerada
            java.util.List<dominio.modelo.Usuario> todosLosUsuarios = repositorioUsuarios.listarTodos();
            String usuarioReportadoId = vista.seleccionarUsuarioDeNumero(todosLosUsuarios);

            if (usuarioReportadoId == null) {
                vista.mostrarMensaje("Operación cancelada.");
                return;
            }

            dominio.estadisticas.ReporteConducta.TipoReporte tipo = vista.solicitarTipoReporte();
            dominio.estadisticas.ReporteConducta.Gravedad gravedad = vista.solicitarGravedad();
            String descripcion = vista.solicitarDescripcion();

            if (descripcion.isEmpty()) {
                vista.mostrarMensaje("Error: La descripción no puede estar vacía.");
                return;
            }

            estadisticasService.reportarConducta(scrimId, tipo, gravedad, usuarioReportadoId,
                    usuario.getId().toString(), descripcion);
            vista.mostrarMensaje("- Reporte creado exitosamente.");
        } catch (Exception e) {
            vista.mostrarMensaje("Error al crear reporte: " + e.getMessage());
        }
    }

    private static void manejarVerReportes(presentacion.view.EstadisticasView vista,
            aplicacion.services.EstadisticasService estadisticasService) {

        try {
            // Seleccionar usuario de lista numerada
            java.util.List<dominio.modelo.Usuario> todosLosUsuarios = repositorioUsuarios.listarTodos();
            String usuarioId = vista.seleccionarUsuarioDeNumero(todosLosUsuarios);

            if (usuarioId == null) {
                vista.mostrarMensaje("Operación cancelada.");
                return;
            }

            java.util.List<dominio.estadisticas.ReporteConducta> reportes = estadisticasService.getSistemaModeracion()
                    .getReportesUsuario(usuarioId);
            vista.mostrarReportes(reportes);
        } catch (Exception e) {
            vista.mostrarMensaje("Error al obtener reportes: " + e.getMessage());
        }
    }

    private static void manejarVerEstadoModeracion(presentacion.view.EstadisticasView vista,
            aplicacion.services.EstadisticasService estadisticasService) {

        try {
            // Seleccionar usuario de lista numerada
            java.util.List<dominio.modelo.Usuario> todosLosUsuarios = repositorioUsuarios.listarTodos();
            String usuarioId = vista.seleccionarUsuarioDeNumero(todosLosUsuarios);

            if (usuarioId == null) {
                vista.mostrarMensaje("Operación cancelada.");
                return;
            }

            dominio.estadisticas.SistemaModeracion moderacion = estadisticasService.getSistemaModeracion();
            vista.mostrarEstadoModeracion(usuarioId, moderacion);
        } catch (Exception e) {
            vista.mostrarMensaje("Error al obtener estado de moderación: " + e.getMessage());
        }
    }

    /**
     * Maneja la gestión de comentarios: crear, ver, moderar.
     */
    private static void manejarGestionComentarios(presentacion.view.EstadisticasView vista,
            aplicacion.services.EstadisticasService estadisticasService, Usuario usuario) {
        boolean salir = false;

        while (!salir) {
            vista.mostrarMenuComentarios();
            int opcion = vista.leerOpcion();

            switch (opcion) {
                case 1:
                    // Crear comentario
                    crearComentario(vista, estadisticasService, usuario);
                    break;
                case 2:
                    // Ver comentarios de scrim
                    verComentariosDeScrim(vista, estadisticasService);
                    break;
                case 3:
                    // Ver mis comentarios
                    verMisComentarios(vista, estadisticasService, usuario);
                    break;
                case 4:
                    // Moderar comentarios (admin)
                    moderarComentarios(vista, estadisticasService);
                    break;
                case 5:
                    salir = true;
                    break;
                default:
                    vista.mostrarMensaje("Opción inválida.");
            }
        }
    }

    private static void crearComentario(presentacion.view.EstadisticasView vista,
            aplicacion.services.EstadisticasService estadisticasService, Usuario usuario) {
        try {
            // Seleccionar scrim de lista numerada
            java.util.List<dominio.modelo.Scrim> todosLosScrims = repositorioScrims.obtenerTodos();
            String scrimId = vista.seleccionarScrimDeNumero(todosLosScrims);

            if (scrimId == null) {
                vista.mostrarMensaje("Operación cancelada.");
                return;
            }

            String contenido = vista.solicitarContenidoComentario();
            int rating = vista.solicitarRating();

            if (contenido.trim().isEmpty()) {
                vista.mostrarMensaje("Error: El contenido es obligatorio.");
                return;
            }

            // Usar el hash del username como jugadorId para simplicidad
            String jugadorId = usuario.getId();

            dominio.estadisticas.Comentario comentario = estadisticasService.crearComentario(jugadorId, scrimId,
                    contenido, rating);
            vista.mostrarMensaje("- Comentario creado exitosamente (ID: " + comentario.getId() + ")");
            vista.mostrarMensaje("El comentario está pendiente de moderación.");

        } catch (Exception e) {
            vista.mostrarMensaje("Error al crear comentario: " + e.getMessage());
        }
    }

    private static void verComentariosDeScrim(presentacion.view.EstadisticasView vista,
            aplicacion.services.EstadisticasService estadisticasService) {
        try {
            // Seleccionar scrim de lista numerada
            java.util.List<dominio.modelo.Scrim> todosLosScrims = repositorioScrims.obtenerTodos();
            String scrimId = vista.seleccionarScrimDeNumero(todosLosScrims);

            if (scrimId == null) {
                vista.mostrarMensaje("Operación cancelada.");
                return;
            }

            java.util.List<dominio.estadisticas.Comentario> comentarios = estadisticasService
                    .obtenerComentariosDeScrim(scrimId);

            // Filtrar solo comentarios aprobados para usuarios normales
            java.util.List<dominio.estadisticas.Comentario> comentariosAprobados = comentarios.stream()
                    .filter(c -> c.getEstado() == dominio.estadisticas.Comentario.EstadoModeracion.APROBADO)
                    .toList();

            vista.mostrarComentarios(comentariosAprobados, "Comentarios del Scrim " + scrimId);

        } catch (Exception e) {
            vista.mostrarMensaje("Error al obtener comentarios: " + e.getMessage());
        }
    }

    private static void verMisComentarios(presentacion.view.EstadisticasView vista,
            aplicacion.services.EstadisticasService estadisticasService, Usuario usuario) {
        try {
            // Con la nueva arquitectura, obtenemos comentarios por scrim
            // Necesitamos obtener todos los scrims y luego filtrar comentarios del usuario
            vista.mostrarMensaje("=== MIS COMENTARIOS ===");

            java.util.List<dominio.estadisticas.EstadisticasScrim> todasLasEstadisticas = estadisticasService
                    .obtenerTodasLasEstadisticasScrims();

            java.util.List<dominio.estadisticas.Comentario> misComentarios = new java.util.ArrayList<>();
            Long jugadorId = Long.valueOf(Math.abs(usuario.getUsername().hashCode()));

            for (dominio.estadisticas.EstadisticasScrim estadisticas : todasLasEstadisticas) {
                java.util.List<dominio.estadisticas.Comentario> comentariosScrim = estadisticasService
                        .obtenerComentariosDeScrim(estadisticas.getScrimId());

                for (dominio.estadisticas.Comentario comentario : comentariosScrim) {
                    if (jugadorId.equals(comentario.getJugadorId())) {
                        misComentarios.add(comentario);
                    }
                }
            }

            vista.mostrarComentarios(misComentarios, "Mis Comentarios");

        } catch (Exception e) {
            vista.mostrarMensaje("Error al obtener sus comentarios: " + e.getMessage());
        }
    }

    private static void moderarComentarios(presentacion.view.EstadisticasView vista,
            aplicacion.services.EstadisticasService estadisticasService) {
        try {
            // Mostrar comentarios pendientes
            java.util.List<dominio.estadisticas.Comentario> pendientes = estadisticasService
                    .obtenerComentariosPendientes();

            if (pendientes.isEmpty()) {
                vista.mostrarMensaje("No hay comentarios pendientes de moderación.");
                return;
            }

            vista.mostrarComentarios(pendientes, "Comentarios Pendientes de Moderación");

            // Solicitar ID del comentario a moderar
            Long comentarioId = vista.solicitarComentarioId();
            if (comentarioId == null) {
                vista.mostrarMensaje("ID de comentario inválido.");
                return;
            }

            // Verificar que el comentario existe
            java.util.Optional<dominio.estadisticas.Comentario> comentarioOpt = estadisticasService
                    .buscarComentario(comentarioId);
            if (comentarioOpt.isEmpty()) {
                vista.mostrarMensaje("No se encontró el comentario con ID: " + comentarioId);
                return;
            }

            // Solicitar nuevo estado
            dominio.estadisticas.Comentario.EstadoModeracion nuevoEstado = vista.solicitarEstadoModeracion();
            String motivoRechazo = null;

            if (nuevoEstado == dominio.estadisticas.Comentario.EstadoModeracion.RECHAZADO) {
                motivoRechazo = vista.solicitarMotivoRechazo();
            }

            // Aplicar moderación
            estadisticasService.moderarComentario(comentarioId, nuevoEstado, motivoRechazo);
            vista.mostrarMensaje("- Comentario moderado exitosamente.");

        } catch (Exception e) {
            vista.mostrarMensaje("Error al moderar comentarios: " + e.getMessage());
        }
    }

    /**
     * Maneja la finalización de scrims que están en juego.
     */
    private static void manejarFinalizarScrimEnJuego(presentacion.view.EstadisticasView vista,
            aplicacion.services.EstadisticasService estadisticasService,
            aplicacion.services.ScrimService scrimService) {
        try {
            vista.mostrarMensaje("=== FINALIZAR SCRIM EN JUEGO ===");

            // Obtener todos los scrims y filtrar los que están en juego
            java.util.List<dominio.modelo.Scrim> todosLosScrims = repositorioScrims.obtenerTodos();
            String scrimId = vista.seleccionarScrimEnJuego(todosLosScrims);

            if (scrimId == null) {
                vista.mostrarMensaje("Operación cancelada.");
                return;
            }

            // Obtener el scrim
            dominio.modelo.Scrim scrim = scrimService.buscarPorId(scrimId);
            if (scrim == null) {
                vista.mostrarMensaje("No se encontró el scrim.");
                return;
            }

            // Obtener o crear estadísticas para el scrim
            dominio.estadisticas.EstadisticasScrim estadisticas = estadisticasService
                    .obtenerEstadisticasParaScrim(scrim);

            // Verificar si hay estadísticas de jugadores registradas
            if (estadisticas.getEstadisticasPorJugador().isEmpty()) {
                vista.mostrarMensaje("- No hay estadísticas de jugadores registradas para este scrim.");
                vista.mostrarMensaje("Se finalizará la partida sin estadísticas individuales.");
            }

            // Finalizar partida con simulación
            estadisticas.finalizarPartidaConSimulacion();

            vista.mostrarMensaje("- Partida finalizada exitosamente!");
            vista.mostrarMensaje("- Ganador determinado: " + estadisticas.getGanador());
            vista.mostrarMensaje("- Duración simulada: " + estadisticas.getDuracionMinutos() + " minutos");

            // Mostrar estadísticas completas
            vista.mostrarEstadisticasScrim(estadisticas);

            // Preguntar si desea generar un resumen de reportes
            System.out.print("\n¿Desea ver un resumen de reportes de conducta? (s/n): ");
            try (java.util.Scanner inputScanner = new java.util.Scanner(System.in)) {
                String respuesta = inputScanner.nextLine().toLowerCase();

                if (respuesta.startsWith("s")) {
                    if (!estadisticas.getReportes().isEmpty()) {
                        vista.mostrarMensaje("\n- RESUMEN DE REPORTES DE CONDUCTA:");
                        estadisticas.getReportes().forEach(reporte -> {
                            System.out.printf("- %s: %s (%s) - %s%n",
                                    reporte.getUsuarioReportadoId(),
                                    reporte.getTipo(),
                                    reporte.getGravedad(),
                                    reporte.getDescripcion());
                        });
                    } else {
                        vista.mostrarMensaje("No hay reportes de conducta para este scrim.");
                    }
                }
            }

        } catch (Exception e) {
            vista.mostrarMensaje("Error al finalizar scrim: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Finaliza la aplicación y libera recursos.
     */
    private static void finalizarAplicacion() {
        menuView.cerrar();
    }
}
