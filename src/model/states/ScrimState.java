package model.states;

import model.Scrim;
import model.Postulacion;
import model.Confirmacion;

/**
 * Interfaz que define el contrato para los diferentes estados de un Scrim.
 * Implementa el patrón State para gestionar el ciclo de vida de los scrims.
 * 
 * Estados posibles:
 * - BuscandoState: buscando jugadores
 * - LobbyArmadoState: todos los cupos llenos, esperando confirmaciones
 * - ConfirmadoState: todas las confirmaciones recibidas
 * - EnJuegoState: partida en curso
 * - FinalizadoState: partida terminada
 * - CanceladoState: scrim cancelado
 * 
 * @author eScrims Team
 */
public interface ScrimState {
    /**
     * Procesa una postulación en el estado actual.
     * 
     * @param scrim       El scrim al que se postula
     * @param postulacion La postulación a procesar
     */
    void postular(Scrim scrim, Postulacion postulacion);

    /**
     * Procesa una confirmación en el estado actual.
     * 
     * @param scrim        El scrim a confirmar
     * @param confirmacion La confirmación a procesar
     */
    void confirmar(Scrim scrim, Confirmacion confirmacion);

    /**
     * Inicia el scrim (transición a EnJuego).
     * 
     * @param scrim El scrim a iniciar
     */
    void iniciar(Scrim scrim);

    /**
     * Finaliza el scrim (transición a Finalizado).
     * 
     * @param scrim El scrim a finalizar
     */
    void finalizar(Scrim scrim);

    /**
     * Cancela el scrim (transición a Cancelado).
     * 
     * @param scrim El scrim a cancelar
     */
    void cancelar(Scrim scrim);

    /**
     * Retorna el nombre del estado actual.
     * 
     * @return El nombre del estado
     */
    String getEstado();
}