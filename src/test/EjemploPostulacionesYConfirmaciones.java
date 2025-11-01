import controller.PostulacionController;
import controller.ConfirmacionController;
import model.Scrim;
import model.ScrimBuilder;
import model.Usuario;
import model.Postulacion;
import model.Confirmacion;
import model.juegos.LeagueOfLegends;
import model.Persistencia.RepositorioFactory;
import model.Persistencia.RepositorioScrim;
import model.Persistencia.RepositorioUsuario;

import java.time.LocalDateTime;

/**
 * Ejemplo de uso completo del sistema de postulaciones y confirmaciones.
 * 
 * Este ejemplo demuestra el flujo completo:
 * 1. Organizador crea un scrim
 * 2. Jugadores se postulan (algunos son aceptados, otros rechazados)
 * 3. Validación automática de requisitos (rango/latencia)
 * 4. Cuando el lobby está lleno, se generan confirmaciones
 * 5. Jugadores confirman su asistencia
 * 6. El scrim está listo para iniciar
 * 
 * @author eScrims Team
 */
public class EjemploPostulacionesYConfirmaciones {

    public static void main(String[] args) {
        System.out.println("=".repeat(80));
        System.out.println("EJEMPLO: SISTEMA DE POSTULACIONES Y CONFIRMACIONES");
        System.out.println("=".repeat(80));

        try {
            // ============================================
            // PASO 1: SETUP - Crear usuarios y scrim
            // ============================================
            System.out.println("\n📋 PASO 1: Configurando usuarios y scrim...");

            RepositorioUsuario repoUsuarios = RepositorioFactory.getRepositorioUsuario();
            RepositorioScrim repoScrims = RepositorioFactory.getRepositorioScrim();

            // Crear organizador
            Usuario organizador = new Usuario("OrganizerPro", "org@escrims.com", "pass123");
            repoUsuarios.guardar(organizador);
            System.out.println("✓ Organizador creado: " + organizador.getUsername());

            // Crear jugadores con diferentes características
            Usuario jugador1 = new Usuario("DiamondPlayer", "diamond@escrims.com", "pass123");
            Usuario jugador2 = new Usuario("GoldPlayer", "gold@escrims.com", "pass123");
            Usuario jugador3 = new Usuario("SilverPlayer", "silver@escrims.com", "pass123");
            Usuario jugador4 = new Usuario("PlatinumPlayer", "plat@escrims.com", "pass123");
            Usuario jugador5 = new Usuario("HighPingPlayer", "lag@escrims.com", "pass123");

            repoUsuarios.guardar(jugador1);
            repoUsuarios.guardar(jugador2);
            repoUsuarios.guardar(jugador3);
            repoUsuarios.guardar(jugador4);
            repoUsuarios.guardar(jugador5);
            System.out.println("✓ 5 jugadores creados");

            // Crear un scrim 5v5 (10 plazas) para League of Legends
            // Usamos el formato específico de LoL: Formato5v5LoL
            Scrim scrim = new ScrimBuilder()
                    .withJuego(LeagueOfLegends.getInstance())
                    .withFormato(new model.formatos.Formato5v5LoL())
                    .withRango(800, 1500) // Gold mínimo - Platinum máximo
                    .withLatenciaMaxima(100) // Máximo 100ms de latencia
                    .withFechaHora(LocalDateTime.now().plusHours(2))
                    .build();

            scrim.setCreatedBy(organizador.getId());

            repoScrims.guardar(scrim);
            System.out.println("✓ Scrim creado: " + scrim.getId());
            System.out.println("  - Juego: League of Legends");
            System.out.println("  - Formato: 5v5");
            System.out.println("  - Requisitos: Rango 800-1500, Latencia máx 100ms");
            System.out.println("  - Plazas: 10");
            System.out.println("  - Estado inicial: " + scrim.getEstado());

            // ============================================
            // PASO 2: POSTULACIONES
            // ============================================
            System.out.println("\n\n📝 PASO 2: Jugadores se postulan al scrim...");
            System.out.println("-".repeat(80));

            PostulacionController postController = new PostulacionController();

            // Postulación 1: Diamond Player (cumple requisitos)
            System.out.println("\n🎮 Postulación de DiamondPlayer:");
            System.out.println("  Rango: 1200, Latencia: 45ms");
            String resultado1 = postController.postularAScrim(
                    scrim.getId(),
                    jugador1.getId(),
                    1200, // Diamond - cumple requisitos
                    45 // Buena latencia
            );
            System.out.println("  → " + resultado1);

            // Postulación 2: Gold Player (cumple requisitos)
            System.out.println("\n🎮 Postulación de GoldPlayer:");
            System.out.println("  Rango: 850, Latencia: 60ms");
            String resultado2 = postController.postularAScrim(
                    scrim.getId(),
                    jugador2.getId(),
                    850, // Gold - cumple requisitos
                    60 // Buena latencia
            );
            System.out.println("  → " + resultado2);

            // Postulación 3: Silver Player (NO cumple - rango bajo)
            System.out.println("\n🎮 Postulación de SilverPlayer:");
            System.out.println("  Rango: 500, Latencia: 50ms");
            String resultado3 = postController.postularAScrim(
                    scrim.getId(),
                    jugador3.getId(),
                    500, // Silver - NO cumple requisito mínimo
                    50 // Buena latencia
            );
            System.out.println("  → " + resultado3);

            // Postulación 4: Platinum Player (cumple requisitos)
            System.out.println("\n🎮 Postulación de PlatinumPlayer:");
            System.out.println("  Rango: 1400, Latencia: 70ms");
            String resultado4 = postController.postularAScrim(
                    scrim.getId(),
                    jugador4.getId(),
                    1400, // Platinum - cumple requisitos
                    70 // Buena latencia
            );
            System.out.println("  → " + resultado4);

            // Postulación 5: High Ping Player (NO cumple - latencia alta)
            System.out.println("\n🎮 Postulación de HighPingPlayer:");
            System.out.println("  Rango: 1000, Latencia: 150ms");
            String resultado5 = postController.postularAScrim(
                    scrim.getId(),
                    jugador5.getId(),
                    1000, // Gold - rango OK
                    150 // Latencia MUY ALTA - NO cumple
            );
            System.out.println("  → " + resultado5);

            // ============================================
            // PASO 3: REVISIÓN DE POSTULACIONES
            // ============================================
            System.out.println("\n\n📊 PASO 3: Estado de postulaciones...");
            System.out.println("-".repeat(80));

            // Recargar scrim para ver cambios
            scrim = repoScrims.buscarPorId(scrim.getId());

            System.out.println("\nPostulaciones totales: " + scrim.getPostulaciones().size());
            System.out.println("Estado del scrim: " + scrim.getEstado());

            System.out.println("\nDetalle de postulaciones:");
            for (Postulacion post : scrim.getPostulaciones()) {
                System.out.println("  - Usuario: " + post.getUserId());
                System.out.println("    Estado: " + post.getEstado());
                System.out.println("    Rango: " + post.getRangoUsuario());
                System.out.println("    Latencia: " + post.getLatenciaUsuario() + "ms");
                if (post.getEstado() == Postulacion.EstadoPostulacion.RECHAZADA) {
                    System.out.println("    ❌ Motivo rechazo: " + post.getMotivoRechazo());
                } else if (post.getEstado() == Postulacion.EstadoPostulacion.ACEPTADA) {
                    System.out.println("    ✅ ACEPTADA");
                } else {
                    System.out.println("    ⏳ PENDIENTE");
                }
                System.out.println();
            }

            // ============================================
            // PASO 4: SIMULAR POSTULACIONES HASTA LLENAR
            // ============================================
            System.out.println("\n\n🚀 PASO 4: Simulando más postulaciones para llenar el scrim...");
            System.out.println("-".repeat(80));

            // Ya tenemos 3 aceptados (jugador1, jugador2, jugador4)
            // Necesitamos 7 más para completar las 10 plazas
            for (int i = 6; i <= 12; i++) {
                Usuario jugador = new Usuario("Player" + i, "player" + i + "@escrims.com", "pass123");
                repoUsuarios.guardar(jugador);

                // Calcular rango que cumpla requisitos (800-1500)
                int rango = 850 + (i * 50); // Rangos entre 850-1450
                if (rango > 1500)
                    rango = 800 + (i * 30); // Ajustar si se pasa
                int latencia = 45 + (i * 5); // Latencias entre 45-95ms (cumple máx 100ms)

                String resultado = postController.postularAScrim(
                        scrim.getId(),
                        jugador.getId(),
                        rango,
                        latencia);

                System.out.println("Player" + i + ": " + resultado);
            }

            // ============================================
            // PASO 5: VERIFICAR TRANSICIÓN A LOBBY_ARMADO
            // ============================================
            System.out.println("\n\n🎯 PASO 5: Verificando transición a LOBBY_ARMADO...");
            System.out.println("-".repeat(80));

            scrim = repoScrims.buscarPorId(scrim.getId());
            System.out.println("Estado actual: " + scrim.getEstado());
            System.out
                    .println("Plazas ocupadas: " + scrim.getPostulacionesAceptadas().size() + "/" + scrim.getPlazas());
            System.out.println("Confirmaciones generadas: " + scrim.getConfirmaciones().size());

            if ("LOBBY_ARMADO".equals(scrim.getEstado())) {
                System.out.println("\n✅ ¡Lobby completo! Se generaron confirmaciones automáticamente.");
                System.out.println("\nJugadores que deben confirmar:");
                for (Confirmacion conf : scrim.getConfirmaciones()) {
                    System.out.println("  - " + conf.getUserId() + " (Estado: " + conf.getEstado() + ")");
                }
            }

            // ============================================
            // PASO 6: CONFIRMACIONES DE ASISTENCIA
            // ============================================
            System.out.println("\n\n✅ PASO 6: Jugadores confirman asistencia...");
            System.out.println("-".repeat(80));

            ConfirmacionController confController = new ConfirmacionController();

            // Todos los jugadores confirman
            int contador = 1;
            for (Confirmacion conf : scrim.getConfirmaciones()) {
                String resultadoConf = confController.confirmarAsistencia(scrim.getId(), conf.getUserId());
                System.out.println(contador + ". " + conf.getUserId() + ": " + resultadoConf);
                contador++;
            }

            // ============================================
            // PASO 7: VERIFICAR TRANSICIÓN A CONFIRMADO
            // ============================================
            System.out.println("\n\n🏆 PASO 7: Estado final del scrim...");
            System.out.println("-".repeat(80));

            scrim = repoScrims.buscarPorId(scrim.getId());
            System.out.println("Estado final: " + scrim.getEstado());

            if ("CONFIRMADO".equals(scrim.getEstado())) {
                System.out.println("\n🎉 ¡SCRIM CONFIRMADO Y LISTO PARA INICIAR!");
                System.out.println("\nResumen:");
                System.out.println("  - Total postulaciones: " + scrim.getPostulaciones().size());
                System.out.println("  - Aceptadas: " + scrim.getPostulacionesAceptadas().size());
                System.out.println("  - Confirmadas: " + scrim.getConfirmaciones().stream()
                        .filter(c -> c.getEstado() == Confirmacion.EstadoConfirmacion.CONFIRMADA)
                        .count());
                System.out.println("  - Estado: " + scrim.getEstado());
            }

            System.out.println("\n" + "=".repeat(80));
            System.out.println("✅ EJEMPLO COMPLETADO EXITOSAMENTE");
            System.out.println("=".repeat(80));

        } catch (Exception e) {
            System.err.println("\n❌ ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
