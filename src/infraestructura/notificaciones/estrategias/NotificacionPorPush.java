package infraestructura.notificaciones.estrategias;

import infraestructura.notificaciones.adapter.IAdapterPush;
import dominio.modelo.Notificacion;
import infraestructura.notificaciones.adapter.FirebaseCloudMessaging;

public class NotificacionPorPush implements IEstrategiaNotificacion {
    
    private IAdapterPush adapter;
    
    public NotificacionPorPush() {
        this.adapter = new FirebaseCloudMessaging();
    }
    
    public NotificacionPorPush(IAdapterPush adapter) {
        this.adapter = adapter;
    }
    
    @Override
    public void enviar(Notificacion notif) {
        adapter.enviar(notif);
    }
    
    public void setAdapter(IAdapterPush adapter) {
        this.adapter = adapter;
    }
}
