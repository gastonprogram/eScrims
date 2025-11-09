package infraestructura.notificaciones;

import infraestructura.notificaciones.factory.NotificadorFactory;
import dominio.modelo.Notificacion;
import dominio.modelo.Usuario;

import java.util.List;
import java.util.Set;

import compartido.utils.ChannelType;
import compartido.utils.NotificationEvent;

public class NotificationManager {
    
    private Notificador notificadorEmail;
    private Notificador notificadorDiscord;
    private Notificador notificadorPush;
    
    public NotificationManager() {
        NotificadorFactory factory = NotificadorFactory.getInstance();
        this.notificadorEmail = factory.crearNotificadorEmail();
        this.notificadorDiscord = factory.crearNotificadorDiscord();
        this.notificadorPush = factory.crearNotificadorPush();
    }
    
    public void notificarUsuario(Usuario usuario, String mensaje, NotificationEvent evento) {
        if (!usuario.isSubscribedToEvent(evento)) {
            return;
        }
        
        Set<ChannelType> canalesPreferidos = usuario.getPreferredChannels();
        
        for (ChannelType canal : canalesPreferidos) {
            String destinatario = usuario.getChannelRecipient(canal);
            if (destinatario == null || destinatario.isEmpty()) {
                continue;
            }
            
            Notificacion notificacion = new Notificacion(usuario, mensaje, destinatario);
            enviarPorCanal(canal, notificacion);
        }
    }
    
    public void notificarUsuarios(List<Usuario> usuarios, String mensaje, NotificationEvent evento) {
        for (Usuario usuario : usuarios) {
            notificarUsuario(usuario, mensaje, evento);
        }
    }
    
    private void enviarPorCanal(ChannelType canal, Notificacion notificacion) {
        switch (canal) {
            case EMAIL:
                notificadorEmail.enviar(notificacion);
                break;
            case DISCORD:
                notificadorDiscord.enviar(notificacion);
                break;
            case PUSH:
                notificadorPush.enviar(notificacion);
                break;
        }
    }
}
