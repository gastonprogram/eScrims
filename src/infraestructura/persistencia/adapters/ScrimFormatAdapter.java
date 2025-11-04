package infraestructura.persistencia.adapters;

import com.google.gson.*;
import dominio.juegos.formatos.Formato5v5LoL;
import dominio.juegos.formatos.FormatoARAMLoL;
import dominio.valueobjects.formatosScrims.ScrimFormat;

import java.lang.reflect.Type;

/**
 * Adaptador de Gson para serializar/deserializar objetos ScrimFormat.
 * 
 * Evita referencias circulares serializando solo el nombre del formato
 * y reconstruyendo la instancia correcta al deserializar.
 * 
 * @author eScrims Team
 */
public class ScrimFormatAdapter implements JsonSerializer<ScrimFormat>, JsonDeserializer<ScrimFormat> {

    @Override
    public JsonElement serialize(ScrimFormat src, Type typeOfSrc, JsonSerializationContext context) {
        if (src == null) {
            return JsonNull.INSTANCE;
        }
        // Serializar solo el nombre del formato
        return new JsonPrimitive(src.getFormatName());
    }

    @Override
    public ScrimFormat deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        if (json == null || json.isJsonNull()) {
            return null;
        }

        String nombreFormato = json.getAsString();

        // Reconstruir la instancia del formato según el nombre
        switch (nombreFormato) {
            case "Summoner's Rift 5v5":
                return new Formato5v5LoL();
            case "ARAM 5v5":
                return new FormatoARAMLoL();
            // Aquí se pueden agregar más formatos en el futuro
            default:
                System.err.println("Formato desconocido: " + nombreFormato + ". Usando 5v5 por defecto.");
                return new Formato5v5LoL();
        }
    }
}
