package presentacion.view;

import java.util.List;
import java.util.Scanner;

import dominio.modelo.Scrim;
import infraestructura.matchmaking.MatchmakingRegistry;
import infraestructura.matchmaking.MatchmakingStrategy;

/**
 * Vista para gestionar estrategias de matchmaking de scrims existentes.
 * Permite al organizador cambiar la estrategia de matchmaking de sus scrims.
 * 
 * @author eScrims Team
 */
public class GestionMatchmakingView {

    private Scanner scanner;

    public GestionMatchmakingView() {
        this.scanner = new Scanner(System.in);
    }

    public void mostrarTitulo() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("           GESTIÓN DE ESTRATEGIAS DE MATCHMAKING");
        System.out.println("=".repeat(60));
    }

    /**
     * Muestra la lista de scrims del organizador enumerados.
     */
    public void mostrarScrimsOrganizador(List<Scrim> scrims) {
        System.out.println("\n--- Sus Scrims ---");

        if (scrims.isEmpty()) {
            System.out.println("No tienes scrims creados.");
            return;
        }

        for (int i = 0; i < scrims.size(); i++) {
            Scrim scrim = scrims.get(i);
            System.out.printf("%d. %s - %s (Estado: %s, Matchmaking: %s)\n",
                    i + 1,
                    scrim.getJuego().getNombre(),
                    scrim.getFormato().getFormatName(),
                    scrim.getState().getEstado(),
                    scrim.getEstrategiaMatchmaking());
        }
    }

    /**
     * Solicita al usuario seleccionar un scrim por número.
     */
    public int solicitarNumeroScrim(int cantidadScrims) {
        System.out.println("\nSeleccione el número del scrim para cambiar su estrategia de matchmaking:");
        System.out.println("0. Volver al menú anterior");
        System.out.print("Opción: ");

        try {
            int numero = Integer.parseInt(scanner.nextLine().trim());

            if (numero == 0) {
                return 0; // Cancelar
            }

            if (numero < 1 || numero > cantidadScrims) {
                System.err.println("Número inválido. Debe estar entre 1 y " + cantidadScrims);
                return solicitarNumeroScrim(cantidadScrims);
            }

            return numero;
        } catch (NumberFormatException e) {
            System.err.println("Debe ingresar un número válido");
            return solicitarNumeroScrim(cantidadScrims);
        }
    }

    /**
     * Muestra los detalles del scrim seleccionado.
     */
    public void mostrarDetalleScrim(Scrim scrim) {
        System.out.println("\n" + "-".repeat(50));
        System.out.println("DETALLES DEL SCRIM SELECCIONADO");
        System.out.println("-".repeat(50));
        System.out.println("ID: " + scrim.getId());
        System.out.println("Juego: " + scrim.getJuego().getNombre());
        System.out.println("Formato: " + scrim.getFormato().getFormatName());
        System.out.println("Fecha/Hora: " + scrim.getFechaHora());
        System.out.println("Rango: " + scrim.getRangoMin() + " - " + scrim.getRangoMax());
        System.out.println(
                "Latencia máx: " + (scrim.getLatenciaMax() == -1 ? "Sin límite" : scrim.getLatenciaMax() + " ms"));
        System.out.println("Estado: " + scrim.getState().getEstado());
        System.out.println("Estrategia actual: " + scrim.getEstrategiaMatchmaking());
        System.out.println("-".repeat(50));
    }

    /**
     * Solicita al usuario seleccionar una nueva estrategia de matchmaking.
     */
    public String solicitarNuevaEstrategia(String estrategiaActual) {
        System.out.println("\n--- Seleccionar Nueva Estrategia ---");
        System.out.println("Estrategia actual: " + estrategiaActual);
        System.out.println("\nEstrategias disponibles:");

        MatchmakingRegistry registry = MatchmakingRegistry.getInstance();
        List<MatchmakingStrategy> estrategias = registry.getEstrategiasDisponibles();

        for (int i = 0; i < estrategias.size(); i++) {
            MatchmakingStrategy estrategia = estrategias.get(i);
            String marca = estrategia.getNombre().equals(estrategiaActual) ? " (ACTUAL)" : "";
            System.out.println((i + 1) + ". " + estrategia.getNombre() +
                    " - " + estrategia.getDescripcion() + marca);
        }
        System.out.println("0. Cancelar");
        System.out.print("Opción: ");

        try {
            int opcion = Integer.parseInt(scanner.nextLine().trim());
            if (opcion == 0) {
                return null; // Cancelar
            }
            if (opcion > 0 && opcion <= estrategias.size()) {
                return estrategias.get(opcion - 1).getNombre();
            }
        } catch (NumberFormatException e) {
            System.err.println("Opción inválida");
        }

        return solicitarNuevaEstrategia(estrategiaActual); // Reintentar
    }

    /**
     * Solicita confirmación para cambiar la estrategia.
     */
    public boolean confirmarCambioEstrategia(String estrategiaActual, String nuevaEstrategia) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("CONFIRMAR CAMBIO DE ESTRATEGIA");
        System.out.println("=".repeat(50));
        System.out.println("Estrategia actual: " + estrategiaActual);
        System.out.println("Nueva estrategia:  " + nuevaEstrategia);
        System.out.println("=".repeat(50));

        System.out.print("\n¿Confirmar cambio? (s/n): ");
        String respuesta = scanner.nextLine().trim().toLowerCase();
        return respuesta.equals("s") || respuesta.equals("si") || respuesta.equals("sí");
    }

    // ================== Métodos para el Controller ==================

    public void mostrarExito(String mensaje) {
        System.out.println("\n- " + mensaje);
    }

    public void mostrarError(String mensaje) {
        System.err.println("\n- " + mensaje);
    }

    public void mostrarInfo(String mensaje) {
        System.out.println("\n" + mensaje);
    }

    public void mostrarCancelacion() {
        System.out.println("\nOperación cancelada");
    }

    public void pausaParaContinuar() {
        System.out.print("\nPresione Enter para continuar...");
        scanner.nextLine();
    }
}