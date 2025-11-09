package infraestructura.notificaciones;

import dominio.modelo.Notificacion;
import infraestructura.notificaciones.estrategias.IEstrategiaNotificacion;

public class Notificador {
    private IEstrategiaNotificacion estrategia;
    
    public Notificador() {
        this.estrategia = null;
    }
    
    public Notificador(IEstrategiaNotificacion estrategia) {
        this.estrategia = estrategia;
    }
    
    public void setEstrategia(IEstrategiaNotificacion estrategia) {
        this.estrategia = estrategia;
    }
    
    public IEstrategiaNotificacion getEstrategia() {
        return estrategia;
    }
    
    public void enviar(Notificacion notif) {
        if (estrategia == null) {
            throw new IllegalStateException("No se ha configurado una estrategia de notificación");
        }
        estrategia.enviar(notif);
    }
}
