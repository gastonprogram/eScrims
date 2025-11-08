package dominio.estados;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import dominio.modelo.Confirmacion;
import dominio.modelo.Postulacion;
import dominio.modelo.Scrim;
import dominio.modelo.Usuario;
import infraestructura.notificaciones.ScrimNotificationObserver;
import infraestructura.persistencia.repository.RepositorioFactory;
import infraestructura.persistencia.repository.RepositorioUsuario;

/**
 * Estado CONFIRMADO: todos los jugadores han confirmado su asistencia.
 * 
 * El scrim está listo para iniciarse. El sistema debe programar
 * automáticamente el cambio a EN_JUEGO cuando llegue la fecha/hora.
 * 
 * Transiciones posibles:
 * - A EN_JUEGO: cuando llega la fecha/hora programada (manual o automático)
 * - A CANCELADO: si el organizador cancela antes del inicio
 * 
 * Reglas de negocio:
 * - NO se aceptan postulaciones ni confirmaciones
 * - Solo se puede iniciar o cancelar
 * 
 * @author eScrims Team
 */
public class ConfirmadoState implements ScrimState {

    @Override
    public void postular(Scrim scrim, Postulacion postulacion) {
        throw new IllegalStateException("No se pueden aceptar postulaciones en estado CONFIRMADO. " +
                "El scrim ya está confirmado");
    }

    @Override
    public void confirmar(Scrim scrim, Confirmacion confirmacion) {
        throw new IllegalStateException("No se pueden procesar confirmaciones en estado CONFIRMADO. " +
                "Todos los jugadores ya confirmaron");
    }

    @Override
    public void iniciar(Scrim scrim) {
        // Verificar que sea la hora correcta (opcional, podría ser manual)
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime fechaScrim = scrim.getFechaHora();

        if (ahora.isBefore(fechaScrim.minusMinutes(10))) {
            throw new IllegalStateException("No se puede iniciar el scrim. " +
                    "Falta más de 10 minutos para la hora programada");
        }

        scrim.setState(new EnJuegoState());
        
        // Notificar inicio del juego
        try {
            List<Usuario> participantes = obtenerUsuariosParticipantes(scrim);
            ScrimNotificationObserver observer = new ScrimNotificationObserver();
            observer.notificarEnJuego(scrim, participantes);
        } catch (Exception e) {
            System.err.println("Error al enviar notificaciones: " + e.getMessage());
        }
    }

    @Override
    public void finalizar(Scrim scrim) {
        throw new IllegalStateException("No se puede finalizar en estado CONFIRMADO. " +
                "Debe estar en EN_JUEGO");
    }

    @Override
    public void cancelar(Scrim scrim) {
        scrim.setState(new CanceladoState());
        
        // Notificar cancelación
        try {
            List<Usuario> participantes = obtenerUsuariosParticipantes(scrim);
            ScrimNotificationObserver observer = new ScrimNotificationObserver();
            observer.notificarCancelado(scrim, participantes, "Scrim cancelado antes del inicio");
        } catch (Exception e) {
            System.err.println("Error al enviar notificaciones: " + e.getMessage());
        }
    }

    @Override
    public String getEstado() {
        return "CONFIRMADO";
    }
    
    private List<Usuario> obtenerUsuariosParticipantes(Scrim scrim) {
        RepositorioUsuario repo = RepositorioFactory.getRepositorioUsuario();
        return scrim.getConfirmacionesConfirmadas().stream()
                .map(c -> repo.buscarPorId(c.getUserId()))
                .filter(u -> u != null)
                .collect(Collectors.toList());
    }
}