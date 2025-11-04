package dominio.estados;

import dominio.modelo.Confirmacion;
import dominio.modelo.Postulacion;
import dominio.modelo.Scrim;

/**
 * Estado EN_JUEGO: la partida está en curso.
 * 
 * Transiciones posibles:
 * - A FINALIZADO: cuando termina la partida
 * 
 * Reglas de negocio:
 * - NO se pueden aceptar postulaciones ni confirmaciones
 * - NO se puede cancelar una vez iniciada
 * - Solo se puede finalizar
 * 
 * @author eScrims Team
 */
public class EnJuegoState implements ScrimState {

    @Override
    public void postular(Scrim scrim, Postulacion postulacion) {
        throw new IllegalStateException("No se pueden aceptar postulaciones en estado EN_JUEGO. " +
                "La partida ya comenzó");
    }

    @Override
    public void confirmar(Scrim scrim, Confirmacion confirmacion) {
        throw new IllegalStateException("No se pueden procesar confirmaciones en estado EN_JUEGO. " +
                "La partida ya comenzó");
    }

    @Override
    public void iniciar(Scrim scrim) {
        throw new IllegalStateException("El scrim ya está en curso");
    }

    @Override
    public void finalizar(Scrim scrim) {
        scrim.setState(new FinalizadoState());
    }

    @Override
    public void cancelar(Scrim scrim) {
        throw new IllegalStateException("No se puede cancelar un scrim en curso. " +
                "Debe finalizarlo primero");
    }

    @Override
    public String getEstado() {
        return "EN_JUEGO";
    }
}