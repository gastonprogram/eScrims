package model.notifications.channels;

import model.notifications.types.Notification;
import model.notifications.types.ChannelType;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

/**
 * Adaptador para notificaciones por Slack (webhook).
 * Esta implementación usa webhooks de Slack para enviar mensajes.
 */
public class SlackNotificationChannel implements NotificationChannel {
    
    private static final Logger LOGGER = Logger.getLogger(SlackNotificationChannel.class.getName());
    private boolean available;
    private String webhookUrl;
    
    public SlackNotificationChannel() {
        // Intentar cargar configuración de variables de entorno
        this.webhookUrl = System.getenv("SLACK_WEBHOOK_URL");
        this.available = (webhookUrl != null && !webhookUrl.isEmpty());
        
        if (!available) {
            LOGGER.warning("Slack Webhook URL no configurado. Slack notifications deshabilitadas.");
        }
    }
    
    @Override
    public boolean send(Notification notification, String userSlackId) {
        if (!isAvailable()) {
            LOGGER.warning("Canal Slack no disponible. Notificación no enviada.");
            return false;
        }
        
        try {
            // Construir payload JSON para Slack webhook
            String jsonPayload = buildSlackPayload(notification, userSlackId);
            
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
                LOGGER.info(String.format("[SLACK] Notificación enviada: %s", notification.getTitle()));
            } else {
                LOGGER.warning(String.format("[SLACK] Error al enviar (HTTP %d)", responseCode));
            }
            
            return success;
        } catch (Exception e) {
            LOGGER.severe("Error al enviar notificación Slack: " + e.getMessage());
            return false;
        }
    }
    
    private String buildSlackPayload(Notification notification, String userSlackId) {
        // Construir mensaje formateado para Slack
        String mention = (userSlackId != null && !userSlackId.isEmpty()) 
            ? "<@" + userSlackId + "> " 
            : "";
        
        String text = mention + "*" + escapeJson(notification.getTitle()) + "*\n" 
                     + escapeJson(notification.getMessage());
        
        return String.format("{\"text\": \"%s\"}", text);
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
        return ChannelType.SLACK;
    }
    
    @Override
    public boolean isAvailable() {
        return available;
    }
    
    /**
     * Configura el webhook de Slack (para testing o configuración programática).
     */
    public void setWebhookUrl(String url) {
        this.webhookUrl = url;
        this.available = (url != null && !url.isEmpty());
    }
}
