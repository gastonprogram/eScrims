package model.states;

import model.Scrim;

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

    @Override
    public void cancelar(Scrim scrim) {
        throw new IllegalStateException("La scrim ya está cancelada");
    }

    @Override
    public String getEstado() {
        return "CANCELADO";
    }
}
