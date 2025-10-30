package controller;

import model.Scrim;
import model.ScrimBuilder;
import model.juegos.Juego;
import model.utils.ScrimFormat;
import model.Persistencia.RepositorioScrim;
import model.Persistencia.impl.RepositorioScrimMemoria;
import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * Controlador para crear scrims.
 * NO depende de las vistas, solo proporciona lógica de negocio pura.
 * Siguiendo el patrón de Login y Register.
 * 
 * @author eScrims Team
 */
public class CrearScrimController {

    private RepositorioScrim repositorio;
    private String currentUserId; // Usuario logueado que crea el scrim

    public CrearScrimController(String currentUserId) {
        this.repositorio = RepositorioScrimMemoria.getInstance();
        this.currentUserId = currentUserId;
    }

    /**
     * Crea un scrim con los parámetros proporcionados.
     * 
     * @param juego       El juego del scrim
     * @param formato     El formato del scrim
     * @param fechaHora   La fecha y hora del scrim
     * @param rangoMin    El rango mínimo requerido
     * @param rangoMax    El rango máximo requerido
     * @param latenciaMax La latencia máxima permitida
     * @return El scrim creado con su ID asignado
     * @throws IllegalArgumentException Si algún parámetro es inválido
     * @throws RuntimeException         Si no se puede guardar el scrim
     */
    public Scrim crearScrim(Juego juego, ScrimFormat formato, LocalDateTime fechaHora,
            int rangoMin, int rangoMax, int latenciaMax) {

        try {
            // Validaciones
            validarParametros(juego, formato, fechaHora, rangoMin, rangoMax, latenciaMax);

            // Obtener roles del juego
            var roles = juego.getRolesDisponibles().stream()
                    .map(rol -> rol.getNombre())
                    .toList();

            // Construir el scrim
            ScrimBuilder builder = new ScrimBuilder();
            Scrim scrim = builder
                    .withJuego(juego)
                    .withFormato(formato)
                    .withFechaHora(fechaHora)
                    .withRango(rangoMin, rangoMax)
                    .withLatenciaMaxima(latenciaMax)
                    .withRolesRequeridos(Arrays.asList(roles.toArray(new String[0])))
                    .build();

            // Establecer el creador
            scrim.setCreatedBy(currentUserId);

            // Guardar en el repositorio
            if (!repositorio.guardar(scrim)) {
                throw new RuntimeException("No se pudo guardar el scrim en el repositorio");
            }

            return scrim;

        } catch (IllegalArgumentException e) {
            throw e; // Re-lanzar excepciones de validación
        } catch (Exception e) {
            throw new RuntimeException("Error al crear el scrim: " + e.getMessage(), e);
        }
    }

    /**
     * Valida los parámetros antes de crear el scrim.
     */
    private void validarParametros(Juego juego, ScrimFormat formato, LocalDateTime fechaHora,
            int rangoMin, int rangoMax, int latenciaMax) {

        if (juego == null) {
            throw new IllegalArgumentException("El juego es requerido");
        }

        if (formato == null) {
            throw new IllegalArgumentException("El formato es requerido");
        }

        if (fechaHora == null) {
            throw new IllegalArgumentException("La fecha y hora son requeridas");
        }

        if (fechaHora.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("La fecha no puede ser anterior a la actual");
        }

        if (rangoMin < 1 || rangoMin > 100) {
            throw new IllegalArgumentException("El rango mínimo debe estar entre 1 y 100");
        }

        if (rangoMax < 1 || rangoMax > 100) {
            throw new IllegalArgumentException("El rango máximo debe estar entre 1 y 100");
        }

        if (rangoMin > rangoMax) {
            throw new IllegalArgumentException("El rango mínimo no puede ser mayor que el máximo");
        }

        if (latenciaMax < 0) {
            throw new IllegalArgumentException("La latencia no puede ser negativa");
        }

        if (latenciaMax > 1000) {
            throw new IllegalArgumentException("La latencia máxima no puede superar 1000ms");
        }
    }

    /**
     * Obtiene el ID del usuario actual que está creando scrims.
     */
    public String getCurrentUserId() {
        return currentUserId;
    }
}
