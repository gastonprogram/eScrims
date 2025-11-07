package dominio.estados;

import dominio.modelo.Confirmacion;
import dominio.modelo.Postulacion;
import dominio.modelo.Scrim;
import infraestructura.notificaciones.core.NotificationService;

/**
 * Estado LOBBY_ARMADO: todos los cupos están llenos con jugadores aceptados.
 * 
 * En este estado, cada jugador debe confirmar su asistencia.
 * 
 * Transiciones posibles:
 * - A CONFIRMADO: cuando todos los jugadores confirman su asistencia
 * - A CANCELADO: si el organizador cancela el scrim
 * 
 * Reglas de negocio:
 * - NO se aceptan nuevas postulaciones
 * - Se esperan confirmaciones de todos los jugadores
 * - Si alguien rechaza, se debería volver a BUSCANDO (implementación futura)
 * 
 * @author eScrims Team
 */
public class LobbyArmadoState implements ScrimState {

    @Override
    public void postular(Scrim scrim, Postulacion postulacion) {
        throw new IllegalStateException("No se pueden aceptar postulaciones en estado LOBBY_ARMADO. " +
                "El cupo ya está completo");
    }

    @Override
    public void confirmar(Scrim scrim, Confirmacion confirmacion) {
        // Buscar la confirmación en el scrim (puede estar PENDIENTE, CONFIRMADA o
        // RECHAZADA)
        Confirmacion confirmacionEnScrim = scrim.getConfirmaciones().stream()
                .filter(c -> c.getUserId().equals(confirmacion.getUserId()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "No existe una confirmación para este usuario"));

        // Si ya fue procesada, no hacer nada (el controller ya cambió el estado)
        if (confirmacionEnScrim.isConfirmada()) {
            // Ya está confirmada, verificar si todos confirmaron

            if (todosConfirmaron(scrim)) {
                scrim.setState(new ConfirmadoState());
                // Notificar que todos confirmaron
                NotificationService.getInstance().notifyConfirmadoTodos(scrim);
            }
        } else if (confirmacionEnScrim.isRechazada()) {
            // Ya está rechazada, volver a BUSCANDO
            // Remover la postulación aceptada del jugador que rechazó
            scrim.getPostulaciones().stream()
                    .filter(p -> p.getUserId().equals(confirmacion.getUserId()))
                    .filter(p -> p.getEstado() == Postulacion.EstadoPostulacion.ACEPTADA)
                    .findFirst()
                    .ifPresent(p -> scrim.getPostulaciones().remove(p));

            // IMPORTANTE: Limpiar todas las confirmaciones cuando volvemos a BUSCANDO
            // (Alguien no confirmó)
            // para que cuando se vuelva a llenar, se generen confirmaciones frescas
            scrim.getConfirmaciones().clear();

            // Volver a estado BUSCANDO (falta un jugador)
            scrim.setState(new BuscandoState());
        } else {
            // Está PENDIENTE, esto no debería pasar si el controller hace su trabajo
            throw new IllegalStateException("Error de flujo: la confirmación sigue pendiente");
        }
    }

    @Override
    public void iniciar(Scrim scrim) {
        throw new IllegalStateException("No se puede iniciar en estado LOBBY_ARMADO. " +
                "Todos los jugadores deben confirmar primero");
    }

    @Override
    public void finalizar(Scrim scrim) {
        throw new IllegalStateException("No se puede finalizar en estado LOBBY_ARMADO. " +
                "Debe estar en EN_JUEGO");
    }

    @Override
    public void cancelar(Scrim scrim) {
        scrim.setState(new CanceladoState());
    }

    @Override
    public String getEstado() {
        return "LOBBY_ARMADO";
    }

    /**
     * Verifica si todos los jugadores confirmaron su asistencia.
     */
    private boolean todosConfirmaron(Scrim scrim) {
        long confirmadas = scrim.getConfirmaciones().stream()
                .filter(Confirmacion::isConfirmada)
                .count();

        return confirmadas == scrim.getPlazas();
    }
}