package model.states;

import model.Scrim;

public class BuscandoState implements ScrimState {
    @Override
    public void postular(Scrim scrim, String userId) {
        if (!scrim.getListaPostulaciones().contains(userId)) {
            scrim.getListaPostulaciones().add(userId);
            if (scrim.getPlazas() == scrim.getListaPostulaciones().size()) {
                scrim.setState(new LobbyArmadoState());
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
    }

    @Override
    public String getEstado() {
        return "BUSCANDO";
    }
}