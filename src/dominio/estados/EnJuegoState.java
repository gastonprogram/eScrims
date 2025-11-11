package dominio.estados;

import java.util.List;
import java.util.stream.Collectors;

import dominio.modelo.Confirmacion;
import dominio.modelo.Postulacion;
import dominio.modelo.Scrim;
import dominio.modelo.Usuario;
import infraestructura.persistencia.repository.RepositorioUsuario;
import infraestructura.notificaciones.observer.ScrimNotificationObserver;
import infraestructura.persistencia.repository.RepositorioFactory;
import dominio.estadisticas.EstadisticasScrim;
import aplicacion.services.EstadisticasService;

/**
 * Estado EN_JUEGO: la partida está en curso.
 * 
 * Transiciones posibles:
 * - A FINALIZADO: cuando termina la partida
 * 
 * Reglas de negocio:
 * - NO se pueden aceptar postulaciones ni confirmaciones
 * - NO se puede cancelar una vez iniciada
 * - Solo se puede finalizar
 * - Al finalizar, genera automáticamente estadísticas simuladas
 * 
 * @author eScrims Team
 */
public class EnJuegoState implements ScrimState {

    @Override
    public void postular(Scrim scrim, Postulacion postulacion) {
        throw new IllegalStateException("No se pueden aceptar postulaciones en estado EN_JUEGO. " +
                "La partida ya comenzó");
    }

    @Override
    public void confirmar(Scrim scrim, Confirmacion confirmacion) {
        throw new IllegalStateException("No se pueden procesar confirmaciones en estado EN_JUEGO. " +
                "La partida ya comenzó");
    }

    @Override
    public void iniciar(Scrim scrim) {
        throw new IllegalStateException("El scrim ya está en curso");
    }

    @Override
    public void finalizar(Scrim scrim) {
        // Generar automáticamente estadísticas simuladas al finalizar
        generarEstadisticasAutomaticas(scrim);

        // Cambiar estado a finalizado
        scrim.setState(new FinalizadoState());

        // Notificar finalización del juego
        try {
            List<Usuario> participantes = obtenerUsuariosParticipantes(scrim);
            ScrimNotificationObserver observer = new ScrimNotificationObserver();
            observer.notificarFinalizado(scrim, participantes);
        } catch (Exception e) {
            System.err.println("Error al enviar notificaciones: " + e.getMessage());
        }
    }

    /**
     * Genera automáticamente estadísticas simuladas para todos los jugadores del
     * scrim.
     * Usa el EstadisticasService para persistir las estadísticas.
     */
    private void generarEstadisticasAutomaticas(Scrim scrim) {
        try {
            EstadisticasService estadisticasService = new EstadisticasService();

            // Crear estadísticas para el scrim
            EstadisticasScrim estadisticas = estadisticasService.obtenerEstadisticasParaScrim(scrim);

            // Obtener todos los jugadores confirmados
            java.util.List<String> jugadoresIds = scrim.getConfirmacionesConfirmadas().stream()
                    .map(Confirmacion::getUserId)
                    .toList();

            // Simular estadísticas para cada jugador
            for (String jugadorId : jugadoresIds) {
                simularEstadisticasJugador(estadisticas, jugadorId);
            }

            // Finalizar partida con estadísticas simuladas (incluye elección de ganador y
            // MVP)
            estadisticas.finalizarPartidaConSimulacion();

            // Guardar estadísticas usando el servicio
            estadisticasService.actualizarEstadisticas(estadisticas);

            System.out.println("Estadísticas automáticas generadas y guardadas para scrim: " + scrim.getId());

        } catch (Exception e) {
            System.err.println("Error generando estadísticas automáticas: " + e.getMessage());
            // El scrim sigue finalizándose aunque falle la generación de estadísticas
        }
    }

    /**
     * Simula estadísticas realistas para un jugador.
     */
    private void simularEstadisticasJugador(EstadisticasScrim estadisticas, String jugadorId) {
        java.util.Random random = new java.util.Random();

        // Simular estadísticas realistas (basado en
        // TestEstadisticasYModeracionCompleto)
        int kills = random.nextInt(15) + 1; // 1-15 kills
        int deaths = random.nextInt(8) + 1; // 1-8 deaths
        int assists = random.nextInt(20) + 1; // 1-20 assists
        int puntuacion = random.nextInt(40) + 60; // 60-100 puntos

        estadisticas.registrarEstadisticasJugador(jugadorId, kills, assists, deaths, puntuacion);
    }

    @Override
    public void cancelar(Scrim scrim) {
        throw new IllegalStateException("No se puede cancelar un scrim en curso. " +
                "Debe finalizarlo primero");
    }

    @Override
    public String getEstado() {
        return "EN_JUEGO";
    }

    private List<Usuario> obtenerUsuariosParticipantes(Scrim scrim) {
        RepositorioUsuario repo = RepositorioFactory.getRepositorioUsuario();
        return scrim.getConfirmacionesConfirmadas().stream()
                .map(c -> repo.buscarPorId(c.getUserId()))
                .filter(u -> u != null)
                .collect(Collectors.toList());
    }
}