package model.notifications.channels;

import model.notifications.types.Notification;
import model.notifications.types.ChannelType;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

/**
 * Adaptador para notificaciones por Discord (webhook).
 * Esta implementación usa webhooks de Discord para enviar mensajes.
 */
public class DiscordNotificationChannel implements NotificationChannel {
    
    private static final Logger LOGGER = Logger.getLogger(DiscordNotificationChannel.class.getName());
    private boolean available;
    private String webhookUrl;
    
    public DiscordNotificationChannel() {
        // Intentar cargar configuración de variables de entorno
        this.webhookUrl = System.getenv("DISCORD_WEBHOOK_URL");
        this.available = (webhookUrl != null && !webhookUrl.isEmpty());
        
        if (!available) {
            LOGGER.warning("Discord Webhook URL no configurado. Discord notifications deshabilitadas.");
        }
    }
    
    @Override
    public boolean send(Notification notification, String userDiscordId) {
        if (!isAvailable()) {
            LOGGER.warning("Canal Discord no disponible. Notificación no enviada.");
            return false;
        }
        
        try {
            // Construir payload JSON para Discord webhook
            String jsonPayload = buildDiscordPayload(notification, userDiscordId);
            
            // TODO: Para producción real, usar una biblioteca HTTP como OkHttp o Apache HttpClient
            // Por ahora: implementación simple con HttpURLConnection
            URL url = new URL(webhookUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            
            int responseCode = conn.getResponseCode();
            boolean success = (responseCode >= 200 && responseCode < 300);
            
            if (success) {
                LOGGER.info(String.format("[DISCORD] Notificación enviada: %s", notification.getTitle()));
            } else {
                LOGGER.warning(String.format("[DISCORD] Error al enviar (HTTP %d)", responseCode));
            }
            
            return success;
        } catch (Exception e) {
            LOGGER.severe("Error al enviar notificación Discord: " + e.getMessage());
            return false;
        }
    }
    
    private String buildDiscordPayload(Notification notification, String userDiscordId) {
        // Construir mensaje embed de Discord
        String mention = (userDiscordId != null && !userDiscordId.isEmpty()) 
            ? "<@" + userDiscordId + ">" 
            : "";
        
        // JSON simple escapado para Discord
        String content = mention + " **" + escapeJson(notification.getTitle()) + "**";
        String description = escapeJson(notification.getMessage());
        
        return String.format(
            "{\"content\": \"%s\", \"embeds\": [{\"description\": \"%s\", \"color\": 5814783}]}",
            content, description
        );
    }
    
    private String escapeJson(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }
    
    @Override
    public ChannelType getChannelType() {
        return ChannelType.DISCORD;
    }
    
    @Override
    public boolean isAvailable() {
        return available;
    }
    
    /**
     * Configura el webhook de Discord (para testing o configuración programática).
     */
    public void setWebhookUrl(String url) {
        this.webhookUrl = url;
        this.available = (url != null && !url.isEmpty());
    }
}
