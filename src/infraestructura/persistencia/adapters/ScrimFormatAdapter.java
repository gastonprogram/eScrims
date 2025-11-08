package infraestructura.persistencia.adapters;

import com.google.gson.*;
import dominio.juegos.formatos.*;
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
            // League of Legends
            case "5v5 Summoner's Rift":
                return new Formato5v5LoL();
            case "5v5 ARAM":
                return new FormatoARAMLoL();

            // Counter-Strike
            case "5v5 Competitive CS":
                return new Formato5v5CompetitiveCS();
            case "2v2 Wingman CS":
                return new Formato2v2WingmanCS();

            // Valorant
            case "5v5 Casual Valorant":
                return new Formato5v5CasualValorant();
            case "5v5 Swift Valorant":
                return new Formato5v5SwiftValorant();
            case "5v5 Competitive Valorant":
                return new Formato5v5CompetitiveValorant();

            // Aquí se pueden agregar más formatos en el futuro
            default:
                System.err.println("Formato desconocido: " + nombreFormato + ". Usando 5v5 LoL por defecto.");
                return new Formato5v5LoL();
        }
    }
}
