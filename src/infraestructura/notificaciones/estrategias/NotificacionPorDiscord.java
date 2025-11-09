package infraestructura.notificaciones.estrategias;

import infraestructura.notificaciones.adapter.IAdapterDiscord;
import dominio.modelo.Notificacion;
import infraestructura.notificaciones.adapter.DiscordWebhook;

public class NotificacionPorDiscord implements IEstrategiaNotificacion {
    
    private IAdapterDiscord adapter;
    
    public NotificacionPorDiscord() {
        this.adapter = new DiscordWebhook();
    }
    
    public NotificacionPorDiscord(IAdapterDiscord adapter) {
        this.adapter = adapter;
    }
    
    @Override
    public void enviar(Notificacion notif) {
        adapter.enviar(notif);
    }
    
    public void setAdapter(IAdapterDiscord adapter) {
        this.adapter = adapter;
    }
}
