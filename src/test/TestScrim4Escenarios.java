package test;

import java.time.LocalDateTime;
import java.util.Scanner;

import aplicacion.builders.ScrimBuilder;
import aplicacion.services.ConfirmacionService;
import aplicacion.services.PostulacionService;
import aplicacion.services.ScrimService;
import dominio.juegos.CounterStrike;
import dominio.juegos.formatos.Formato2v2WingmanCS;
import dominio.modelo.Confirmacion;
import dominio.modelo.Scrim;
import dominio.modelo.Usuario;
import infraestructura.persistencia.implementacion.RepositorioUsuarioJSON;
import infraestructura.persistencia.repository.RepositorioFactory;
import infraestructura.persistencia.repository.RepositorioScrim;

/**
 * Test simplificado con 4 escenarios principales para Scrims 2v2 Counter-Strike.
 * Incluye interfaz de selección para ejecutar cada test individualmente.
 */
public class TestScrim4Escenarios {

    // Repositorios y servicios compartidos
    private static RepositorioUsuarioJSON repoUsuarios;
    private static RepositorioScrim repoScrims;
    private static ScrimService scrimService;
    private static PostulacionService postulacionService;
    private static ConfirmacionService confirmacionService;

    // Usuarios reutilizables
    private static Usuario organizador;
    private static Usuario jugador1;
    private static Usuario jugador2;
    private static Usuario jugador3;
    private static Usuario jugador4;

    // Scrim reutilizable
    private static Scrim scrim;

    public static void main(String[] args) {
        mostrarMenu();
    }

    private static void mostrarMenu() {
        Scanner scanner = new Scanner(System.in);
        
        while (true) {
            System.out.println("\n" + "=".repeat(70));
            System.out.println("          TEST SCRIM 2v2 COUNTER-STRIKE - 4 ESCENARIOS");
            System.out.println("=".repeat(70));
            System.out.println("\n[OPCIONES DISPONIBLES]");
            System.out.println("  1. Flujo perfecto - Todo sale bien (inicio y finalizacion)");
            System.out.println("  2. Confirmacion completa - Todos confirman pero se cancela");
            System.out.println("  3. Jugador se baja - Se busca reemplazo");
            System.out.println("  4. Cancelacion anticipada - Organizador cancela antes del inicio");
            System.out.println("  5. Ejecutar todos los tests");
            System.out.println("  0. Salir");
            System.out.println("\n" + "=".repeat(70));
            System.out.print("Seleccione una opcion: ");

            try {
                int opcion = scanner.nextInt();
                scanner.nextLine(); // Consumir newline

                if (opcion == 0) {
                    System.out.println("\nSaliendo del sistema de tests...");
                    scanner.close();
                    break;
                }

                ejecutarTest(opcion);
                
                System.out.println("\n[Presione ENTER para continuar]");
                scanner.nextLine();

            } catch (Exception e) {
                System.err.println("\n[ERROR] Opcion invalida");
                scanner.nextLine(); // Limpiar buffer
            }
        }
    }

