package model.states;

import model.Scrim;

class ConfirmadoState implements ScrimState {
    @Override
    public void postular(Scrim scrim, String userId) {
        throw new IllegalStateException("No se puede postular en estado Confirmado");
    }

    @Override
    public void confirmar(Scrim scrim, String userId) {
        throw new IllegalStateException("No se puede confirmar en estado Confirmado");
    }

    @Override
    public void iniciar(Scrim scrim) {
        scrim.setState(new EnJuegoState());
    }

    @Override
    public void finalizar(Scrim scrim) {
        throw new IllegalStateException("No se puede finalizar en estado Confirmado");
    }

    @Override
    public void cancelar(Scrim scrim) {
        scrim.setState(new CanceladoState());
    }

    @Override
    public String getEstado() {
        return "CONFIRMADO";
    }
}