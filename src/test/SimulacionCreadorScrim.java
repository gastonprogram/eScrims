package test;

import aplicacion.services.ConfirmacionService;
import aplicacion.services.PostulacionService;
import aplicacion.services.ScrimService;
import dominio.juegos.LeagueOfLegends;
import dominio.modelo.Scrim;
import dominio.modelo.Usuario;
import dominio.valueobjects.formatosScrims.ScrimFormat;
import infraestructura.persistencia.implementacion.RepositorioScrimMemoria;
import infraestructura.persistencia.implementacion.RepositorioUsuarioJSON;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Simulación del flujo desde el punto de vista del creador/organizador de
 * scrims.
 *
 * - Crea usuarios de prueba
 * - Crea un scrim
 * - Simula postulaciones de usuarios
 * - El organizador acepta/rechaza algunas postulaciones
 * - Cuando el lobby se arma, simula confirmaciones (algunas
 * aceptadas/rechazadas)
 * - Si hay rechazos, vuelve a aceptar nuevas postulaciones hasta confirmar el
 * scrim
 *
 * Ejecutar: java -cp bin;lib/* test.SimulacionCreadorScrim
 */
public class SimulacionCreadorScrim {

    public static void main(String[] args) throws Exception {
        System.out.println("--- INICIANDO SIMULACIÓN (CREADOR) ---");

        // Repositorios y servicios
        RepositorioScrimMemoria repoScrims = RepositorioScrimMemoria.getInstance();
        RepositorioUsuarioJSON repoUsuarios = new RepositorioUsuarioJSON();

        ScrimService scrimService = new ScrimService(repoScrims);
        PostulacionService postulacionService = new PostulacionService(repoScrims, repoUsuarios);
        ConfirmacionService confirmacionService = new ConfirmacionService(repoScrims);

        // Limpieza inicial (opcional)
        repoScrims.limpiar();

        // 1) Crear usuarios
        System.out.println("Creando usuarios de prueba...");
        Usuario organizador = new Usuario("organizador1", "org1@example.com", "pass123");
        organizador.setJuegoPrincipal(LeagueOfLegends.getInstance());
        organizador.setRangoParaJuego(LeagueOfLegends.getInstance().getNombre(), 70);
        repoUsuarios.guardar(organizador);

        List<Usuario> candidatos = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            Usuario u = new Usuario("user" + i, "user" + i + "@example.com", "pwd" + i);
            u.setJuegoPrincipal(LeagueOfLegends.getInstance());
            // Distribuir rangos: algunos dentro, algunos fuera
            int rango = 40 + (i * 4); // 44,48,52,... etc
            u.setRangoParaJuego(LeagueOfLegends.getInstance().getNombre(), rango);
            // Latencia aleatoria razonable
            u.setLatenciaPromedio(30 + (i % 5) * 20);
            repoUsuarios.guardar(u);
            candidatos.add(u);
        }

        // 2) Creador crea un scrim (5v5 -> 10 plazas)
        System.out.println("Creando scrim por el organizador...");
        ScrimFormat formato = LeagueOfLegends.getInstance().getFormatoEstandar();
        LocalDateTime fecha = LocalDateTime.now().plusDays(1);

        Scrim scrim = scrimService.crearScrimConEstrategia(
                LeagueOfLegends.getInstance(),
                formato,
                fecha,
                40, // rangoMin
                80, // rangoMax
                200, // latenciaMax
                "MMR",
                organizador.getId());

        System.out.println("Scrim creado: ID=" + scrim.getId() + " Plazas=" + scrim.getPlazas());

        // 3) Simular postulaciones automáticas
        System.out.println("Simulando postulaciones (aceptación automática por requisitos)...");

