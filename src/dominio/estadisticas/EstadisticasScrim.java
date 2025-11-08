package dominio.estadisticas;

import dominio.modelo.Scrim;
import compartido.utils.SimuladorPartida;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Estadísticas completas asociadas a un Scrim (partida).
 * Incluye estadísticas individuales de jugadores, reportes y métricas
 * generales.
 * 
 * Diseño integrado: Un solo objeto contiene toda la información estadística del
 * scrim.
 */
public class EstadisticasScrim {
    private String scrimId;
    private Scrim scrimReferencia; // Referencia al scrim para acceder a juego y formato

    // Estadísticas individuales por jugador
    private Map<String, EstadisticasJugador> estadisticasPorJugador;

    // Estadísticas de equipos (legacy - mantenidas para compatibilidad)
    private Map<String, Integer> victoriasPorEquipo;
    private Map<String, Integer> derrotasPorEquipo;
    private Map<String, Integer> puntuacionPromedio;

    // Reportes y moderación
    private List<ReporteConducta> reportes;

    // Información general del scrim
    private LocalDateTime fechaHoraInicio;
    private LocalDateTime fechaHoraFin;
    private String ganador;
    private int duracionMinutos;

    public EstadisticasScrim(Scrim scrim) {
        this.scrimId = scrim.getId();
        this.scrimReferencia = scrim; // Guardar referencia para el simulador
        this.estadisticasPorJugador = new HashMap<>();
        this.victoriasPorEquipo = new HashMap<>();
        this.derrotasPorEquipo = new HashMap<>();
        this.puntuacionPromedio = new HashMap<>();
        this.reportes = new ArrayList<>();
        this.fechaHoraInicio = LocalDateTime.now();

        // Inicializar participantes desde confirmaciones del scrim
        try {
            if (scrim.getConfirmaciones() != null && !scrim.getConfirmaciones().isEmpty()) {
                // Crear estadísticas vacías para cada participante confirmado
                scrim.getConfirmaciones().stream()
                        .filter(confirmacion -> confirmacion != null && confirmacion.isConfirmada())
                        .forEach(confirmacion -> {
                            estadisticasPorJugador.put(confirmacion.getUserId(),
                                    new EstadisticasJugador(confirmacion.getUserId()));
                        });
            }
        } catch (Exception e) {
            // En caso de error, continuar sin participantes
        }
    }

    /**
     * Constructor alternativo cuando solo se conoce el id del scrim
     */
    public EstadisticasScrim(String scrimId) {
        this.scrimId = scrimId;
        this.scrimReferencia = null; // No hay referencia disponible
        this.estadisticasPorJugador = new HashMap<>();
        this.victoriasPorEquipo = new HashMap<>();
        this.derrotasPorEquipo = new HashMap<>();
        this.puntuacionPromedio = new HashMap<>();
        this.reportes = new ArrayList<>();
        this.fechaHoraInicio = LocalDateTime.now();
    }

    // ========== MÉTODOS PARA ESTADÍSTICAS INDIVIDUALES ==========

    /**
     * Registra las estadísticas completas de un jugador.
     */
    public void registrarEstadisticasJugador(String userId, int kills, int assists, int deaths, int puntuacion) {
        EstadisticasJugador stats = new EstadisticasJugador(userId, kills, assists, deaths, puntuacion);
        estadisticasPorJugador.put(userId, stats);
    }

    /**
     * Obtiene las estadísticas de un jugador específico.
     */
    public EstadisticasJugador obtenerEstadisticasJugador(String userId) {
        return estadisticasPorJugador.get(userId);
    }

    /**
     * Designa a un jugador como MVP del scrim.
     */
    public void designarMVP(String userId) {
        // Limpiar MVP anterior
        estadisticasPorJugador.values().forEach(stats -> stats.setEsMVP(false));

        // Establecer nuevo MVP
        EstadisticasJugador mvpStats = estadisticasPorJugador.get(userId);
        if (mvpStats != null) {
            mvpStats.setEsMVP(true);
        }
    }

    /**
     * Obtiene el ranking de jugadores ordenado por KDA.
     */
    public List<EstadisticasJugador> obtenerRankingPorKDA() {
        return estadisticasPorJugador.values().stream()
                .sorted((a, b) -> Double.compare(b.getKDA(), a.getKDA()))
                .toList();
    }

