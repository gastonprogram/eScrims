package dominio.modelo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import dominio.estados.BuscandoState;
import dominio.estados.ScrimState;
import dominio.estados.ScrimStateFactory;
import dominio.juegos.Juego;
import dominio.valueobjects.formatosScrims.ScrimFormat;

/**
 * Representa un Scrim (partida de práctica) en la plataforma eScrims.
 * 
 * Un Scrim contiene toda la información necesaria para organizar una partida:
 * - Juego y formato de la partida
 * - Requisitos (rangos, latencia, roles)
 * - Participantes (postulaciones y confirmaciones)
 * - Estado actual (usando State Pattern)
 * 
 * Esta clase colabora con:
 * - Juego: define qué juego se jugará y valida roles
 * - ScrimFormat: define el formato de la partida (5v5, etc.)
 * - ScrimState: maneja las transiciones de estado del scrim
 * - ScrimOrganizador: gestiona las acciones del organizador
 * 
 * @author eScrims Team
 */
public class Scrim {

    private String id;
    private Juego juego; // Cambiado de String a Juego para mejor diseño
    private ScrimFormat formato;
    private int rangoMin;
    private int rangoMax;
    private int latenciaMax;
    private LocalDateTime fechaHora;
    private int plazas;
    private List<String> rolesRequeridos;
    private List<Postulacion> postulaciones;
    private List<Confirmacion> confirmaciones;
    private String createdBy;
    private LocalDateTime createdAt;
    private transient ScrimState state; // transient = no se serializa (evita referencias circulares)
    private String estadoActual; // Estado serializable para persistencia
    private String estrategiaMatchmaking; // Estrategia de matchmaking: "MMR", "Latency", "History"

    /**
     * Constructor protegido porque usa ScrimBuilder para instanciarse.
     * Este constructor aplica el patrón Builder para facilitar la creación
     * de scrims con múltiples parámetros opcionales.
     * 
     * @param juego           el juego del scrim
     * @param formato         el formato de la partida
     * @param fechaHora       fecha y hora programada
     * @param rangoMin        rango mínimo requerido
     * @param rangoMax        rango máximo permitido
     * @param rolesRequeridos lista de roles necesarios
     * @param latenciaMax     latencia máxima permitida
     * @param plazas          cantidad de plazas disponibles
     */
    public Scrim(Juego juego, ScrimFormat formato, LocalDateTime fechaHora,
            int rangoMin, int rangoMax, List<String> rolesRequeridos,
            int latenciaMax, int plazas) {
        this.id = UUID.randomUUID().toString();
        this.juego = juego;
        this.formato = formato;
        this.fechaHora = fechaHora;
        this.rangoMin = rangoMin;
        this.rangoMax = rangoMax;
        this.rolesRequeridos = rolesRequeridos;
        this.latenciaMax = latenciaMax;
        this.plazas = plazas;
        this.postulaciones = new ArrayList<>();
        this.confirmaciones = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.state = new BuscandoState();
        this.estadoActual = "BUSCANDO";
        this.estrategiaMatchmaking = "MMR"; // Estrategia por defecto
    }

    /**
     * Reconstruye el objeto ScrimState desde el String estadoActual.
     * Este método debe llamarse después de deserializar desde JSON.
     */
    public void reconstruirEstado() {
        if (state == null && estadoActual != null) {
            state = ScrimStateFactory.crearEstado(estadoActual);
        }
        // Si todavía es null, usar estado por defecto
        if (state == null) {
            state = new BuscandoState();
            estadoActual = "BUSCANDO";
        }
    }

    // Métodos para el State Pattern
    public void postular(Postulacion postulacion) {
        if (state == null)
            reconstruirEstado();
        state.postular(this, postulacion);
    }

    public void confirmar(Confirmacion confirmacion) {
        if (state == null)
            reconstruirEstado();
        state.confirmar(this, confirmacion);
    }

    public void iniciar() {
        if (state == null)
            reconstruirEstado();

        System.out.println("Estado Test1: " + this.getEstado());
        state.iniciar(this);
        System.out.println("Estado Test2: " + this.getEstado());
    }

    public void finalizar() {
        if (state == null)
            reconstruirEstado();
        state.finalizar(this);
    }

