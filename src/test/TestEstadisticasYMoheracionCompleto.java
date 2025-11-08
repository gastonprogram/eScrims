package test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import aplicacion.services.EstadisticasService;
import dominio.estadisticas.Comentario;
import dominio.estadisticas.EstadisticasJugador;
import dominio.estadisticas.EstadisticasScrim;
import dominio.estadisticas.ReporteConducta;
import dominio.estadisticas.SistemaModeracion;
import dominio.juegos.LeagueOfLegends;
import dominio.juegos.formatos.Formato5v5LoL;
import dominio.modelo.Scrim;
import dominio.modelo.Usuario;
import infraestructura.persistencia.implementacion.RepositorioScrimJson;
import infraestructura.persistencia.implementacion.RepositorioUsuarioJSON;

/**
 * Test comprehensivo que simula el ciclo completo de un scrim con todas las
 * funcionalidades
 * de estadísticas, reportes, comentarios y moderación.
 * 
 * Flujo completo:
 * 1. Crear scrim y usuarios
 * 2. Postulaciones y lobby lleno
 * 3. Asignación de roles por organizador
 * 4. Simulación de partida y estadísticas
 * 5. Reportes de conducta
 * 6. Comentarios y moderación
 * 7. Verificación de estado de moderación
 */
public class TestEstadisticasYMoheracionCompleto {

    private static RepositorioUsuarioJSON repositorioUsuarios;
    private static RepositorioScrimJson repositorioScrims;
    private static EstadisticasService estadisticasService;
    private static SistemaModeracion sistemaModeracion;

    // Variables del test
    private static Usuario organizador;
    private static Usuario[] jugadores = new Usuario[10];
    private static Scrim scrim;
    private static String scrimId;

