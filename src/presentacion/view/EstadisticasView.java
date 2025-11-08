package presentacion.view;

import java.util.List;
import java.util.Scanner;

import dominio.estadisticas.EstadisticasJugador;
import dominio.estadisticas.EstadisticasPartido;
import dominio.estadisticas.EstadisticasScrim;
import dominio.estadisticas.ReporteConducta;
import dominio.estadisticas.SistemaModeracion;

public class EstadisticasView {
    private Scanner scanner;

    public EstadisticasView() {
        this.scanner = new Scanner(System.in);
    }

    public void mostrarTitulo() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("         REGISTRO DE ESTADÍSTICAS DEL PARTIDO");
        System.out.println("=".repeat(50));
    }

    public EstadisticasJugador solicitarEstadisticasJugador(Long jugadorId, String nombreJugador) {
        System.out.println("\n--- Estadísticas de " + nombreJugador + " ---");
        EstadisticasJugador stats = new EstadisticasJugador(jugadorId);

        System.out.print("Kills: ");
        stats.setKills(Integer.parseInt(scanner.nextLine()));

        System.out.print("Assists: ");
        stats.setAssists(Integer.parseInt(scanner.nextLine()));

        System.out.print("Deaths: ");
        stats.setDeaths(Integer.parseInt(scanner.nextLine()));

        System.out.print("Puntuación: ");
        stats.setPuntuacion(Integer.parseInt(scanner.nextLine()));

        return stats;
    }

    public Long seleccionarMVP(EstadisticasPartido estadisticas) {
        System.out.println("\n--- Selección de MVP ---");
        estadisticas.getEstadisticasPorJugador().forEach((id, stats) -> {
            System.out.printf("ID: %d | KDA: %.2f | Puntos: %d%n",
                    id, stats.getKDA(), stats.getPuntuacion());
        });

        System.out.print("\nID del MVP: ");
        return Long.parseLong(scanner.nextLine());
    }

    public void mostrarResumen(EstadisticasPartido estadisticas) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("              RESUMEN DE ESTADÍSTICAS");
        System.out.println("=".repeat(50));

        estadisticas.getEstadisticasPorJugador().forEach((id, stats) -> {
            String mvpLabel = stats.isEsMVP() ? " ⭐ MVP" : "";
            System.out.printf("\nJugador ID %d%s:%n", id, mvpLabel);
            System.out.printf("  K/D/A: %d/%d/%d (KDA: %.2f)%n",
                    stats.getKills(), stats.getDeaths(), stats.getAssists(), stats.getKDA());
            System.out.printf("  Puntuación: %d%n", stats.getPuntuacion());
        });
    }

    // ========== FUNCIONALIDAD EXTENDIDA: REPORTES Y MODERACIÓN ==========

    public void mostrarMenuCompleto() {
        System.out.println("\n=== MÓDULO DE ESTADÍSTICAS Y MODERACIÓN ===");
        System.out.println("1. Registrar estadísticas de jugador");
        System.out.println("2. Ver estadísticas de scrim");
        System.out.println("3. Reportar conducta");
        System.out.println("4. Ver reportes de un usuario");
        System.out.println("5. Ver estado de moderación de un usuario");
        System.out.println("6. Volver al menú principal");
        System.out.print("Seleccione una opción: ");
    }

    public void mostrarEstadisticasScrim(EstadisticasScrim estadisticas) {
        if (estadisticas == null) {
            System.out.println("No hay estadísticas disponibles para mostrar.");
            return;
        }

        System.out.println("\n=== ESTADÍSTICAS DEL SCRIM ===");
        System.out.println("ID: " + estadisticas.getScrimId());
        System.out.println("Inicio: " + estadisticas.getFechaHoraInicio());
        if (estadisticas.getFechaHoraFin() != null) {
            System.out.println("Fin: " + estadisticas.getFechaHoraFin());
            System.out.println("Duración: " + estadisticas.getDuracionMinutos() + " minutos");
        }
        System.out.println(
                "Ganador: " + (estadisticas.getGanador() != null ? estadisticas.getGanador() : "Sin determinar"));

        System.out.println("\n=== ESTADÍSTICAS DE EQUIPOS ===");
        if (!estadisticas.getVictoriasPorEquipo().isEmpty()) {
            System.out.println("Equipo | Victorias | Derrotas");
            System.out.println("----------------------------");
            estadisticas.getVictoriasPorEquipo().forEach((equipo, victorias) -> {
                int derrotas = estadisticas.getDerrotasPorEquipo().getOrDefault(equipo, 0);
                System.out.printf("%-10s | %-9d | %d%n", equipo, victorias, derrotas);
            });
        } else {
            System.out.println("No hay estadísticas de equipos registradas.");
        }

        System.out.println("\n=== PARTICIPANTES ===");
        System.out.println("Total participantes: " + estadisticas.getParticipantesTotales());
        System.out.println("Abandonos: " + estadisticas.getParticipantesAbandonaron());
        System.out.printf("Tasa de abandono: %.2f%%%n", estadisticas.calcularTasaAbandono());

        System.out.println("\n=== REPORTES DE CONDUCTA ===");
        System.out.println("Total de reportes: " + estadisticas.getReportes().size());
        if (!estadisticas.getReportes().isEmpty()) {
            System.out.println("\nReportes:");
            estadisticas.getReportes().forEach(reporte -> System.out.println(
                    "- " + reporte.getTipo() + " (" + reporte.getGravedad() + "): " + reporte.getDescripcion()));
        }
    }

    public void mostrarReportes(List<ReporteConducta> reportes) {
        if (reportes.isEmpty()) {
            System.out.println("No hay reportes para mostrar.");
            return;
        }

        System.out.println("\n=== REPORTES DE CONDUCTA ===");
        System.out.printf("%-10s | %-15s | %-20s | %-15s | %-10s | %-30s%n",
                "ID", "Usuario", "Tipo", "Gravedad", "Estado", "Descripción");
        System.out.println("-".repeat(110));

        for (ReporteConducta reporte : reportes) {
            System.out.printf("%-10s | %-15s | %-20s | %-15s | %-10s | %-30s%n",
                    reporte.getId().substring(0, 8),
                    reporte.getUsuarioReportadoId(),
                    reporte.getTipo(),
                    reporte.getGravedad(),
                    reporte.isRevisado() ? (reporte.isSancionado() ? "Sancionado" : "Revisado") : "Pendiente",
                    reporte.getDescripcion().substring(0, Math.min(30, reporte.getDescripcion().length())));
        }
    }

    public void mostrarEstadoModeracion(String usuarioId, SistemaModeracion moderacion) {
        System.out.println("\n=== ESTADO DE MODERACIÓN ===");
        System.out.println("Usuario: " + usuarioId);
        System.out.println("Strikes actuales: " + moderacion.getStrikes(usuarioId));
        System.out.println("Total reportes: " + moderacion.getReportesUsuario(usuarioId).size());

        if (moderacion.estaPenalizado(usuarioId)) {
            long horasRestantes = moderacion.getTiempoRestantePenalizacion(usuarioId);
            System.out.println("🔴 PENALIZADO - Horas restantes: " + horasRestantes);
        } else if (moderacion.estaEnCooldown(usuarioId)) {
            System.out.println("🟡 EN COOLDOWN");
        } else {
            System.out.println("🟢 Sin penalizaciones activas");
        }
    }

    public String solicitarScrimId() {
        System.out.print("Ingrese el ID del scrim: ");
        return scanner.nextLine().trim();
    }

    public String solicitarUsuarioId() {
        System.out.print("Ingrese el ID del usuario: ");
        return scanner.nextLine().trim();
    }

    public ReporteConducta.TipoReporte solicitarTipoReporte() {
        System.out.println("\n=== TIPOS DE REPORTE ===");
        ReporteConducta.TipoReporte[] tipos = ReporteConducta.TipoReporte.values();
        for (int i = 0; i < tipos.length; i++) {
            System.out.println((i + 1) + ". " + tipos[i]);
        }
        System.out.print("Seleccione tipo de reporte (1-" + tipos.length + "): ");

        try {
            int opcion = Integer.parseInt(scanner.nextLine());
            if (opcion >= 1 && opcion <= tipos.length) {
                return tipos[opcion - 1];
            }
        } catch (NumberFormatException e) {
            // Fall through to default
        }

        System.out.println("Opción inválida, usando OTRO por defecto.");
        return ReporteConducta.TipoReporte.OTRO;
    }

    public ReporteConducta.Gravedad solicitarGravedad() {
        System.out.println("\n=== GRAVEDAD ===");
        ReporteConducta.Gravedad[] gravedades = ReporteConducta.Gravedad.values();
        for (int i = 0; i < gravedades.length; i++) {
            System.out.println((i + 1) + ". " + gravedades[i]);
        }
        System.out.print("Seleccione gravedad (1-" + gravedades.length + "): ");

        try {
            int opcion = Integer.parseInt(scanner.nextLine());
            if (opcion >= 1 && opcion <= gravedades.length) {
                return gravedades[opcion - 1];
            }
        } catch (NumberFormatException e) {
            // Fall through to default
        }

        System.out.println("Opción inválida, usando LEVE por defecto.");
        return ReporteConducta.Gravedad.LEVE;
    }

    public String solicitarDescripcion() {
        System.out.print("Descripción del reporte: ");
        return scanner.nextLine().trim();
    }

    public String solicitarEquipoGanador() {
        System.out.print("Ingrese el equipo ganador: ");
        return scanner.nextLine().trim();
    }

    public void mostrarMensaje(String mensaje) {
        System.out.println("\n" + mensaje);
    }

    public int leerOpcion() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
