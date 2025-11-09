package infraestructura.notificaciones.factory;

import infraestructura.notificaciones.Notificador;
import infraestructura.notificaciones.adapter.DiscordWebhook;
import infraestructura.notificaciones.adapter.FirebaseCloudMessaging;
import infraestructura.notificaciones.adapter.JavaMail;
import infraestructura.notificaciones.estrategias.NotificacionPorDiscord;
import infraestructura.notificaciones.estrategias.NotificacionPorMail;
import infraestructura.notificaciones.estrategias.NotificacionPorPush;

public class NotificadorFactory implements INotificadorFactory {
    
    private static NotificadorFactory instance;
    
    private NotificadorFactory() {}
    
    public static NotificadorFactory getInstance() {
        if (instance == null) {
            instance = new NotificadorFactory();
        }
        return instance;
    }
    
    @Override
    public Notificador crearNotificadorEmail() {
        JavaMail adapter = new JavaMail();
        NotificacionPorMail estrategia = new NotificacionPorMail(adapter);
        return new Notificador(estrategia);
    }
    
    @Override
    public Notificador crearNotificadorDiscord() {
        DiscordWebhook adapter = new DiscordWebhook();
        NotificacionPorDiscord estrategia = new NotificacionPorDiscord(adapter);
        return new Notificador(estrategia);
    }
    
    @Override
    public Notificador crearNotificadorPush() {
        FirebaseCloudMessaging adapter = new FirebaseCloudMessaging();
        NotificacionPorPush estrategia = new NotificacionPorPush(adapter);
        return new Notificador(estrategia);
    }
}