    /**
     * Obtiene el ranking de jugadores ordenado por puntuación.
     */
    public List<EstadisticasJugador> obtenerRankingPorPuntuacion() {
        return estadisticasPorJugador.values().stream()
                .sorted((a, b) -> Integer.compare(b.getPuntuacion(), a.getPuntuacion()))
                .toList();
    }

    /**
     * Obtiene el jugador MVP actual.
     */
    public EstadisticasJugador obtenerMVP() {
        return estadisticasPorJugador.values().stream()
                .filter(EstadisticasJugador::isEsMVP)
                .findFirst()
                .orElse(null);
    }

    /**
     * Verifica si todos los jugadores tienen estadísticas registradas.
     */
    public boolean tieneEstadisticasCompletas() {
        return estadisticasPorJugador.values().stream()
                .allMatch(stats -> stats.getKills() > 0 || stats.getAssists() > 0 ||
                        stats.getDeaths() > 0 || stats.getPuntuacion() > 0);
    }

    /**
     * Obtiene todas las estadísticas de jugadores.
     */
    public Collection<EstadisticasJugador> obtenerTodasLasEstadisticas() {
        return estadisticasPorJugador.values();
    }

    /**
     * Obtiene el mapa completo de estadísticas por jugador.
     */
    public Map<String, EstadisticasJugador> getEstadisticasPorJugador() {
        return new HashMap<>(estadisticasPorJugador);
    }

    // ========== MÉTODOS LEGACY (para compatibilidad) ==========

    public void registrarVictoria(String equipo) {
        victoriasPorEquipo.put(equipo, victoriasPorEquipo.getOrDefault(equipo, 0) + 1);
    }

    public void registrarDerrota(String equipo) {
        derrotasPorEquipo.put(equipo, derrotasPorEquipo.getOrDefault(equipo, 0) + 1);
    }

    public void agregarReporte(ReporteConducta reporte) {
        reportes.add(reporte);
    }

    public void finalizarPartida(String equipoGanador) {
        this.fechaHoraFin = LocalDateTime.now();

        // Usar el simulador para generar una duración realista
        if (scrimReferencia != null) {
            this.duracionMinutos = SimuladorPartida.simularPartidaCompleta(
                    scrimReferencia.getJuego(),
                    scrimReferencia.getFormato());
        } else {
            this.duracionMinutos = SimuladorPartida.simularDuracionGeneral();
        }

        // Si no se especificó ganador, determinarlo por estadísticas
        if (equipoGanador == null || equipoGanador.trim().isEmpty()) {
            String[] equipos = scrimReferencia != null
                    ? SimuladorPartida.generarNombresEquipos(scrimReferencia.getJuego())
                    : SimuladorPartida.generarNombresEquipos(null);
            this.ganador = determinarGanadorPorEstadisticas(equipos[0], equipos[1]);
        } else {
            this.ganador = equipoGanador;
        }
    }

    /**
     * Finaliza la partida con simulación completa de equipos.
     * Útil cuando no se tiene información previa de equipos.
     */
    public void finalizarPartidaConSimulacion() {
        this.fechaHoraFin = LocalDateTime.now();

        // Generar nombres de equipos aleatorios
        String[] equipos;
        if (scrimReferencia != null) {
            equipos = SimuladorPartida.generarNombresEquipos(scrimReferencia.getJuego());
            this.duracionMinutos = SimuladorPartida.simularPartidaCompleta(
                    scrimReferencia.getJuego(),
                    scrimReferencia.getFormato());
        } else {
            equipos = SimuladorPartida.generarNombresEquipos(null);
            this.duracionMinutos = SimuladorPartida.simularDuracionGeneral();
        }

        // Determinar ganador basándose en estadísticas reales
        this.ganador = determinarGanadorPorEstadisticas(equipos[0], equipos[1]);
    }

