package presentacion.view;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

import dominio.juegos.Juego;
import dominio.juegos.JuegosRegistry;
import dominio.valueobjects.formatosScrims.ScrimFormat;
import infraestructura.matchmaking.MatchmakingRegistry;
import infraestructura.matchmaking.MatchmakingStrategy;

/**
 * Vista para crear scrims mediante formularios en consola.
 * Guía al usuario paso a paso para ingresar todos los datos necesarios.
 * Refactorizada para usar el nuevo ScrimController.
 * 
 * @author eScrims Team
 */
public class CrearScrimView {

    private Scanner scanner;
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public CrearScrimView() {
        this.scanner = new Scanner(System.in);
    }

    public void mostrarTitulo() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("           CREAR NUEVO SCRIM");
        System.out.println("=".repeat(50));
    }

    /**
     * Solicita al usuario seleccionar un juego de todos los disponibles.
     */
    public Juego solicitarJuego() {
        System.out.println("\n--- Seleccione el Juego ---");

        JuegosRegistry registry = JuegosRegistry.getInstance();
        List<Juego> juegos = registry.getJuegosDisponibles();

        for (int i = 0; i < juegos.size(); i++) {
            System.out.println((i + 1) + ". " + juegos.get(i).getNombre());
        }
        System.out.println("0. Cancelar");
        System.out.print("Opción: ");

        try {
            int opcion = Integer.parseInt(scanner.nextLine().trim());
            if (opcion >= 1 && opcion <= juegos.size()) {
                return juegos.get(opcion - 1);
            } else if (opcion == 0) {
                return null; // Cancelar
            }
        } catch (NumberFormatException e) {
            System.err.println("✗ Opción inválida");
        }

        return solicitarJuego(); // Reintentar
    }

    /**
     * Solicita al usuario seleccionar un formato del juego.
     */
    public ScrimFormat solicitarFormato(Juego juego) {
        System.out.println("\n--- Seleccione el Formato ---");

        List<ScrimFormat> formatos = juego.getFormatosDisponibles();
        for (int i = 0; i < formatos.size(); i++) {
            ScrimFormat formato = formatos.get(i);
            System.out.println((i + 1) + ". " + formato.getFormatName() +
                    " (" + (formato.getPlayersPerTeam() * 2) + " jugadores)");
        }
        System.out.println("0. Cancelar");
        System.out.print("Opción: ");

        try {
            int opcion = Integer.parseInt(scanner.nextLine().trim());
            if (opcion == 0) {
                return null; // Cancelar
            }
            if (opcion > 0 && opcion <= formatos.size()) {
                return formatos.get(opcion - 1);
            }
        } catch (NumberFormatException e) {
            System.err.println("✗ Opción inválida");
        }

        return solicitarFormato(juego); // Reintentar
    }

    /**
     * Solicita la fecha y hora del scrim.
     */
    public LocalDateTime solicitarFechaHora() {
        System.out.println("\n--- Fecha y Hora del Scrim ---");
        System.out.println("Formato: dd/MM/yyyy HH:mm");
        System.out.println("Ejemplo: 31/12/2025 20:30");
        System.out.print("Fecha y hora: ");

        String input = scanner.nextLine().trim();

        if (input.equalsIgnoreCase("cancelar")) {
            return null;
        }

        try {
            LocalDateTime fechaHora = LocalDateTime.parse(input, FORMATO_FECHA);

            // Validar que sea en el futuro
            if (fechaHora.isBefore(LocalDateTime.now())) {
                System.err.println("✗ La fecha debe ser en el futuro");
                return solicitarFechaHora();
            }

            return fechaHora;
        } catch (DateTimeParseException e) {
            System.err.println("✗ Formato de fecha inválido");
            return solicitarFechaHora();
        }
    }

    /**
     * Solicita el rango mínimo permitido.
     */
    public int solicitarRangoMinimo() {
        System.out.println("\n--- Rango Mínimo ---");
        System.out.println("Ej: 1 (Iron), 20 (Silver), 40 (Gold), 60 (Platinum), 80 (Diamond), 100 (Master)");
        System.out.print("Rango mínimo (1-100): ");

        try {
            int rango = Integer.parseInt(scanner.nextLine().trim());
            if (rango < 1 || rango > 100) {
                System.err.println("✗ El rango debe estar entre 1 y 100");
                return solicitarRangoMinimo();
            }
            return rango;
        } catch (NumberFormatException e) {
            System.err.println("✗ Debe ingresar un número");
            return solicitarRangoMinimo();
        }
    }

    /**
     * Solicita el rango máximo permitido.
     */
    public int solicitarRangoMaximo() {
        System.out.println("\n--- Rango Máximo ---");
        System.out.println("Ej: 60 (Platinum), 80 (Diamond), 100 (Master/Challenger)");
        System.out.print("Rango máximo (1-100): ");

        try {
            int rango = Integer.parseInt(scanner.nextLine().trim());
            if (rango < 1 || rango > 100) {
                System.err.println("✗ El rango debe estar entre 1 y 100");
                return solicitarRangoMaximo();
            }
            return rango;
        } catch (NumberFormatException e) {
            System.err.println("✗ Debe ingresar un número");
            return solicitarRangoMaximo();
        }
    }

    /**
     * Solicita la latencia máxima permitida.
     */
    public int solicitarLatenciaMaxima() {
        System.out.println("\n--- Latencia Máxima ---");
        System.out.println("Latencia máxima permitida en ms (recomendado: 50-100ms)");
        System.out.print("Latencia máxima: ");

        try {
            int latencia = Integer.parseInt(scanner.nextLine().trim());
            if (latencia < 0 || latencia > 1000) {
                System.err.println("✗ La latencia debe estar entre 0 y 1000ms");
                return solicitarLatenciaMaxima();
            }
            return latencia;
        } catch (NumberFormatException e) {
            System.err.println("✗ Debe ingresar un número");
            return solicitarLatenciaMaxima();
        }
    }

    /**
     * Solicita al usuario seleccionar una estrategia de matchmaking.
     */
    public String solicitarEstrategiaMatchmaking() {
        System.out.println("\n--- Estrategia de Matchmaking ---");
        System.out.println("Seleccione cómo se emparejará a los jugadores:");

        MatchmakingRegistry registry = MatchmakingRegistry.getInstance();
        List<MatchmakingStrategy> estrategias = registry.getEstrategiasDisponibles();

        for (int i = 0; i < estrategias.size(); i++) {
            MatchmakingStrategy estrategia = estrategias.get(i);
            System.out.println((i + 1) + ". " + estrategia.getNombre() +
                    " - " + estrategia.getDescripcion());
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
            System.err.println("✗ Opción inválida");
        }

        return solicitarEstrategiaMatchmaking(); // Reintentar
    }

    /**
     * Muestra un resumen del scrim antes de crearlo.
     */
    public boolean confirmarCreacion(String juego, String formato, LocalDateTime fechaHora,
            int rangoMin, int rangoMax, int latenciaMax, String estrategiaMatchmaking) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("           RESUMEN DEL SCRIM");
        System.out.println("=".repeat(50));
        System.out.println("Juego:         " + juego);
        System.out.println("Formato:       " + formato);
        System.out.println("Fecha/Hora:    " + fechaHora.format(FORMATO_FECHA));
        System.out.println("Rango:         " + rangoMin + " - " +
                (rangoMax == Integer.MAX_VALUE ? "Sin límite" : rangoMax));
        System.out.println("Latencia máx:  " +
                (latenciaMax == -1 ? "Sin límite" : latenciaMax + " ms"));
        System.out.println("Matchmaking:   " + estrategiaMatchmaking);
        System.out.println("=".repeat(50));

        System.out.print("\n¿Confirmar creación? (s/n): ");
        String respuesta = scanner.nextLine().trim().toLowerCase();
        return respuesta.equals("s") || respuesta.equals("si") || respuesta.equals("sí");
    }

    public void mostrarCancelacion() {
        System.out.println("\n✗ Creación de scrim cancelada");
    }

    // ================== Métodos para el Controller ==================

    public void mostrarExito(String mensaje) {
        System.out.println("\n✓ " + mensaje);
    }

    public void mostrarError(String mensaje) {
        System.err.println("\n✗ " + mensaje);
    }

    public void mostrarInfo(String mensaje) {
        System.out.println("\n" + mensaje);
    }
}
