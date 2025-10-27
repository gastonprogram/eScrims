package model.states;

import model.Scrim;

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
