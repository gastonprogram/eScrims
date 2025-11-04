package infraestructura.notificaciones.core;

import java.util.*;
import java.util.logging.Logger;

import dominio.modelo.Scrim;
import dominio.modelo.Usuario;
import infraestructura.notificaciones.channels.NotificationChannel;
import infraestructura.notificaciones.factory.NotificationChannelFactory;
import infraestructura.notificaciones.types.*;
import infraestructura.persistencia.repository.RepositorioFactory;
import infraestructura.persistencia.repository.RepositorioUsuario;

/**
 * Servicio central de notificaciones (Observer Pattern).
 * Coordina el envío de notificaciones a usuarios según sus preferencias.
 */
public class NotificationService {

    private static final Logger LOGGER = Logger.getLogger(NotificationService.class.getName());
    private static NotificationService instance;

    private final NotificationChannelFactory channelFactory;
    private final RepositorioUsuario repositorioUsuario;
    private final Map<ChannelType, NotificationChannel> channelCache;

    private NotificationService() {
        this.channelFactory = NotificationChannelFactory.getDefaultFactory();
        this.repositorioUsuario = RepositorioFactory.getRepositorioUsuario();
        this.channelCache = new HashMap<>();
    }

    /**
     * Obtiene la instancia única del servicio (Singleton).
     */
    public static synchronized NotificationService getInstance() {
        if (instance == null) {
            instance = new NotificationService();
        }
        return instance;
    }

    /**
     * Notifica sobre la creación de un scrim que coincide con preferencias de
     * usuarios.
     */
    public void notifyScrimCreated(Scrim scrim) {
        // TODO: Implementar lógica para encontrar usuarios con preferencias matching
        // Por ahora: notificar a todos los usuarios suscritos al evento
        List<Usuario> interestedUsers = findUsersInterestedInScrim(scrim);

        String title = "Nuevo Scrim Disponible";
        String message = String.format("Se creó un scrim de %s para %s. ¡Postúlate ahora!",
                scrim.getJuego().getNombre(), scrim.getFechaHora());

        notifyUsers(NotificationEvent.SCRIM_CREATED_MATCH, scrim, interestedUsers, title, message);
    }

    /**
     * Notifica cambio a estado Lobby Armado (cupo completo).
     */
    public void notifyLobbyArmado(Scrim scrim) {
        List<String> userIds = scrim.getPostulacionesAceptadas().stream()
                .map(p -> p.getUserId())
                .toList();
        List<Usuario> participants = getUsersByIds(userIds);

        String title = "Lobby Completo";
        String message = String.format("El lobby del scrim de %s está completo. Por favor confirma tu participación.",
                scrim.getJuego().getNombre());

        notifyUsers(NotificationEvent.LOBBY_ARMADO, scrim, participants, title, message);
    }

    /**
     * Notifica que todos confirmaron su participación.
     */
    public void notifyConfirmadoTodos(Scrim scrim) {
        List<String> userIds = scrim.getConfirmacionesConfirmadas().stream()
                .map(c -> c.getUserId())
                .toList();
        List<Usuario> participants = getUsersByIds(userIds);

        String title = "Scrim Confirmado";
        String message = String.format("Todos confirmaron. El scrim de %s comenzará en %s.",
                scrim.getJuego().getNombre(), scrim.getFechaHora());

        notifyUsers(NotificationEvent.CONFIRMADO_TODOS, scrim, participants, title, message);
    }

    /**
     * Notifica cambio a estado En Juego.
     */
    public void notifyEnJuego(Scrim scrim) {
        List<String> userIds = scrim.getConfirmacionesConfirmadas().stream()
                .map(c -> c.getUserId())
                .toList();
        List<Usuario> participants = getUsersByIds(userIds);

        String title = "Scrim Iniciado";
        String message = String.format("El scrim de %s ha comenzado. ¡Buena suerte!",
                scrim.getJuego().getNombre());

        notifyUsers(NotificationEvent.EN_JUEGO, scrim, participants, title, message);
    }

    /**
     * Notifica que el scrim finalizó.
     */
    public void notifyFinalizado(Scrim scrim) {
        List<String> userIds = scrim.getConfirmacionesConfirmadas().stream()
                .map(c -> c.getUserId())
                .toList();
        List<Usuario> participants = getUsersByIds(userIds);

        String title = "Scrim Finalizado";
        String message = String.format("El scrim de %s ha finalizado. ¡Gracias por participar!",
                scrim.getJuego().getNombre());

        notifyUsers(NotificationEvent.FINALIZADO, scrim, participants, title, message);
    }

