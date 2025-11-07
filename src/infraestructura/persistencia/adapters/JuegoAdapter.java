package infraestructura.persistencia.adapters;

import com.google.gson.*;
import dominio.juegos.Juego;
import dominio.juegos.JuegosRegistry;

import java.lang.reflect.Type;

/**
 * Adaptador personalizado para serializar/deserializar objetos Juego
 * con Gson, evitando problemas de referencias circulares.
 */
public class JuegoAdapter implements JsonSerializer<Juego>, JsonDeserializer<Juego> {

    @Override
    public JsonElement serialize(Juego src, Type typeOfSrc, JsonSerializationContext context) {
        if (src == null) {
            return JsonNull.INSTANCE;
        }
        // Solo serializar el nombre del juego
        return new JsonPrimitive(src.getNombre());
    }

    @Override
    public Juego deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        if (json == null || json.isJsonNull()) {
            return null;
        }

        String nombreJuego = json.getAsString();

        // Buscar el juego en el registro usando el nombre
        JuegosRegistry registry = JuegosRegistry.getInstance();
        return registry.buscarPorNombre(nombreJuego);
    }
}
