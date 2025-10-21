package model.states;

import model.Scrim;
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
            }
        }
    }

    @Override
    public void iniciar(Scrim scrim) {
        throw new IllegalStateException("No se puede iniciar en estado Lobby Armado");
    }

    @Override
    public void finalizar(Scrim scrim) {
        throw new IllegalStateException("No se puede finalizar en estado Lobby Armado");
    }

    @Override
    public void cancelar(Scrim scrim) {
        scrim.setState(new CanceladoState());
    }

    @Override
    public String getEstado() {
        return "LOBBY_ARMADO";
    }
}