    public void cancelar() {
        if (state == null)
            reconstruirEstado();
        state.cancelar(this);
    }

    // Getters necesarios para los estados y lógica de negocio
    public List<Postulacion> getPostulaciones() {
        return postulaciones;
    }

    public List<Confirmacion> getConfirmaciones() {
        return confirmaciones;
    }

    /**
     * Retorna solo las postulaciones aceptadas.
     */
    public List<Postulacion> getPostulacionesAceptadas() {
        return postulaciones.stream()
                .filter(Postulacion::isAceptada)
                .toList();
    }

    /**
     * Retorna solo las postulaciones pendientes.
     */
    public List<Postulacion> getPostulacionesPendientes() {
        return postulaciones.stream()
                .filter(Postulacion::isPendiente)
                .toList();
    }

    /**
     * Retorna solo las confirmaciones confirmadas.
     */
    public List<Confirmacion> getConfirmacionesConfirmadas() {
        return confirmaciones.stream()
                .filter(Confirmacion::isConfirmada)
                .toList();
    }

    /**
     * Retorna las confirmaciones confirmadas que tienen roles asignados.
     * Útil para obtener la formación final del equipo con roles.
     */
    public List<Confirmacion> getConfirmacionesConRoles() {
        return confirmaciones.stream()
                .filter(Confirmacion::isConfirmada)
                .filter(Confirmacion::tieneRolAsignado)
                .toList();
    }

    /**
     * Obtiene la confirmación de un usuario específico si está confirmada.
     * 
     * @param userId ID del usuario
     * @return la confirmación del usuario o null si no existe o no está confirmada
     */
    public Confirmacion getConfirmacionUsuario(String userId) {
        return confirmaciones.stream()
                .filter(conf -> conf.getUserId().equals(userId))
                .filter(Confirmacion::isConfirmada)
                .findFirst()
                .orElse(null);
    }

    /**
     * Verifica si un usuario ya se postuló a este scrim.
     */
    public boolean yaSePostulo(String userId) {
        return postulaciones.stream()
                .anyMatch(p -> p.getUserId().equals(userId));
    }

    /**
     * Obtiene las postulaciones aceptadas para mantener compatibilidad con vistas.
     * 
     * @deprecated Usar getPostulacionesAceptadas() en su lugar
     */
    @Deprecated
    public List<String> getListaPostulaciones() {
        return postulaciones.stream()
                .filter(Postulacion::isAceptada)
                .map(Postulacion::getUserId)
                .toList();
    }

    /**
     * Obtiene las confirmaciones confirmadas para mantener compatibilidad con
     * vistas.
     * 
     * @deprecated Usar getConfirmacionesConfirmadas() en su lugar
     */
    @Deprecated
    public List<String> getListaConfirmaciones() {
        return confirmaciones.stream()
                .filter(Confirmacion::isConfirmada)
                .map(Confirmacion::getUserId)
                .toList();
    }

    public int getPlazas() {
        return plazas;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    // Setter protegido para el state
    public void setState(ScrimState newState) {
        this.state = newState;
        // Sincronizar el estado actual para persistencia
        if (newState != null) {
            this.estadoActual = newState.getEstado();
        }
    }

    public String getEstado() {
        // Si state es null (después de deserializar), usar estadoActual
        if (state == null) {
            return estadoActual != null ? estadoActual : "BUSCANDO";
        }
        return state.getEstado();
    }

    // Getters adicionales necesarios para el organizador y validaciones

    // Getters adicionales
    public String getId() {
        return id;
    }

    public Juego getJuego() {
        return juego;
    }

    public ScrimFormat getFormato() {
        return formato;
    }

    public List<String> getRolesRequeridos() {
        return rolesRequeridos;
    }

    public int getRangoMin() {
        return rangoMin;
    }

    public int getRangoMax() {
        return rangoMax;
    }

    public int getLatenciaMax() {
        return latenciaMax;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public ScrimState getState() {
        return state;
    }

    public String getEstrategiaMatchmaking() {
        return estrategiaMatchmaking;
    }

    public void setEstrategiaMatchmaking(String estrategiaMatchmaking) {
        this.estrategiaMatchmaking = estrategiaMatchmaking;
    }
}