    /**
     * Determina el equipo ganador basándose en las estadísticas de los jugadores.
     * Asigna jugadores aleatoriamente a equipos y calcula el rendimiento promedio.
     */
    private String determinarGanadorPorEstadisticas(String equipo1, String equipo2) {
        if (estadisticasPorJugador.isEmpty()) {
            // Si no hay estadísticas, usar aleatorio como fallback
            return SimuladorPartida.determinarGanadorAleatorio(equipo1, equipo2);
        }

        // Dividir jugadores en dos equipos aleatoriamente
        java.util.List<EstadisticasJugador> jugadores = new java.util.ArrayList<>(estadisticasPorJugador.values());
        java.util.Collections.shuffle(jugadores);

        int mitad = jugadores.size() / 2;
        java.util.List<EstadisticasJugador> equipoA = jugadores.subList(0, mitad);
        java.util.List<EstadisticasJugador> equipoB = jugadores.subList(mitad, jugadores.size());

        // Calcular rendimiento promedio de cada equipo
        double rendimientoA = calcularRendimientoEquipo(equipoA);
        double rendimientoB = calcularRendimientoEquipo(equipoB);

        // El equipo con mejor rendimiento gana
        return rendimientoA > rendimientoB ? equipo1 : equipo2;
    }

    /**
     * Calcula el rendimiento promedio de un equipo basándose en KDA y puntuación.
     */
    private double calcularRendimientoEquipo(java.util.List<EstadisticasJugador> equipo) {
        if (equipo.isEmpty())
            return 0.0;

        double sumaKDA = equipo.stream().mapToDouble(EstadisticasJugador::getKDA).sum();
        double sumaPuntuacion = equipo.stream().mapToDouble(EstadisticasJugador::getPuntuacion).sum();

        // Ponderar KDA (70%) y puntuación (30%)
        double promedioKDA = sumaKDA / equipo.size();
        double promedioPuntuacion = sumaPuntuacion / equipo.size();

        return (promedioKDA * 0.7) + (promedioPuntuacion * 0.0003); // 0.0003 para normalizar puntos
    }

    // ========== MÉTODOS DE CONSULTA ==========

    public int getTotalReportes() {
        return reportes.size();
    }

    public List<ReporteConducta> getReportesPorUsuario(String usuarioId) {
        return reportes.stream()
                .filter(reporte -> reporte.getUsuarioReportadoId().equals(usuarioId))
                .toList();
    }

    // ========== GETTERS ==========
    public String getScrimId() {
        return scrimId;
    }

    public Map<String, Integer> getVictoriasPorEquipo() {
        return victoriasPorEquipo;
    }

    public Map<String, Integer> getDerrotasPorEquipo() {
        return derrotasPorEquipo;
    }

    public Map<String, Integer> getPuntuacionPromedio() {
        return puntuacionPromedio;
    }

    public List<ReporteConducta> getReportes() {
        return reportes;
    }

    public LocalDateTime getFechaHoraInicio() {
        return fechaHoraInicio;
    }

    public LocalDateTime getFechaHoraFin() {
        return fechaHoraFin;
    }

    public String getGanador() {
        return ganador;
    }

    public int getDuracionMinutos() {
        return duracionMinutos;
    }

    /**
     * Obtiene una descripción del tipo de partida basada en su duración.
     */
    public String getDescripcionPartida() {
        return SimuladorPartida.obtenerDescripcionPartida(duracionMinutos);
    }

    /**
     * Muestra cómo se dividieron los equipos para la partida.
     * Útil para debugging y visualización.
     */
    public String getFormacionEquipos() {
        if (estadisticasPorJugador.isEmpty()) {
            return "No hay jugadores registrados";
        }

        java.util.List<EstadisticasJugador> jugadores = new java.util.ArrayList<>(estadisticasPorJugador.values());
        java.util.Collections.shuffle(jugadores); // Usar la misma lógica que en determinarGanadorPorEstadisticas

        int mitad = jugadores.size() / 2;
        java.util.List<EstadisticasJugador> equipoA = jugadores.subList(0, mitad);
        java.util.List<EstadisticasJugador> equipoB = jugadores.subList(mitad, jugadores.size());

        StringBuilder sb = new StringBuilder();
        sb.append("FORMACIÓN DE EQUIPOS:\n");
        sb.append("Equipo A: ");
        equipoA.forEach(j -> sb.append(j.getJugadorId().substring(j.getJugadorId().length() - 1)).append(" "));
        sb.append("\nEquipo B: ");
        equipoB.forEach(j -> sb.append(j.getJugadorId().substring(j.getJugadorId().length() - 1)).append(" "));

        return sb.toString();
    }
}
