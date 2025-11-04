package infraestructura.persistencia.implementacion;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

import aplicacion.builders.FiltrosScrim;
import dominio.modelo.Scrim;
import infraestructura.persistencia.repository.RepositorioScrim;

/**
 * Implementación en memoria del repositorio de Scrims.
 * Los datos se pierden al cerrar la aplicación.
 * 
 * Esta implementación es simple y útil para desarrollo y testing.
 * Puede ser reemplazada por una implementación con JSON o base de datos.
 * 
 * @author eScrims Team
 */
public class RepositorioScrimMemoria implements RepositorioScrim {

    // archivo json para scrims
    private static final String ARCHIVO_JSON = "data/scrims.json";
    private final Gson gson;
    private static RepositorioScrimMemoria instance;
    private List<Scrim> scrims;

    /**
     * Constructor privado para Singleton.
     */
    private RepositorioScrimMemoria() {
        this.scrims = new ArrayList<>();
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

        return scrims.removeIf(s -> s.getId().equals(id));
    }

    @Override
    public int contar() {
        return scrims.size();
    }

    /**
     * Limpia todos los scrims (útil para testing).
     */
    public void limpiar() {
        scrims.clear();
    }
}
