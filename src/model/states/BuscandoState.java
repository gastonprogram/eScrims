package model.states;

import model.Scrim;
import model.Postulacion;
import model.Confirmacion;

/**
 * Estado BUSCANDO: el scrim está buscando jugadores para completar el cupo.
 * 
 * Transiciones posibles:
 * - A LOBBY_ARMADO: cuando se completan todos los cupos con postulaciones
 * aceptadas
 * - A CANCELADO: si el organizador cancela el scrim
 * 
 * Reglas de negocio:
 * - Se pueden recibir postulaciones
 * - Se valida rango y latencia automáticamente
 * - Si no cumple requisitos, se rechaza automáticamente con motivo
 * - Si cumple requisitos, se acepta automáticamente
 * - Cuando se llena el cupo de jugadores aceptados, transiciona a LOBBY_ARMADO
 * - Al transicionar a LOBBY_ARMADO, se generan confirmaciones automáticamente
 * 
 * @author eScrims Team
 */
public class BuscandoState implements ScrimState {

    @Override
    public void postular(Scrim scrim, Postulacion postulacion) {
        // Verificar que no se haya postulado antes
        if (scrim.yaSePostulo(postulacion.getUserId())) {
            throw new IllegalStateException("El usuario ya se postuló a este scrim");
import model.notifications.core.NotificationService;

public class BuscandoState implements ScrimState {
    @Override
    public void postular(Scrim scrim, String userId) {
        if (!scrim.getListaPostulaciones().contains(userId)) {
            scrim.getListaPostulaciones().add(userId);
            if (scrim.getPlazas() == scrim.getListaPostulaciones().size()) {
                scrim.setState(new LobbyArmadoState());
                // Notificar que el lobby está completo
                NotificationService.getInstance().notifyLobbyArmado(scrim);
            }
        }
    }

    @Override
    public void confirmar(Scrim scrim, String userId) {
        throw new IllegalStateException("No se puede confirmar en estado Buscando");
    }

    @Override
    public void iniciar(Scrim scrim) {
        throw new IllegalStateException("No se puede iniciar en estado Buscando");
    }

    @Override
    public void finalizar(Scrim scrim) {
        throw new IllegalStateException("No se puede finalizar en estado Buscando");
    }

    @Override
    public void cancelar(Scrim scrim) {
        scrim.setState(new CanceladoState());
        // Notificar cancelación
        NotificationService.getInstance().notifyCancelado(scrim);
    }

    @Override
    public String getEstado() {
        return "BUSCANDO";
    }
}

class LobbyArmadoState implements ScrimState {
    @Override
    public void postular(Scrim scrim, String userId) {
        throw new IllegalStateException("No se puede postular en estado Lobby Armado");
    }

    @Override
    public void confirmar(Scrim scrim, String userId) {
        if (scrim.getListaPostulaciones().contains(userId) && !scrim.getListaConfirmaciones().contains(userId)) {
            scrim.getListaConfirmaciones().add(userId);
            if (scrim.getListaConfirmaciones().size() == scrim.getPlazas()) {
                scrim.setState(new ConfirmadoState());
                // Notificar que todos confirmaron
                NotificationService.getInstance().notifyConfirmadoTodos(scrim);
            }
        }

        // Validar requisitos automáticamente
        String mensajeError = postulacion.validarRequisitos(
                scrim.getRangoMin(),
                scrim.getRangoMax(),
                scrim.getLatenciaMax());

        if (mensajeError != null) {
            // No cumple requisitos, rechazar automáticamente
            postulacion.rechazar(mensajeError);
            scrim.getPostulaciones().add(postulacion);
            throw new IllegalArgumentException("Postulación rechazada: " + mensajeError);
        }
    @Override
    public void cancelar(Scrim scrim) {
        scrim.setState(new CanceladoState());
        // Notificar cancelación
        NotificationService.getInstance().notifyCancelado(scrim);
    }

        // Cumple requisitos, aceptar automáticamente
        postulacion.aceptar();
        scrim.getPostulaciones().add(postulacion);

        // Si ya hay suficientes postulaciones aceptadas, transicionar
        if (scrim.getPostulacionesAceptadas().size() >= scrim.getPlazas()) {
            transicionarALobbyArmado(scrim);
        }
    }

    @Override
    public void confirmar(Scrim scrim, Confirmacion confirmacion) {
        throw new IllegalStateException("No se puede confirmar en estado BUSCANDO. " +
                "El scrim debe estar en LOBBY_ARMADO");
    }

    @Override
    public void iniciar(Scrim scrim) {
        throw new IllegalStateException("No se puede iniciar en estado BUSCANDO. " +
                "Debe estar en CONFIRMADO");
        scrim.setState(new EnJuegoState());
        // Notificar que el juego ha comenzado
        NotificationService.getInstance().notifyEnJuego(scrim);
    }

    @Override
    public void finalizar(Scrim scrim) {
        throw new IllegalStateException("No se puede finalizar en estado BUSCANDO. " +
                "Debe estar en EN_JUEGO");
    }

    @Override
    public void cancelar(Scrim scrim) {
        scrim.setState(new CanceladoState());
        // Notificar cancelación
        NotificationService.getInstance().notifyCancelado(scrim);
    }

    @Override
    public String getEstado() {
        return "BUSCANDO";
        return "CONFIRMADO";
    }
}

class EnJuegoState implements ScrimState {
    @Override
    public void postular(Scrim scrim, String userId) {
        throw new IllegalStateException("No se puede postular en estado En Juego");
    }

    @Override
    public void confirmar(Scrim scrim, String userId) {
        throw new IllegalStateException("No se puede confirmar en estado En Juego");
    }

    @Override
    public void iniciar(Scrim scrim) {
        throw new IllegalStateException("La scrim ya está en juego");
    }

    @Override
    public void finalizar(Scrim scrim) {
        scrim.setState(new FinalizadoState());
        // Notificar que el scrim ha finalizado
        NotificationService.getInstance().notifyFinalizado(scrim);
    }

    @Override
    public void cancelar(Scrim scrim) {
        throw new IllegalStateException("No se puede cancelar una scrim en juego");
    }

    @Override
    public String getEstado() {
        return "EN_JUEGO";
    }
}

class FinalizadoState implements ScrimState {
    @Override
    public void postular(Scrim scrim, String userId) {
        throw new IllegalStateException("La scrim ya está finalizada");
    }

    @Override
    public void confirmar(Scrim scrim, String userId) {
        throw new IllegalStateException("La scrim ya está finalizada");
    }

    @Override
    public void iniciar(Scrim scrim) {
        throw new IllegalStateException("La scrim ya está finalizada");
    }

    @Override
    public void finalizar(Scrim scrim) {
        throw new IllegalStateException("La scrim ya está finalizada");
    }

    @Override
    public void cancelar(Scrim scrim) {
        throw new IllegalStateException("La scrim ya está finalizada");
    }

    @Override
    public String getEstado() {
        return "FINALIZADO";
    }
}

class CanceladoState implements ScrimState {
    @Override
    public void postular(Scrim scrim, String userId) {
        throw new IllegalStateException("La scrim está cancelada");
    }

    @Override
    public void confirmar(Scrim scrim, String userId) {
        throw new IllegalStateException("La scrim está cancelada");
    }

    @Override
    public void iniciar(Scrim scrim) {
        throw new IllegalStateException("La scrim está cancelada");
    }

    @Override
    public void finalizar(Scrim scrim) {
        throw new IllegalStateException("La scrim está cancelada");
    }

    /**
     * Transiciona el scrim a LOBBY_ARMADO y genera confirmaciones para cada
     * jugador.
     */
    private void transicionarALobbyArmado(Scrim scrim) {
        // Generar confirmaciones para cada jugador aceptado
        for (Postulacion postulacion : scrim.getPostulacionesAceptadas()) {
            Confirmacion confirmacion = new Confirmacion(scrim.getId(), postulacion.getUserId());
            scrim.getConfirmaciones().add(confirmacion);
        }

        // Cambiar estado
        scrim.setState(new LobbyArmadoState());
    }
}