package infraestructura.notificaciones.types;

/**
 * Tipos de eventos que pueden disparar notificaciones en el sistema.
 */
public enum NotificationEvent {
    SCRIM_CREATED_MATCH, // Scrim creado que coincide con preferencias del usuario
    LOBBY_ARMADO, // Cambio a Lobby armado (cupo completo)
    CONFIRMADO_TODOS, // Confirmado por todos los participantes
    EN_JUEGO, // Scrim cambia a estado En Juego
    FINALIZADO, // Scrim finalizado
    CANCELADO // Scrim cancelado
}
