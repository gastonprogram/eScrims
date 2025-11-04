package infraestructura.notificaciones.types;

/**
 * Tipos de canales de notificación disponibles.
 */
public enum ChannelType {
    PUSH, // Firebase Cloud Messaging
    EMAIL, // Email (JavaMail/SendGrid)
    DISCORD, // Discord webhook
    SLACK // Slack webhook
}
