package test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import org.w3c.dom.css.Counter;

import aplicacion.services.AuthService;
import aplicacion.services.ConfirmacionService;
import aplicacion.services.EstadisticasService;
import aplicacion.services.MatchmakerService;
import aplicacion.services.PostulacionService;
import aplicacion.services.ScrimService;
import aplicacion.services.UsuarioService;
import dominio.estadisticas.Comentario;
import dominio.estadisticas.EstadisticasJugador;
import dominio.estadisticas.EstadisticasScrim;
import dominio.estadisticas.ReporteConducta.Gravedad;
import dominio.estadisticas.ReporteConducta.TipoReporte;
import dominio.modelo.Confirmacion;
import dominio.modelo.Scrim;
import dominio.modelo.Usuario;
import infraestructura.persistencia.implementacion.RepositorioUsuarioJSON;
import infraestructura.persistencia.repository.RepositorioFactory;
import infraestructura.persistencia.repository.RepositorioScrim;
import infraestructura.matchmaking.strategies.ByMMRStrategy;
import dominio.juegos.CounterStrike;
import dominio.juegos.formatos.Formato5v5CompetitiveCS;

public class TestCreacionScrimPositivo {

    private static RepositorioUsuarioJSON repoUsuarios = new RepositorioUsuarioJSON();
    private static RepositorioScrim repoScrims = RepositorioFactory.getRepositorioScrim();
    private static AuthService authService = new AuthService(repoUsuarios);
    private static ScrimService scrimService = new ScrimService(repoScrims);
    private static PostulacionService postService = new PostulacionService(repoScrims, repoUsuarios);
    private static UsuarioService usuarioService = new UsuarioService(repoUsuarios);
    private static ConfirmacionService confService = new ConfirmacionService(repoScrims);
    private static EstadisticasService estadisticasService = new EstadisticasService();
    private static Random random = new Random();

