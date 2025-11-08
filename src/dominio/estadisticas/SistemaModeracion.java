package dominio.estadisticas;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Sistema simple de moderación y penalidades.
 * - Registra reportes
 * - Lleva strikes por usuario
 * - Aplica penalizaciones temporales cuando se superan strikes
 * - Permite cooldowns y limpieza de reportes antiguos
 */
public class SistemaModeracion {
    private static final int MAX_STRIKES = 3;
    private static final int COOLDOWN_HORAS = 24;

    private Map<String, Integer> strikesPorUsuario;
    private Map<String, LocalDateTime> cooldownUsuarios;
    private Map<String, List<ReporteConducta>> reportesPorUsuario;
    private Map<String, LocalDateTime> penalizacionesActivas;

    public SistemaModeracion() {
        this.strikesPorUsuario = new HashMap<>();
        this.cooldownUsuarios = new HashMap<>();
        this.reportesPorUsuario = new HashMap<>();
        this.penalizacionesActivas = new HashMap<>();
    }

    public void registrarReporte(ReporteConducta reporte) {
        String usuarioId = reporte.getUsuarioReportadoId();

        // Agregar a la lista de reportes del usuario
        reportesPorUsuario.computeIfAbsent(usuarioId, k -> new ArrayList<>()).add(reporte);

        // Aplicar strike si corresponde
        if (reporte.getGravedad() != ReporteConducta.Gravedad.LEVE) {
            int strikes = strikesPorUsuario.getOrDefault(usuarioId, 0) + 1;
            strikesPorUsuario.put(usuarioId, strikes);

            if (strikes >= MAX_STRIKES) {
                aplicarPenalizacion(usuarioId, strikes);
            }
        }
    }

    private void aplicarPenalizacion(String usuarioId, int strikes) {
        int horasPenalizacion = strikes * 24; // 24h por cada strike
        LocalDateTime finPenalizacion = LocalDateTime.now().plusHours(horasPenalizacion);
        penalizacionesActivas.put(usuarioId, finPenalizacion);

        // Reiniciar strikes después de aplicar penalización
        strikesPorUsuario.put(usuarioId, 0);
    }

    public boolean estaEnCooldown(String usuarioId) {
        if (cooldownUsuarios.containsKey(usuarioId)) {
            LocalDateTime finCooldown = cooldownUsuarios.get(usuarioId);
            if (LocalDateTime.now().isBefore(finCooldown)) {
                return true;
            } else {
                cooldownUsuarios.remove(usuarioId);
                return false;
            }
        }
        return false;
    }

    public boolean estaPenalizado(String usuarioId) {
        if (penalizacionesActivas.containsKey(usuarioId)) {
            LocalDateTime finPenalizacion = penalizacionesActivas.get(usuarioId);
            if (LocalDateTime.now().isBefore(finPenalizacion)) {
                return true;
            } else {
                penalizacionesActivas.remove(usuarioId);
                return false;
            }
        }
        return false;
    }

    public long getTiempoRestantePenalizacion(String usuarioId) {
        if (penalizacionesActivas.containsKey(usuarioId)) {
            LocalDateTime ahora = LocalDateTime.now();
            LocalDateTime fin = penalizacionesActivas.get(usuarioId);
            if (ahora.isBefore(fin)) {
                return ChronoUnit.HOURS.between(ahora, fin);
            }
        }
        return 0;
    }

    public int getStrikes(String usuarioId) {
        return strikesPorUsuario.getOrDefault(usuarioId, 0);
    }

    public List<ReporteConducta> getReportesUsuario(String usuarioId) {
        return reportesPorUsuario.getOrDefault(usuarioId, new ArrayList<>());
    }

    public void aplicarCooldown(String usuarioId) {
        cooldownUsuarios.put(usuarioId, LocalDateTime.now().plusHours(COOLDOWN_HORAS));
    }

    public void limpiarReportesAntiguos(int dias) {
        LocalDateTime limite = LocalDateTime.now().minusDays(dias);

        reportesPorUsuario.forEach((usuarioId, reportes) -> {
            reportes.removeIf(reporte -> reporte.getFechaHora().isBefore(limite));
        });

        // Limpiar usuarios sin reportes
        reportesPorUsuario.entrySet().removeIf(entry -> entry.getValue().isEmpty());
    }
}
