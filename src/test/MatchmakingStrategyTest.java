package test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import aplicacion.builders.ScrimBuilder;
import aplicacion.services.MatchmakerService;
import dominio.juegos.Juego;
import dominio.juegos.LeagueOfLegends;
import dominio.juegos.formatos.Formato5v5LoL;
import dominio.modelo.HistorialUsuario;
import dominio.modelo.Scrim;
import dominio.modelo.Usuario;
import infraestructura.matchmaking.strategies.ByHistoryStrategy;
import infraestructura.matchmaking.strategies.ByLatencyStrategy;
import infraestructura.matchmaking.strategies.ByMMRStrategy;

/**
 * Ejemplo completo del sistema de estrategias de matchmaking.
 * 
 * Este test demuestra:
 * 1. Creación de usuarios con diferentes perfiles (rango, latencia, historial)
 * 2. Configuración de scrims con requisitos específicos
 * 3. Aplicación de las 3 estrategias de matchmaking
 * 4. Comparación de resultados según cada estrategia
 * 5. Cambio dinámico de estrategias
 * 
 * Escenarios demostrados:
 * - Estrategia MMR: Selecciona por rango/skill balanceado
 * - Estrategia Latency: Prioriza conexión estable
 * - Estrategia History: Considera comportamiento y compatibilidad
 * 
 * @author eScrims Team
 */
public class MatchmakingStrategyTest {

        public static void main(String[] args) {
                System.out.println("═".repeat(80));
                System.out.println("     SISTEMA DE ESTRATEGIAS DE MATCHMAKING - PATRÓN STRATEGY");
                System.out.println("═".repeat(80));

                // ============================================
                // PASO 1: CREAR POOL DE USUARIOS CANDIDATOS
                // ============================================
                System.out.println("\nPASO 1: Creando pool de candidatos con perfiles variados...");
                System.out.println("-".repeat(80));

                List<Usuario> poolCandidatos = crearPoolCandidatos();

                System.out.println("- " + poolCandidatos.size() + " candidatos creados\n");
                mostrarPoolCandidatos(poolCandidatos);

                // ============================================
                // PASO 2: CREAR SCRIM
                // ============================================
                System.out.println("\n\nPASO 2: Creando scrim con requisitos...");
                System.out.println("-".repeat(80));

                Scrim scrim = new ScrimBuilder()
                                .withJuego(LeagueOfLegends.getInstance())
                                .withFormato(new Formato5v5LoL())
                                .withFechaHora(LocalDateTime.now().plusHours(2))
                                .withRango(1500, 2200)
                                .withLatenciaMaxima(100)
                                .build();

                System.out.println("Juego: " + scrim.getJuego().getNombre());
                System.out.println("Formato: " + scrim.getFormato());
                System.out.println("Rango: " + scrim.getRangoMin() + " - " + scrim.getRangoMax());
                System.out.println("Latencia máx: " + scrim.getLatenciaMax() + "ms");
                System.out.println("Plazas: " + scrim.getPlazas());

                // ============================================
                // PASO 3: ESTRATEGIA BY MMR
                // ============================================
                System.out.println("\n\nPASO 3: Aplicando estrategia BY MMR...");
                System.out.println("-".repeat(80));

                MatchmakerService serviceMMR = new MatchmakerService(new ByMMRStrategy());
                List<Usuario> seleccionadosMMR = serviceMMR.seleccionarJugadores(poolCandidatos, scrim);

                System.out.println("Estrategia: " + serviceMMR.getNombreEstrategia());
                System.out.println("Descripción: " + serviceMMR.getDescripcionEstrategia());
                System.out.println("\nJugadores seleccionados: " + seleccionadosMMR.size());
                mostrarJugadoresSeleccionados(seleccionadosMMR, "MMR");

                // ============================================
                // PASO 4: ESTRATEGIA BY LATENCY
                // ============================================
                System.out.println("\n\nPASO 4: Aplicando estrategia BY LATENCY...");
                System.out.println("-".repeat(80));

                MatchmakerService serviceLatency = new MatchmakerService(new ByLatencyStrategy());
                List<Usuario> seleccionadosLatency = serviceLatency.seleccionarJugadores(poolCandidatos, scrim);

                System.out.println("Estrategia: " + serviceLatency.getNombreEstrategia());
                System.out.println("Descripción: " + serviceLatency.getDescripcionEstrategia());
                System.out.println("\nJugadores seleccionados: " + seleccionadosLatency.size());
                mostrarJugadoresSeleccionados(seleccionadosLatency, "Latency");

                // ============================================
                // PASO 5: ESTRATEGIA BY HISTORY
                // ============================================
                System.out.println("\n\nPASO 5: Aplicando estrategia BY HISTORY...");
                System.out.println("-".repeat(80));

                MatchmakerService serviceHistory = new MatchmakerService(new ByHistoryStrategy());
                List<Usuario> seleccionadosHistory = serviceHistory.seleccionarJugadores(poolCandidatos, scrim);

                System.out.println("Estrategia: " + serviceHistory.getNombreEstrategia());
                System.out.println("Descripción: " + serviceHistory.getDescripcionEstrategia());
                System.out.println("\n- Jugadores seleccionados: " + seleccionadosHistory.size());
                mostrarJugadoresSeleccionados(seleccionadosHistory, "History");

                // ============================================
                // PASO 6: CAMBIO DINÁMICO DE ESTRATEGIA
                // ============================================
                System.out.println("\n\nPASO 6: Demostración de cambio dinámico de estrategia...");
                System.out.println("-".repeat(80));

                MatchmakerService service = new MatchmakerService(new ByMMRStrategy());
                System.out.println("Estrategia inicial: " + service.getNombreEstrategia());

                service.setEstrategia(new ByLatencyStrategy());
                System.out.println("Estrategia cambiada a: " + service.getNombreEstrategia());

                service.setEstrategia(new ByHistoryStrategy());
                System.out.println("Estrategia cambiada a: " + service.getNombreEstrategia());

                // ============================================
                // PASO 7: COMPARACIÓN DE RESULTADOS
                // ============================================
                System.out.println("\n\nPASO 7: Comparación de resultados...");
                System.out.println("-".repeat(80));

                compararResultados(seleccionadosMMR, seleccionadosLatency, seleccionadosHistory);

                System.out.println("\n" + "=".repeat(80));
                System.out.println("     TEST COMPLETADO EXITOSAMENTE");
                System.out.println("═".repeat(80));
        }

