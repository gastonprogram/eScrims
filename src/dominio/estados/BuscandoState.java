package dominio.estados;

import dominio.modelo.Confirmacion;
import dominio.modelo.Postulacion;
import dominio.modelo.Scrim;
import dominio.modelo.Usuario;
import infraestructura.notificaciones.ScrimNotificationObserver;
import infraestructura.persistencia.repository.RepositorioFactory;
import infraestructura.persistencia.repository.RepositorioUsuario;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Estado BUSCANDO: el scrim está buscando jugadores para completar el cupo.
 * 
 * Transiciones posibles:
 * - A LOBBY_ARMADO: cuando se completan todos los cupos con postulaciones
 * aceptadas
 * - A CANCELADO: si el organizador cancela el scrim
 * 
 * Reglas de negocio:
 * - Se pueden recibir postulaciones
 * - Se valida rango y latencia automáticamente
 * - Si no cumple requisitos, se rechaza automáticamente con motivo
 * - Si cumple requisitos, se acepta automáticamente
 * - Cuando se llena el cupo de jugadores aceptados, transiciona a LOBBY_ARMADO
 * - Al transicionar a LOBBY_ARMADO, se generan confirmaciones automáticamente
 * 
 * @author eScrims Team
 */
public class BuscandoState implements ScrimState {

    @Override
    public void postular(Scrim scrim, Postulacion postulacion) {
        // Verificar que no se haya postulado antes
        if (scrim.yaSePostulo(postulacion.getUserId())) {
            throw new IllegalStateException("El usuario ya se postuló a este scrim");
        }

        // Validar requisitos automáticamente
        String mensajeError = postulacion.validarRequisitos(
                scrim.getRangoMin(),
                scrim.getRangoMax(),
                scrim.getLatenciaMax());

        if (mensajeError != null) {
            // No cumple requisitos, rechazar automáticamente
            postulacion.rechazar(mensajeError);
            scrim.getPostulaciones().add(postulacion);
            throw new IllegalArgumentException("Postulación rechazada: " + mensajeError);
        }

        // Cumple requisitos, aceptar automáticamente
        postulacion.aceptar();
        scrim.getPostulaciones().add(postulacion);

        // Si ya hay suficientes postulaciones aceptadas, transicionar
        if (scrim.getPostulacionesAceptadas().size() >= scrim.getPlazas()) {
            transicionarALobbyArmado(scrim);
        }
    }

    @Override
    public void confirmar(Scrim scrim, Confirmacion confirmacion) {
        throw new IllegalStateException("No se puede confirmar en estado BUSCANDO. " +
                "El scrim debe estar en LOBBY_ARMADO");
    }

    @Override
    public void iniciar(Scrim scrim) {
        throw new IllegalStateException("No se puede iniciar en estado BUSCANDO. " +
                "Debe estar en CONFIRMADO");
    }

    @Override
    public void finalizar(Scrim scrim) {
        throw new IllegalStateException("No se puede finalizar en estado BUSCANDO. " +
                "Debe estar en EN_JUEGO");
    }

    @Override
    public void cancelar(Scrim scrim) {
        scrim.setState(new CanceladoState());
        
        // Notificar cancelación
        try {
            List<Usuario> participantes = obtenerUsuariosParticipantes(scrim);
            ScrimNotificationObserver observer = new ScrimNotificationObserver();
            observer.notificarCancelado(scrim, participantes, "Scrim cancelado por el organizador");
        } catch (Exception e) {
            System.err.println("Error al enviar notificaciones: " + e.getMessage());
        }
    }

    @Override
    public String getEstado() {
        return "BUSCANDO";
    }

    /**
     * Transiciona el scrim a LOBBY_ARMADO y genera confirmaciones para cada
     * jugador.
     */
    private void transicionarALobbyArmado(Scrim scrim) {
        // Generar confirmaciones para cada jugador aceptado
        for (Postulacion postulacion : scrim.getPostulacionesAceptadas()) {
            Confirmacion confirmacion = new Confirmacion(scrim.getId(), postulacion.getUserId());
            scrim.getConfirmaciones().add(confirmacion);
        }

        // Cambiar estado
        scrim.setState(new LobbyArmadoState());
        
        // Notificar a todos los participantes
        try {
            List<Usuario> participantes = obtenerUsuariosParticipantes(scrim);
            ScrimNotificationObserver observer = new ScrimNotificationObserver();
            observer.notificarLobbyArmado(scrim, participantes);
        } catch (Exception e) {
            System.err.println("Error al enviar notificaciones: " + e.getMessage());
        }
    }
    
    private List<Usuario> obtenerUsuariosParticipantes(Scrim scrim) {
        RepositorioUsuario repo = RepositorioFactory.getRepositorioUsuario();
        return scrim.getPostulacionesAceptadas().stream()
                .map(p -> repo.buscarPorId(p.getUserId()))
                .filter(u -> u != null)
                .collect(Collectors.toList());
    }
}