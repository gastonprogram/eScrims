package dominio.estados;

import dominio.modelo.Confirmacion;
import dominio.modelo.Postulacion;
import dominio.modelo.Scrim;

/**
 * Estado CANCELADO: el scrim fue cancelado por el organizador antes del inicio.
 * 
 * Este es un estado final. El scrim no se llevará a cabo.
 * 
 * Transiciones posibles:
 * - Ninguna (estado final)
 * 
 * Reglas de negocio:
 * - NO se permiten más cambios de estado
 * - Todos los jugadores deben ser notificados (implementación futura)
 * 
 * @author eScrims Team
 */
public class CanceladoState implements ScrimState {

    @Override
    public void postular(Scrim scrim, Postulacion postulacion) {
        throw new IllegalStateException("El scrim está cancelado");
    }

    @Override
    public void confirmar(Scrim scrim, Confirmacion confirmacion) {
        throw new IllegalStateException("El scrim está cancelado");
    }

    @Override
    public void iniciar(Scrim scrim) {
        throw new IllegalStateException("No se puede iniciar un scrim cancelado");
    }

    @Override
    public void finalizar(Scrim scrim) {
        throw new IllegalStateException("No se puede finalizar un scrim cancelado");
    }

    @Override
    public void cancelar(Scrim scrim) {
        throw new IllegalStateException("El scrim ya está cancelado");
    }

    @Override
    public String getEstado() {
        return "CANCELADO";
    }
}
