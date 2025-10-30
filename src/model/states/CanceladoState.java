package model.states;

import model.Scrim;

/**
 * Estado de un scrim cancelado.
 * Una vez cancelado, no se pueden realizar más acciones sobre el scrim.
 */
public class CanceladoState implements ScrimState {
    @Override
    public void postular(Scrim scrim, String userId) {
        throw new IllegalStateException("La scrim ha sido cancelada");
    }

    @Override
    public void confirmar(Scrim scrim, String userId) {
        throw new IllegalStateException("La scrim ha sido cancelada");
    }

    @Override
    public void iniciar(Scrim scrim) {
        throw new IllegalStateException("La scrim ha sido cancelada");
    }

    @Override
    public void finalizar(Scrim scrim) {
        throw new IllegalStateException("La scrim ha sido cancelada");
    }

    @Override
    public void cancelar(Scrim scrim) {
        throw new IllegalStateException("La scrim ya está cancelada");
    }

    @Override
    public String getEstado() {
        return "CANCELADO";
    }
}
