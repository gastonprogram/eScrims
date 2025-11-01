package model.states;

import model.Scrim;
import model.Postulacion;
import model.Confirmacion;

/**
 * Estado FINALIZADO: la partida terminó.
 * 
 * Este es un estado final. En este estado se pueden:
 * - Cargar resultados y estadísticas
 * - Dejar feedback y valoraciones
 * 
 * Transiciones posibles:
 * - Ninguna (estado final)
 * 
 * Reglas de negocio:
 * - NO se permiten más cambios de estado
 * - Se habilita carga de resultados (implementación futura)
 * 
 * @author eScrims Team
 */
public class FinalizadoState implements ScrimState {

    @Override
    public void postular(Scrim scrim, Postulacion postulacion) {
        throw new IllegalStateException("El scrim ya finalizó");
    }

    @Override
    public void confirmar(Scrim scrim, Confirmacion confirmacion) {
        throw new IllegalStateException("El scrim ya finalizó");
    }

    @Override
    public void iniciar(Scrim scrim) {
        throw new IllegalStateException("El scrim ya finalizó");
    }

    @Override
    public void finalizar(Scrim scrim) {
        throw new IllegalStateException("El scrim ya está finalizado");
    }

    @Override
    public void cancelar(Scrim scrim) {
        throw new IllegalStateException("No se puede cancelar un scrim finalizado");
    }

    @Override
    public String getEstado() {
        return "FINALIZADO";
    }
}
