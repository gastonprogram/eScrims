package aplicacion.builders;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import dominio.acciones.AccionOrganizador;
import dominio.modelo.Confirmacion;
import dominio.modelo.ParticipanteScrim;
import dominio.modelo.Scrim;
import dominio.roles.RolJuego;

/**
 * Clase que gestiona las acciones del organizador sobre un scrim.
 * 
 * Esta clase actúa como un Facade que simplifica las operaciones complejas
 * sobre el scrim, encapsulando la lógica de gestión de participantes,
 * validaciones y el historial de acciones para deshacer.
 * 
 * El ScrimOrganizador mantiene:
 * - La lista de participantes del scrim
 * - El historial de acciones para implementar undo
 * - El estado de bloqueo que impide cambios después de confirmar
 * 
 * Implementa los principios SOLID:
 * - Single Responsibility: solo gestiona las operaciones del organizador
 * - Open/Closed: extensible mediante nuevas AccionOrganizador sin modificar
 * esta clase
 * - Dependency Inversion: depende de abstracciones (AccionOrganizador) no de
 * implementaciones
 * 
 * @author eScrims Team
 */
public class ScrimOrganizador {

    /**
     * El scrim que está siendo organizado.
     */
    private final Scrim scrim;

    /**
     * Lista de participantes en el scrim con sus roles asignados.
     */
    private final List<ParticipanteScrim> participantes;

    /**
     * Historial de acciones ejecutadas, permitiendo deshacer operaciones.
     * Implementa el patrón Memento de forma simplificada usando un Stack.
     */
    private final Stack<AccionOrganizador> historialAcciones;

    /**
     * Indica si el scrim está bloqueado para modificaciones.
     * Se bloquea cuando el scrim es confirmado.
     */
    private boolean bloqueado;

    /**
     * Constructor para crear un organizador para un scrim específico.
     * 
     * @param scrim el scrim a organizar
     * @throws IllegalArgumentException si el scrim es null
     */
    public ScrimOrganizador(Scrim scrim) {
        if (scrim == null) {
            throw new IllegalArgumentException("El scrim no puede ser null");
        }

        this.scrim = scrim;
        this.participantes = new ArrayList<>();
        this.historialAcciones = new Stack<>();
        this.bloqueado = false;
    }

    /**
     * Ejecuta una acción del organizador sobre el scrim.
     * La acción se valida, ejecuta y agrega al historial para permitir undo.
     * 
     * Este método aplica el patrón Strategy: acepta cualquier AccionOrganizador
     * y delega la ejecución a la acción específica.
     * 
     * @param accion la acción a ejecutar
     * @throws IllegalStateException    si el scrim está bloqueado o la acción no
     *                                  puede ejecutarse
     * @throws IllegalArgumentException si la acción es null
     */
    public void ejecutarAccion(AccionOrganizador accion) {
        if (accion == null) {
            throw new IllegalArgumentException("La acción no puede ser null");
        }

        validarOperacion();

        if (!accion.puedeEjecutarse(this)) {
            throw new IllegalStateException(
                    "La acción '" + accion.getDescripcion() + "' no puede ejecutarse en el estado actual");
        }

        // Ejecutar la acción
        accion.ejecutar(this);

        // Agregar al historial para poder deshacer
        historialAcciones.push(accion);

        System.out.println("[ScrimOrganizador] Ejecutada: " + accion.getDescripcion());
    }

    /**
     * Deshace la última acción ejecutada.
     * 
     * Este método implementa la funcionalidad de undo solicitada,
     * permitiendo revertir acciones antes de que el scrim sea confirmado.
     * 
     * @throws IllegalStateException si el scrim está bloqueado o no hay acciones
     *                               para deshacer
     */
    public void deshacerUltimaAccion() {
        validarOperacion();

        if (historialAcciones.isEmpty()) {
            throw new IllegalStateException("No hay acciones para deshacer");
        }

        AccionOrganizador accion = historialAcciones.pop();
        accion.deshacer(this);

        System.out.println("[ScrimOrganizador] Deshecha: " + accion.getDescripcion());
    }