    public static void main(String[] args) {

        try {

            System.out.println("\n============================================================");
            System.out.println("        🎮 TEST CREACIÓN DE SCRIM POSITIVO 🎮");
            System.out.println("============================================================\n");

            // creacion de jugadores y organizador
            System.out.println("📋 Fase 1: Creación de usuarios...");

            Usuario organizador = crearUsuario("organizador", "organizador@escrims.com", "password123",
                    "Counter-Strike", 60);
            Usuario jugador1 = crearUsuario("juanli", "juanli@escrims.com", "password123", "Counter-Strike", 50);
            Usuario jugador2 = crearUsuario("gusabelu", "gusabelu@escrims.com", "password123", "Counter-Strike", 80);
            Usuario jugador3 = crearUsuario("mariano78", "mariano78@escrims.com", "password123", "Counter-Strike", 77);
            Usuario jugador4 = crearUsuario("PeppoCS", "PeppoCS@escrims.com", "password123", "Counter-Strike", 75);
            Usuario jugador5 = crearUsuario("matiaslol", "matiaslol@escrims.com", "password123", "Counter-Strike", 100);
            Usuario jugador6 = crearUsuario("LeftWing terror", "LeftWingterror@escrims.com", "password123",
                    "Counter-Strike",
                    70);
            Usuario jugador7 = crearUsuario("Gastocash", "Gastocash@escrims.com", "password123", "Counter-Strike", 50);
            Usuario jugador8 = crearUsuario("AWPro", "AWPro@escrims.com", "password123", "Counter-Strike", 45);
            Usuario jugador9 = crearUsuario("santucho123", "santucho123@escrims.com", "password123", "Counter-Strike",
                    30);
            Usuario jugador10 = crearUsuario("killer3000", "killer3000@escrims.com", "password123", "Counter-Strike",
                    55);
            Usuario jugador11 = crearUsuario("noobmaster", "noobmaster@escrims.com", "password123", "Counter-Strike",
                    40);
            Usuario jugador12 = crearUsuario("proGamer", "proGamer@escrims.com", "password123", "Counter-Strike", 50);
            Usuario jugador13 = crearUsuario("juanceto01", "juanceto@escrims.com", "password123", "Counter-Strike", 60);

            List<Usuario> jugadores = List.of(
                    jugador1, jugador2, jugador3, jugador4, jugador5,
                    jugador6, jugador7, jugador8, jugador9, jugador10,
                    jugador11, jugador12, jugador13);

            System.out.println("- " + (jugadores.size() + 1) + " usuarios creados exitosamente\n");

            // configuracion latencia para los jugadores de forma aleatoria entre 20 y 80 ms
            System.out.println("🌐 Fase 2: Configurando latencias aleatorias...");
            for (Usuario jugador : jugadores) {
                int latenciaAleatoria = 20 + random.nextInt(61);
                jugador.setLatenciaPromedio(latenciaAleatoria);
            }
            System.out.println("Latencias configuradas\n");

            // creacion de scrim con estrategia de matchmaking, CS formato 5v5 competitivo
            System.out.println("- Fase 3: Creando scrim...");

            Scrim scrim = scrimService.crearScrimConEstrategia(
                    CounterStrike.getInstance(),
                    new Formato5v5CompetitiveCS(),
                    LocalDateTime.now().plusSeconds(1),
                    25,
                    81,
                    80,
                    "MMR",
                    organizador.getId());

            System.out.println("Scrim creado: " + scrim.getId());
            System.out.println("   - Fecha: " + scrim.getFechaHora());
            System.out.println("   - Juego: " + scrim.getJuego().getNombre());
            System.out.println("   - Formato: " + scrim.getFormato().getFormatName() + "\n");

            // usar matchmaking service para seleccionar jugadores segun la estrategia
            System.out.println("Fase 4: Seleccionando jugadores con matchmaking...");
            MatchmakerService matchmakerService = new MatchmakerService(new ByMMRStrategy());
            List<Usuario> jugadoresMatchmaking = matchmakerService.seleccionarJugadores(jugadores, scrim);
            System.out.println("- " + jugadoresMatchmaking.size() + " jugadores seleccionados\n");

            // postulacion de jugadores al scrim y armar lobby completa
            System.out.println("Fase 5: Procesando postulaciones...");
            String nombreJuegoCS = scrim.getJuego().getNombre();

            for (Usuario jugador : jugadoresMatchmaking) {
                int rangoJugador = jugador.getRangoPorJuego().get(nombreJuegoCS);
                int latenciaJugador = jugador.getLatenciaPromedio();

                postService.postularAScrim(scrim.getId(), jugador.getId(), rangoJugador, latenciaJugador);
                System.out.println("   ✓ " + jugador.getUsername() + " (Rango: " + rangoJugador + ", Latencia: "
                        + latenciaJugador + "ms)");
            }
            System.out.println("Postulaciones completadas\n");

            // confirmacion de todos los jugadores
            System.out.println("Fase 6: Procesando confirmaciones...");
            List<Confirmacion> confirmaciones = scrim.getConfirmaciones();
            for (int i = 0; i < scrim.getPlazas(); i++) {
                try {
                    confService.confirmarAsistencia(scrim.getId(), confirmaciones.get(i).getUserId());
                    System.out.println("   ✓ Confirmación " + (i + 1) + "/" + scrim.getPlazas());
                } catch (Exception e) {
                    System.out.println("   ✗ Error en confirmación " + (i + 1) + ": " + e.getMessage());
                }
            }
            System.out.println("Todas las confirmaciones procesadas\n");

            // inicio del scrim
            System.out.println("Fase 7: Iniciando scrim...");
            scrimService.iniciarPartida(scrim.getId());
            System.out.println("Scrim iniciado - Estado: EN_JUEGO\n");

            // simular duracion del scrim
            System.out.println("⏱️  Simulando duración de la partida...");
            Thread.sleep(3000);
            System.out.println("Partida en progreso...\n");

            // finalizar scrim
            System.out.println("🏁 Fase 8: Finalizando scrim...");
            scrimService.finalizarPartida(scrim.getId());
            Thread.sleep(2000);
            System.out.println("Scrim finalizado - Estado: FINALIZADO\n");

            // mostrar estadisticas del scrim
            System.out.println("============================================================");
            System.out.println("        ESTADÍSTICAS DEL SCRIM ");
            System.out.println("============================================================\n");

            EstadisticasScrim estadisticas = estadisticasService.obtenerEstadisticasParaScrim(scrim);

            if (estadisticas != null) {
                // Información general del scrim
                System.out.println("INFORMACIÓN GENERAL:");
                System.out.println("   Scrim ID: " + estadisticas.getScrimId());
                System.out.println("   Duración: " + estadisticas.getDuracionMinutos() + " minutos");
                System.out.println("   Fecha inicio: " + estadisticas.getFechaHoraInicio());

                // Mostrar descripción de la partida (equipos, ganador, etc.)
                if (estadisticas.getDescripcionPartida() != null) {
                    System.out.println("\nRESUMEN DE LA PARTIDA:");
                    System.out.println(estadisticas.getDescripcionPartida());
                }

                // Ranking por KDA
                System.out.println("\n🏆 RANKING POR KDA:");
                System.out.println("┌─────┬─────────────────────┬───────┬────────┬─────────┐");
                System.out.println("│ Pos │ Jugador             │ Kills │ Deaths │ Assists │");
                System.out.println("├─────┼─────────────────────┼───────┼────────┼─────────┤");

                List<EstadisticasJugador> rankingKDA = estadisticasService.obtenerRankingPorKDA(scrim.getId());
                int posicion = 1;
                for (EstadisticasJugador stats : rankingKDA) {
                    System.out.printf("│ %3d │ %-19s │ %5d │ %6d │ %7d │%n",
                            posicion,
                            stats.getJugadorId().substring(0, Math.min(19, stats.getJugadorId().length())),
                            stats.getKills(),
                            stats.getDeaths(),
                            stats.getAssists());
                    posicion++;
                }
                System.out.println("└─────┴─────────────────────┴───────┴────────┴─────────┘");

                // Ranking por puntuación
                System.out.println("\nRANKING POR PUNTUACIÓN:");
                System.out.println("┌─────┬─────────────────────┬──────────────┐");
                System.out.println("│ Pos │ Jugador             │  Puntuación  │");
                System.out.println("├─────┼─────────────────────┼──────────────┤");

                List<EstadisticasJugador> rankingPuntuacion = estadisticasService
                        .obtenerRankingPorPuntuacion(scrim.getId());
                posicion = 1;
                for (EstadisticasJugador stats : rankingPuntuacion) {

                    System.out.printf("│ %3d │ %-19s │ %12d │%n",
                            posicion,
                            stats.getJugadorId().substring(0, Math.min(19, stats.getJugadorId().length())),
                            stats.getPuntuacion());
                    posicion++;
                }
                System.out.println("└─────┴─────────────────────┴──────────────┘");

                // MVP
                EstadisticasJugador mvp = estadisticasService.obtenerMVP(scrim.getId());
                if (mvp != null) {
                    System.out.println("\nMVP DE LA PARTIDA:");
                    System.out.println("   Jugador: " + mvp.getJugadorId());
                    System.out.println("   Puntuación: " + mvp.getPuntuacion());
                    System.out.println("   K/D/A: " + mvp.getKills() + "/" + mvp.getDeaths() + "/" + mvp.getAssists());
                }

                System.out.println("\nEstadísticas cargadas exitosamente");
            } else {
                System.out.println("No se encontraron estadísticas para este scrim");
            }

            // generar dos comentarios a jugadores y mostrarlos, uno positivo y otro
            // negativo

            System.out.println("============================================================");
            System.out.println("        COMENTARIOS DE LA PARTIDA");
            System.out.println("============================================================\n");

            estadisticasService.crearComentario(jugadoresMatchmaking.get(4).getId(),
                    scrim.getId(),
                    "El manejo del AWP en el equipo fue muy bueno", 5);

            estadisticasService.crearComentario(jugadoresMatchmaking.get(8).getId(),
                    scrim.getId(),
                    "El equipo era malo y no supieron trabajar en conjunto", 1);
            // Obtener y mostrar todos los comentarios del scrim
            List<Comentario> comentariosScrim = estadisticasService.obtenerComentariosDeScrim(scrim.getId());

            if (!comentariosScrim.isEmpty()) {
                System.out.println("Total de comentarios: " + comentariosScrim.size() + "\n");

                int numComentario = 1;
                for (Comentario comentario : comentariosScrim) {
                    System.out.println("----------------------------------------------------");
                    System.out.println("COMENTARIO #" + numComentario);
                    System.out.println("----------------------------------------------------");
                    System.out.println("- Autor: " + comentario.getJugadorId());
                    System.out.println("- Scrim: " + comentario.getScrimId());
                    System.out.println("- Rating: " + comentario.getRating() + "/5");
                    System.out.println("- Contenido: \"" + comentario.getContenido() + "\"");
                    System.out.println("- Fecha: " + comentario.getFechaCreacion());
                    System.out.println();
                    numComentario++;
                }

                System.out.println("Comentarios mostrados exitosamente\n");
            } else {
                System.out.println("No se encontraron comentarios para este scrim\n");
            }

            System.out.println("\n============================================================");
            System.out.println("        TEST COMPLETADO EXITOSAMENTE");
            System.out.println("============================================================\n");

        } catch (Exception e) {
            System.err.println("\nError durante el test: " + e.getMessage());
            e.printStackTrace();
        }

    }

    // ...existing code...

    private static Usuario crearUsuario(String username, String email, String password, String juegoPreferido,
            int rango) {
        Usuario usuario = authService.registrarUsuario(username, email, password, juegoPreferido, rango);
        usuario.soloEmail("supportking@escrims.com")
                .unsubscribeFromAllEvents();

        return usuario;
    }

}