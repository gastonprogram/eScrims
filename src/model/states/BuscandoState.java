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

    @Override
    public void cancelar(Scrim scrim) {
        throw new IllegalStateException("La scrim ya está cancelada");
    }

    @Override
    public String getEstado() {
        return "CANCELADO";
    }
}