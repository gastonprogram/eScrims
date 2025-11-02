package model;

import java.time.LocalDateTime;

/**
 * Representa el historial de comportamiento de un usuario en la plataforma.
 * 
 * Esta clase mantiene métricas clave para evaluar la compatibilidad y
 * confiabilidad
 * de un jugador al participar en scrims. Se utiliza principalmente por la
 * estrategia
 * de matchmaking basada en historial/compatibilidad.
 * 
 * Métricas rastreadas:
 * - Partidas totales jugadas
 * - Partidas abandonadas (ragequit, desconexión, etc.)
 * - Tasa de fair play (comportamiento ético)
 * - Última actividad registrada
 * 
 * La tasa de fair play se calcula considerando:
 * - Reportes recibidos por comportamiento tóxico
 * - Sanciones aplicadas por moderadores
 * - Valoraciones positivas de otros jugadores
 * 
 * @author eScrims Team
 * @version 1.0
 * @see model.matchmaking.strategies.ByHistoryStrategy
 */
public class HistorialUsuario {

    private String userId;
    private int partidasJugadas;
    private int partidasAbandonadas;
    private double tasaFairPlay; // Valor entre 0.0 y 1.0 (1.0 es perfecto comportamiento)
    private LocalDateTime ultimaActividad;

    /**
     * Constructor para nuevo historial de usuario.
     * 
     * @param userId ID único del usuario
     */
    public HistorialUsuario(String userId) {
        this.userId = userId;
        this.partidasJugadas = 0;
        this.partidasAbandonadas = 0;
        this.tasaFairPlay = 1.0; // Iniciar con comportamiento perfecto
        this.ultimaActividad = LocalDateTime.now();
    }

    /**
     * Constructor completo (útil para deserialización).
     */
    public HistorialUsuario(String userId, int partidasJugadas, int partidasAbandonadas,
            double tasaFairPlay, LocalDateTime ultimaActividad) {
        this.userId = userId;
        this.partidasJugadas = partidasJugadas;
        this.partidasAbandonadas = partidasAbandonadas;
        this.tasaFairPlay = Math.max(0.0, Math.min(1.0, tasaFairPlay)); // Clamp entre 0 y 1
        this.ultimaActividad = ultimaActividad;
    }

    /**
     * Registra una partida completada.
     */
    public void registrarPartidaCompletada() {
        this.partidasJugadas++;
        this.ultimaActividad = LocalDateTime.now();
    }

    /**
     * Registra una partida abandonada.
     * Esto afecta negativamente la tasa de fair play.
     */
    public void registrarPartidaAbandonada() {
        this.partidasJugadas++;
        this.partidasAbandonadas++;
        this.ultimaActividad = LocalDateTime.now();
        // Penalizar fair play por abandono
        reducirFairPlay(0.1);
    }

    /**
     * Calcula la tasa de abandono del usuario.
     * 
     * @return porcentaje de partidas abandonadas (0.0 a 1.0)
     */
    public double getTasaAbandono() {
        if (partidasJugadas == 0) {
            return 0.0;
        }
        return (double) partidasAbandonadas / partidasJugadas;
    }

    /**
     * Reduce la tasa de fair play del usuario.
     * 
     * @param cantidad cantidad a reducir (0.0 a 1.0)
     */
    public void reducirFairPlay(double cantidad) {
        this.tasaFairPlay = Math.max(0.0, this.tasaFairPlay - cantidad);
    }

    /**
     * Aumenta la tasa de fair play del usuario.
     * 
     * @param cantidad cantidad a aumentar (0.0 a 1.0)
     */
    public void aumentarFairPlay(double cantidad) {
        this.tasaFairPlay = Math.min(1.0, this.tasaFairPlay + cantidad);
    }

    /**
     * Verifica si el usuario tiene buen comportamiento.
     * Considera tanto fair play como tasa de abandono.
     * 
     * @return true si el usuario es confiable
     */
    public boolean tieneBuenComportamiento() {
        return tasaFairPlay >= 0.7 && getTasaAbandono() <= 0.15;
    }

    /**
     * Calcula un score de confiabilidad del usuario.
     * Combina fair play, tasa de abandono y experiencia.
     * 
     * @return score entre 0.0 y 100.0
     */
    public double getScoreConfiabilidad() {
        double scoreFairPlay = tasaFairPlay * 50.0; // Máximo 50 puntos
        double scoreAbandono = (1.0 - getTasaAbandono()) * 30.0; // Máximo 30 puntos
        double scoreExperiencia = Math.min(20.0, partidasJugadas / 5.0); // Máximo 20 puntos

        return scoreFairPlay + scoreAbandono + scoreExperiencia;
    }

    // Getters y Setters

    public String getUserId() {
        return userId;
    }

    public int getPartidasJugadas() {
        return partidasJugadas;
    }

    public void setPartidasJugadas(int partidasJugadas) {
        this.partidasJugadas = Math.max(0, partidasJugadas);
    }

    public int getPartidasAbandonadas() {
        return partidasAbandonadas;
    }

    public void setPartidasAbandonadas(int partidasAbandonadas) {
        this.partidasAbandonadas = Math.max(0, partidasAbandonadas);
    }

    public double getTasaFairPlay() {
        return tasaFairPlay;
    }

    public void setTasaFairPlay(double tasaFairPlay) {
        this.tasaFairPlay = Math.max(0.0, Math.min(1.0, tasaFairPlay));
    }

    public LocalDateTime getUltimaActividad() {
        return ultimaActividad;
    }

    public void setUltimaActividad(LocalDateTime ultimaActividad) {
        this.ultimaActividad = ultimaActividad;
    }

    @Override
    public String toString() {
        return String.format("HistorialUsuario[userId=%s, partidas=%d/%d, fairPlay=%.2f, score=%.2f]",
                userId, partidasJugadas - partidasAbandonadas, partidasJugadas,
                tasaFairPlay, getScoreConfiabilidad());
    }
}
