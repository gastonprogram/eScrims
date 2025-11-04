
package test;

import dominio.juegos.LeagueOfLegends;
import dominio.modelo.Confirmacion;
import dominio.modelo.Postulacion;
import dominio.modelo.Scrim;
import dominio.modelo.Usuario;
import infraestructura.persistencia.repository.RepositorioFactory;
import infraestructura.persistencia.repository.RepositorioScrim;
import infraestructura.persistencia.repository.RepositorioUsuario;
import aplicacion.services.PostulacionService;
import aplicacion.services.ConfirmacionService;

import java.time.LocalDateTime;
import java.util.List;

import aplicacion.builders.ScrimBuilder;

/**
 * Ejemplo de uso del sistema desde la perspectiva del ORGANIZADOR.
 * 
 * Este ejemplo demuestra:
 * 1. Organizador crea un scrim
 * 2. Recibe múltiples postulaciones (algunas con validación automática)
 * 3. Organizador revisa postulaciones pendientes
 * 4. Organizador acepta/rechaza manualmente postulaciones
 * 5. Gestiona confirmaciones cuando el lobby está listo
 * 6. Un jugador rechaza confirmación (slot se libera)
 * 7. Reorganización del scrim
 * 
 * @author eScrims Team
 */
public class EjemploGestionPostulacionesOrganizador {

