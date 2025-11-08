package test;

import aplicacion.builders.ScrimOrganizador;
import aplicacion.services.OrganizadorService;
import aplicacion.services.RolPersistenceService;
import aplicacion.services.ScrimService;
import dominio.acciones.AsignarRolAccion;
import dominio.acciones.InvitarJugadorAccion;
import dominio.acciones.SwapJugadoresAccion;
import dominio.juegos.LeagueOfLegends;
import dominio.modelo.Confirmacion;
import dominio.modelo.ParticipanteScrim;
import dominio.modelo.Scrim;
import dominio.modelo.Usuario;
import dominio.roles.lol.*;
import dominio.valueobjects.formatosScrims.ScrimFormat;
import infraestructura.persistencia.implementacion.RepositorioScrimMemoria;
import infraestructura.persistencia.implementacion.RepositorioUsuarioJSON;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Test integral del ScrimOrganizador que demuestra todas las funcionalidades:
 * - Crear scrim y organizador
 * - Invitar jugadores con roles específicos
 * - Asignar y reasignar roles
 * - Intercambiar jugadores (swap)
 * - Deshacer acciones
 * - Confirmar scrim CON PERSISTENCIA DE ROLES
 * - Validar restricciones y estados
 * - Verificar que los roles se mantienen después de la confirmación
 * 
 * Este test simula un flujo completo desde la perspectiva del organizador
 * usando tanto el patrón directo (ScrimOrganizador) como el servicio.
 * 
 * Ejecutar: java -cp bin;lib/* test.TestScrimOrganizadorCompleto
 */
public class TestScrimOrganizadorCompleto {

