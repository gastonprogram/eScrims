package model.states;

import model.Scrim;
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