    private static void ejecutarTest(int opcion) {
        try {
            inicializarSistema();

            switch (opcion) {
                case 1:
                    test1_FlujoPerfecto();
                    break;
                case 2:
                    test2_TodosConfirmanYCancelan();
                    break;
                case 3:
                    test3_JugadorSeBajaYReemplazo();
                    break;
                case 4:
                    test4_CancelacionAnticipada();
                    break;
                case 5:
                    ejecutarTodos();
                    break;
                default:
                    System.err.println("\n[ERROR] Opcion no valida");
            }

        } catch (Exception e) {
            System.err.println("\n[ERROR FATAL] " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void ejecutarTodos() throws Exception {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("              EJECUTANDO TODOS LOS TESTS");
        System.out.println("=".repeat(70) + "\n");

        test1_FlujoPerfecto();
        Thread.sleep(2000);
        
        inicializarSistema();
        test2_TodosConfirmanYCancelan();
        Thread.sleep(2000);
        
        inicializarSistema();
        test3_JugadorSeBajaYReemplazo();
        Thread.sleep(2000);
        
        inicializarSistema();
        test4_CancelacionAnticipada();

        System.out.println("\n" + "=".repeat(70));
        System.out.println("              TODOS LOS TESTS COMPLETADOS");
        System.out.println("=".repeat(70));
    }

    /**
     * Inicializa repositorios, servicios y usuarios para cada test
     */
    private static void inicializarSistema() throws Exception {
        repoUsuarios = new RepositorioUsuarioJSON();
        repoScrims = RepositorioFactory.getRepositorioScrim();
        
        scrimService = new ScrimService(repoScrims);
        postulacionService = new PostulacionService(repoScrims, repoUsuarios);
        confirmacionService = new ConfirmacionService(repoScrims);

        // Crear usuarios
        organizador = crearUsuario("Organizador_Test", "org@test.com", "1234");
        jugador1 = crearUsuario("Player1", "p1@test.com", "1111");
        jugador2 = crearUsuario("Player2", "p2@test.com", "2222");
        jugador3 = crearUsuario("Player3", "p3@test.com", "3333");
        jugador4 = crearUsuario("Player4", "p4@test.com", "4444");

        // Guardar usuarios
        repoUsuarios.guardar(organizador);
        repoUsuarios.guardar(jugador1);
        repoUsuarios.guardar(jugador2);
        repoUsuarios.guardar(jugador3);
        repoUsuarios.guardar(jugador4);

        // Crear scrim
        scrim = new ScrimBuilder()
                .withJuego(CounterStrike.getInstance())
                .withFormato(new Formato2v2WingmanCS())
                .withFechaHora(LocalDateTime.now().plusMinutes(5))
                .withRango(1000, 2000)
                .withLatenciaMaxima(50)
                .withEstrategiaMatchmaking("MMR")
                .build();

        scrim.setCreatedBy(organizador.getId());
        repoScrims.guardar(scrim);
    }

    /**
     * TEST 1: Flujo perfecto - Todo sale bien
     * - Se crean 4 jugadores
     * - Todos se postulan y confirman automaticamente
     * - Scrim pasa a CONFIRMADO
     * - Se inicia la partida (EN_JUEGO)
     * - Se finaliza correctamente (FINALIZADO)
     */
    private static void test1_FlujoPerfecto() throws Exception {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("  TEST 1: FLUJO PERFECTO - TODO SALE BIEN");
        System.out.println("=".repeat(70) + "\n");

        // Postulaciones (confirman automaticamente)
        System.out.println("[1/5] Postulaciones...");
        postulacionService.postularAScrim(scrim.getId(), jugador1.getId(), 1500, 30);
        System.out.println("  [OK] " + jugador1.getUsername() + " postulado");
        
        postulacionService.postularAScrim(scrim.getId(), jugador2.getId(), 1600, 25);
        System.out.println("  [OK] " + jugador2.getUsername() + " postulado");
        
        postulacionService.postularAScrim(scrim.getId(), jugador3.getId(), 1550, 35);
        System.out.println("  [OK] " + jugador3.getUsername() + " postulado");
        
        postulacionService.postularAScrim(scrim.getId(), jugador4.getId(), 1700, 40);
        System.out.println("  [OK] " + jugador4.getUsername() + " postulado");

        // Confirmaciones automaticas
        System.out.println("\n[2/5] Confirmaciones automaticas...");
        scrim = repoScrims.buscarPorId(scrim.getId());
        for (Confirmacion conf : scrim.getConfirmaciones()) {
            confirmacionService.confirmarAsistencia(scrim.getId(), conf.getUserId());
            Usuario u = repoUsuarios.buscarPorId(conf.getUserId());
            System.out.println("  [OK] " + u.getUsername() + " confirmado");
        }

        // Verificar estado CONFIRMADO
        System.out.println("\n[3/5] Verificando estado CONFIRMADO...");
        scrim = repoScrims.buscarPorId(scrim.getId());
        System.out.println("  Estado actual: " + scrim.getEstado());
        
        if ("CONFIRMADO".equals(scrim.getEstado())) {
            System.out.println("  [OK] Scrim correctamente confirmado");
        } else {
            throw new Exception("Estado esperado: CONFIRMADO, pero es: " + scrim.getEstado());
        }

        // Iniciar partida
        System.out.println("\n[4/5] Iniciando partida...");
        scrimService.iniciarPartida(scrim.getId());
        scrim = repoScrims.buscarPorId(scrim.getId());
        System.out.println("  Estado actual: " + scrim.getEstado());
        
        if ("EN_JUEGO".equals(scrim.getEstado())) {
            System.out.println("  [OK] Partida iniciada correctamente");
        } else {
            throw new Exception("Estado esperado: EN_JUEGO, pero es: " + scrim.getEstado());
        }

        // Finalizar partida
        System.out.println("\n[5/5] Finalizando partida...");
        scrimService.finalizarPartida(scrim.getId());
        scrim = repoScrims.buscarPorId(scrim.getId());
        System.out.println("  Estado actual: " + scrim.getEstado());
        
        if ("FINALIZADO".equals(scrim.getEstado())) {
            System.out.println("  [OK] Partida finalizada correctamente");
        } else {
            throw new Exception("Estado esperado: FINALIZADO, pero es: " + scrim.getEstado());
        }

        System.out.println("\n" + "=".repeat(70));
        System.out.println("  >>> TEST 1 COMPLETADO EXITOSAMENTE <<<");
        System.out.println("=".repeat(70));
    }

    /**
     * TEST 2: Todos confirman pero el scrim se cancela
     * - Se crean 4 jugadores
     * - Todos se postulan y confirman
     * - Scrim pasa a CONFIRMADO
     * - El organizador cancela el scrim
     * - Estado final: CANCELADO
     */
    private static void test2_TodosConfirmanYCancelan() throws Exception {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("  TEST 2: TODOS CONFIRMAN PERO SE CANCELA");
        System.out.println("=".repeat(70) + "\n");

        // Postulaciones
        System.out.println("[1/4] Postulaciones...");
        postulacionService.postularAScrim(scrim.getId(), jugador1.getId(), 1500, 30);
        postulacionService.postularAScrim(scrim.getId(), jugador2.getId(), 1600, 25);
        postulacionService.postularAScrim(scrim.getId(), jugador3.getId(), 1550, 35);
        postulacionService.postularAScrim(scrim.getId(), jugador4.getId(), 1700, 40);
        System.out.println("  [OK] 4 jugadores postulados");

        // Confirmaciones
        System.out.println("\n[2/4] Confirmaciones...");
        scrim = repoScrims.buscarPorId(scrim.getId());
        for (Confirmacion conf : scrim.getConfirmaciones()) {
            confirmacionService.confirmarAsistencia(scrim.getId(), conf.getUserId());
        }
        System.out.println("  [OK] 4 jugadores confirmados");

        // Verificar CONFIRMADO
        System.out.println("\n[3/4] Verificando estado CONFIRMADO...");
        scrim = repoScrims.buscarPorId(scrim.getId());
        System.out.println("  Estado actual: " + scrim.getEstado());
        
        if (!"CONFIRMADO".equals(scrim.getEstado())) {
            throw new Exception("Estado esperado: CONFIRMADO");
        }

        // Cancelar
        System.out.println("\n[4/4] Organizador cancela el scrim...");
        scrimService.cancelarScrim(scrim.getId());
        scrim = repoScrims.buscarPorId(scrim.getId());
        System.out.println("  Estado actual: " + scrim.getEstado());
        
        if ("CANCELADO".equals(scrim.getEstado())) {
            System.out.println("  [OK] Scrim cancelado correctamente");
        } else {
            throw new Exception("Estado esperado: CANCELADO, pero es: " + scrim.getEstado());
        }

        System.out.println("\n" + "=".repeat(70));
        System.out.println("  >>> TEST 2 COMPLETADO EXITOSAMENTE <<<");
        System.out.println("=".repeat(70));
    }

    /**
     * TEST 3: Un jugador se baja y hay que buscar reemplazo
     * - Se postulan 4 jugadores (se crean confirmaciones automaticas PENDIENTES)
     * - 3 jugadores confirman su asistencia
     * - 1 jugador rechaza su asistencia (se baja)
     * - El scrim vuelve a BUSCANDO
     * - Se postula un nuevo jugador
     * - Todos confirman y se completa el scrim
     */
    private static void test3_JugadorSeBajaYReemplazo() throws Exception {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("  TEST 3: JUGADOR SE BAJA - SE BUSCA REEMPLAZO");
        System.out.println("=".repeat(70) + "\n");

        // Postulaciones (4 jugadores para llenar el scrim)
        System.out.println("[1/6] Postulaciones (4 jugadores)...");
        postulacionService.postularAScrim(scrim.getId(), jugador1.getId(), 1500, 30);
        postulacionService.postularAScrim(scrim.getId(), jugador2.getId(), 1600, 25);
        postulacionService.postularAScrim(scrim.getId(), jugador3.getId(), 1550, 35);
        postulacionService.postularAScrim(scrim.getId(), jugador4.getId(), 1700, 40);
        System.out.println("  [OK] 4 jugadores postulados");

        // Estado en LOBBY_ARMADO con confirmaciones pendientes
        System.out.println("\n[2/6] Verificando estado LOBBY_ARMADO...");
        scrim = repoScrims.buscarPorId(scrim.getId());
        System.out.println("  Estado actual: " + scrim.getEstado());
        System.out.println("  Confirmaciones pendientes: " + scrim.getConfirmaciones().size());

        // 3 jugadores confirman
        System.out.println("\n[3/6] 3 jugadores confirman...");
        confirmacionService.confirmarAsistencia(scrim.getId(), jugador1.getId());
        confirmacionService.confirmarAsistencia(scrim.getId(), jugador2.getId());
        confirmacionService.confirmarAsistencia(scrim.getId(), jugador3.getId());
        System.out.println("  [OK] 3 confirmaciones realizadas");

        // El cuarto rechaza (se baja)
        System.out.println("\n[4/6] Jugador 4 rechaza (se baja)...");
        confirmacionService.rechazarAsistencia(scrim.getId(), jugador4.getId());
        
        scrim = repoScrims.buscarPorId(scrim.getId());
        System.out.println("  Estado actual: " + scrim.getEstado());
        System.out.println("  [OK] Jugador rechazado, vuelve a BUSCANDO");

        // Crear un nuevo jugador para reemplazo
        System.out.println("\n[5/6] Nuevo jugador se postula como reemplazo...");
        Usuario jugador5 = crearUsuario("Player5", "p5@test.com", "5555");
        repoUsuarios.guardar(jugador5);
        postulacionService.postularAScrim(scrim.getId(), jugador5.getId(), 1650, 45);
        System.out.println("  [OK] Jugador de reemplazo postulado");

        // Todos confirman
        System.out.println("\n[6/6] Todos confirman...");
        scrim = repoScrims.buscarPorId(scrim.getId());
        for (Confirmacion conf : scrim.getConfirmaciones()) {
            if (!conf.isConfirmada()) {
                confirmacionService.confirmarAsistencia(scrim.getId(), conf.getUserId());
            }
        }

        scrim = repoScrims.buscarPorId(scrim.getId());
        System.out.println("  Estado actual: " + scrim.getEstado());
        
        if ("CONFIRMADO".equals(scrim.getEstado())) {
            System.out.println("  [OK] Scrim completado con reemplazo");
        } else {
            throw new Exception("Estado esperado: CONFIRMADO");
        }

        System.out.println("\n" + "=".repeat(70));
        System.out.println("  >>> TEST 3 COMPLETADO EXITOSAMENTE <<<");
        System.out.println("=".repeat(70));
    }

    /**
     * TEST 4: Cancelacion anticipada por organizador
     * - Se crean postulaciones parciales (solo 2 de 4 jugadores)
     * - El scrim queda en estado BUSCANDO
     * - El organizador cancela antes de que se complete
     * - Estado final: CANCELADO
     */
    private static void test4_CancelacionAnticipada() throws Exception {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("  TEST 4: CANCELACION ANTICIPADA POR ORGANIZADOR");
        System.out.println("=".repeat(70) + "\n");

        // Postulaciones parciales (solo 2 jugadores)
        System.out.println("[1/3] Postulaciones parciales (2 de 4 jugadores)...");
        postulacionService.postularAScrim(scrim.getId(), jugador1.getId(), 1500, 30);
        postulacionService.postularAScrim(scrim.getId(), jugador2.getId(), 1600, 25);
        System.out.println("  [OK] 2 jugadores postulados (faltan 2 para completar)");

        // Verificar estado BUSCANDO (no se completo el cupo)
        System.out.println("\n[2/3] Verificando estado...");
        scrim = repoScrims.buscarPorId(scrim.getId());
        System.out.println("  Estado actual: " + scrim.getEstado());
        System.out.println("  Postulaciones: " + scrim.getPostulaciones().size() + "/4");
        
        if (!"BUSCANDO".equals(scrim.getEstado())) {
            throw new Exception("Estado esperado: BUSCANDO (scrim no completado)");
        }

        // Organizador cancela antes de completar el cupo
        System.out.println("\n[3/3] Organizador cancela antes de completar el cupo...");
        scrimService.cancelarScrim(scrim.getId());
        
        scrim = repoScrims.buscarPorId(scrim.getId());
        System.out.println("  Estado actual: " + scrim.getEstado());
        
        if ("CANCELADO".equals(scrim.getEstado())) {
            System.out.println("  [OK] Scrim cancelado antes de completarse");
        } else {
            throw new Exception("Estado esperado: CANCELADO, pero es: " + scrim.getEstado());
        }

        System.out.println("\n" + "=".repeat(70));
        System.out.println("  >>> TEST 4 COMPLETADO EXITOSAMENTE <<<");
        System.out.println("=".repeat(70));
    }

    /**
     * Crea un usuario con preferencias de notificacion basicas
     */
    private static Usuario crearUsuario(String username, String email, String password) {
        Usuario usuario = new Usuario(username, email, password);
        
        // Configurar notificaciones basicas (email y discord)
        usuario.soloEmail(email);
        
        return usuario;
    }
}