    public static void main(String[] args) {
        System.out.println("=".repeat(80));
        System.out.println("EJEMPLO: GESTIÓN DE POSTULACIONES POR ORGANIZADOR");
        System.out.println("=".repeat(80));

        try {
            // ============================================
            // PASO 1: SETUP INICIAL
            // ============================================
            System.out.println("\n📋 PASO 1: Setup inicial...");

            RepositorioUsuario repoUsuarios = RepositorioFactory.getRepositorioUsuario();
            RepositorioScrim repoScrims = RepositorioFactory.getRepositorioScrim();

            // Crear organizador
            Usuario organizador = new Usuario("ProOrganizer", "organizer@escrims.com", "pass123");
            repoUsuarios.guardar(organizador);
            System.out.println("✓ Organizador: " + organizador.getUsername() + " (ID: " + organizador.getId() + ")");

            // Para este ejemplo, usamos ARAM de LoL (5v5 pero más rápido)
            // ARAM es un formato válido de League of Legends
            Scrim scrim = new ScrimBuilder()
                    .withJuego(LeagueOfLegends.getInstance())
                    .withFormato(new dominio.juegos.formatos.FormatoARAMLoL())
                    .withRango(500, 1000) // Bronze/Silver - Gold
                    .withLatenciaMaxima(80) // Máximo 80ms
                    .withFechaHora(LocalDateTime.now().plusHours(3))
                    .build();

            scrim.setCreatedBy(organizador.getId());

            repoScrims.guardar(scrim);
            System.out.println("✓ Scrim creado con 10 plazas (ARAM 5v5)");
            System.out.println("  Requisitos: Rango 500-1000, Latencia máx 80ms"); // ============================================
            // PASO 2: POSTULACIONES DE JUGADORES
            // ============================================
            System.out.println("\n\n📝 PASO 2: Llegan postulaciones de jugadores...");
            System.out.println("-".repeat(80));

            PostulacionService postService = new PostulacionService(repoScrims, repoUsuarios);

            // Crear jugadores y postularlos (necesitamos 10 para ARAM 5v5)
            String[][] jugadores = {
                    { "TopLaner", "800", "50" }, // ✅ Cumple todo
                    { "MidLaner", "700", "60" }, // ✅ Cumple todo
                    { "ADCPlayer", "400", "40" }, // ❌ NO cumple rango (muy bajo)
                    { "JungleMain", "900", "70" }, // ✅ Cumple todo
                    { "SupportPro", "850", "90" }, // ❌ NO cumple latencia (muy alta)
                    { "FlexPlayer", "950", "55" }, // ✅ Cumple todo
                    { "SoloQKing", "1200", "45" }, // ❌ NO cumple rango (muy alto)
                    { "Player8", "600", "65" }, // ✅ Cumple todo
                    { "Player9", "750", "50" }, // ✅ Cumple todo
                    { "Player10", "820", "70" }, // ✅ Cumple todo
                    { "Player11", "680", "45" }, // ✅ Cumple todo
                    { "Player12", "920", "60" }, // ✅ Cumple todo
                    { "Player13", "780", "75" } // ✅ Cumple todo
            };

            for (String[] jugadorData : jugadores) {
                Usuario jugador = new Usuario(jugadorData[0], jugadorData[0].toLowerCase() + "@escrims.com", "pass123");
                repoUsuarios.guardar(jugador);

                int rango = Integer.parseInt(jugadorData[1]);
                int latencia = Integer.parseInt(jugadorData[2]);

                try {
                    postService.postularAScrim(scrim.getId(), jugador.getId(), rango, latencia);
                    System.out.println(
                            "\n🎮 " + jugadorData[0] + " (Rango: " + rango + ", Latencia: " + latencia + "ms)");
                    System.out.println("   → Postulación procesada exitosamente");
                } catch (Exception e) {
                    System.out.println(
                            "\n🎮 " + jugadorData[0] + " (Rango: " + rango + ", Latencia: " + latencia + "ms)");
                    System.out.println("   → " + e.getMessage());
                }
            }

            // ============================================
            // PASO 3: ORGANIZADOR REVISA POSTULACIONES
            // ============================================
            System.out.println("\n\n👀 PASO 3: Organizador revisa todas las postulaciones...");
            System.out.println("-".repeat(80));

            List<Postulacion> todasPostulaciones = postService.listarTodasLasPostulaciones(
                    scrim.getId(),
                    organizador.getId());

            System.out.println("\n📊 Total de postulaciones: " + todasPostulaciones.size());

            int aceptadas = 0, rechazadas = 0, pendientes = 0;
            for (Postulacion post : todasPostulaciones) {
                switch (post.getEstado()) {
                    case ACEPTADA:
                        aceptadas++;
                        System.out.println("\n✅ " + post.getUserId());
                        System.out.println("   Rango: " + post.getRangoUsuario() + " | Latencia: "
                                + post.getLatenciaUsuario() + "ms");
                        System.out.println("   Estado: ACEPTADA (automáticamente)");
                        break;
                    case RECHAZADA:
                        rechazadas++;
                        System.out.println("\n❌ " + post.getUserId());
                        System.out.println("   Rango: " + post.getRangoUsuario() + " | Latencia: "
                                + post.getLatenciaUsuario() + "ms");
                        System.out.println("   Estado: RECHAZADA");
                        System.out.println("   Motivo: " + post.getMotivoRechazo());
                        break;
                    case PENDIENTE:
                        pendientes++;
                        System.out.println("\n⏳ " + post.getUserId());
                        System.out.println("   Rango: " + post.getRangoUsuario() + " | Latencia: "
                                + post.getLatenciaUsuario() + "ms");
                        System.out.println("   Estado: PENDIENTE (requiere revisión manual)");
                        break;
                }
            }

            System.out.println("\n📈 Resumen:");
            System.out.println("  Aceptadas: " + aceptadas);
            System.out.println("  Rechazadas: " + rechazadas);
            System.out.println("  Pendientes: " + pendientes);
            System.out.println("  Plazas ocupadas: " + aceptadas + "/10");

            // ============================================
            // PASO 4: ORGANIZADOR GESTIONA PENDIENTES
            // ============================================
            if (pendientes > 0) {
                System.out.println("\n\n⚙️ PASO 4: Organizador gestiona postulaciones pendientes...");
                System.out.println("-".repeat(80));

                List<Postulacion> postulacionesPendientes = postService.listarPostulacionesPendientes(
                        scrim.getId(),
                        organizador.getId());

                System.out.println("\nPostulaciones pendientes: " + postulacionesPendientes.size());

                // El organizador acepta la primera pendiente
                if (!postulacionesPendientes.isEmpty()) {
                    Postulacion primeraPendiente = postulacionesPendientes.get(0);
                    System.out.println("\n💡 Organizador decide ACEPTAR a: " + primeraPendiente.getUserId());

                    try {
                        postService.aceptarPostulacion(
                                scrim.getId(),
                                primeraPendiente.getUserId(),
                                organizador.getId());
                        System.out.println("   → Postulación aceptada exitosamente");
                    } catch (Exception e) {
                        System.out.println("   → Error: " + e.getMessage());
                    }
                }

                // Rechaza otras si quedan
                postulacionesPendientes = postService.listarPostulacionesPendientes(
                        scrim.getId(),
                        organizador.getId());

                for (Postulacion post : postulacionesPendientes) {
                    System.out.println("\n💡 Organizador decide RECHAZAR a: " + post.getUserId());

                    try {
                        postService.rechazarPostulacion(
                                scrim.getId(),
                                post.getUserId(),
                                organizador.getId(),
                                "Preferimos jugadores con mejor experiencia");
                        System.out.println("   → Postulación rechazada");
                    } catch (Exception e) {
                        System.out.println("   → Error: " + e.getMessage());
                    }
                }
            }

            // ============================================
            // PASO 5: LOBBY ARMADO - CONFIRMACIONES
            // ============================================
            System.out.println("\n\n🎯 PASO 5: Estado del scrim tras aceptar postulaciones...");
            System.out.println("-".repeat(80));

            scrim = repoScrims.buscarPorId(scrim.getId());
            System.out.println("Estado: " + scrim.getEstado());
            System.out.println("Plazas: " + scrim.getPostulacionesAceptadas().size() + "/" + scrim.getPlazas());

            if ("LOBBY_ARMADO".equals(scrim.getEstado())) {
                System.out.println("\n✅ ¡LOBBY ARMADO! Confirmaciones generadas automáticamente.");

                ConfirmacionService confService = new ConfirmacionService(repoScrims);

                System.out.println("\n👥 Jugadores que deben confirmar:");
                List<Confirmacion> confirmaciones = confService.listarConfirmaciones(
                        scrim.getId(),
                        organizador.getId());

                for (Confirmacion conf : confirmaciones) {
                    System.out.println("  - " + conf.getUserId() + " (Estado: " + conf.getEstado() + ")");
                }

                // ============================================
                // PASO 6: JUGADORES CONFIRMAN
                // ============================================
                System.out.println("\n\n✅ PASO 6: Jugadores confirman asistencia...");
                System.out.println("-".repeat(80));

                // Primeros 2 confirman
                List<Confirmacion> confs = scrim.getConfirmaciones();
                for (int i = 0; i < Math.min(2, confs.size()); i++) {
                    try {
                        confService.confirmarAsistencia(scrim.getId(), confs.get(i).getUserId());
                        System.out.println((i + 1) + ". " + confs.get(i).getUserId() + ": Confirmación exitosa");
                    } catch (Exception e) {
                        System.out.println((i + 1) + ". " + confs.get(i).getUserId() + ": Error - " + e.getMessage());
                    }
                }

                // El tercero RECHAZA
                if (confs.size() > 2) {
                    System.out.println("\n⚠️ ¡" + confs.get(2).getUserId() + " rechaza la confirmación!");
                    try {
                        confService.rechazarAsistencia(scrim.getId(), confs.get(2).getUserId());
                        System.out.println("   → Rechazo procesado exitosamente");
                    } catch (Exception e) {
                        System.out.println("   → Error: " + e.getMessage());
                    }

                    // Recargar scrim para ver el cambio de estado
                    scrim = repoScrims.buscarPorId(scrim.getId());
                    System.out.println("\n💡 Estado del scrim tras el rechazo: " + scrim.getEstado());

                    // Si volvió a BUSCANDO, no podemos seguir confirmando
                    if ("BUSCANDO".equals(scrim.getEstado())) {
                        System.out.println("   ⏸️  El scrim volvió a BUSCANDO. No se pueden confirmar más jugadores.");
                        System.out.println("   📋 Postulaciones aceptadas restantes: "
                                + scrim.getPostulacionesAceptadas().size() + "/" + scrim.getPlazas());
                    }
                }

                // Verificar estado antes de continuar
                scrim = repoScrims.buscarPorId(scrim.getId());
                if ("LOBBY_ARMADO".equals(scrim.getEstado())) {
                    // El cuarto confirma (solo si seguimos en LOBBY_ARMADO)
                    if (confs.size() > 3) {
                        try {
                            confService.confirmarAsistencia(scrim.getId(), confs.get(3).getUserId());
                            System.out.println("\n4. " + confs.get(3).getUserId() + ": Confirmación exitosa");
                        } catch (Exception e) {
                            System.out.println("\n4. " + confs.get(3).getUserId() + ": Error - " + e.getMessage());
                        }
                    }
                }

                // ============================================
                // PASO 7: ESTADO FINAL
                // ============================================
                System.out.println("\n\n🏁 PASO 7: Estado final tras confirmaciones...");
                System.out.println("-".repeat(80));

                scrim = repoScrims.buscarPorId(scrim.getId());
                System.out.println("Estado final: " + scrim.getEstado());

                if ("BUSCANDO".equals(scrim.getEstado())) {
                    System.out.println("\n⏪ El scrim volvió a BUSCANDO porque un jugador rechazó.");
                    System.out.println("   El organizador puede aceptar nuevas postulaciones para llenar el slot.");
                } else if ("CONFIRMADO".equals(scrim.getEstado())) {
                    System.out.println("\n🎉 ¡SCRIM CONFIRMADO! Listo para iniciar.");
                }

                System.out.println("\n📊 Resumen final:");
                System.out.println("  - Total postulaciones: " + scrim.getPostulaciones().size());
                System.out.println("  - Aceptadas: " + scrim.getPostulacionesAceptadas().size());
                System.out.println("  - Total confirmaciones: " + scrim.getConfirmaciones().size());

                long confirmadas = scrim.getConfirmaciones().stream()
                        .filter(c -> c.getEstado() == Confirmacion.EstadoConfirmacion.CONFIRMADA)
                        .count();
                long rechazadasConf = scrim.getConfirmaciones().stream()
                        .filter(c -> c.getEstado() == Confirmacion.EstadoConfirmacion.RECHAZADA)
                        .count();

                System.out.println("    * Confirmadas: " + confirmadas);
                System.out.println("    * Rechazadas: " + rechazadasConf);
                System.out.println(
                        "    * Pendientes: " + (scrim.getConfirmaciones().size() - confirmadas - rechazadasConf));
            }

            System.out.println("\n" + "=".repeat(80));
            System.out.println("✅ EJEMPLO COMPLETADO - Flujo del Organizador demostrado");
            System.out.println("=".repeat(80));

        } catch (Exception e) {
            System.err.println("\n❌ ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
