package model;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;

import model.utils.PasswordHasher;
import model.utils.Rol;
import model.notifications.types.NotificationEvent;
import model.notifications.types.ChannelType;

public class Usuario {

    private String id;
    private String username;
    private String email;
    private String hashedPassword;
    private String salt;

    private Map<String, Integer> rangoPorJuego;
    private Rol rol;
    private String region;

    private boolean disponible;

    // estado mail

    private Date createdAt;
    private Date updatedAt;

    // Preferencias de notificaciones (Observer pattern)
    private Set<NotificationEvent> subscribedEvents;
    private Set<ChannelType> preferredChannels;
    private Map<ChannelType, String> channelRecipients; // email, fcmToken, discordId, slackId, etc.

    // Constructor
    public Usuario(String username, String email, String password) {
        this.id = java.util.UUID.randomUUID().toString();
        this.username = username;
        this.email = email;
        setPassword(password);
        this.rol = Rol.USER;
        this.createdAt = new Date();
        this.updatedAt = new Date();
        
        // Inicializar preferencias de notificación con valores por defecto
        this.subscribedEvents = new HashSet<>();
        this.preferredChannels = new HashSet<>();
        this.channelRecipients = new HashMap<>();
        
        // Por defecto, suscribir a todos los eventos y usar email
        subscribeToAllEvents();
        addPreferredChannel(ChannelType.EMAIL, this.email);
    }

    public void setPassword(String password) {
        this.salt = PasswordHasher.generateSalt();
        this.hashedPassword = PasswordHasher.hashPassword(password, this.salt);
        this.updatedAt = new Date();
    }

    public boolean verifyPassword(String password) {
        return PasswordHasher.verifyPassword(password, this.hashedPassword, this.salt);
    }

    // Getters y setters

    // No incluir setter público para hashedPassword y salt
    // Solo deben modificarse a través de setPassword()
    public String getSalt() {
        return salt;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    // getters y setters

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
        this.updatedAt = new Date();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        this.updatedAt = new Date();
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public boolean esAdmin() {
        return rol == Rol.ADMIN;
    }

    public boolean esModerador() {
        return rol == Rol.MOD;
    }

    public boolean esUsuario() {
        return rol == Rol.USER;
    }

    public void setRangoPrincipal(String nuevoRango) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setRangoPrincipal'");
    }

    public void setRolesPreferidos(List<String> nuevosRolesPreferidos) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setRolesPreferidos'");
    }

    public void setJuegoPrincipal(String nuevoJuegoPrincipal) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setJuegoPrincipal'");
    }

    public void setRegion(String nuevaRegion) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setRegion'");
    }

    public void setDisponibilidad(String nuevaDisponibilidad) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setDisponibilidad'");
    // Métodos de gestión de notificaciones

    /**
     * Suscribe al usuario a un tipo de evento.
     */
    public void subscribeToEvent(NotificationEvent event) {
        this.subscribedEvents.add(event);
        this.updatedAt = new Date();
    }

    /**
     * Cancela la suscripción del usuario a un tipo de evento.
     */
    public void unsubscribeFromEvent(NotificationEvent event) {
        this.subscribedEvents.remove(event);
        this.updatedAt = new Date();
    }

    /**
     * Verifica si el usuario está suscrito a un tipo de evento.
     */
    public boolean isSubscribedToEvent(NotificationEvent event) {
        return subscribedEvents.contains(event);
    }

    /**
     * Suscribe al usuario a todos los tipos de eventos.
     */
    public void subscribeToAllEvents() {
        for (NotificationEvent event : NotificationEvent.values()) {
            subscribedEvents.add(event);
        }
        this.updatedAt = new Date();
    }

    /**
     * Cancela todas las suscripciones del usuario.
     */
    public void unsubscribeFromAllEvents() {
        subscribedEvents.clear();
        this.updatedAt = new Date();
    }

    /**
     * Agrega un canal preferido para recibir notificaciones.
     * 
     * @param channelType Tipo de canal (PUSH, EMAIL, DISCORD, SLACK)
     * @param recipient Identificador del destinatario (email, token, webhook, etc.)
     */
    public void addPreferredChannel(ChannelType channelType, String recipient) {
        this.preferredChannels.add(channelType);
        this.channelRecipients.put(channelType, recipient);
        this.updatedAt = new Date();
    }

    /**
     * Remueve un canal preferido.
     */
    public void removePreferredChannel(ChannelType channelType) {
        this.preferredChannels.remove(channelType);
        this.channelRecipients.remove(channelType);
        this.updatedAt = new Date();
    }

    /**
     * Obtiene los canales preferidos del usuario.
     */
    public Set<ChannelType> getPreferredChannels() {
        return new HashSet<>(preferredChannels);
    }

    /**
     * Obtiene el identificador del destinatario para un canal específico.
     */
    public String getChannelRecipient(ChannelType channelType) {
        return channelRecipients.get(channelType);
    }

    /**
     * Obtiene los eventos a los que está suscrito el usuario.
     */
    public Set<NotificationEvent> getSubscribedEvents() {
        return new HashSet<>(subscribedEvents);
    }

}