        /**
         * Crea un pool diverso de candidatos con diferentes perfiles.
         */
        private static List<Usuario> crearPoolCandidatos() {
                List<Usuario> pool = new ArrayList<>();
                Juego lol = LeagueOfLegends.getInstance();
                String nombreJuego = lol.getNombre(); // Usar getNombre() para consistencia

                // Usuario 1: Alto rango, latencia media, buen historial
                Usuario u1 = new Usuario("ProPlayer", "pro@escrims.com", "pass");
                u1.setRangoParaJuego(nombreJuego, 2100);
                u1.setLatenciaPromedio(60);
                u1.getHistorial().setPartidasJugadas(150);
                u1.getHistorial().setPartidasAbandonadas(5);
                u1.getHistorial().setTasaFairPlay(0.95);
                u1.agregarRolPreferido(LeagueOfLegends.getInstance(), "MID");
                pool.add(u1);

                // Usuario 2: Rango medio, excelente latencia, historial perfecto
                Usuario u2 = new Usuario("SpeedDemon", "speed@escrims.com", "pass");
                u2.setRangoParaJuego(nombreJuego, 1700);
                u2.setLatenciaPromedio(25);
                u2.getHistorial().setPartidasJugadas(80);
                u2.getHistorial().setPartidasAbandonadas(0);
                u2.getHistorial().setTasaFairPlay(1.0);
                u2.agregarRolPreferido(LeagueOfLegends.getInstance(), "ADC");
                pool.add(u2);

                // Usuario 3: Buen rango, alta latencia, buen historial
                Usuario u3 = new Usuario("LagKing", "lag@escrims.com", "pass");
                u3.setRangoParaJuego(nombreJuego, 1900);
                u3.setLatenciaPromedio(150);
                u3.getHistorial().setPartidasJugadas(100);
                u3.getHistorial().setPartidasAbandonadas(8);
                u3.getHistorial().setTasaFairPlay(0.85);
                u3.agregarRolPreferido(LeagueOfLegends.getInstance(), "TOP");
                pool.add(u3);

                // Usuario 4: Rango excelente, buena latencia, historial promedio
                Usuario u4 = new Usuario("HighElo", "high@escrims.com", "pass");
                u4.setRangoParaJuego(nombreJuego, 2200);
                u4.setLatenciaPromedio(45);
                u4.getHistorial().setPartidasJugadas(200);
                u4.getHistorial().setPartidasAbandonadas(25);
                u4.getHistorial().setTasaFairPlay(0.75);
                u4.agregarRolPreferido(LeagueOfLegends.getInstance(), "JUNGLE");
                pool.add(u4);

                // Usuario 5: Rango bajo límite, buena latencia, excelente historial
                Usuario u5 = new Usuario("SteadyPlayer", "steady@escrims.com", "pass");
                u5.setRangoParaJuego(nombreJuego, 1550);
                u5.setLatenciaPromedio(50);
                u5.getHistorial().setPartidasJugadas(120);
                u5.getHistorial().setPartidasAbandonadas(3);
                u5.getHistorial().setTasaFairPlay(0.98);
                u5.agregarRolPreferido(LeagueOfLegends.getInstance(), "SUPPORT");
                pool.add(u5);

                // Usuario 6: Rango medio-alto, latencia excelente, buen historial
                Usuario u6 = new Usuario("PingPerfect", "ping@escrims.com", "pass");
                u6.setRangoParaJuego(nombreJuego, 1850);
                u6.setLatenciaPromedio(20);
                u6.getHistorial().setPartidasJugadas(90);
                u6.getHistorial().setPartidasAbandonadas(4);
                u6.getHistorial().setTasaFairPlay(0.92);
                u6.agregarRolPreferido(LeagueOfLegends.getInstance(), "MID");
                pool.add(u6);

                // Usuario 7: Rango medio, latencia media, muchos abandonos (problemático)
                Usuario u7 = new Usuario("Ragequitter", "rage@escrims.com", "pass");
                u7.setRangoParaJuego(nombreJuego, 1650);
                u7.setLatenciaPromedio(70);
                u7.getHistorial().setPartidasJugadas(50);
                u7.getHistorial().setPartidasAbandonadas(18);
                u7.getHistorial().setTasaFairPlay(0.45); // Bajo fair play
                u7.agregarRolPreferido(LeagueOfLegends.getInstance(), "TOP");
                pool.add(u7);

                // Usuario 8: Rango alto, latencia buena, historial decente
                Usuario u8 = new Usuario("Climber", "climb@escrims.com", "pass");
                u8.setRangoParaJuego(nombreJuego, 2000);
                u8.setLatenciaPromedio(55);
                u8.getHistorial().setPartidasJugadas(110);
                u8.getHistorial().setPartidasAbandonadas(10);
                u8.getHistorial().setTasaFairPlay(0.88);
                u8.agregarRolPreferido(LeagueOfLegends.getInstance(), "ADC");
                pool.add(u8);

                return pool;
        }