    /**
     * Confirma el scrim y bloquea futuras modificaciones.
     * A partir de este momento no se pueden ejecutar más acciones
     * ni deshacer las existentes.
     * 
     * También marca a todos los participantes como confirmados y
     * actualiza las listas del scrim con los roles asignados.
     */
    public void confirmarScrim() {
        this.bloqueado = true;

        // Confirmar todos los participantes y transferir roles asignados
        for (ParticipanteScrim participante : participantes) {
            participante.confirmar();

            String userId = participante.getUserId();
            
            // Buscar confirmación existente o crear una nueva
            Confirmacion confirmacion = scrim.getConfirmaciones().stream()
                    .filter(conf -> conf.getUserId().equals(userId))
                    .filter(conf -> conf.getEstado() == Confirmacion.EstadoConfirmacion.PENDIENTE)
                    .findFirst()
                    .orElse(null);
            
            // Si no existe confirmación, crear una nueva
            if (confirmacion == null) {
                confirmacion = new Confirmacion(scrim.getId(), userId);
                scrim.getConfirmaciones().add(confirmacion);
                System.out.println("[ScrimOrganizador] Creada nueva confirmación para " + userId);
            }
            
            // Confirmar y transferir rol
            confirmacion.confirmar();
            if (participante.getRolAsignado() != null) {
                confirmacion.setRolAsignado(participante.getRolAsignado());
                System.out.println("[ScrimOrganizador] Rol " + 
                    participante.getRolAsignado().getNombre() + 
                    " asignado a " + userId + " persistido en confirmación.");
            }
        }

        // Limpiar el historial ya que no se puede deshacer después de confirmar
        historialAcciones.clear();

        System.out.println("[ScrimOrganizador] Scrim confirmado con roles asignados. No se pueden realizar más cambios.");
    }

    /**
     * Busca un participante por su ID de usuario.
     * 
     * @param userId el identificador del usuario
     * @return el participante encontrado, o null si no existe
     */
    public ParticipanteScrim buscarParticipante(String userId) {
        if (userId == null) {
            return null;
        }

        return participantes.stream()
                .filter(p -> p.getUserId().equals(userId))
                .findFirst()
                .orElse(null);
    }

    /**
     * Busca un participante que tenga asignado un rol específico.
     * 
     * @param rol el rol a buscar
     * @return el participante con ese rol, o null si no existe
     */
    public ParticipanteScrim buscarParticipantePorRol(RolJuego rol) {
        if (rol == null) {
            return null;
        }

        return participantes.stream()
                .filter(p -> p.getRolAsignado().getNombre().equals(rol.getNombre()))
                .findFirst()
                .orElse(null);
    }

    /**
     * Verifica si un rol específico ya está ocupado por algún participante.
     * 
     * @param rol el rol a verificar
     * @return true si el rol está ocupado, false en caso contrario
     */
    public boolean esRolOcupado(RolJuego rol) {
        return buscarParticipantePorRol(rol) != null;
    }

    /**
     * Agrega un participante a la lista (método público, usado por las acciones).
     * Este método permite que las acciones del organizador manipulen
     * la lista de participantes de forma controlada.
     * 
     * @param participante el participante a agregar
     */
    public void agregarParticipante(ParticipanteScrim participante) {
        if (!participantes.contains(participante)) {
            participantes.add(participante);
        }
    }

    /**
     * Remueve un participante de la lista (método público, usado por las acciones).
     * Este método permite que las acciones del organizador manipulen
     * la lista de participantes de forma controlada.
     * 
     * @param userId el ID del usuario a remover
     */
    public void removerParticipante(String userId) {
        participantes.removeIf(p -> p.getUserId().equals(userId));
    }

    /**
     * Valida que se pueda realizar una operación sobre el scrim.
     * 
     * @throws IllegalStateException si el scrim está bloqueado o en un estado no
     *                               modificable
     */
    private void validarOperacion() {
        if (bloqueado) {
            throw new IllegalStateException(
                    "No se pueden realizar cambios después de confirmar el scrim");
        }

        String estado = scrim.getEstado();
        if ("CONFIRMADO".equals(estado) || "EN_JUEGO".equals(estado) ||
                "FINALIZADO".equals(estado) || "CANCELADO".equals(estado)) {
            throw new IllegalStateException(
                    "No se pueden realizar cambios en el estado actual: " + estado);
        }
    }

    // Getters

    public Scrim getScrim() {
        return scrim;
    }

    /**
     * Obtiene una copia de la lista de participantes para prevenir modificaciones
     * externas.
     * 
     * @return lista inmutable de participantes
     */
    public List<ParticipanteScrim> getParticipantes() {
        return new ArrayList<>(participantes);
    }

    public boolean isBloqueado() {
        return bloqueado;
    }

    /**
     * Obtiene la cantidad de acciones en el historial.
     * 
     * @return número de acciones que pueden deshacerse
     */
    public int getCantidadAccionesEnHistorial() {
        return historialAcciones.size();
    }

    /**
     * Obtiene el historial completo de acciones (para auditoría/logging).
     * 
     * @return lista de acciones ejecutadas
     */
    public List<AccionOrganizador> getHistorialAcciones() {
        return new ArrayList<>(historialAcciones);
    }
}