    /**
     * Notifica que el scrim fue cancelado.
     */
    public void notifyCancelado(Scrim scrim) {
        // Notificar tanto a postulantes como confirmados
        Set<String> allUserIds = new HashSet<>();

        // Agregar postulaciones aceptadas
        scrim.getPostulacionesAceptadas().stream()
                .map(p -> p.getUserId())
                .forEach(allUserIds::add);

        // Agregar confirmaciones confirmadas
        scrim.getConfirmacionesConfirmadas().stream()
                .map(c -> c.getUserId())
                .forEach(allUserIds::add);

        List<Usuario> affectedUsers = getUsersByIds(new ArrayList<>(allUserIds));

        String title = "Scrim Cancelado";
        String message = String.format("El scrim de %s programado para %s ha sido cancelado.",
                scrim.getJuego().getNombre(), scrim.getFechaHora());

        notifyUsers(NotificationEvent.CANCELADO, scrim, affectedUsers, title, message);
    }

    /**
     * Envía notificaciones a una lista de usuarios según sus preferencias.
     */
    private void notifyUsers(NotificationEvent event, Scrim scrim, List<Usuario> users,
            String title, String message) {
        if (users == null || users.isEmpty()) {
            LOGGER.info(String.format("No hay usuarios para notificar del evento %s", event));
            return;
        }

        LOGGER.info(String.format("Notificando evento %s a %d usuarios", event, users.size()));

        for (Usuario user : users) {
            notifyUser(event, scrim, user, title, message);
        }
    }

    /**
     * Envía notificación a un usuario específico según sus preferencias de canal.
     */
    private void notifyUser(NotificationEvent event, Scrim scrim, Usuario user,
            String title, String message) {
        // Verificar si el usuario está suscrito a este tipo de evento
        if (!user.isSubscribedToEvent(event)) {
            LOGGER.fine(String.format("Usuario %s no suscrito a evento %s", user.getUsername(), event));
            return;
        }

        Notification notification = new Notification(event, scrim, user.getUsername(), title, message);

        // Enviar por cada canal preferido del usuario
        Set<ChannelType> preferredChannels = user.getPreferredChannels();
        if (preferredChannels == null || preferredChannels.isEmpty()) {
            LOGGER.fine(String.format("Usuario %s no tiene canales configurados", user.getUsername()));
            return;
        }

        for (ChannelType channelType : preferredChannels) {
            sendNotification(notification, user, channelType);
        }
    }

    /**
     * Envía una notificación por un canal específico.
     */
    private void sendNotification(Notification notification, Usuario user, ChannelType channelType) {
        NotificationChannel channel = getChannel(channelType);

        if (!channel.isAvailable()) {
            LOGGER.warning(String.format("Canal %s no disponible para usuario %s",
                    channelType, user.getUsername()));
            return;
        }

        // Obtener el identificador del usuario para este canal
        String recipient = user.getChannelRecipient(channelType);
        if (recipient == null || recipient.isEmpty()) {
            LOGGER.warning(String.format("Usuario %s no tiene configurado %s",
                    user.getUsername(), channelType));
            return;
        }

        boolean success = channel.send(notification, recipient);
        if (success) {
            LOGGER.info(String.format("Notificación enviada a %s vía %s",
                    user.getUsername(), channelType));
        } else {
            LOGGER.warning(String.format("Fallo al enviar notificación a %s vía %s",
                    user.getUsername(), channelType));
        }
    }

    /**
     * Obtiene o crea un canal de notificación (cache).
     */
    private NotificationChannel getChannel(ChannelType type) {
        return channelCache.computeIfAbsent(type, channelFactory::createChannel);
    }

    /**
     * Encuentra usuarios interesados en un scrim según sus preferencias.
     * TODO: Implementar lógica de matching basada en preferencias del usuario.
     */
    private List<Usuario> findUsersInterestedInScrim(Scrim scrim) {
        // Placeholder: retornar lista vacía por ahora
        // En una implementación completa, buscaría usuarios con:
        // - Juego coincidente
        // - Rango dentro de límites
        // - Disponibilidad en fecha/hora
        // - Región compatible (latencia)
        return new ArrayList<>();
    }

    /**
     * Obtiene usuarios por sus IDs desde el repositorio.
     */
    private List<Usuario> getUsersByIds(List<String> userIds) {
        List<Usuario> users = new ArrayList<>();
        if (userIds == null)
            return users;

        for (String userId : userIds) {
            try {
                // TODO: Implementar búsqueda por username en RepositorioUsuario
                // Por ahora retornamos lista vacía
                // Usuario user = repositorioUsuario.buscarPorUsername(userId);
                // if (user != null) users.add(user);
            } catch (Exception e) {
                LOGGER.warning("Error al buscar usuario " + userId + ": " + e.getMessage());
            }
        }

        return users;
    }
}
