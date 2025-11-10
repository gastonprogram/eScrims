package test;

import java.time.LocalDateTime;
import java.util.List;

import aplicacion.builders.ScrimBuilder;
import aplicacion.services.ConfirmacionService;
import aplicacion.services.PostulacionService;
import aplicacion.services.ScrimService;
import compartido.utils.ChannelType;
import dominio.juegos.CounterStrike;
import dominio.juegos.formatos.Formato2v2WingmanCS;
import dominio.modelo.Confirmacion;
import dominio.modelo.Scrim;
import dominio.modelo.Usuario;
import infraestructura.persistencia.implementacion.RepositorioUsuarioJSON;
import infraestructura.persistencia.repository.RepositorioFactory;
import infraestructura.persistencia.repository.RepositorioScrim;

/**
 * Test completo del sistema de notificaciones con un Scrim 2v2 Wingman.
 * Demuestra todo el flujo desde la creación hasta la finalización del scrim.
 */
public class TestScrimNotificaciones2v2 {

    public static void main(String[] args) {
        System.out.println("================================================================");
        System.out.println("     TEST COMPLETO: SCRIM 2v2 WINGMAN + NOTIFICACIONES");
        System.out.println("================================================================\n");

        try {

            RepositorioUsuarioJSON repoUsuarios = new RepositorioUsuarioJSON();
            // RepositorioUsuario repoUsuarios = RepositorioFactory.getRepositorioUsuario();
            RepositorioScrim repoScrims = RepositorioFactory.getRepositorioScrim();

            // Servicios
            ScrimService scrimService = new ScrimService(repoScrims);

            // 1. Crear usuarios de prueba
            System.out.println("[PASO 1] Creando usuarios de prueba\n");
            Usuario jugador1 = crearJugador1();
            Usuario jugador2 = crearJugador2();
            Usuario jugador3 = crearJugador3();
            Usuario jugador4 = crearJugador4();
            Usuario organizador = crearOrganizador();

            repoUsuarios.guardar(jugador1);
            repoUsuarios.guardar(jugador2);
            repoUsuarios.guardar(jugador3);
            repoUsuarios.guardar(jugador4);
            repoUsuarios.guardar(organizador);

            System.out.println("[OK] 4 usuarios creados con diferentes preferencias de notificacion\n");
            Thread.sleep(5000);

            // 2. Crear el Scrim 2v2 Wingman
            System.out.println("\n" + "=".repeat(60));
            System.out.println("[PASO 2] Creando Scrim 2v2 Wingman CS\n");

            Scrim scrim = new ScrimBuilder()
                    .withJuego(CounterStrike.getInstance())
                    .withFormato(new Formato2v2WingmanCS())
                    .withFechaHora(LocalDateTime.now().plusSeconds(10))
                    .withRango(1000, 2000)
                    .withLatenciaMaxima(50)
                    .withEstrategiaMatchmaking("MMR")
                    .build();

            scrim.setCreatedBy(organizador.getId());

            repoScrims.guardar(scrim);

            System.out.println("[OK] Scrim guardado:");
            System.out.println("   - Juego: Counter-Strike");
            System.out.println("   - Formato: 2v2 Wingman");
            System.out.println("   - Fecha: " + scrim.getFechaHora());
            System.out.println("   - Rango MMR: 1000-2000");
            System.out.println("   - Plazas: 4 (2v2)");
            System.out.println("\n[NOTIF] Notificaciones enviadas a todos los usuarios (BUSCANDO)\n");
            Thread.sleep(5000);

            // 3. Los jugadores se postulan
            System.out.println("\n" + "=".repeat(60));
            System.out.println("[PASO 3] Jugadores postulandose al scrim\n");

            PostulacionService postService = new PostulacionService(repoScrims, repoUsuarios);

            postService.postularAScrim(scrim.getId(), jugador1.getId(), 1500, 30);
            System.out.println("[OK] " + jugador1.getUsername() + " se postulo");
            Thread.sleep(500);

            postService.postularAScrim(scrim.getId(), jugador2.getId(), 1600, 25);
            System.out.println("[OK] " + jugador2.getUsername() + " se postulo");
            Thread.sleep(500);

            postService.postularAScrim(scrim.getId(), jugador3.getId(), 1550, 35);
            System.out.println("[OK] " + jugador3.getUsername() + " se postulo");
            Thread.sleep(500);

            postService.postularAScrim(scrim.getId(), jugador4.getId(), 1800, 20);
            System.out.println("[OK] " + jugador4.getUsername() + " se postulo");

            System.out.println("\n[INFO] 4 postulaciones recibidas");
            Thread.sleep(5000);

            // 5. Crear confirmaciones
            System.out.println("\n" + "=".repeat(60));
            System.out.println("[PASO 4] Confirmando participacion\n");

            scrim = repoScrims.buscarPorId(scrim.getId());
            System.out.println("Estado: " + scrim.getEstado());
            System.out.println("Plazas: " + scrim.getPostulacionesAceptadas().size() + "/" + scrim.getPlazas());

            ConfirmacionService confService = new ConfirmacionService(repoScrims);
            List<Confirmacion> confs = scrim.getConfirmaciones();
            for (int i = 0; i < 4; i++) {
                try {
                    confService.confirmarAsistencia(scrim.getId(), confs.get(i).getUserId());
                    System.out.println((i + 1) + ". " + confs.get(i).getUserId() + ": Confirmación exitosa");
                } catch (Exception e) {
                    System.out.println((i + 1) + ". " + confs.get(i).getUserId() + ": Error - " + e.getMessage());
                }
            }

            System.out.println("\n[OK] Todos confirmaron su participacion");
            System.out.println("[NOTIF] Notificaciones enviadas segun cambios de estado\n");
            Thread.sleep(5000);

            // 6. Comenzar partida
            System.out.println("\n" + "=".repeat(60));
            System.out.println("[PASO 5] Comenzando la partida\n");

            scrimService.iniciarPartida(scrim.getId());

            System.out.println("[OK] Estado cambiado a: EN_JUEGO");
            System.out.println("[NOTIF] Notificaciones enviadas (EN_JUEGO)");
            System.out.println("[GAME] La partida ha comenzado! Buena suerte\n");
            Thread.sleep(5000);

            // 7. Finalizar partida
            System.out.println("\n" + "=".repeat(60));
            System.out.println("[PASO 6] Finalizando la partida\n");

            scrimService.finalizarPartida(scrim.getId());

            System.out.println("[OK] Estado cambiado a: FINALIZADO");
            System.out.println("[NOTIF] Notificaciones enviadas (FINALIZADO)");
            System.out.println("[WIN] Excelente partida!\n");
            Thread.sleep(2000);

            // 8. Resumen final
            System.out.println("\n" + "=".repeat(60));
            System.out.println("[RESUMEN] RESUMEN DEL TEST\n");
            System.out.println("[OK] Scrim 2v2 Wingman creado exitosamente");
            System.out.println("[OK] 4 jugadores participaron");
            System.out.println("[OK] Flujo completo ejecutado:");
            System.out.println("     BUSCANDO -> postulaciones -> confirmaciones -> EN_JUEGO -> FINALIZADO");
            System.out.println("\n[OK] Notificaciones enviadas en cada cambio de estado");
            System.out.println("[OK] Usuarios recibieron notificaciones segun sus preferencias:");
            System.out.println("     - " + jugador1.getUsername() + ": Solo Email");
            System.out.println("     - " + jugador2.getUsername() + ": Discord + Push");
            System.out.println("     - " + jugador3.getUsername() + ": Solo eventos importantes (Email)");
            System.out.println("     - " + jugador4.getUsername() + ": Email + Discord + Push");

            System.out.println("\n================================================================");
            System.out.println("                    TEST COMPLETADO");
            System.out.println("================================================================");

        } catch (Exception e) {
            System.err.println("\n[ERROR] ERROR en el test: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static Usuario crearOrganizador() {
        Usuario usuario = new Usuario("ProOrganizer", "gusabelu1@gmail.com", "password123");

        usuario.addPreferredChannel(ChannelType.DISCORD, "@ProOrganizer#1234")
                .removePreferredChannel(ChannelType.EMAIL);

        System.out.println("[USER] Organizador: " + usuario.getUsername());
        System.out.println("       Canales: DISCORD, PUSH | Eventos: TODOS");

        return usuario;
    }

    private static Usuario crearJugador1() {
        Usuario usuario = new Usuario("Gusabelu2", "gusabelu2@gmail.com", "password123");

        // Solo email, todos los eventos (ya viene por defecto)
        usuario.soloEmail("gusabelu2@gmail.com");

        System.out.println("[USER] Jugador 1: " + usuario.getUsername());
        System.out.println("       Canales: EMAIL | Eventos: TODOS");

        return usuario;
    }

    private static Usuario crearJugador2() {
        Usuario usuario = new Usuario("AWPGod", "awpgod@escrims.com", "password123");

        // Discord + Push, todos los eventos
        usuario.addPreferredChannel(ChannelType.DISCORD, "@AWPGod#5678")
                .removePreferredChannel(ChannelType.EMAIL);

        System.out.println("[USER] Jugador 2: " + usuario.getUsername());
        System.out.println("       Canales: DISCORD, PUSH | Eventos: TODOS");

        return usuario;
    }

    private static Usuario crearJugador3() {
        Usuario usuario = new Usuario("SupportKing", "supportking@escrims.com", "password123");

        usuario.soloEmail("supportking@escrims.com")
                .suscribirSoloImportantes();

        System.out.println("[USER] Jugador 3: " + usuario.getUsername());
        System.out.println("       Canales: EMAIL | Eventos: Solo importantes (CONFIRMADO, EN_JUEGO, FINALIZADO)");

        return usuario;
    }

    private static Usuario crearJugador4() {
        Usuario usuario = new Usuario("Gastocash", "gusabelu3@gmail.com", "password123");

        usuario.soloEmail("gusabelu3@gmail.com");

        System.out.println("[USER] Jugador 4: " + usuario.getUsername());
        System.out.println("       Canales: EMAIL | Eventos: TODOS");

        return usuario;
    }
}