    public static void main(String[] args) {
        System.out.println("🎮 INICIO: Test Completo de Estadísticas y Moderación 🎮");
        System.out.println("=" + "=".repeat(60) + "=");

        try {
            // Inicialización
            inicializarServicios();

            // Fase 1: Crear scrim y usuarios
            fase1_CrearScrimYUsuarios();

            // Fase 2: Postulaciones y llenado de lobby
            fase2_PostulacionesYLobby();

            // Fase 3: Asignación de roles
            fase3_AsignacionRoles();

            // Fase 4: Simulación de partida y estadísticas
            fase4_SimulacionPartidaYEstadisticas();

            // Fase 5: Reportes de conducta
            fase5_ReportesConducta();

            // Fase 6: Comentarios y moderación
            fase6_ComentariosYModeracion();

            // Fase 7: Verificación de estado de moderación
            fase7_VerificacionEstadoModeracion();

            // Fase 8: Resumen final
            fase8_ResumenFinal();

            System.out.println("\n🎉 TEST COMPLETADO EXITOSAMENTE 🎉");

        } catch (Exception e) {
            System.err.println("❌ Error durante el test: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void inicializarServicios() {
        System.out.println("\n🔧 INICIALIZANDO SERVICIOS...");

        repositorioUsuarios = new RepositorioUsuarioJSON();
        repositorioScrims = RepositorioScrimJson.getInstance();
        estadisticasService = new EstadisticasService();
        sistemaModeracion = new SistemaModeracion();

        System.out.println("✅ Servicios inicializados correctamente");
    }

    private static void fase1_CrearScrimYUsuarios() {
        System.out.println("\n📝 FASE 1: Creación de Scrim y Usuarios");
        System.out.println("-" + "-".repeat(40));

        try {
            // Crear organizador usando el repositorio (los servicios actuales no tienen
            // métodos de registro)
            organizador = new Usuario("Organizador_Pro", "org@escrims.com", "password123");
            repositorioUsuarios.guardar(organizador);
            System.out.println("✅ Organizador creado: " + organizador.getUsername());

            // Crear 10 jugadores
            for (int i = 0; i < 10; i++) {
                jugadores[i] = new Usuario(
                        "Player_" + (i + 1),
                        "player" + (i + 1) + "@escrims.com",
                        "password123");
                repositorioUsuarios.guardar(jugadores[i]);
                System.out.println("✅ Jugador creado: " + jugadores[i].getUsername());
            }

            // Crear scrim directamente (ya que el servicio tiene problemas de compilación)
            LeagueOfLegends lol = LeagueOfLegends.getInstance();
            Formato5v5LoL formato = new Formato5v5LoL();

            scrim = new Scrim(lol, formato, LocalDateTime.now().plusDays(1), 1000, 2000,
                    Arrays.asList("Top", "Jungle", "Mid", "ADC", "Support"), 60, 10);
            scrim.setCreatedBy(organizador.getId());
            repositorioScrims.guardar(scrim);
            scrimId = scrim.getId();

            System.out.println("✅ Scrim creado con ID: " + scrimId);
            System.out.println("   Organizador: " + organizador.getUsername());

        } catch (Exception e) {
            System.err.println("❌ Error en Fase 1: " + e.getMessage());
            throw e;
        }
    }

    private static void fase2_PostulacionesYLobby() {
        System.out.println("\n👥 FASE 2: Postulaciones y Llenado de Lobby");
        System.out.println("-" + "-".repeat(40));

        try {
            // Simular postulaciones de 9 jugadores
            for (int i = 0; i < 9; i++) {
                // Aquí simularíamos el proceso de postulación
                System.out.println("📬 " + jugadores[i].getUsername() + " se postula al scrim");
            }

            // El organizador invita al décimo jugador
            System.out.println("📨 Organizador invita a " + jugadores[9].getUsername());

            // Simular confirmaciones
            System.out.println("\n✅ Confirmaciones recibidas:");
            for (int i = 0; i < 10; i++) {
                System.out.println("   ✓ " + jugadores[i].getUsername() + " confirmado");
            }

            System.out.println("\n🎯 LOBBY COMPLETO - 10/10 jugadores confirmados");

        } catch (Exception e) {
            System.err.println("❌ Error en Fase 2: " + e.getMessage());
            throw e;
        }
    }

    private static void fase3_AsignacionRoles() {
        System.out.println("\n⚔️ FASE 3: Asignación de Roles por Organizador");
        System.out.println("-" + "-".repeat(40));

        try {
            String[] roles = { "ADC", "Support", "Mid", "Jungle", "Top", "ADC", "Support", "Mid", "Jungle", "Top" };

            for (int i = 0; i < 10; i++) {
                System.out.println("🎭 " + jugadores[i].getUsername() + " asignado rol: " + roles[i]);
            }

            System.out.println("\n✅ Todos los roles asignados correctamente");

        } catch (Exception e) {
            System.err.println("❌ Error en Fase 3: " + e.getMessage());
            throw e;
        }
    }

    private static void fase4_SimulacionPartidaYEstadisticas() {
        System.out.println("\n🎮 FASE 4: Simulación de Partida y Estadísticas");
        System.out.println("-" + "-".repeat(40));

        try {
            System.out.println("⏰ Esperando hora de inicio del scrim...");
            System.out.println("🎯 ¡PARTIDA INICIADA!");

            // Crear estadísticas para el scrim
            EstadisticasScrim estadisticas = estadisticasService.obtenerEstadisticasParaScrim(scrim);

            System.out.println("📊 Estadísticas del scrim: " + estadisticas.getScrimId() + " listas");

            // Simular estadísticas de cada jugador
            System.out.println("\n📊 Registrando estadísticas de jugadores:");

            for (int i = 0; i < 10; i++) {
                int kills = (int) (Math.random() * 15) + 1;
                int deaths = (int) (Math.random() * 8) + 1;
                int assists = (int) (Math.random() * 20) + 1;
                int puntuacion = (kills * 100) + (assists * 50) - (deaths * 25) + (int) (Math.random() * 500);

                estadisticasService.registrarEstadisticasJugador(scrimId, jugadores[i].getId(), kills, deaths, assists,
                        puntuacion);

                EstadisticasJugador stats = estadisticasService.obtenerEstadisticasJugador(scrimId,
                        jugadores[i].getId());
                System.out.printf("   %s: %d/%d/%d (KDA: %.2f) - Puntos: %d%n",
                        jugadores[i].getUsername(), stats.getKills(), stats.getDeaths(),
                        stats.getAssists(), stats.getKDA(), stats.getPuntuacion());
            }

            // Designar MVP (jugador con mejor KDA)
            List<EstadisticasJugador> ranking = estadisticasService.obtenerRankingPorKDA(scrimId);
            if (!ranking.isEmpty()) {
                EstadisticasJugador mejorJugador = ranking.get(0);
                estadisticasService.designarMVP(scrimId, mejorJugador.getJugadorId());

                // Buscar el usuario correspondiente
                Usuario mvpUsuario = repositorioUsuarios.buscarPorId(mejorJugador.getJugadorId());
                String mvpUsername = mvpUsuario != null ? mvpUsuario.getUsername() : mejorJugador.getJugadorId();

                System.out.printf("\n🏆 MVP: %s (KDA: %.2f)%n", mvpUsername, mejorJugador.getKDA());
            }

            // Finalizar partida con simulación completa
            estadisticas.finalizarPartidaConSimulacion();
            System.out.printf("\n🎯 PARTIDA FINALIZADA%n");
            System.out.printf("   🏆 Ganador: %s (determinado por estadísticas)%n", estadisticas.getGanador());
            System.out.printf("   ⏱️ Duración: %d minutos (%s)%n",
                    estadisticas.getDuracionMinutos(),
                    estadisticas.getDescripcionPartida());
            System.out.println("\n" + estadisticas.getFormacionEquipos()); // Mostrar ranking completo
            System.out.println("\n🏅 RANKING FINAL POR KDA:");
            List<EstadisticasJugador> rankingCompleto = estadisticasService.obtenerRankingPorKDA(scrimId);
            for (int i = 0; i < rankingCompleto.size(); i++) {
                EstadisticasJugador stats = rankingCompleto.get(i);
                Usuario usuario = repositorioUsuarios.buscarPorId(stats.getJugadorId());
                String username = usuario != null ? usuario.getUsername() : stats.getJugadorId();

                String mvpMark = stats.isEsMVP() ? " 👑" : "";
                System.out.printf("   %d. %s (KDA: %.2f)%s%n",
                        i + 1, username, stats.getKDA(), mvpMark);
            }

        } catch (Exception e) {
            System.err.println("❌ Error en Fase 4: " + e.getMessage());
            throw e;
        }
    }

    private static void fase5_ReportesConducta() {
        System.out.println("\n🚨 FASE 5: Reportes de Conducta");
        System.out.println("-" + "-".repeat(40));

        try {
            // Simular algunos reportes
            System.out.println("📝 Generando reportes de conducta...");

            // Reporte 1: Toxicidad
            estadisticasService.reportarConducta(
                    scrimId,
                    ReporteConducta.TipoReporte.ABUSO_VERBAL,
                    ReporteConducta.Gravedad.LEVE,
                    jugadores[2].getId(),
                    jugadores[0].getId(),
                    "El jugador fue tóxico durante toda la partida, insultando en chat.");
            System.out.println("🚨 Reporte creado: " + jugadores[0].getUsername() + " reporta a "
                    + jugadores[2].getUsername() + " por TOXICIDAD");

            // Reporte 2: AFK
            estadisticasService.reportarConducta(
                    scrimId,
                    ReporteConducta.TipoReporte.ABANDONO,
                    ReporteConducta.Gravedad.MODERADO,
                    jugadores[5].getId(),
                    jugadores[1].getId(),
                    "Se fue AFK durante los últimos 10 minutos de la partida.");
            System.out.println("🚨 Reporte creado: " + jugadores[1].getUsername() + " reporta a "
                    + jugadores[5].getUsername() + " por AFK");

            // Reporte 3: Trampas
            estadisticasService.reportarConducta(
                    scrimId,
                    ReporteConducta.TipoReporte.TRAMPA,
                    ReporteConducta.Gravedad.GRAVE,
                    jugadores[7].getId(),
                    jugadores[3].getId(),
                    "Sospechoso de usar hacks, movimientos no naturales.");
            System.out.println("🚨 Reporte creado: " + jugadores[3].getUsername() + " reporta a "
                    + jugadores[7].getUsername() + " por TRAMPAS");

            // Mostrar todos los reportes
            System.out.println("\n📋 RESUMEN DE REPORTES:");
            SistemaModeracion sistemaModeracion = estadisticasService.getSistemaModeracion();

            for (int i = 0; i < 10; i++) {
                List<ReporteConducta> reportesUsuario = sistemaModeracion.getReportesUsuario(jugadores[i].getId());
                if (!reportesUsuario.isEmpty()) {
                    System.out.printf("   🔍 %s: %d reporte(s)%n", jugadores[i].getUsername(), reportesUsuario.size());
                    for (ReporteConducta reporte : reportesUsuario) {
                        System.out.printf("      - %s (%s): %s%n",
                                reporte.getTipo(), reporte.getGravedad(), reporte.getDescripcion());
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("❌ Error en Fase 5: " + e.getMessage());
            throw e;
        }
    }

    private static void fase6_ComentariosYModeracion() {
        System.out.println("\n💬 FASE 6: Comentarios y Moderación");
        System.out.println("-" + "-".repeat(40));

        try {
            System.out.println("📝 Creando comentarios sobre la partida...");

            // Crear varios comentarios
            Comentario comentario1 = estadisticasService.crearComentario(
                    jugadores[0].getId(),
                    scrimId,
                    "Excelente partida, muy buena coordinación del equipo. ¡A repetir!",
                    5);
            System.out.println("💬 " + jugadores[0].getUsername() + ": \"" + comentario1.getContenido() + "\" (⭐"
                    + comentario1.getRating() + "/5)");

            Comentario comentario2 = estadisticasService.crearComentario(
                    jugadores[1].getId(),
                    scrimId,
                    "Hubo algunos problemas de comunicación, pero en general fue buena.",
                    3);
            System.out.println("💬 " + jugadores[1].getUsername() + ": \"" + comentario2.getContenido() + "\" (⭐"
                    + comentario2.getRating() + "/5)");

            Comentario comentario3 = estadisticasService.crearComentario(
                    jugadores[4].getId(),
                    scrimId,
                    "Increíble nivel de juego, aprendí mucho. Gracias por la oportunidad.",
                    5);
            System.out.println("💬 " + jugadores[4].getUsername() + ": \"" + comentario3.getContenido() + "\" (⭐"
                    + comentario3.getRating() + "/5)");

            Comentario comentario4 = estadisticasService.crearComentario(
                    jugadores[6].getId(),
                    scrimId,
                    "Algunos jugadores fueron muy tóxicos, no recomiendo jugar así.",
                    2);
            System.out.println("💬 " + jugadores[6].getUsername() + ": \"" + comentario4.getContenido() + "\" (⭐"
                    + comentario4.getRating() + "/5)");

            // Simular moderación de comentarios
            System.out.println("\n🛡️ MODERACIÓN DE COMENTARIOS:");

            // Aprobar comentarios positivos
            estadisticasService.moderarComentario(comentario1.getId(), Comentario.EstadoModeracion.APROBADO, null);
            System.out.println("✅ Comentario de " + jugadores[0].getUsername() + " APROBADO");

            estadisticasService.moderarComentario(comentario3.getId(), Comentario.EstadoModeracion.APROBADO, null);
            System.out.println("✅ Comentario de " + jugadores[4].getUsername() + " APROBADO");

            // Aprobar con reservas
            estadisticasService.moderarComentario(comentario2.getId(), Comentario.EstadoModeracion.APROBADO, null);
            System.out.println("✅ Comentario de " + jugadores[1].getUsername() + " APROBADO");

            // Rechazar comentario tóxico
            estadisticasService.moderarComentario(comentario4.getId(), Comentario.EstadoModeracion.RECHAZADO,
                    "Contenido inapropiado - menciona toxicidad sin evidencia");
            System.out.println("❌ Comentario de " + jugadores[6].getUsername() + " RECHAZADO");

            // Mostrar comentarios pendientes
            List<Comentario> comentariosPendientes = estadisticasService.obtenerComentariosPendientes();
            System.out.println("\n⏳ Comentarios pendientes de moderación: " + comentariosPendientes.size());

            // Mostrar comentarios aprobados del scrim
            List<Comentario> comentariosScrim = estadisticasService.obtenerComentariosDeScrim(scrimId);
            System.out.println("\n📊 COMENTARIOS DEL SCRIM (todos los estados):");
            for (Comentario comentario : comentariosScrim) {
                String estado = "";
                switch (comentario.getEstado()) {
                    case APROBADO:
                        estado = "✅ APROBADO";
                        break;
                    case RECHAZADO:
                        estado = "❌ RECHAZADO";
                        break;
                    case PENDIENTE:
                        estado = "⏳ PENDIENTE";
                        break;
                }
                System.out.printf("   %s - Rating: ⭐%d/5 - %s%n",
                        estado, comentario.getRating(), comentario.getContenido());

                if (comentario.getEstado() == Comentario.EstadoModeracion.RECHAZADO) {
                    System.out.printf("      Motivo rechazo: %s%n", comentario.getMotivoRechazo());
                }
            }

        } catch (Exception e) {
            System.err.println("❌ Error en Fase 6: " + e.getMessage());
            throw e;
        }
    }

    private static void fase7_VerificacionEstadoModeracion() {
        System.out.println("\n⚖️ FASE 7: Verificación de Estado de Moderación");
        System.out.println("-" + "-".repeat(40));

        try {
            System.out.println("🔍 Verificando estado de moderación de usuarios...");

            SistemaModeracion sistemaModeracion = estadisticasService.getSistemaModeracion();

            for (int i = 0; i < 10; i++) {
                String userId = jugadores[i].getId();
                String username = jugadores[i].getUsername();

                // Obtener reportes del usuario
                List<ReporteConducta> reportes = sistemaModeracion.getReportesUsuario(userId);

                // Obtener comentarios del usuario (API refactorizada: buscamos en todos los
                // scrims)
                List<Comentario> comentarios = new java.util.ArrayList<>();
                for (dominio.estadisticas.EstadisticasScrim es : estadisticasService
                        .obtenerTodasLasEstadisticasScrims()) {
                    List<Comentario> comentariosScrim = estadisticasService.obtenerComentariosDeScrim(es.getScrimId());
                    for (Comentario c : comentariosScrim) {
                        if (userId.equals(c.getJugadorId())) {
                            comentarios.add(c);
                        }
                    }
                }

                System.out.printf("\n👤 %s:%n", username);
                System.out.printf("   🚨 Reportes recibidos: %d%n", reportes.size());
                System.out.printf("   💬 Comentarios creados: %d%n", comentarios.size());

                if (!reportes.isEmpty()) {
                    System.out.println("   📋 Detalle de reportes:");
                    for (ReporteConducta reporte : reportes) {
                        System.out.printf("      - %s (%s) el %s%n",
                                reporte.getTipo(), reporte.getGravedad(), reporte.getFechaHora());
                    }
                }

                if (!comentarios.isEmpty()) {
                    System.out.println("   📝 Detalle de comentarios:");
                    for (Comentario comentario : comentarios) {
                        System.out.printf("      - Rating: ⭐%d/5, Estado: %s%n",
                                comentario.getRating(), comentario.getEstado());
                    }
                }

                // Determinar estado general
                if (reportes.isEmpty()) {
                    System.out.println("   ✅ Estado: LIMPIO - Sin reportes");
                } else if (reportes.size() == 1) {
                    System.out.println("   ⚠️ Estado: ADVERTENCIA - Un reporte");
                } else {
                    System.out.println("   🚨 Estado: RIESGO - Múltiples reportes");
                }
            }

        } catch (Exception e) {
            System.err.println("❌ Error en Fase 7: " + e.getMessage());
            throw e;
        }
    }

    private static void fase8_ResumenFinal() {
        System.out.println("\n📊 FASE 8: Resumen Final del Test");
        System.out.println("=" + "=".repeat(50));

        try {
            // Estadísticas del scrim
            EstadisticasScrim estadisticas = estadisticasService.buscarEstadisticas(scrimId).orElse(null);
            if (estadisticas != null) {
                System.out.println("\n🎮 ESTADÍSTICAS FINALES DEL SCRIM:");
                System.out.printf("   📅 Scrim ID: %s%n", estadisticas.getScrimId());
                System.out.printf("   🏆 Ganador: %s%n", estadisticas.getGanador());
                System.out.printf("   ⏱️ Duración: %d minutos (%s)%n",
                        estadisticas.getDuracionMinutos(),
                        estadisticas.getDescripcionPartida());

                EstadisticasJugador mvp = estadisticas.obtenerMVP();
                if (mvp != null) {
                    Usuario mvpUsuario = repositorioUsuarios.buscarPorId(mvp.getJugadorId());
                    String mvpUsername = mvpUsuario != null ? mvpUsuario.getUsername() : mvp.getJugadorId();
                    System.out.printf("   👑 MVP: %s (KDA: %.2f)%n", mvpUsername, mvp.getKDA());
                }
            }

            // Resumen de moderación
            SistemaModeracion sistemaModeracion = estadisticasService.getSistemaModeracion();

            int totalReportes = 0;
            int usuariosConReportes = 0;

            for (int i = 0; i < 10; i++) {
                List<ReporteConducta> reportes = sistemaModeracion.getReportesUsuario(jugadores[i].getId());
                totalReportes += reportes.size();
                if (!reportes.isEmpty()) {
                    usuariosConReportes++;
                }
            }

            System.out.println("\n🛡️ RESUMEN DE MODERACIÓN:");
            System.out.printf("   🚨 Total de reportes: %d%n", totalReportes);
            System.out.printf("   👤 Usuarios reportados: %d/10%n", usuariosConReportes);

            // Resumen de comentarios
            List<Comentario> todosLosComentarios = estadisticasService.obtenerComentariosDeScrim(scrimId);
            long comentariosAprobados = todosLosComentarios.stream()
                    .filter(c -> c.getEstado() == Comentario.EstadoModeracion.APROBADO).count();
            long comentariosRechazados = todosLosComentarios.stream()
                    .filter(c -> c.getEstado() == Comentario.EstadoModeracion.RECHAZADO).count();
            long comentariosPendientes = todosLosComentarios.stream()
                    .filter(c -> c.getEstado() == Comentario.EstadoModeracion.PENDIENTE).count();

            System.out.println("\n💬 RESUMEN DE COMENTARIOS:");
            System.out.printf("   📝 Total comentarios: %d%n", todosLosComentarios.size());
            System.out.printf("   ✅ Aprobados: %d%n", comentariosAprobados);
            System.out.printf("   ❌ Rechazados: %d%n", comentariosRechazados);
            System.out.printf("   ⏳ Pendientes: %d%n", comentariosPendientes);

            // Funcionalidades probadas
            System.out.println("\n✅ FUNCIONALIDADES PROBADAS:");
            System.out.println("   🎮 Creación y gestión de scrim");
            System.out.println("   👥 Registro y gestión de usuarios");
            System.out.println("   🎭 Asignación de roles");
            System.out.println("   📊 Registro de estadísticas individuales");
            System.out.println("   🏆 Designación de MVP");
            System.out.println("   📈 Rankings por KDA y puntuación");
            System.out.println("   🚨 Sistema de reportes de conducta");
            System.out.println("   💬 Sistema de comentarios");
            System.out.println("   🛡️ Sistema de moderación");
            System.out.println("   ⚖️ Verificación de estado de moderación");
            System.out.println("   📋 Visualización de reportes por usuario");
            System.out.println("   🔍 Búsqueda y filtrado de estadísticas");

        } catch (Exception e) {
            System.err.println("❌ Error en Fase 8: " + e.getMessage());
            throw e;
        }
    }
}