package test;

import aplicacion.services.PostulacionService;
import aplicacion.services.ConfirmacionService;
import dominio.juegos.LeagueOfLegends;
import dominio.modelo.Confirmacion;
import dominio.modelo.Postulacion;
import dominio.modelo.Scrim;
import dominio.modelo.Usuario;
import infraestructura.persistencia.repository.RepositorioFactory;
import infraestructura.persistencia.repository.RepositorioScrim;
import infraestructura.persistencia.repository.RepositorioUsuario;

import java.time.LocalDateTime;

import aplicacion.builders.ScrimBuilder;

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
                    .withFormato(new dominio.juegos.formatos.Formato5v5LoL())
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

            PostulacionService postService = new PostulacionService(repoScrims, repoUsuarios);

            // Postulación 1: Diamond Player (cumple requisitos)
            System.out.println("\n🎮 Postulación de DiamondPlayer:");
            System.out.println("  Rango: 1200, Latencia: 45ms");
            try {
                postService.postularAScrim(scrim.getId(), jugador1.getId(), 1200, 45);
                System.out.println("  → Postulación procesada exitosamente");
            } catch (Exception e) {
                System.out.println("  → " + e.getMessage());
            }

            // Postulación 2: Gold Player (cumple requisitos)
            System.out.println("\n🎮 Postulación de GoldPlayer:");
            System.out.println("  Rango: 850, Latencia: 60ms");
            try {
                postService.postularAScrim(scrim.getId(), jugador2.getId(), 850, 60);
                System.out.println("  → Postulación procesada exitosamente");
            } catch (Exception e) {
                System.out.println("  → " + e.getMessage());
            }

            // Postulación 3: Silver Player (NO cumple - rango bajo)
            System.out.println("\n🎮 Postulación de SilverPlayer:");
            System.out.println("  Rango: 500, Latencia: 50ms");
            try {
                postService.postularAScrim(scrim.getId(), jugador3.getId(), 500, 50);
                System.out.println("  → Postulación procesada exitosamente");
            } catch (Exception e) {
                System.out.println("  → " + e.getMessage());
            }

            // Postulación 4: Platinum Player (cumple requisitos)
            System.out.println("\n🎮 Postulación de PlatinumPlayer:");
            System.out.println("  Rango: 1400, Latencia: 70ms");
            try {
                postService.postularAScrim(scrim.getId(), jugador4.getId(), 1400, 70);
                System.out.println("  → Postulación procesada exitosamente");
            } catch (Exception e) {
                System.out.println("  → " + e.getMessage());
            }

            // Postulación 5: High Ping Player (NO cumple - latencia alta)
            System.out.println("\n🎮 Postulación de HighPingPlayer:");
            System.out.println("  Rango: 1000, Latencia: 150ms");
            try {
                postService.postularAScrim(scrim.getId(), jugador5.getId(), 1000, 150);
                System.out.println("  → Postulación procesada exitosamente");
            } catch (Exception e) {
                System.out.println("  → " + e.getMessage());
            }

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
            // Crear postulaciones con el nuevo sistema
            for (int i = 0; i < 10; i++) {
                Usuario jugador = new Usuario("Player" + (i + 1), "player" + (i + 1) + "@escrims.com", "pass123");
                repoUsuarios.guardar(jugador);

                // Calcular rango que cumpla requisitos (800-1500)
                int rango = 850 + (i * 50); // Rangos entre 850-1450
                if (rango > 1500)
                    rango = 800 + (i * 30); // Ajustar si se pasa
                int latencia = 45 + (i * 5); // Latencias entre 45-95ms (cumple máx 100ms)

                try {
                    postService.postularAScrim(scrim.getId(), jugador.getId(), rango, latencia);
                    System.out.println("Player" + (i + 1) + ": Postulación procesada exitosamente");
                } catch (Exception e) {
                    System.out.println("Player" + (i + 1) + ": " + e.getMessage());
                }
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

            ConfirmacionService confService = new ConfirmacionService(repoScrims);

            // Todos los jugadores confirman
            int contador = 1;
            for (Confirmacion conf : scrim.getConfirmaciones()) {
                try {
                    confService.confirmarAsistencia(scrim.getId(), conf.getUserId());
                    System.out.println(contador + ". " + conf.getUserId() + ": Confirmación exitosa");
                } catch (Exception e) {
                    System.out.println(contador + ". " + conf.getUserId() + ": Error - " + e.getMessage());
                }
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
