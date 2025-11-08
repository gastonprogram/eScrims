package infraestructura.persistencia.implementacion;

import infraestructura.persistencia.repository.RepositorioEstadisticas;
import dominio.estadisticas.EstadisticasScrim;
import dominio.estadisticas.EstadisticasJugador;
import dominio.estadisticas.ReporteConducta;
import dominio.estadisticas.Comentario;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.google.gson.stream.JsonToken;
import com.google.gson.reflect.TypeToken;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementación JSON del repositorio de estadísticas.
 * Maneja la persistencia en archivos JSON separados para cada tipo de dato.
 * 
 * @author eScrims Team
 */
public class RepositorioEstadisticasJSON implements RepositorioEstadisticas {

    private static final String ARCHIVO_ESTADISTICAS_SCRIM = "data/estadisticas_scrims.json";
    private static final String ARCHIVO_REPORTES = "data/reportes_conducta.json";
    private static final String ARCHIVO_COMENTARIOS = "data/comentarios.json";

    private final Gson gson;
    private Map<String, EstadisticasScrim> estadisticasScrims;
    private List<ReporteConducta> reportesConducta;
    private List<Comentario> comentarios;

    public RepositorioEstadisticasJSON() {
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
        cargarDatos();
    }

    /**
     * Adaptador personalizado para serializar/deserializar LocalDateTime.
     */
    private static class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
        private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        @Override
        public void write(JsonWriter out, LocalDateTime value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                out.value(value.format(formatter));
            }
        }

        @Override
        public LocalDateTime read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            return LocalDateTime.parse(in.nextString(), formatter);
        }
    }

    // ========== CARGA Y GUARDADO ==========

    private void cargarDatos() {
        cargarEstadisticasScrims();
        cargarReportesConducta();
        cargarComentarios();
    }

    private void cargarEstadisticasScrims() {
        java.io.File archivo = new java.io.File(ARCHIVO_ESTADISTICAS_SCRIM);
        if (!archivo.exists() || archivo.length() == 0) {
            estadisticasScrims = new HashMap<>();
            return;
        }

        try (FileReader reader = new FileReader(archivo)) {
            Type type = new TypeToken<Map<String, EstadisticasScrim>>() {
            }.getType();
            Map<String, EstadisticasScrim> cargadas = gson.fromJson(reader, type);
            estadisticasScrims = (cargadas != null) ? cargadas : new HashMap<>();
        } catch (IOException | com.google.gson.JsonSyntaxException e) {
            estadisticasScrims = new HashMap<>();
        }
    }

    private void cargarReportesConducta() {
        java.io.File archivo = new java.io.File(ARCHIVO_REPORTES);
        if (!archivo.exists() || archivo.length() == 0) {
            reportesConducta = new ArrayList<>();
            return;
        }

        try (FileReader reader = new FileReader(archivo)) {
            Type type = new TypeToken<List<ReporteConducta>>() {
            }.getType();
            List<ReporteConducta> cargados = gson.fromJson(reader, type);
            reportesConducta = (cargados != null) ? cargados : new ArrayList<>();
        } catch (IOException | com.google.gson.JsonSyntaxException e) {
            reportesConducta = new ArrayList<>();
        }
    }

    private void cargarComentarios() {
        java.io.File archivo = new java.io.File(ARCHIVO_COMENTARIOS);
        if (!archivo.exists() || archivo.length() == 0) {
            comentarios = new ArrayList<>();
            return;
        }

        try (FileReader reader = new FileReader(archivo)) {
            Type type = new TypeToken<List<Comentario>>() {
            }.getType();
            List<Comentario> cargados = gson.fromJson(reader, type);
            comentarios = (cargados != null) ? cargados : new ArrayList<>();
        } catch (IOException | com.google.gson.JsonSyntaxException e) {
            comentarios = new ArrayList<>();
        }
    }

    private void guardarEstadisticasScrims() {
        try {
            // Crear directorio si no existe
            java.io.File file = new java.io.File(ARCHIVO_ESTADISTICAS_SCRIM);
            file.getParentFile().mkdirs();

            try (FileWriter writer = new FileWriter(ARCHIVO_ESTADISTICAS_SCRIM)) {
                gson.toJson(estadisticasScrims, writer);
            }
        } catch (IOException e) {
            System.err.println("Error al guardar estadísticas de scrims: " + e.getMessage());
        }
    }

    private void guardarReportesConducta() {
        try {
            java.io.File file = new java.io.File(ARCHIVO_REPORTES);
            file.getParentFile().mkdirs();

            try (FileWriter writer = new FileWriter(ARCHIVO_REPORTES)) {
                gson.toJson(reportesConducta, writer);
            }
        } catch (IOException e) {
            System.err.println("Error al guardar reportes de conducta: " + e.getMessage());
        }
    }

    private void guardarComentarios() {
        try {
            java.io.File file = new java.io.File(ARCHIVO_COMENTARIOS);
            file.getParentFile().mkdirs();

            try (FileWriter writer = new FileWriter(ARCHIVO_COMENTARIOS)) {
                gson.toJson(comentarios, writer);
            }
        } catch (IOException e) {
            System.err.println("Error al guardar comentarios: " + e.getMessage());
        }
    }

    // ========== ESTADÍSTICAS DE SCRIM ==========

    @Override
    public synchronized void guardarEstadisticasScrim(EstadisticasScrim estadisticas) {
        if (estadisticas != null && estadisticas.getScrimId() != null) {
            estadisticasScrims.put(estadisticas.getScrimId(), estadisticas);
            guardarEstadisticasScrims();
        }
    }

    @Override
    public EstadisticasScrim obtenerEstadisticasScrim(String scrimId) {
        return estadisticasScrims.get(scrimId);
    }

    @Override
    public List<EstadisticasScrim> obtenerTodasLasEstadisticasScrims() {
        return new ArrayList<>(estadisticasScrims.values());
    }

    @Override
    public synchronized void actualizarEstadisticasScrim(EstadisticasScrim estadisticas) {
        if (estadisticas != null && estadisticas.getScrimId() != null &&
                estadisticasScrims.containsKey(estadisticas.getScrimId())) {
            estadisticasScrims.put(estadisticas.getScrimId(), estadisticas);
            guardarEstadisticasScrims();
        }
    }

    @Override
    public synchronized void eliminarEstadisticasScrim(String scrimId) {
        if (estadisticasScrims.remove(scrimId) != null) {
            guardarEstadisticasScrims();
        }
    }

    // ========== ESTADÍSTICAS DE JUGADOR ==========

    @Override
    public synchronized void guardarEstadisticasJugador(String scrimId, String jugadorId,
            EstadisticasJugador estadisticas) {
        EstadisticasScrim estadisticasScrim = estadisticasScrims.get(scrimId);
        if (estadisticasScrim != null) {
            estadisticasScrim.getEstadisticasPorJugador().put(jugadorId, estadisticas);
            guardarEstadisticasScrims();
        }
    }

    @Override
    public EstadisticasJugador obtenerEstadisticasJugador(String scrimId, String jugadorId) {
        EstadisticasScrim estadisticasScrim = estadisticasScrims.get(scrimId);
        if (estadisticasScrim != null) {
            return estadisticasScrim.getEstadisticasPorJugador().get(jugadorId);
        }
        return null;
    }

    @Override
    public Map<String, EstadisticasJugador> obtenerEstadisticasJugadorGeneral(String jugadorId) {
        Map<String, EstadisticasJugador> estadisticasJugador = new HashMap<>();

        for (Map.Entry<String, EstadisticasScrim> entry : estadisticasScrims.entrySet()) {
            EstadisticasJugador stats = entry.getValue().getEstadisticasPorJugador().get(jugadorId);
            if (stats != null) {
                estadisticasJugador.put(entry.getKey(), stats);
            }
        }

        return estadisticasJugador;
    }

    // ========== REPORTES DE CONDUCTA ==========

    @Override
    public synchronized void guardarReporteConducta(ReporteConducta reporte) {
        if (reporte != null) {
            reportesConducta.add(reporte);
            guardarReportesConducta();
        }
    }

    @Override
    public List<ReporteConducta> obtenerReportesScrim(String scrimId) {
        return reportesConducta.stream()
                .filter(reporte -> scrimId.equals(reporte.getScrimId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<ReporteConducta> obtenerReportesPendientes() {
        return reportesConducta.stream()
                .filter(reporte -> !reporte.isRevisado())
                .collect(Collectors.toList());
    }

    @Override
    public List<ReporteConducta> obtenerReportesUsuario(String usuarioId) {
        return reportesConducta.stream()
                .filter(reporte -> usuarioId.equals(reporte.getUsuarioReportadoId()))
                .collect(Collectors.toList());
    }

    @Override
    public synchronized void actualizarEstadoReporte(String reporteId, String nuevoEstado) {
        reportesConducta.stream()
                .filter(reporte -> reporteId.equals(reporte.getId()))
                .findFirst()
                .ifPresent(reporte -> {
                    // Actualizar estado basado en la lógica de negocio de ReporteConducta
                    if ("REVISADO".equals(nuevoEstado)) {
                        reporte.setRevisado(true);
                    } else if ("SANCIONADO".equals(nuevoEstado)) {
                        reporte.setSancionado(true);
                        reporte.setRevisado(true);
                    }
                    guardarReportesConducta();
                });
    }

    // ========== COMENTARIOS ==========

    @Override
    public synchronized void guardarComentario(Comentario comentario) {
        if (comentario != null) {
            comentarios.add(comentario);
            guardarComentarios();
        }
    }

    @Override
    public List<Comentario> obtenerComentariosScrim(String scrimId) {
        return comentarios.stream()
                .filter(comentario -> scrimId.equals(comentario.getScrimId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Comentario> obtenerComentariosPendientes() {
        return comentarios.stream()
                .filter(comentario -> Comentario.EstadoModeracion.PENDIENTE.equals(comentario.getEstado()))
                .collect(Collectors.toList());
    }

    @Override
    public synchronized void moderarComentario(String comentarioId, boolean aprobado) {
        comentarios.stream()
                .filter(comentario -> comentarioId.equals(String.valueOf(comentario.getId())))
                .findFirst()
                .ifPresent(comentario -> {
                    if (aprobado) {
                        comentario.setEstado(Comentario.EstadoModeracion.APROBADO);
                    } else {
                        comentario.setEstado(Comentario.EstadoModeracion.RECHAZADO);
                    }
                    guardarComentarios();
                });
    }

    // ========== UTILIDADES ==========

    @Override
    public int contarEstadisticas() {
        return estadisticasScrims.size();
    }

    @Override
    public synchronized void limpiarTodas() {
        estadisticasScrims.clear();
        reportesConducta.clear();
        comentarios.clear();
        guardarEstadisticasScrims();
        guardarReportesConducta();
        guardarComentarios();
    }
}