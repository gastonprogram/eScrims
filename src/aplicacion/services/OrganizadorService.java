package aplicacion.services;

import aplicacion.builders.ScrimOrganizador;
import dominio.acciones.AsignarRolAccion;
import dominio.acciones.InvitarJugadorAccion;
import dominio.acciones.SwapJugadoresAccion;
import dominio.modelo.ParticipanteScrim;
import dominio.modelo.Scrim;
import dominio.modelo.Usuario;
import dominio.roles.RolJuego;
import infraestructura.persistencia.repository.RepositorioScrim;
import infraestructura.persistencia.repository.RepositorioUsuario;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Servicio para gestionar las operaciones del organizador de scrims.
 * Actúa como una capa de servicio sobre ScrimOrganizador, manejando
 * la persistencia y validaciones de negocio.
 */
public class OrganizadorService {

    private final RepositorioScrim repositorioScrim;
    private final RepositorioUsuario repositorioUsuario;
    private final ScrimService scrimService;

    // Cache de organizadores activos por scrim
    private final Map<String, ScrimOrganizador> organizadoresActivos;

    public OrganizadorService(RepositorioScrim repositorioScrim, RepositorioUsuario repositorioUsuario,
            ScrimService scrimService) {
        this.repositorioScrim = repositorioScrim;
        this.repositorioUsuario = repositorioUsuario;
        this.scrimService = scrimService;
        this.organizadoresActivos = new HashMap<>();
    }

    /**
     * Obtiene o crea un ScrimOrganizador para un scrim específico.
     */
    public ScrimOrganizador obtenerOrganizadorParaScrim(String scrimId, String usuarioId) {
        // Verificar que el usuario sea el organizador del scrim
        Scrim scrim = scrimService.buscarPorId(scrimId);
        if (!scrim.getCreatedBy().equals(usuarioId)) {
            throw new IllegalArgumentException("Solo el organizador del scrim puede acceder a estas opciones");
        }

        // Obtener o crear el organizador
        return organizadoresActivos.computeIfAbsent(scrimId, id -> new ScrimOrganizador(scrim));
    }

    /**
     * Obtiene todos los scrims que organiza un usuario específico.
     */
    public List<Scrim> obtenerScrimsDelOrganizador(String usuarioId) {
        return scrimService.obtenerScrimsPorOrganizador(usuarioId);
    }

    /**
     * Invita a un jugador al scrim con un rol específico.
     */
    public void invitarJugador(String scrimId, String organizadorId, String jugadorId, RolJuego rol) {
        ScrimOrganizador organizador = obtenerOrganizadorParaScrim(scrimId, organizadorId);

        // Validar que el jugador existe
        Usuario jugador = repositorioUsuario.buscarPorId(jugadorId);
        if (jugador == null) {
            throw new IllegalArgumentException("El jugador con ID " + jugadorId + " no existe");
        }

        // Crear y ejecutar la acción
        InvitarJugadorAccion accion = new InvitarJugadorAccion(jugador, rol);
        organizador.ejecutarAccion(accion);

        // Persistir cambios
        guardarCambios(organizador.getScrim());
    }

    /**
     * Asigna un rol específico a un participante.
     */
    public void asignarRol(String scrimId, String organizadorId, String participanteId, RolJuego nuevoRol) {
        ScrimOrganizador organizador = obtenerOrganizadorParaScrim(scrimId, organizadorId);

        // Crear y ejecutar la acción
        AsignarRolAccion accion = new AsignarRolAccion(participanteId, nuevoRol);
        organizador.ejecutarAccion(accion);

        // Persistir cambios
        guardarCambios(organizador.getScrim());
    }

    /**
     * Intercambia las posiciones de dos jugadores.
     */
    public void swapJugadores(String scrimId, String organizadorId, String jugador1Id, String jugador2Id) {
        ScrimOrganizador organizador = obtenerOrganizadorParaScrim(scrimId, organizadorId);

        // Crear y ejecutar la acción
        SwapJugadoresAccion accion = new SwapJugadoresAccion(jugador1Id, jugador2Id);
        organizador.ejecutarAccion(accion);

        // Persistir cambios
        guardarCambios(organizador.getScrim());
    }

    /**
     * Deshace la última acción realizada en el scrim.
     */
    public void deshacerUltimaAccion(String scrimId, String organizadorId) {
        ScrimOrganizador organizador = obtenerOrganizadorParaScrim(scrimId, organizadorId);
        organizador.deshacerUltimaAccion();

        // Persistir cambios
        guardarCambios(organizador.getScrim());
    }

    /**
     * Confirma el scrim y bloquea futuras modificaciones.
     */
    public void confirmarScrim(String scrimId, String organizadorId) {
        ScrimOrganizador organizador = obtenerOrganizadorParaScrim(scrimId, organizadorId);
        organizador.confirmarScrim();

        // Persistir cambios
        guardarCambios(organizador.getScrim());

        // Remover del cache ya que está bloqueado
        organizadoresActivos.remove(scrimId);
    }

    /**
     * Obtiene los participantes actuales del scrim.
     */
    public List<ParticipanteScrim> obtenerParticipantes(String scrimId, String organizadorId) {
        ScrimOrganizador organizador = obtenerOrganizadorParaScrim(scrimId, organizadorId);
        return organizador.getParticipantes();
    }

    /**
     * Verifica si hay acciones que se pueden deshacer.
     */
    public boolean puedeDeshacer(String scrimId, String organizadorId) {
        ScrimOrganizador organizador = obtenerOrganizadorParaScrim(scrimId, organizadorId);
        return organizador.getCantidadAccionesEnHistorial() > 0;
    }

    /**
     * Obtiene información del historial de acciones.
     */
    public int getCantidadAccionesEnHistorial(String scrimId, String organizadorId) {
        ScrimOrganizador organizador = obtenerOrganizadorParaScrim(scrimId, organizadorId);
        return organizador.getCantidadAccionesEnHistorial();
    }

    /**
     * Verifica si el scrim está bloqueado para modificaciones.
     */
    public boolean estaBloqueado(String scrimId, String organizadorId) {
        ScrimOrganizador organizador = obtenerOrganizadorParaScrim(scrimId, organizadorId);
        return organizador.isBloqueado();
    }

    /**
     * Guarda los cambios del scrim en el repositorio.
     */
    private void guardarCambios(Scrim scrim) {
        boolean actualizado = repositorioScrim.actualizar(scrim);
        if (!actualizado) {
            throw new RuntimeException("No se pudieron guardar los cambios del scrim");
        }
    }

    /**
     * Limpia el cache de organizadores (útil para testing o cuando se reinicia la
     * aplicación).
     */
    public void limpiarCache() {
        organizadoresActivos.clear();
    }
}