        /**
         * Muestra información del pool de candidatos.
         */
        private static void mostrarPoolCandidatos(List<Usuario> pool) {
                System.out.println("\n" + String.format("%-15s | %-6s | %-8s | %-7s | %-10s | %-8s",
                                "Username", "Rango", "Latencia", "FairPlay", "Partidas", "Rol"));
                System.out.println("-".repeat(80));

                String nombreJuego = "League of Legends";
                for (Usuario u : pool) {
                        Integer rango = u.getRangoPorJuego().get(nombreJuego);
                        int latencia = u.getLatenciaPromedio();
                        HistorialUsuario hist = u.getHistorial();
                        double fairPlay = hist.getTasaFairPlay();
                        int partidas = hist.getPartidasJugadas();
                        String rol = u.getRolesPreferidosParaJuego(nombreJuego).isEmpty()
                                        ? "N/A"
                                        : u.getRolesPreferidosParaJuego(nombreJuego).get(0);

                        System.out.println(String.format("%-15s | %6d | %6dms | %7.2f | %4d/%4d | %-8s",
                                        u.getUsername(), rango, latencia, fairPlay,
                                        partidas - hist.getPartidasAbandonadas(), partidas, rol));
                }
        }

        /**
         * Muestra los jugadores seleccionados por una estrategia.
         */
        private static void mostrarJugadoresSeleccionados(List<Usuario> seleccionados, String estrategia) {
                if (seleccionados.isEmpty()) {
                        System.out.println("- No se encontraron suficientes candidatos válidos");
                        return;
                }

                System.out.println("\n" + String.format("%-15s | %-6s | %-8s | %-7s | %-5s",
                                "Username", "Rango", "Latencia", "FairPlay", "Score"));
                System.out.println("-".repeat(70));

                String nombreJuego = "League of Legends";
                for (Usuario u : seleccionados) {
                        Integer rango = u.getRangoPorJuego().get(nombreJuego);
                        int latencia = u.getLatenciaPromedio();
                        HistorialUsuario hist = u.getHistorial();
                        double fairPlay = hist.getTasaFairPlay();
                        double score = hist.getScoreConfiabilidad();

                        System.out.println(String.format("%-15s | %6d | %6dms | %7.2f | %5.1f",
                                        u.getUsername(), rango, latencia, fairPlay, score));
                }
        }

