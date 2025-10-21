package model.states;

import model.Scrim;

public interface ScrimState {
    void postular(Scrim scrim, String userId);
    void confirmar(Scrim scrim, String userId);
    void iniciar(Scrim scrim);
    void finalizar(Scrim scrim);
    void cancelar(Scrim scrim);
    String getEstado();
}