        // En estado BUSCANDO, las postulaciones se aceptan automáticamente si cumplen
        // requisitos
        for (int i = 0; i < candidatos.size(); i++) {
            Usuario candidato = candidatos.get(i);
            try {
                System.out.println("-> " + candidato.getUsername() + " se postula...");
                postulacionService.postularAScrim(scrim.getId(), candidato.getId(),
                        candidato.getRangoPorJuego().get(LeagueOfLegends.getInstance().getNombre()),
                        candidato.getLatenciaPromedio());
                System.out.println("   ✓ Aceptado automáticamente (cumple requisitos)");

                // Verificar estado del scrim después de cada postulación
                scrim = scrimService.buscarPorId(scrim.getId());
                System.out.println("   Estado: " + scrim.getEstado() + ", Aceptados: " +
                        scrim.getPostulacionesAceptadas().size() + "/" + scrim.getPlazas());

                // Si llegó a LOBBY_ARMADO, detener postulaciones
                if ("LOBBY_ARMADO".equals(scrim.getEstado())) {
                    System.out.println("Lobby completo! Transición automática a LOBBY_ARMADO");
                    break;
                }

            } catch (IllegalArgumentException e) {
                // Postulación rechazada por requisitos
                System.out.println("   ✗ Rechazado: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("   ✗ Error: " + e.getMessage());
            }
        }

        // 4) Simular confirmaciones
        scrim = scrimService.buscarPorId(scrim.getId());
        if ("LOBBY_ARMADO".equals(scrim.getEstado())) {
            System.out.println("\nSimulando confirmaciones...");
            System.out.println("Confirmaciones pendientes: " + scrim.getConfirmaciones().size());

            // Simular que el primer jugador rechaza para demostrar re-búsqueda
            boolean primeraConfirmacion = true;
            for (var confirmacion : scrim.getConfirmaciones()) {
                try {
                    if (primeraConfirmacion) {
                        confirmacionService.rechazarAsistencia(scrim.getId(), confirmacion.getUserId());
                        System.out.println("Usuario " + confirmacion.getUserId() + " rechazó la confirmación");
                        primeraConfirmacion = false;
                        break; // Solo uno rechaza, el resto se manejará después
                    }
                } catch (Exception e) {
                    System.err.println("Error en confirmación: " + e.getMessage());
                }
            }

            // 5) Re-búsqueda después del rechazo
            scrim = scrimService.buscarPorId(scrim.getId());
            System.out.println("Estado después del rechazo: " + scrim.getEstado());
            System.out.println("Confirmaciones restantes después del rechazo: " + scrim.getConfirmaciones().size());

            if ("BUSCANDO".equals(scrim.getEstado())) {
                System.out.println("\nRe-búsqueda activada. Buscando nuevos postulantes...");

                // Postular usuarios restantes para llenar el slot vacío
                for (int i = 0; i < candidatos.size(); i++) {
                    Usuario candidato = candidatos.get(i);

                    // Verificar si ya se postuló
                    if (!scrim.yaSePostulo(candidato.getId())) {
                        try {
                            System.out.println("-> " + candidato.getUsername() + " se postula en re-búsqueda...");
                            postulacionService.postularAScrim(scrim.getId(), candidato.getId(),
                                    candidato.getRangoPorJuego().get(LeagueOfLegends.getInstance().getNombre()),
                                    candidato.getLatenciaPromedio());
                            System.out.println("   ✓ Aceptado automáticamente");

                            scrim = scrimService.buscarPorId(scrim.getId());
                            if ("LOBBY_ARMADO".equals(scrim.getEstado())) {
                                System.out.println("Lobby completado nuevamente!");
                                break;
                            }
                        } catch (Exception e) {
                            System.err.println("   ✗ " + e.getMessage());
                        }
                    }
                }

                // 6) Confirmaciones finales
                scrim = scrimService.buscarPorId(scrim.getId());
                if ("LOBBY_ARMADO".equals(scrim.getEstado())) {
                    System.out.println("\nConfirmaciones finales...");
                    // Filtrar solo confirmaciones pendientes para evitar duplicados
                    var confirmacionesPendientes = scrim.getConfirmaciones().stream()
                            .filter(c -> c.getEstado() == dominio.modelo.Confirmacion.EstadoConfirmacion.PENDIENTE)
                            .toList();

                    System.out.println("Confirmaciones pendientes: " + confirmacionesPendientes.size());

                    for (var confirmacion : confirmacionesPendientes) {
                        try {
                            confirmacionService.confirmarAsistencia(scrim.getId(), confirmacion.getUserId());
                            System.out.println("Usuario " + confirmacion.getUserId() + " confirmó asistencia");
                        } catch (Exception e) {
                            System.err.println("Error en confirmación final: " + e.getMessage());
                        }
                    }
                }
            }
        }

        // Resultado final
        scrim = scrimService.buscarPorId(scrim.getId());
        System.out.println("\n=== RESULTADO FINAL ===");
        System.out.println("Estado del scrim: " + scrim.getEstado());
        System.out.println("Postulaciones totales: " + scrim.getPostulaciones().size());
        System.out.println("Postulaciones aceptadas: " + scrim.getPostulacionesAceptadas().size());
        System.out.println("Confirmaciones: " + scrim.getConfirmaciones().size());
        if (!scrim.getConfirmaciones().isEmpty()) {
            long confirmadas = scrim.getConfirmaciones().stream()
                    .filter(c -> c.getEstado() == dominio.modelo.Confirmacion.EstadoConfirmacion.CONFIRMADA)
                    .count();
            System.out.println("Confirmadas: " + confirmadas + "/" + scrim.getConfirmaciones().size());
        }
        System.out.println("Estrategia de matchmaking: " + scrim.getEstrategiaMatchmaking());

        System.out.println("--- SIMULACIÓN FINALIZADA ---");
    }
}
