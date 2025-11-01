package model.notifications.channels;

import model.notifications.types.Notification;
import model.notifications.types.ChannelType;
import java.util.logging.Logger;

/**
 * Adaptador para notificaciones por Email (JavaMail/SendGrid).
 * Esta es una implementación mock/stub para desarrollo.
 * Para producción, integrar JavaMail API o SendGrid API.
 */
public class EmailNotificationChannel implements NotificationChannel {
    
    private static final Logger LOGGER = Logger.getLogger(EmailNotificationChannel.class.getName());
    private boolean available;
    private String smtpHost;
    private String smtpPort;
    private String smtpUser;
    private String smtpPassword;
    private String sendgridApiKey;
    
    public EmailNotificationChannel() {
        // Intentar cargar configuración de variables de entorno
        this.sendgridApiKey = System.getenv("SENDGRID_API_KEY");
        this.smtpHost = System.getenv("SMTP_HOST");
        this.smtpPort = System.getenv("SMTP_PORT");
        this.smtpUser = System.getenv("SMTP_USER");
        this.smtpPassword = System.getenv("SMTP_PASSWORD");
        
        // Disponible si tiene SendGrid o configuración SMTP completa
        this.available = (sendgridApiKey != null && !sendgridApiKey.isEmpty()) ||
                         (smtpHost != null && smtpUser != null && smtpPassword != null);
        
        if (!available) {
            LOGGER.warning("Email no configurado. Email notifications deshabilitadas.");
        }
    }
    
    @Override
    public boolean send(Notification notification, String emailAddress) {
        if (!isAvailable()) {
            LOGGER.warning("Canal Email no disponible. Notificación no enviada.");
            return false;
        }
        
        try {
            // TODO: Implementar integración real con JavaMail o SendGrid
            // Si usa SendGrid:
            // Mail mail = new Mail(from, subject, to, content);
            // SendGrid sg = new SendGrid(sendgridApiKey);
            // Request request = new Request();
            // request.setMethod(Method.POST);
            // request.setEndpoint("mail/send");
            // request.setBody(mail.build());
            // Response response = sg.api(request);
            
            // Por ahora: mock que simula el envío
            LOGGER.info(String.format("[EMAIL MOCK] Enviando a %s:\nAsunto: %s\nMensaje: %s",
                emailAddress, notification.getTitle(), notification.getMessage()));
            
            return true;
        } catch (Exception e) {
            LOGGER.severe("Error al enviar email: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public ChannelType getChannelType() {
        return ChannelType.EMAIL;
    }
    
    @Override
    public boolean isAvailable() {
        return available;
    }
    
    /**
     * Configura SendGrid API Key (para testing o configuración programática).
     */
    public void setSendgridApiKey(String apiKey) {
        this.sendgridApiKey = apiKey;
        this.available = (apiKey != null && !apiKey.isEmpty());
    }
    
    /**
     * Configura SMTP (para testing o configuración programática).
     */
    public void setSmtpConfig(String host, String port, String user, String password) {
        this.smtpHost = host;
        this.smtpPort = port;
        this.smtpUser = user;
        this.smtpPassword = password;
        this.available = (host != null && user != null && password != null);
    }
}
