package view;

import model.Scrim;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * Vista para buscar scrims con filtros en consola.
 * Permite al usuario especificar criterios de búsqueda opcionales.
 * 
 * @author eScrims Team
 */
public class BuscarScrimView {

    private Scanner scanner;
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public BuscarScrimView() {
        this.scanner = new Scanner(System.in);
    }

    public void mostrarTitulo() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("           BUSCAR SCRIMS");
        System.out.println("=".repeat(50));
        System.out.println("Ingrese los filtros deseados (Enter para omitir)");
    }

    /**
     * Solicita el nombre del juego (opcional).
     */
    public String solicitarJuego() {
        System.out.println("\n--- Filtrar por Juego ---");
        System.out.println("1. League of Legends");
        System.out.println("2. Cualquier juego (sin filtro)");
        System.out.print("Opción: ");

        String input = scanner.nextLine().trim();
        if (input.isEmpty() || input.equals("2")) {
            return null;
        }
        if (input.equals("1")) {
            return "League of Legends";
        }

        return null;
    }

    /**
     * Solicita el formato (opcional).
     */
    public String solicitarFormato() {
        System.out.println("\n--- Filtrar por Formato ---");
        System.out.println("Ejemplos: 5v5 Summoner's Rift, 5v5 ARAM");
        System.out.print("Formato (o Enter para omitir): ");

        String input = scanner.nextLine().trim();
        return input.isEmpty() ? null : input;
    }

    /**
     * Solicita el rango mínimo (opcional).
     */
    public Integer solicitarRangoMin() {
        System.out.println("\n--- Filtrar por Rango Mínimo ---");
        System.out.print("Rango mínimo (o Enter para omitir): ");

        String input = scanner.nextLine().trim();
        if (input.isEmpty()) {
            return null;
        }

        try {
            int rango = Integer.parseInt(input);
            return rango >= 0 ? rango : null;
        } catch (NumberFormatException e) {
            System.err.println("✗ Número inválido, se omitirá el filtro");
            return null;
        }
    }

    /**
     * Solicita el rango máximo (opcional).
     */
    public Integer solicitarRangoMax() {
        System.out.println("\n--- Filtrar por Rango Máximo ---");
        System.out.print("Rango máximo (o Enter para omitir): ");

        String input = scanner.nextLine().trim();
        if (input.isEmpty()) {
            return null;
        }

        try {
            int rango = Integer.parseInt(input);
            return rango >= 0 ? rango : null;
        } catch (NumberFormatException e) {
            System.err.println("✗ Número inválido, se omitirá el filtro");
            return null;
        }
    }

    /**
     * Solicita la latencia máxima (opcional).
     */
    public Integer solicitarLatenciaMax() {
        System.out.println("\n--- Filtrar por Latencia Máxima ---");
        System.out.print("Latencia máxima en ms (o Enter para omitir): ");

        String input = scanner.nextLine().trim();
        if (input.isEmpty()) {
            return null;
        }

        try {
            int latencia = Integer.parseInt(input);
            return latencia >= 0 ? latencia : null;
        } catch (NumberFormatException e) {
            System.err.println("✗ Número inválido, se omitirá el filtro");
            return null;
        }
    }

    /**
     * Solicita fecha desde (opcional).
     */
    public LocalDateTime solicitarFechaDesde() {
        System.out.println("\n--- Filtrar por Fecha Desde ---");
        System.out.println("Formato: dd/MM/yyyy HH:mm");
        System.out.print("Fecha desde (o Enter para omitir): ");

        String input = scanner.nextLine().trim();
        if (input.isEmpty()) {
            return null;
        }

        try {
            return LocalDateTime.parse(input, FORMATO_FECHA);
        } catch (DateTimeParseException e) {
            System.err.println("✗ Formato inválido, se omitirá el filtro");
            return null;
        }
    }

    /**
     * Solicita fecha hasta (opcional).
     */
    public LocalDateTime solicitarFechaHasta() {
        System.out.println("\n--- Filtrar por Fecha Hasta ---");
        System.out.println("Formato: dd/MM/yyyy HH:mm");
        System.out.print("Fecha hasta (o Enter para omitir): ");

        String input = scanner.nextLine().trim();
        if (input.isEmpty()) {
            return null;
        }

        try {
            return LocalDateTime.parse(input, FORMATO_FECHA);
        } catch (DateTimeParseException e) {
            System.err.println("✗ Formato inválido, se omitirá el filtro");
            return null;
        }
    }

    /**
     * Muestra los resultados de la búsqueda.
     */
    public void mostrarResultados(List<Scrim> scrims) {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("           RESULTADOS DE BÚSQUEDA");
        System.out.println("=".repeat(70));

        if (scrims.isEmpty()) {
            System.out.println("\n✗ No se encontraron scrims con los filtros especificados");
            return;
        }

        System.out.println("\nSe encontraron " + scrims.size() + " scrim(s):\n");

        for (int i = 0; i < scrims.size(); i++) {
            Scrim scrim = scrims.get(i);
            System.out.println((i + 1) + ". " + formatearScrim(scrim));
            System.out.println("   " + "-".repeat(65));
        }
    }

    /**
     * Formatea un scrim para mostrarlo de forma legible.
     */
    private String formatearScrim(Scrim scrim) {
        StringBuilder sb = new StringBuilder();
        sb.append("ID: ").append(scrim.getId().substring(0, 8)).append("...\n");
        sb.append("   Juego: ").append(scrim.getJuego().getNombre()).append("\n");
        sb.append("   Formato: ").append(scrim.getFormato().getFormatName()).append("\n");
        sb.append("   Fecha: ").append(scrim.getFechaHora().format(FORMATO_FECHA)).append("\n");
        sb.append("   Rango: ").append(scrim.getRangoMin()).append(" - ").append(scrim.getRangoMax()).append("\n");
        sb.append("   Latencia máx: ")
                .append(scrim.getLatenciaMax() == -1 ? "Sin límite" : scrim.getLatenciaMax() + " ms").append("\n");
        sb.append("   Estado: ").append(scrim.getEstado()).append("\n");
        sb.append("   Plazas: ").append(scrim.getListaPostulaciones().size()).append("/").append(scrim.getPlazas());

        return sb.toString();
    }

    /**
     * Muestra detalles completos de un scrim específico.
     */
    public void mostrarDetalleScrim(Scrim scrim) {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("           DETALLE DEL SCRIM");
        System.out.println("=".repeat(70));
        System.out.println("ID:              " + scrim.getId());
        System.out.println("Juego:           " + scrim.getJuego().getNombre());
        System.out.println("Formato:         " + scrim.getFormato().getFormatName());
        System.out.println("Fecha/Hora:      " + scrim.getFechaHora().format(FORMATO_FECHA));
        System.out.println("Rango:           " + scrim.getRangoMin() + " - " + scrim.getRangoMax());
        System.out.println(
                "Latencia máx:    " + (scrim.getLatenciaMax() == -1 ? "Sin límite" : scrim.getLatenciaMax() + " ms"));
        System.out.println("Estado:          " + scrim.getEstado());
        System.out.println("Plazas ocupadas: " + scrim.getListaPostulaciones().size() + "/" + scrim.getPlazas());
        System.out.println("Creado por:      " + (scrim.getCreatedBy() != null ? scrim.getCreatedBy() : "Anónimo"));
        System.out.println("Creado el:       " + scrim.getCreatedAt().format(FORMATO_FECHA));

        if (!scrim.getListaPostulaciones().isEmpty()) {
            System.out.println("\nPostulados:");
            for (String userId : scrim.getListaPostulaciones()) {
                System.out.println("  - " + userId);
            }
        }

        System.out.println("=".repeat(70));
    }

    public void mostrarSinResultados() {
        System.out.println("\n✗ No hay scrims disponibles en el sistema");
    }

    /**
     * Inicia el flujo de búsqueda de scrims usando el controller proporcionado.
     * La vista orquesta el flujo, el controller solo provee la lógica de negocio.
     * 
     * @param controller El controlador desacoplado de la vista
     */
    public void iniciarBusqueda(controller.BuscarScrimController controller) {
        mostrarTitulo();

        try {
            // Construir filtros desde la entrada del usuario
            model.Persistencia.FiltrosScrim.Builder builder = new model.Persistencia.FiltrosScrim.Builder();

            // Solicitar cada filtro (todos opcionales)
            String juego = solicitarJuego();
            if (juego != null) {
                builder.conJuego(juego);
            }

            String formato = solicitarFormato();
            if (formato != null) {
                builder.conFormato(formato);
            }

            Integer rangoMin = solicitarRangoMin();
            if (rangoMin != null) {
                builder.conRangoMin(rangoMin);
            }

            Integer rangoMax = solicitarRangoMax();
            if (rangoMax != null) {
                builder.conRangoMax(rangoMax);
            }

            Integer latenciaMax = solicitarLatenciaMax();
            if (latenciaMax != null) {
                builder.conLatenciaMax(latenciaMax);
            }

            LocalDateTime fechaDesde = solicitarFechaDesde();
            if (fechaDesde != null) {
                builder.conFechaDesde(fechaDesde);
            }

            LocalDateTime fechaHasta = solicitarFechaHasta();
            if (fechaHasta != null) {
                builder.conFechaHasta(fechaHasta);
            }

            // Por defecto, solo buscar scrims en estado BUSCANDO
            builder.conEstado("BUSCANDO");

            model.Persistencia.FiltrosScrim filtros = builder.build();

            // Buscar usando el controller (lógica de negocio pura)
            List<Scrim> resultados = controller.buscarScrims(filtros);

            // Mostrar resultados
            mostrarResultados(resultados);

        } catch (IllegalArgumentException e) {
            System.err.println("\n✗ Error de validación: " + e.getMessage());
        } catch (RuntimeException e) {
            System.err.println("\n✗ Error al buscar scrims: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("\n✗ Error inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
