package infraestructura.notificaciones.adapter;

import dominio.modelo.Notificacion;

public interface IAdapterDiscord {
    void enviar(Notificacion notif);
}
