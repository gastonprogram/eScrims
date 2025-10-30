package model.notifications.factory;

import model.notifications.channels.*;
import model.notifications.types.ChannelType;

/**
 * Abstract Factory para crear canales de notificación.
 * Permite instanciar el canal adecuado según el tipo solicitado.
 */
public abstract class NotificationChannelFactory {
    
    /**
     * Crea un canal de notificación del tipo especificado.
     * 
     * @param type Tipo de canal a crear
     * @return Instancia del canal de notificación
     */
    public abstract NotificationChannel createChannel(ChannelType type);
    
    /**
     * Obtiene la factory concreta por defecto.
     * Utiliza implementaciones mock/stub para desarrollo inicial.
     */
    public static NotificationChannelFactory getDefaultFactory() {
        return new DefaultChannelFactory();
    }
}

/**
 * Implementación por defecto de la factory.
 * Crea canales con implementaciones mock para desarrollo.
 */
class DefaultChannelFactory extends NotificationChannelFactory {
    
    @Override
    public NotificationChannel createChannel(ChannelType type) {
        switch (type) {
            case PUSH:
                return new PushNotificationChannel();
            case EMAIL:
                return new EmailNotificationChannel();
            case DISCORD:
                return new DiscordNotificationChannel();
            case SLACK:
                return new SlackNotificationChannel();
            default:
                throw new IllegalArgumentException("Tipo de canal no soportado: " + type);
        }
    }
}
