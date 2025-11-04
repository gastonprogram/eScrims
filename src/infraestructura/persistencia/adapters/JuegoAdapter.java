package infraestructura.persistencia.adapters;

import com.google.gson.*;
import dominio.juegos.Juego;
import dominio.juegos.LeagueOfLegends;

import java.lang.reflect.Type;

/**
 * Adaptador de Gson para serializar/deserializar objetos Juego.
 * 
 * Evita referencias circulares serializando solo el nombre del juego
 * y reconstruyendo la instancia correcta al deserializar.
 * 
 * @author eScrims Team
 */
public class JuegoAdapter implements JsonSerializer<Juego>, JsonDeserializer<Juego> {

    @Override
    public JsonElement serialize(Juego src, Type typeOfSrc, JsonSerializationContext context) {
        if (src == null) {
            return JsonNull.INSTANCE;
        }
        // Serializar solo el nombre del juego
        return new JsonPrimitive(src.getNombre());
    }

    @Override
    public Juego deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        if (json == null || json.isJsonNull()) {
            return null;
        }

        String nombreJuego = json.getAsString();

        // Reconstruir la instancia del juego según el nombre
        switch (nombreJuego) {
            case "League of Legends":
                return LeagueOfLegends.getInstance();
            // Aquí se pueden agregar más juegos en el futuro
            default:
                System.err.println("Juego desconocido: " + nombreJuego + ". Usando League of Legends por defecto.");
                return LeagueOfLegends.getInstance();
        }
    }
}
