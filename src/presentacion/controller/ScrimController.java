package presentacion.controller;

import java.time.LocalDateTime;
import java.util.List;

import aplicacion.builders.FiltrosScrim;
import aplicacion.services.ScrimService;
import dominio.juegos.Juego;
import dominio.modelo.Scrim;
import dominio.valueobjects.formatosScrims.ScrimFormat;
import presentacion.view.BuscarScrimView;
import presentacion.view.CrearScrimView;

/**
 * Controller de presentación para la gestión de Scrims.
 * 
 * Responsabilidades:
 * - Coordinar entre vistas y servicios
 * - Manejar flujo de navegación
 * - Formatear mensajes para el usuario
 * - Capturar y traducir excepciones
 * 
 * @author eScrims Team
 */
public class ScrimController {

    private final ScrimService scrimService;
    private final CrearScrimView crearScrimView;
    private final BuscarScrimView buscarScrimView;
    private final String usuarioActualId;

    public ScrimController(ScrimService scrimService,
            CrearScrimView crearScrimView,
            BuscarScrimView buscarScrimView,
            String usuarioActualId) {
        this.scrimService = scrimService;
        this.crearScrimView = crearScrimView;
        this.buscarScrimView = buscarScrimView;
        this.usuarioActualId = usuarioActualId;
    }

    /**
     * Inicia el flujo de creación de un scrim.
     */
    public void crearScrim() {
        try {
            crearScrimView.mostrarTitulo();

            // 1. Solicitar datos básicos a la vista
            Juego juego = crearScrimView.solicitarJuego();
            if (juego == null) {
                crearScrimView.mostrarCancelacion();
                return;
            }

            ScrimFormat formato = crearScrimView.solicitarFormato(juego);
            if (formato == null) {
                crearScrimView.mostrarCancelacion();
                return;
            }

            LocalDateTime fechaHora = crearScrimView.solicitarFechaHora();
            if (fechaHora == null) {
                crearScrimView.mostrarCancelacion();
                return;
            }

            int rangoMin = crearScrimView.solicitarRangoMinimo();
            int rangoMax = crearScrimView.solicitarRangoMaximo();
            int latenciaMax = crearScrimView.solicitarLatenciaMaxima();

            // 2. Solicitar estrategia de matchmaking
            String estrategiaMatchmaking = crearScrimView.solicitarEstrategiaMatchmaking();
            if (estrategiaMatchmaking == null) {
                crearScrimView.mostrarCancelacion();
                return;
            }

            // 3. Confirmar creación
            boolean confirmar = crearScrimView.confirmarCreacion(
                    juego.getNombre(), formato.getFormatName(), fechaHora,
                    rangoMin, rangoMax, latenciaMax, estrategiaMatchmaking);

            if (!confirmar) {
                crearScrimView.mostrarCancelacion();
                return;
            }

            // 4. Llamar al servicio
            Scrim scrim = scrimService.crearScrimConEstrategia(
                    juego, formato, fechaHora,
                    rangoMin, rangoMax, latenciaMax,
                    estrategiaMatchmaking, usuarioActualId);

            // 5. Mostrar resultado exitoso
            crearScrimView.mostrarExito(
                    "¡Scrim creado exitosamente!\n" +
                            "ID: " + scrim.getId() + "\n" +
                            "Estado: " + scrim.getState().getEstado() + "\n" +
                            "Estrategia: " + scrim.getEstrategiaMatchmaking());

        } catch (IllegalArgumentException e) {
            crearScrimView.mostrarError("Error de validación: " + e.getMessage());
        } catch (Exception e) {
            crearScrimView.mostrarError("Error inesperado: " + e.getMessage());
        }
    }

    /**
     * Inicia el flujo de búsqueda de scrims.
     */
    public void buscarScrims() {
        try {
            // 1. Solicitar filtros a la vista
            FiltrosScrim filtros = buscarScrimView.solicitarFiltros();

            // 2. Llamar al servicio
            List<Scrim> scrims = scrimService.buscarScrims(filtros);

            // 3. Mostrar resultados
            if (scrims.isEmpty()) {
                buscarScrimView.mostrarInfo("No se encontraron scrims con los filtros especificados");
            } else {
                buscarScrimView.mostrarListaScrims(scrims);
            }

        } catch (IllegalArgumentException e) {
            buscarScrimView.mostrarError("Error: " + e.getMessage());
        } catch (Exception e) {
            buscarScrimView.mostrarError("Error inesperado: " + e.getMessage());
        }
    }

    /**
     * Muestra todos los scrims disponibles.
     */
    public void listarTodosScrims() {
        try {
            List<Scrim> scrims = scrimService.obtenerTodos();

            if (scrims.isEmpty()) {
                buscarScrimView.mostrarInfo("No hay scrims disponibles");
            } else {
                buscarScrimView.mostrarListaScrims(scrims);
            }

        } catch (Exception e) {
            buscarScrimView.mostrarError("Error al listar scrims: " + e.getMessage());
        }
    }

    /**
     * Muestra los detalles de un scrim específico.
     */
    public void verDetalleScrim() {
        try {
            String scrimId = buscarScrimView.solicitarIdScrim();
            Scrim scrim = scrimService.buscarPorId(scrimId);

            buscarScrimView.mostrarDetalleScrim(scrim);

        } catch (IllegalArgumentException e) {
            buscarScrimView.mostrarError("Error: " + e.getMessage());
        } catch (Exception e) {
            buscarScrimView.mostrarError("Error inesperado: " + e.getMessage());
        }
    }

    /**
     * Elimina un scrim (solo el organizador).
     */
    public void eliminarScrim() {
        try {
            String scrimId = buscarScrimView.solicitarIdScrim();

            // Validar que el usuario es el organizador
            Scrim scrim = scrimService.buscarPorId(scrimId);
            if (!scrim.getCreatedBy().equals(usuarioActualId)) {
                buscarScrimView.mostrarError("Solo el organizador puede eliminar el scrim");
                return;
            }

            // Confirmar eliminación
            boolean confirmado = buscarScrimView.confirmarEliminacion(scrim);
            if (!confirmado) {
                buscarScrimView.mostrarInfo("Eliminación cancelada");
                return;
            }

            // Eliminar
            scrimService.eliminarScrim(scrimId);
            buscarScrimView.mostrarExito("Scrim eliminado exitosamente");

        } catch (IllegalArgumentException e) {
            buscarScrimView.mostrarError("Error: " + e.getMessage());
        } catch (Exception e) {
            buscarScrimView.mostrarError("Error inesperado: " + e.getMessage());
        }
    }

    /**
     * Muestra estadísticas de scrims.
     */
    public void mostrarEstadisticas() {
        try {
            int totalScrims = scrimService.contarScrims();
            buscarScrimView.mostrarInfo("Total de scrims: " + totalScrims);

        } catch (Exception e) {
            buscarScrimView.mostrarError("Error al obtener estadísticas: " + e.getMessage());
        }
    }
}
