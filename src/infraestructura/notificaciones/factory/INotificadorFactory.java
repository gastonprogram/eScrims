package infraestructura.notificaciones.factory;

import infraestructura.notificaciones.Notificador;

public interface INotificadorFactory {
    Notificador crearNotificadorEmail();
    Notificador crearNotificadorDiscord();
    Notificador crearNotificadorPush();
}
