package view;

import model.Scrim;
import model.estadisticas.EstadisticasScrim;
import model.estadisticas.GeneradorDatosRandom;
import model.estadisticas.ReporteConducta;

import java.util.List;
import java.util.Scanner;

public class EstadisticasView {
    private Scanner scanner;

    public EstadisticasView() {
        this.scanner = new Scanner(System.in);
    }

    public void mostrarMenu() {
        System.out.println("\n=== MÓDULO DE ESTADÍSTICAS ===");
        System.out.println("1. Ver estadísticas de partida actual");
        System.out.println("2. Ver historial de partidas");
        System.out.println("3. Ver reportes de conducta");
        System.out.println("4. Ver sistema de moderación");
        System.out.println("5. Volver al menú principal");
        System.out.print("Seleccione una opción: ");
    }

    public void mostrarEstadisticas(EstadisticasScrim estadisticas) {
        if (estadisticas == null) {
            System.out.println("No hay estadísticas disponibles para mostrar.");
            return;
        }

        System.out.println("\n=== ESTADÍSTICAS DE LA PARTIDA ===");
        System.out.println("ID de la partida: " + estadisticas.getScrimId());
        System.out.println("Inicio: " + estadisticas.getFechaHoraInicio());
        System.out.println("Duración: " + estadisticas.getDuracionMinutos() + " minutos");
        System.out.println("Ganador: " + (estadisticas.getGanador() != null ? estadisticas.getGanador() : "Sin determinar"));
        
        System.out.println("\n=== ESTADÍSTICAS DE EQUIPOS ===");
        System.out.println("Equipo | Victorias | Derrotas");
        System.out.println("----------------------------");
        estadisticas.getVictoriasPorEquipo().forEach((equipo, victorias) -> {
            int derrotas = estadisticas.getDerrotasPorEquipo().getOrDefault(equipo, 0);
            System.out.printf("%-10s | %-9d | %d%n", equipo, victorias, derrotas);
        });

        System.out.println("\n=== ESTADÍSTICAS DE PARTICIPANTES ===");
        System.out.println("Total participantes: " + estadisticas.getParticipantesTotales());
        System.out.println("Abandonos: " + estadisticas.getParticipantesAbandonaron());
        System.out.printf("Tasa de abandono: %.2f%%%n", estadisticas.calcularTasaAbandono());
        
        System.out.println("\n=== REPORTES DE CONDUCTA ===");
        System.out.println("Total de reportes: " + estadisticas.getReportes().size());
        if (!estadisticas.getReportes().isEmpty()) {
            System.out.println("\nÚltimos 3 reportes:");
            estadisticas.getReportes().stream()
                .limit(3)
                .forEach(reporte -> System.out.println("- " + reporte));
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
                reporte.getDescripcion().substring(0, Math.min(30, reporte.getDescripcion().length()))
            );
        }
    }

    public void mostrarMenuModeracion() {
        System.out.println("\n=== SISTEMA DE MODERACIÓN ===");
        System.out.println("1. Ver usuarios con más reportes");
        System.out.println("2. Ver sanciones activas");
        System.out.println("3. Revisar reportes pendientes");
        System.out.println("4. Volver al menú anterior");
        System.out.print("Seleccione una opción: ");
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

    public String leerTexto(String mensaje) {
        System.out.print(mensaje + ": ");
        return scanner.nextLine();
    }

    public void cerrar() {
        if (scanner != null) {
            scanner.close();
        }
    }
}