    public static void main(String[] args) {
        System.out.println("=".repeat(80));
        System.out.println("            TEST INTEGRAL DEL SCRIM ORGANIZADOR");
        System.out.println("=".repeat(80));

        try {
            // Setup inicial
            RepositorioScrimMemoria repoScrims = RepositorioScrimMemoria.getInstance();
            RepositorioUsuarioJSON repoUsuarios = new RepositorioUsuarioJSON();
            repoScrims.limpiar();

            // Servicios
            ScrimService scrimService = new ScrimService(repoScrims);
            OrganizadorService organizadorService = new OrganizadorService(repoScrims, repoUsuarios, scrimService);

            // 1. CREAR USUARIOS DE PRUEBA
            System.out.println("\n1. CREANDO USUARIOS DE PRUEBA");
            System.out.println("-".repeat(50));

            Usuario organizador = crearUsuario(repoUsuarios, "Faker", "faker@t1.com");
            Usuario jugador1 = crearUsuario(repoUsuarios, "Zeus", "zeus@t1.com");
            Usuario jugador2 = crearUsuario(repoUsuarios, "Canyon", "canyon@drx.com");
            Usuario jugador3 = crearUsuario(repoUsuarios, "Chovy", "chovy@geng.com");
            Usuario jugador4 = crearUsuario(repoUsuarios, "Gumayusi", "guma@t1.com");
            Usuario jugador5 = crearUsuario(repoUsuarios, "Keria", "keria@t1.com");

            System.out.println("✓ Usuarios creados: " + organizador.getUsername() + ", " +
                    jugador1.getUsername() + ", " + jugador2.getUsername() + ", " +
                    jugador3.getUsername() + ", " + jugador4.getUsername() + ", " +
                    jugador5.getUsername());

            // 2. CREAR SCRIM
            System.out.println("\n2. CREANDO SCRIM");
            System.out.println("-".repeat(50));

            Scrim scrim = crearScrim(organizador);
            repoScrims.guardar(scrim);
            System.out.println("✓ Scrim creado: ID=" + scrim.getId() + ", Juego=" + scrim.getJuego().getNombre());

            // 3. CREAR SCRIM ORGANIZADOR
            System.out.println("\n3. INICIALIZANDO SCRIM ORGANIZADOR");
            System.out.println("-".repeat(50));

            ScrimOrganizador scrimOrganizador = new ScrimOrganizador(scrim);
            System.out.println("✓ ScrimOrganizador inicializado");
            System.out.println("  - Participantes actuales: " + scrimOrganizador.getParticipantes().size());
            System.out.println("  - Estado bloqueado: " + scrimOrganizador.isBloqueado());
            System.out.println("  - Acciones en historial: " + scrimOrganizador.getCantidadAccionesEnHistorial());

            // 4. INVITAR JUGADORES CON ROLES
            System.out.println("\n4. INVITANDO JUGADORES CON ROLES ESPECÍFICOS");
            System.out.println("-".repeat(50));

            testInvitarJugadores(scrimOrganizador, jugador1, jugador2, jugador3, jugador4, jugador5);

            // 5. MOSTRAR ESTADO ACTUAL
            System.out.println("\n5. ESTADO ACTUAL DEL SCRIM");
            System.out.println("-".repeat(50));
            mostrarEstadoScrim(scrimOrganizador);

            // 6. ASIGNAR Y REASIGNAR ROLES
            System.out.println("\n6. ASIGNACIÓN Y REASIGNACIÓN DE ROLES");
            System.out.println("-".repeat(50));
            testAsignarRoles(scrimOrganizador);

            // 7. INTERCAMBIO DE JUGADORES (SWAP)
            System.out.println("\n7. INTERCAMBIO DE JUGADORES (SWAP)");
            System.out.println("-".repeat(50));
            testSwapJugadores(scrimOrganizador);

            // 8. FUNCIONALIDAD DE DESHACER
            System.out.println("\n8. FUNCIONALIDAD DE DESHACER");
            System.out.println("-".repeat(50));
            testDeshacerAcciones(scrimOrganizador);

            // 9. VALIDACIONES Y RESTRICCIONES
            System.out.println("\n9. VALIDACIONES Y RESTRICCIONES");
            System.out.println("-".repeat(50));
            testValidaciones(scrimOrganizador, jugador1);

            // 10. CONFIRMACIÓN DEL SCRIM CON PERSISTENCIA DE ROLES
            System.out.println("\n10. CONFIRMACIÓN DEL SCRIM CON PERSISTENCIA DE ROLES");
            System.out.println("-".repeat(50));
            testConfirmarScrimConPersistencia(scrimOrganizador);

            // 11. VERIFICACIÓN DE PERSISTENCIA DE ROLES
            System.out.println("\n11. VERIFICACIÓN DE PERSISTENCIA DE ROLES");
            System.out.println("-".repeat(50));
            testVerificarPersistenciaRoles(scrim);

            // 12. DEMOSTRACIÓN DEL SERVICIO
            System.out.println("\n12. DEMOSTRACIÓN DEL ORGANIZADOR SERVICE");
            System.out.println("-".repeat(50));
            testOrganizadorService(organizadorService, scrim.getId(), organizador.getId());

            System.out.println("\n" + "=".repeat(80));
            System.out.println("            ✓ TEST COMPLETADO EXITOSAMENTE");
            System.out.println("=".repeat(80));

        } catch (Exception e) {
            System.err.println("\n✗ ERROR EN EL TEST: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static Usuario crearUsuario(RepositorioUsuarioJSON repo, String username, String email) {
        Usuario usuario = new Usuario(username, email, "password123");
        usuario.setJuegoPrincipal(LeagueOfLegends.getInstance());

        // Verificar si ya existe
        Usuario existente = repo.buscarPorId(username);
        if (existente != null) {
            System.out.println("⚠ Usuario " + username + " ya existía, usando el existente");
            return existente;
        }

        repo.guardar(usuario);
        return usuario;
    }

    private static Scrim crearScrim(Usuario organizador) {
        LeagueOfLegends lol = LeagueOfLegends.getInstance();
        ScrimFormat formato = lol.getFormatosDisponibles().get(0); // 5v5 Summoner's Rift

        // Crear lista de roles requeridos para LoL
        List<String> rolesRequeridos = List.of("Top", "Jungle", "Mid", "ADC", "Support");

        Scrim scrim = new Scrim(
                lol,
                formato,
                LocalDateTime.now().plusDays(1),
                1, // rangoMin
                30, // rangoMax
                rolesRequeridos,
                50, // latenciaMax
                10 // plazas
        );

        // Establecer el creador
        scrim.setCreatedBy(organizador.getId());

        return scrim;
    }

    private static void testInvitarJugadores(ScrimOrganizador organizador,
            Usuario jugador1, Usuario jugador2,
            Usuario jugador3, Usuario jugador4,
            Usuario jugador5) {
        // Invitar jugadores con roles específicos
        System.out.println("Invitando jugadores...");

        // Zeus - Top
        InvitarJugadorAccion invitarZeus = new InvitarJugadorAccion(jugador1, new RolTopLoL());
        organizador.ejecutarAccion(invitarZeus);
        System.out.println("✓ " + jugador1.getUsername() + " invitado como Top");

        // Canyon - Jungle
        InvitarJugadorAccion invitarCanyon = new InvitarJugadorAccion(jugador2, new RolJungleLoL());
        organizador.ejecutarAccion(invitarCanyon);
        System.out.println("✓ " + jugador2.getUsername() + " invitado como Jungle");

        // Chovy - Mid
        InvitarJugadorAccion invitarChovy = new InvitarJugadorAccion(jugador3, new RolMidLoL());
        organizador.ejecutarAccion(invitarChovy);
        System.out.println("✓ " + jugador3.getUsername() + " invitado como Mid");

        // Gumayusi - ADC
        InvitarJugadorAccion invitarGuma = new InvitarJugadorAccion(jugador4, new RolADCLoL());
        organizador.ejecutarAccion(invitarGuma);
        System.out.println("✓ " + jugador4.getUsername() + " invitado como ADC");

        // Keria - Support
        InvitarJugadorAccion invitarKeria = new InvitarJugadorAccion(jugador5, new RolSupportLoL());
        organizador.ejecutarAccion(invitarKeria);
        System.out.println("✓ " + jugador5.getUsername() + " invitado como Support");

        System.out.println("Total de acciones en historial: " + organizador.getCantidadAccionesEnHistorial());
    }

    private static void mostrarEstadoScrim(ScrimOrganizador organizador) {
        List<ParticipanteScrim> participantes = organizador.getParticipantes();
        System.out.println("Participantes actuales (" + participantes.size() + "):");

        for (ParticipanteScrim participante : participantes) {
            System.out.printf("  - %-10s: %s%n",
                    participante.getUserId(),
                    participante.getRolAsignado().getNombre());
        }

        System.out.println("Estado del organizador:");
        System.out.println("  - Bloqueado: " + organizador.isBloqueado());
        System.out.println("  - Acciones en historial: " + organizador.getCantidadAccionesEnHistorial());
    }

    private static void testAsignarRoles(ScrimOrganizador organizador) {
        System.out.println("Probando asignación de roles...");

        // Cambiar Zeus de Top a Mid (temporal)
        AsignarRolAccion cambiarZeus = new AsignarRolAccion("Zeus", new RolMidLoL());

        try {
            // Esto debería fallar porque Mid ya está ocupado por Chovy
            organizador.ejecutarAccion(cambiarZeus);
            System.out.println("✗ ERROR: Debería haber fallado - rol ya ocupado");
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Validación correcta: " + e.getMessage());
        }

        // Cambiar Chovy a un rol libre (asumiendo que hay uno)
        AsignarRolAccion liberarMid = new AsignarRolAccion("Chovy", new RolTopLoL());
        try {
            organizador.ejecutarAccion(liberarMid);
            System.out.println("✗ ERROR: Debería haber fallado - Top ya ocupado por Zeus");
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Validación correcta: " + e.getMessage());
        }
    }

    private static void testSwapJugadores(ScrimOrganizador organizador) {
        System.out.println("Probando intercambio de jugadores...");

        // Intercambiar Zeus (Top) con Canyon (Jungle)
        SwapJugadoresAccion swap = new SwapJugadoresAccion("Zeus", "Canyon");
        organizador.ejecutarAccion(swap);
        System.out.println("✓ Intercambio realizado: Zeus <-> Canyon");

        // Mostrar el nuevo estado
        System.out.println("Estado después del swap:");
        List<ParticipanteScrim> participantes = organizador.getParticipantes();
        for (ParticipanteScrim p : participantes) {
            if (p.getUserId().equals("Zeus") || p.getUserId().equals("Canyon")) {
                System.out.printf("  - %-10s: %s%n", p.getUserId(), p.getRolAsignado().getNombre());
            }
        }
    }

    private static void testDeshacerAcciones(ScrimOrganizador organizador) {
        System.out.println("Probando funcionalidad de deshacer...");

        int accionesAntes = organizador.getCantidadAccionesEnHistorial();
        System.out.println("Acciones antes de deshacer: " + accionesAntes);

        // Deshacer la última acción (el swap)
        organizador.deshacerUltimaAccion();
        System.out.println("✓ Última acción deshecha");

        int accionesDespues = organizador.getCantidadAccionesEnHistorial();
        System.out.println("Acciones después de deshacer: " + accionesDespues);

        // Verificar que Zeus y Canyon volvieron a sus roles originales
        System.out.println("Estado después de deshacer:");
        List<ParticipanteScrim> participantes = organizador.getParticipantes();
        for (ParticipanteScrim p : participantes) {
            if (p.getUserId().equals("Zeus") || p.getUserId().equals("Canyon")) {
                System.out.printf("  - %-10s: %s%n", p.getUserId(), p.getRolAsignado().getNombre());
            }
        }
    }

    private static void testValidaciones(ScrimOrganizador organizador, Usuario jugadorExistente) {
        System.out.println("Probando validaciones...");

        // Intentar invitar un jugador que ya está en el scrim
        try {
            InvitarJugadorAccion invitarDuplicado = new InvitarJugadorAccion(jugadorExistente, new RolTopLoL());
            organizador.ejecutarAccion(invitarDuplicado);
            System.out.println("✗ ERROR: Debería haber fallado - jugador duplicado");
        } catch (IllegalStateException e) {
            System.out.println("✓ Validación correcta: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Validación correcta: " + e.getMessage());
        }

        // Intentar swap con usuario inexistente
        try {
            SwapJugadoresAccion swapInvalido = new SwapJugadoresAccion("Zeus", "UsuarioInexistente");
            organizador.ejecutarAccion(swapInvalido);
            System.out.println("✗ ERROR: Debería haber fallado - usuario inexistente");
        } catch (IllegalStateException e) {
            System.out.println("✓ Validación correcta: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Validación correcta: " + e.getMessage());
        }
    }

    private static void testConfirmarScrim(ScrimOrganizador organizador) {
        System.out.println("Confirmando scrim...");

        boolean antesBloqueo = organizador.isBloqueado();
        System.out.println("Bloqueado antes de confirmar: " + antesBloqueo);

        organizador.confirmarScrim();

        boolean despuesBloqueo = organizador.isBloqueado();
        System.out.println("✓ Scrim confirmado");
        System.out.println("Bloqueado después de confirmar: " + despuesBloqueo);

        // Intentar hacer una acción después de confirmar (debería fallar)
        try {
            AsignarRolAccion accionDespuesConfirmar = new AsignarRolAccion("Zeus", new RolSupportLoL());
            organizador.ejecutarAccion(accionDespuesConfirmar);
            System.out.println("✗ ERROR: Debería haber fallado - scrim bloqueado");
        } catch (IllegalStateException e) {
            System.out.println("✓ Validación correcta: " + e.getMessage());
        }
    }

    private static void testOrganizadorService(OrganizadorService service, String scrimId, String organizadorId) {
        System.out.println("Demostrando OrganizadorService...");
        System.out.println("NOTA: El servicio crea una nueva instancia desde el scrim persistido,");
        System.out.println("      por lo que los datos pueden diferir del organizador usado anteriormente.");

        try {
            // Obtener scrims del organizador
            List<Scrim> scrims = service.obtenerScrimsDelOrganizador(organizadorId);
            System.out.println("✓ Scrims del organizador: " + scrims.size());

            // Verificar estado bloqueado
            boolean bloqueado = service.estaBloqueado(scrimId, organizadorId);
            System.out.println("✓ Estado bloqueado verificado: " + bloqueado);

            // Obtener participantes
            List<ParticipanteScrim> participantes = service.obtenerParticipantes(scrimId, organizadorId);
            System.out.println("✓ Participantes obtenidos: " + participantes.size());

            // Verificar historial
            int acciones = service.getCantidadAccionesEnHistorial(scrimId, organizadorId);
            System.out.println("✓ Acciones en historial: " + acciones);

            if (participantes.size() > 0) {
                System.out.println("Participantes encontrados en el servicio:");
                for (ParticipanteScrim p : participantes) {
                    System.out.printf("  - %-10s: %s%n", p.getUserId(),
                            p.getRolAsignado() != null ? p.getRolAsignado().getNombre() : "Sin rol");
                }
            }

        } catch (Exception e) {
            System.out.println("⚠ Service test limitado debido a estado bloqueado: " + e.getMessage());
        }
    }

    /**
     * Test de confirmación del scrim con persistencia de roles.
     */
    private static void testConfirmarScrimConPersistencia(ScrimOrganizador organizador) {
        System.out.println("Roles antes de confirmar:");
        List<ParticipanteScrim> participantesAntes = organizador.getParticipantes();
        for (ParticipanteScrim p : participantesAntes) {
            System.out.printf("  %-12s: %s%n", p.getUserId(),
                    p.getRolAsignado() != null ? p.getRolAsignado().getNombre() : "Sin rol");
        }

        System.out.println("\nConfirmando scrim...");
        organizador.confirmarScrim(); // Este método ya transferirá los roles

        System.out.println("✓ Scrim confirmado exitosamente");
        System.out.println("✓ Estado actual: " + organizador.getScrim().getEstado());
        System.out.println("✓ Bloqueado para modificaciones: " + organizador.isBloqueado());
    }

    /**
     * Test de verificación de persistencia de roles en confirmaciones.
     */
    private static void testVerificarPersistenciaRoles(Scrim scrim) {
        System.out.println("Verificando persistencia de roles...");

        // Obtener confirmaciones con roles
        List<Confirmacion> confirmacionesConRoles = scrim.getConfirmacionesConRoles();
        List<Confirmacion> todasConfirmaciones = scrim.getConfirmacionesConfirmadas();

        System.out.println("Total de confirmaciones: " + todasConfirmaciones.size());
        System.out.println("Confirmaciones con roles: " + confirmacionesConRoles.size());

        if (confirmacionesConRoles.isEmpty()) {
            System.out.println("⚠ No se encontraron confirmaciones con roles persistidos");
        } else {
            System.out.println("\n✓ ROLES PERSISTIDOS EN CONFIRMACIONES:");
            for (Confirmacion conf : confirmacionesConRoles) {
                System.out.printf("  %-12s: %s (Estado: %s)%n",
                        conf.getUserId(),
                        conf.getRolAsignado().getNombre(),
                        conf.getEstado());
            }

            // Usar el servicio de persistencia para verificar
            String resumen = RolPersistenceService.obtenerResumenRoles(scrim);
            System.out.println("\nResumen desde RolPersistenceService:");
            System.out.println(resumen);

            // Estadísticas adicionales
            boolean todosConRoles = RolPersistenceService.todosLosParticipantesTienenRoles(scrim);
            int rolesAsignados = RolPersistenceService.contarRolesAsignados(scrim);

            System.out.println("Todos los participantes tienen roles: " + (todosConRoles ? "✓ SÍ" : "✗ NO"));
            System.out.println("Total de roles asignados: " + rolesAsignados);
        }
    }
}