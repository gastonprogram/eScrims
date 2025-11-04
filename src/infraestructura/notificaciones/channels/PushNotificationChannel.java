package infraestructura.notificaciones.channels;

import java.util.logging.Logger;

import infraestructura.notificaciones.types.ChannelType;
import infraestructura.notificaciones.types.Notification;

/**
 * Adaptador para notificaciones Push (Firebase Cloud Messaging).
 * Esta es una implementación mock/stub para desarrollo.
 * Para producción, integrar Firebase Admin SDK.
 */
public class PushNotificationChannel implements NotificationChannel {

    private static final Logger LOGGER = Logger.getLogger(PushNotificationChannel.class.getName());
    private boolean available;
    private String firebaseServerKey;

    public PushNotificationChannel() {
        // Intentar cargar configuración de variables de entorno
        this.firebaseServerKey = System.getenv("FIREBASE_SERVER_KEY");
        this.available = (firebaseServerKey != null && !firebaseServerKey.isEmpty());

        if (!available) {
            LOGGER.warning("Firebase Server Key no configurado. Push notifications deshabilitadas.");
        }
    }

    @Override
    public boolean send(Notification notification, String fcmToken) {
        if (!isAvailable()) {
            LOGGER.warning("Canal Push no disponible. Notificación no enviada.");
            return false;
        }

        try {
            // TODO: Implementar integración real con Firebase
            // FirebaseMessaging.getInstance().send(message);

            // Por ahora: mock que simula el envío
            LOGGER.info(String.format("[PUSH MOCK] Enviando a token %s: %s - %s",
                    fcmToken, notification.getTitle(), notification.getMessage()));

            return true;
        } catch (Exception e) {
            LOGGER.severe("Error al enviar notificación Push: " + e.getMessage());
            return false;
        }
    }

    @Override
    public ChannelType getChannelType() {
        return ChannelType.PUSH;
    }

    @Override
    public boolean isAvailable() {
        return available;
    }

    /**
     * Configura el servidor de Firebase (para testing o configuración
     * programática).
     */
    public void setFirebaseServerKey(String key) {
        this.firebaseServerKey = key;
        this.available = (key != null && !key.isEmpty());
    }
}
