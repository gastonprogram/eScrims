package infraestructura.notificaciones.estrategias;

import dominio.modelo.Notificacion;

public interface IEstrategiaNotificacion {
    void enviar(Notificacion notif);
}
