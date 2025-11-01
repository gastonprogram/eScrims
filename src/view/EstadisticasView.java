package view;

import model.estadisticas.EstadisticasJugador;
import model.estadisticas.EstadisticasPartido;
import java.util.Scanner;

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
}
