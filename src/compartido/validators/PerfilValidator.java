package compartido.validators;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PerfilValidator {

    // Constantes para validación de Región y Horario
    private static final Set<String> REGIONES_VALIDAS = Set.of("EUW", "NA", "LATAM", "KR", "ASIA", "OCE", "RU");

    // Regex para el formato estricto HH:MM-HH:MM
    private static final Pattern PATRON_HORARIO = Pattern.compile("^\\d{2}:\\d{2}-\\d{2}:\\d{2}$");
    private static final DateTimeFormatter FORMATEADOR_TIEMPO = DateTimeFormatter.ofPattern("HH:mm");

    /**
     * Valida la región contra una lista de regiones predefinidas.
     */
    public static void validarRegion(String region) {
        if (region == null || region.trim().isEmpty()) {
            throw new IllegalArgumentException("La región es requerida.");
        }
        if (!REGIONES_VALIDAS.contains(region.toUpperCase().trim())) {
            throw new IllegalArgumentException("Región inválida. Regiones aceptadas: " + REGIONES_VALIDAS);
        }
    }

    /**
     * Valida el formato HH:MM-HH:MM y la validez semántica de las horas.
     */
    public static void validarDisponibilidad(String disponibilidad) {
        if (disponibilidad == null || disponibilidad.trim().isEmpty()) {
            throw new IllegalArgumentException("La disponibilidad horaria es requerida.");
        }

        String horario = disponibilidad.trim();
        Matcher matcher = PATRON_HORARIO.matcher(horario);

        if (!matcher.matches()) {
            throw new IllegalArgumentException("Formato de disponibilidad horaria incorrecto. Se espera HH:MM-HH:MM.");
        }

        // Validación semántica (chequea si es una hora válida, ej: no permite 25:00)
        String[] partes = horario.split("-");
        try {
            LocalTime.parse(partes[0], FORMATEADOR_TIEMPO);
            LocalTime.parse(partes[1], FORMATEADOR_TIEMPO);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Los valores de hora y/o minuto no son válidos (ej: 25:00).");
        }
    }

    /**
     * Valida que se haya seleccionado al menos un rol.
     */
    public static void validarRoles(List<String> roles) {
        if (roles == null || roles.isEmpty() || roles.stream().allMatch(String::isEmpty)) {
            throw new IllegalArgumentException("Debe seleccionar al menos un rol preferido.");
        }
    }
}