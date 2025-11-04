package infraestructura.notificaciones.types;

import java.time.LocalDateTime;

import dominio.modelo.Scrim;

/**
 * Representa una notificación a enviar a un usuario.
 * Contiene toda la información necesaria para ser enviada por cualquier canal.
 */
public class Notification {
    private final NotificationEvent event;
    private final Scrim scrim;
    private final String recipientId;
    private final String title;
    private final String message;
    private final LocalDateTime timestamp;

    public Notification(NotificationEvent event, Scrim scrim, String recipientId, String title, String message) {
        this.event = event;
        this.scrim = scrim;
        this.recipientId = recipientId;
        this.title = title;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public NotificationEvent getEvent() {
        return event;
    }

    public Scrim getScrim() {
        return scrim;
    }

    public String getRecipientId() {
        return recipientId;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s: %s - %s", timestamp, event, title, message);
    }
}
