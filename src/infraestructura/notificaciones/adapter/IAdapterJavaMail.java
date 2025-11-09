package infraestructura.notificaciones.adapter;

import dominio.modelo.Notificacion;

public interface IAdapterJavaMail {
    void enviar(Notificacion notif);
}
