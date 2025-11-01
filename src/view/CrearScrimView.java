package view;

import model.juegos.Juego;
import model.juegos.LeagueOfLegends;
import model.utils.ScrimFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import java.util.List;

/**
 * Vista para crear scrims mediante formularios en consola.
 * Guía al usuario paso a paso para ingresar todos los datos necesarios.
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
     * Solicita al usuario seleccionar un juego.
     * Por ahora solo League of Legends está disponible.
     */
    public Juego solicitarJuego() {
        System.out.println("\n--- Seleccione el Juego ---");
        System.out.println("1. League of Legends");
        System.out.println("0. Cancelar");
        System.out.print("Opción: ");

        try {
            int opcion = Integer.parseInt(scanner.nextLine().trim());
            if (opcion == 1) {
                return LeagueOfLegends.getInstance();
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
    public int solicitarRangoMin() {
        System.out.println("\n--- Rango Mínimo ---");
        System.out.println("Ej: 1000 (Iron), 2000 (Silver), 3000 (Gold)");
        System.out.print("Rango mínimo (o 0 para sin límite): ");

        try {
            int rango = Integer.parseInt(scanner.nextLine().trim());
            if (rango < 0) {
                System.err.println("✗ El rango no puede ser negativo");
                return solicitarRangoMin();
            }
            return rango;
        } catch (NumberFormatException e) {
            System.err.println("✗ Debe ingresar un número");
            return solicitarRangoMin();
        }
    }

    /**
     * Solicita el rango máximo permitido.
     */
    public int solicitarRangoMax(int rangoMin) {
        System.out.println("\n--- Rango Máximo ---");
        System.out.println("Ej: 5000 (Platinum), 7000 (Diamond), 9000 (Master)");
        System.out.print("Rango máximo (o 0 para sin límite): ");

        try {
            int rango = Integer.parseInt(scanner.nextLine().trim());
            if (rango < 0) {
                System.err.println("✗ El rango no puede ser negativo");
                return solicitarRangoMax(rangoMin);
            }
            if (rango > 0 && rango < rangoMin) {
                System.err.println("✗ El rango máximo debe ser mayor al mínimo (" + rangoMin + ")");
                return solicitarRangoMax(rangoMin);
            }
            return rango == 0 ? Integer.MAX_VALUE : rango;
        } catch (NumberFormatException e) {
            System.err.println("✗ Debe ingresar un número");
            return solicitarRangoMax(rangoMin);
        }
    }

    /**
     * Solicita la latencia máxima permitida.
     */
    public int solicitarLatenciaMax() {
        System.out.println("\n--- Latencia Máxima ---");
        System.out.println("Latencia máxima permitida en ms");
        System.out.print("Latencia máxima (o 0 para sin límite): ");

        try {
            int latencia = Integer.parseInt(scanner.nextLine().trim());
            if (latencia < 0) {
                System.err.println("✗ La latencia no puede ser negativa");
                return solicitarLatenciaMax();
            }
            return latencia == 0 ? -1 : latencia; // -1 indica sin límite
        } catch (NumberFormatException e) {
            System.err.println("✗ Debe ingresar un número");
            return solicitarLatenciaMax();
        }
    }

    /**
     * Muestra un resumen del scrim antes de crearlo.
     */
    public boolean confirmarCreacion(String juego, String formato, LocalDateTime fechaHora,
            int rangoMin, int rangoMax, int latenciaMax) {
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
        System.out.println("=".repeat(50));

        System.out.print("\n¿Confirmar creación? (s/n): ");
        String respuesta = scanner.nextLine().trim().toLowerCase();
        return respuesta.equals("s") || respuesta.equals("si") || respuesta.equals("sí");
    }

    public void mostrarExito(String scrimId) {
        System.out.println("\n✓ ¡Scrim creado exitosamente!");
        System.out.println("ID del scrim: " + scrimId);
    }

    public void mostrarError(String mensaje) {
        System.err.println("\n✗ Error al crear scrim: " + mensaje);
    }

    public void mostrarCancelacion() {
        System.out.println("\n✗ Creación de scrim cancelada");
    }

    /**
     * Inicia el flujo de creación de scrim usando el controller proporcionado.
     * La vista orquesta el flujo, el controller solo provee la lógica de negocio.
     * 
     * @param controller El controlador desacoplado de la vista
     */
    public void iniciarCreacion(controller.CrearScrimController controller) {
        mostrarTitulo();

        try {
            // 1. Seleccionar juego
            Juego juego = solicitarJuego();
            if (juego == null) {
                mostrarCancelacion();
                return;
            }

            // 2. Seleccionar formato
            ScrimFormat formato = solicitarFormato(juego);
            if (formato == null) {
                mostrarCancelacion();
                return;
            }

            // 3. Fecha y hora
            LocalDateTime fechaHora = solicitarFechaHora();
            if (fechaHora == null) {
                mostrarCancelacion();
                return;
            }

            // 4. Rangos
            int rangoMin = solicitarRangoMin();
            int rangoMax = solicitarRangoMax(rangoMin);

            // 5. Latencia
            int latenciaMax = solicitarLatenciaMax();

            // 6. Confirmación
            if (!confirmarCreacion(juego.getNombre(), formato.getFormatName(),
                    fechaHora, rangoMin, rangoMax, latenciaMax)) {
                mostrarCancelacion();
                return;
            }

            // 7. Crear scrim usando el controller (lógica de negocio pura)
            model.Scrim scrim = controller.crearScrim(juego, formato, fechaHora,
                    rangoMin, rangoMax, latenciaMax);

            // 8. Mostrar éxito
            mostrarExito(scrim.getId());

        } catch (IllegalArgumentException e) {
            mostrarError(e.getMessage());
        } catch (RuntimeException e) {
            mostrarError(e.getMessage());
        } catch (Exception e) {
            mostrarError("Error inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
