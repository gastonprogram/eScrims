package model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;

import model.juegos.Juego;
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
    private Juego juegoPrincipal; // ✅ Objeto Juego en lugar de String
    private Map<String, List<String>> rolesPorJuego; // ✅ Roles organizados por juego
    private String disponibilidad;
    private int latenciaPromedio; // Latencia en ms

    private boolean disponible;

    // estado mail

    private Date createdAt;
    private Date updatedAt;

    // Historial de comportamiento para matchmaking
    private HistorialUsuario historial;

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

        // Inicializar atributos de perfil
        this.rangoPorJuego = new HashMap<>();
        this.region = "";
        this.juegoPrincipal = null; // Se asignará cuando el usuario elija un juego
        this.rolesPorJuego = new HashMap<>();
        this.disponibilidad = "";
        this.disponible = true;
        this.latenciaPromedio = 50; // Default: 50ms (buena latencia)
        this.historial = new HistorialUsuario(this.id); // Inicializar historial con el ID

        // Inicializar preferencias de notificación con valores por defecto
        this.subscribedEvents = new HashSet<>();
        this.preferredChannels = new HashSet<>();
        this.channelRecipients = new HashMap<>();

        // Por defecto, suscribir a todos los eventos y usar email
        subscribeToAllEvents();
        addPreferredChannel(ChannelType.EMAIL, this.email);

        // Inicializar historial de usuario
        this.historial = new HistorialUsuario(this.id);
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

    // Getters y Setters para perfil del usuario

    /**
     * Obtiene el rango principal del usuario (del juego principal o primer juego
     * disponible).
     */
    public String getRangoPrincipal() {
        if (rangoPorJuego.isEmpty()) {
            return "Sin rango";
        }
        if (juegoPrincipal != null && rangoPorJuego.containsKey(juegoPrincipal.getNombre())) {
            return String.valueOf(rangoPorJuego.get(juegoPrincipal.getNombre()));
        }
        // Retornar el primer rango disponible
        return String.valueOf(rangoPorJuego.values().iterator().next());
    }

    public void setRangoPrincipal(String nuevoRango) {
        try {
            int rango = Integer.parseInt(nuevoRango);
            if (juegoPrincipal != null) {
                rangoPorJuego.put(juegoPrincipal.getNombre(), rango);
            } else {
                // Si no hay juego principal, usar "General"
                rangoPorJuego.put("General", rango);
            }
            this.updatedAt = new Date();
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El rango debe ser un número válido");
        }
    }

    /**
     * Obtiene los roles preferidos del usuario para el juego principal.
     * Si no hay juego principal, retorna lista vacía.
     */
    public List<String> getRolesPreferidos() {
        if (juegoPrincipal != null) {
            return new ArrayList<>(rolesPorJuego.getOrDefault(juegoPrincipal.getNombre(), new ArrayList<>()));
        }
        return new ArrayList<>();
    }

    /**
     * Establece los roles preferidos para el juego principal.
     * Si no hay juego principal, lanza excepción.
     */
    public void setRolesPreferidos(List<String> nuevosRolesPreferidos) {
        if (juegoPrincipal == null) {
            throw new IllegalStateException("Debe establecer un juego principal antes de configurar roles");
        }
        this.rolesPorJuego.put(juegoPrincipal.getNombre(),
                nuevosRolesPreferidos != null ? new ArrayList<>(nuevosRolesPreferidos) : new ArrayList<>());
        this.updatedAt = new Date();
    }

    /**
     * Agrega un rol preferido para un juego específico con validación.
     */
    public void agregarRolPreferido(Juego juego, String rol) {
        if (juego == null) {
            throw new IllegalArgumentException("El juego no puede ser null");
        }
        // Crear un RolJuego temporal para validar
        // Nota: esto asume que tienes una forma de crear RolJuego desde String
        // Si no, puedes omitir la validación o implementarla de otra forma
        rolesPorJuego
                .computeIfAbsent(juego.getNombre(), k -> new ArrayList<>())
                .add(rol);
        this.updatedAt = new Date();
    }

    /**
     * Obtiene los roles preferidos para un juego específico.
     */
    public List<String> getRolesPreferidosParaJuego(String nombreJuego) {
        return new ArrayList<>(rolesPorJuego.getOrDefault(nombreJuego, new ArrayList<>()));
    }

    /**
     * Obtiene el juego principal del usuario.
     * 
     * @return Objeto Juego o null si no está configurado
     */
    public Juego getJuegoPrincipal() {
        return juegoPrincipal;
    }

    /**
     * Obtiene el nombre del juego principal (para compatibilidad con vistas).
     * 
     * @return Nombre del juego o string vacío
     */
    public String getJuegoPrincipalNombre() {
        return juegoPrincipal != null ? juegoPrincipal.getNombre() : "";
    }

    /**
     * Establece el juego principal del usuario.
     */
    public void setJuegoPrincipal(Juego nuevoJuegoPrincipal) {
        this.juegoPrincipal = nuevoJuegoPrincipal;
        this.updatedAt = new Date();
    }

    public String getRegion() {
        return region != null ? region : "";
    }

    public void setRegion(String nuevaRegion) {
        this.region = nuevaRegion != null ? nuevaRegion : "";
        this.updatedAt = new Date();
    }

    public String getDisponibilidad() {
        return disponibilidad != null ? disponibilidad : "";
    }

    public void setDisponibilidad(String nuevaDisponibilidad) {
        this.disponibilidad = nuevaDisponibilidad != null ? nuevaDisponibilidad : "";
        this.updatedAt = new Date();
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
        this.updatedAt = new Date();
    }

    public Map<String, Integer> getRangoPorJuego() {
        return new HashMap<>(rangoPorJuego);
    }

    public void setRangoParaJuego(String juego, int rango) {
        this.rangoPorJuego.put(juego, rango);
        this.updatedAt = new Date();
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }
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
     * @param recipient   Identificador del destinatario (email, token, webhook,
     *                    etc.)
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

    // Métodos de gestión de historial

    /**
     * Obtiene el historial de comportamiento del usuario.
     */
    public HistorialUsuario getHistorial() {
        return historial;
    }

    /**
     * Establece el historial del usuario (útil para deserialización).
     */
    public void setHistorial(HistorialUsuario historial) {
        this.historial = historial;
    }

    // Métodos de gestión de latencia

    /**
     * Obtiene la latencia promedio del usuario en ms.
     */
    public int getLatenciaPromedio() {
        return latenciaPromedio;
    }

    /**
     * Establece la latencia promedio del usuario.
     * 
     * @param latenciaPromedio latencia en ms (debe ser >= 0)
     */
    public void setLatenciaPromedio(int latenciaPromedio) {
        if (latenciaPromedio < 0) {
            throw new IllegalArgumentException("La latencia no puede ser negativa");
        }
        this.latenciaPromedio = latenciaPromedio;
        this.updatedAt = new Date();
    }

}