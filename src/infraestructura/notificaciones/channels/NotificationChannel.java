package infraestructura.notificaciones.channels;

import infraestructura.notificaciones.types.ChannelType;
import infraestructura.notificaciones.types.Notification;

/**
 * Interfaz para los canales de notificación (Adapter Pattern).
 * Cada implementación adapta el envío a un proveedor específico (Push, Email,
 * Webhook).
 */
public interface NotificationChannel {
    /**
     * Envía una notificación a través del canal.
     * 
     * @param notification La notificación a enviar
     * @param recipient    Información del destinatario (email, token, webhook URL,
     *                     etc.)
     * @return true si el envío fue exitoso, false en caso contrario
     */
    boolean send(Notification notification, String recipient);

    /**
     * Retorna el tipo de canal que implementa.
     */
    ChannelType getChannelType();

    /**
     * Verifica si el canal está disponible/configurado.
     */
    boolean isAvailable();
}
