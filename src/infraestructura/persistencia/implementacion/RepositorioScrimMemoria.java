package infraestructura.persistencia.implementacion;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

import aplicacion.builders.FiltrosScrim;
import dominio.juegos.Juego;
import dominio.modelo.Scrim;
import dominio.valueobjects.formatosScrims.ScrimFormat;
import infraestructura.persistencia.adapters.JuegoAdapter;
import infraestructura.persistencia.adapters.ScrimFormatAdapter;
import infraestructura.persistencia.repository.RepositorioScrim;

/**
 * Implementación del repositorio de Scrims con persistencia en JSON.
 * Los datos se guardan en el archivo data/scrims.json.
 * 
 * Usa Gson para serialización/deserialización con soporte para LocalDateTime.
 * El campo ScrimState se excluye de la serialización (transient) para evitar
 * referencias circulares, y se reconstruye al cargar desde JSON.
 * 
 * @author eScrims Team
 */
public class RepositorioScrimMemoria implements RepositorioScrim {

    private static final String ARCHIVO_JSON = "data/scrims.json";
    private final Gson gson;
    private static RepositorioScrimMemoria instance;
    private List<Scrim> scrims;

    /**
     * Constructor privado para Singleton.
     */
    private RepositorioScrimMemoria() {
        // Configurar Gson con adaptadores para java.time (igual que RepositorioUsuarioJSON)
        DateTimeFormatter dtf = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        JsonSerializer<LocalDateTime> serLDT = (src, typeOfSrc, context) -> src == null ? null
                : new com.google.gson.JsonPrimitive(src.format(dtf));
        JsonDeserializer<LocalDateTime> deserLDT = (json, typeOfT, context) -> 
                json == null || json.getAsString().isEmpty() ? null
                : LocalDateTime.parse(json.getAsString(), dtf);

        DateTimeFormatter df = DateTimeFormatter.ISO_LOCAL_DATE;
        JsonSerializer<LocalDate> serLD = (src, typeOfSrc, context) -> src == null ? null
                : new com.google.gson.JsonPrimitive(src.format(df));
        JsonDeserializer<LocalDate> deserLD = (json, typeOfT, context) -> 
                json == null || json.getAsString().isEmpty() ? null
                : LocalDate.parse(json.getAsString(), df);

        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, serLDT)
                .registerTypeAdapter(LocalDateTime.class, deserLDT)
                .registerTypeAdapter(LocalDate.class, serLD)
                .registerTypeAdapter(LocalDate.class, deserLD)
                .registerTypeAdapter(Juego.class, new JuegoAdapter())
                .registerTypeAdapter(ScrimFormat.class, new ScrimFormatAdapter())
                .setPrettyPrinting()
                .create();
        
