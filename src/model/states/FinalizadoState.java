package model.states;

import model.Scrim;

/**
 * Estado de un scrim finalizado.
 * Una vez finalizado, no se pueden realizar más acciones sobre el scrim.
 */
public class FinalizadoState implements ScrimState {
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
        throw new IllegalStateException("No se puede cancelar una scrim finalizada");
    }

    @Override
    public String getEstado() {
        return "FINALIZADO";
    }
}