        /**
         * Compara los resultados de las tres estrategias.
         */
        private static void compararResultados(
                        List<Usuario> mmr,
                        List<Usuario> latency,
                        List<Usuario> history) {

                String nombreJuego = "League of Legends";

                // Calcular estadísticas
                double rangoPromedioMMR = calcularRangoPromedio(mmr, nombreJuego);
                double rangoPromedioLatency = calcularRangoPromedio(latency, nombreJuego);
                double rangoPromedioHistory = calcularRangoPromedio(history, nombreJuego);

                double latenciaPromedioMMR = ByLatencyStrategy.calcularLatenciaPromedio(mmr);
                double latenciaPromedioLatency = ByLatencyStrategy.calcularLatenciaPromedio(latency);
                double latenciaPromedioHistory = ByLatencyStrategy.calcularLatenciaPromedio(history);

                double compatibilidadMMR = ByHistoryStrategy.evaluarCompatibilidadGrupal(mmr);
                double compatibilidadLatency = ByHistoryStrategy.evaluarCompatibilidadGrupal(latency);
                double compatibilidadHistory = ByHistoryStrategy.evaluarCompatibilidadGrupal(history);

                double varianzaRolesMMR = ByHistoryStrategy.calcularVarianzaRoles(mmr, nombreJuego);
                double varianzaRolesLatency = ByHistoryStrategy.calcularVarianzaRoles(latency, nombreJuego);
                double varianzaRolesHistory = ByHistoryStrategy.calcularVarianzaRoles(history, nombreJuego);

                System.out.println("\n" + String.format("%-20s | %-10s | %-10s | %-10s",
                                "Métrica", "BY MMR", "BY LATENCY", "BY HISTORY"));
                System.out.println("-".repeat(70));

                System.out.println(String.format("%-20s | %10.0f | %10.0f | %10.0f",
                                "Rango promedio", rangoPromedioMMR, rangoPromedioLatency, rangoPromedioHistory));

                System.out.println(String.format("%-20s | %9.0fms | %9.0fms | %9.0fms",
                                "Latencia promedio", latenciaPromedioMMR, latenciaPromedioLatency,
                                latenciaPromedioHistory));

                System.out.println(String.format("%-20s | %10.1f | %10.1f | %10.1f",
                                "Compatibilidad", compatibilidadMMR, compatibilidadLatency, compatibilidadHistory));

                System.out.println(String.format("%-20s | %10.2f | %10.2f | %10.2f",
                                "Varianza roles", varianzaRolesMMR, varianzaRolesLatency, varianzaRolesHistory));

                System.out.println("\nAnálisis:");
                System.out.println("  • BY MMR: Mejor balance de skill (" + rangoPromedioMMR + " avg)");
                System.out.println("  • BY LATENCY: Menor latencia promedio (" + latenciaPromedioLatency + "ms)");
                System.out.println("  • BY HISTORY: Mayor compatibilidad grupal (" + compatibilidadHistory + " score)");
        }

        private static double calcularRangoPromedio(List<Usuario> usuarios, String nombreJuego) {
                return usuarios.stream()
                                .mapToInt(u -> u.getRangoPorJuego().get(nombreJuego))
                                .average()
                                .orElse(0.0);
        }
}
