package model.estadisticas;

import model.Scrim;
import java.time.LocalDateTime;
import java.util.*;

public class EstadisticasScrim {
    private String scrimId;
    private Map<String, Integer> victoriasPorEquipo;
    private Map<String, Integer> derrotasPorEquipo;
    private Map<String, Integer> puntuacionPromedio;
    private List<ReporteConducta> reportes;
    private LocalDateTime fechaHoraInicio;
    private LocalDateTime fechaHoraFin;
    private String ganador;
    private int duracionMinutos;
    private int participantesTotales;
    private int participantesAbandonaron;

    public EstadisticasScrim(Scrim scrim) {
        this.scrimId = scrim.getId();
        this.victoriasPorEquipo = new HashMap<>();
        this.derrotasPorEquipo = new HashMap<>();
        this.puntuacionPromedio = new HashMap<>();
        this.reportes = new ArrayList<>();
        this.fechaHoraInicio = LocalDateTime.now();
        this.participantesTotales = scrim.getListaConfirmaciones().size();
    }

    // Getters y setters
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
        this.ganador = equipoGanador;
        this.fechaHoraFin = LocalDateTime.now();
        this.duracionMinutos = (int) java.time.Duration.between(fechaHoraInicio, fechaHoraFin).toMinutes();
    }

    public void registrarAbandono(String usuarioId) {
        participantesAbandonaron++;
    }

    // Métodos para obtener estadísticas
    public double calcularTasaAbandono() {
        return participantesTotales > 0 ? (double) participantesAbandonaron / participantesTotales * 100 : 0;
    }

    public int getTotalReportes() {
        return reportes.size();
    }

    public List<ReporteConducta> getReportesPorUsuario(String usuarioId) {
        List<ReporteConducta> reportesUsuario = new ArrayList<>();
        for (ReporteConducta reporte : reportes) {
            if (reporte.getUsuarioReportadoId().equals(usuarioId)) {
                reportesUsuario.add(reporte);
            }
        }
        return reportesUsuario;
    }

    // Getters
    public String getScrimId() { return scrimId; }
    public Map<String, Integer> getVictoriasPorEquipo() { return victoriasPorEquipo; }
    public Map<String, Integer> getDerrotasPorEquipo() { return derrotasPorEquipo; }
    public Map<String, Integer> getPuntuacionPromedio() { return puntuacionPromedio; }
    public List<ReporteConducta> getReportes() { return reportes; }
    public LocalDateTime getFechaHoraInicio() { return fechaHoraInicio; }
    public LocalDateTime getFechaHoraFin() { return fechaHoraFin; }
    public String getGanador() { return ganador; }
    public int getDuracionMinutos() { return duracionMinutos; }
    public int getParticipantesTotales() { return participantesTotales; }
    public int getParticipantesAbandonaron() { return participantesAbandonaron; }
}
