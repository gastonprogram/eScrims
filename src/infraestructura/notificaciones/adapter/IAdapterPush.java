package infraestructura.notificaciones.adapter;

import dominio.modelo.Notificacion;

public interface IAdapterPush {
    void enviar(Notificacion notif);
}
