package infraestructura.notificaciones.estrategias;

import dominio.modelo.Notificacion;
import infraestructura.notificaciones.adapter.IAdapterJavaMail;
import infraestructura.notificaciones.adapter.JavaMail;

public class NotificacionPorMail implements IEstrategiaNotificacion {
    
    private IAdapterJavaMail adapter;
    
    public NotificacionPorMail() {
        this.adapter = new JavaMail();
    }
    
    public NotificacionPorMail(IAdapterJavaMail adapter) {
        this.adapter = adapter;
    }
    
    @Override
    public void enviar(Notificacion notif) {
        adapter.enviar(notif);
    }
    
    public void setAdapter(IAdapterJavaMail adapter) {
        this.adapter = adapter;
    }
}