        this.scrims = cargarScrims();
    }

    /**
     * Carga los scrims desde el archivo JSON.
     */
    private List<Scrim> cargarScrims() {
        try {
            // Crear el directorio si no existe
            Path dir = Paths.get("data");
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
            }

            // Si el archivo no existe, devolver lista vacía
            if (!Files.exists(Paths.get(ARCHIVO_JSON))) {
                return new ArrayList<>();
            }

            try (FileReader reader = new FileReader(ARCHIVO_JSON)) {
                Type tipoListaScrims = new TypeToken<List<Scrim>>() {}.getType();
                List<Scrim> scrimsCargados = gson.fromJson(reader, tipoListaScrims);
                
                if (scrimsCargados != null) {
                    // Reconstruir el estado de cada scrim después de deserializar
                    for (Scrim scrim : scrimsCargados) {
                        scrim.reconstruirEstado();
                    }
                    return scrimsCargados;
                }
                return new ArrayList<>();
            }
        } catch (IOException e) {
            System.err.println("Error al cargar scrims: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Guarda los scrims en el archivo JSON.
     */
    private void guardarScrims() {
        try {
            // Crear el directorio si no existe
            Path dir = Paths.get("data");
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
            }

            try (FileWriter writer = new FileWriter(ARCHIVO_JSON)) {
                gson.toJson(scrims, writer);
            }
        } catch (IOException e) {
            System.err.println("Error al guardar scrims: " + e.getMessage());
        }
    }

    /**
     * Obtiene la instancia única del repositorio (Singleton).
     * 
     * @return instancia del repositorio
     */
    public static synchronized RepositorioScrimMemoria getInstance() {
        if (instance == null) {
            instance = new RepositorioScrimMemoria();
        }
        return instance;
    }

    @Override
    public boolean guardar(Scrim scrim) {
        if (scrim == null) {
            return false;
        }

        // Verificar si ya existe
        if (buscarPorId(scrim.getId()) != null) {
            return false; // Ya existe
        }

        scrims.add(scrim);
        guardarScrims(); // Persistir en JSON
        return true;
    }

    @Override
    public Scrim buscarPorId(String id) {
        if (id == null) {
            return null;
        }

        return scrims.stream()
                .filter(s -> s.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Scrim> obtenerTodos() {
        return new ArrayList<>(scrims);
    }

    @Override
    public List<Scrim> buscarConFiltros(FiltrosScrim filtros) {
        if (filtros == null) {
            return obtenerTodos();
        }

        return scrims.stream()
                .filter(scrim -> cumpleFiltros(scrim, filtros))
                .collect(Collectors.toList());
    }

    /**
     * Verifica si un scrim cumple con los filtros especificados.
     */
    private boolean cumpleFiltros(Scrim scrim, FiltrosScrim filtros) {
        // Filtro por juego
        if (filtros.getJuego() != null) {
            if (!scrim.getJuego().getNombre().equalsIgnoreCase(filtros.getJuego())) {
                return false;
            }
        }

        // Filtro por formato
        if (filtros.getFormato() != null) {
            if (!scrim.getFormato().getFormatName().equalsIgnoreCase(filtros.getFormato())) {
                return false;
            }
        }

        // Filtro por rango mínimo
        if (filtros.getRangoMin() != null) {
            if (scrim.getRangoMin() < filtros.getRangoMin()) {
                return false;
            }
        }

        // Filtro por rango máximo
        if (filtros.getRangoMax() != null) {
            if (scrim.getRangoMax() > filtros.getRangoMax()) {
                return false;
            }
        }

        // Filtro por latencia máxima
        if (filtros.getLatenciaMax() != null) {
            if (scrim.getLatenciaMax() > filtros.getLatenciaMax()) {
                return false;
            }
        }

        // Filtro por fecha desde
        if (filtros.getFechaDesde() != null) {
            if (scrim.getFechaHora().isBefore(filtros.getFechaDesde())) {
                return false;
            }
        }

        // Filtro por fecha hasta
        if (filtros.getFechaHasta() != null) {
            if (scrim.getFechaHora().isAfter(filtros.getFechaHasta())) {
                return false;
            }
        }

        // Filtro por estado
        if (filtros.getEstado() != null) {
            if (!scrim.getEstado().equalsIgnoreCase(filtros.getEstado())) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean actualizar(Scrim scrim) {
        if (scrim == null || scrim.getId() == null) {
            return false;
        }

        // Buscar el índice del scrim existente
        for (int i = 0; i < scrims.size(); i++) {
            if (scrims.get(i).getId().equals(scrim.getId())) {
                scrims.set(i, scrim);
                guardarScrims(); // Persistir en JSON
                return true;
            }
        }

        // Si no existe, no actualizamos nada
        return false;
    }

    @Override
    public boolean eliminar(String id) {
        if (id == null) {
            return false;
        }

        boolean eliminado = scrims.removeIf(s -> s.getId().equals(id));
        if (eliminado) {
            guardarScrims(); // Persistir en JSON
        }
        return eliminado;
    }

    @Override
    public int contar() {
        return scrims.size();
    }

    /**
     * Limpia todos los scrims (útil para testing).
     * También elimina el contenido del archivo JSON.
     */
    public void limpiar() {
        scrims.clear();
        guardarScrims();
    }

    /**
     * Recarga los scrims desde el archivo JSON.
     * Útil para sincronizar con cambios externos.
     */
    public void recargar() {
        this.scrims = cargarScrims();
    }
}
