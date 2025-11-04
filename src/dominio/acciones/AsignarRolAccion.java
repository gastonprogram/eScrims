package dominio.acciones;

import aplicacion.builders.ScrimOrganizador;
import dominio.modelo.ParticipanteScrim;
import dominio.roles.RolJuego;

/**
 * Acción para asignar o cambiar el rol de un jugador ya existente en el scrim.
 * 
 * Esta acción permite al organizador reasignar roles a jugadores que ya
 * están participando en el scrim, siempre y cuando el scrim no haya sido
 * confirmado aún.
 * 
 * Implementa el patrón Strategy para encapsular esta operación específica.
 * 
 * @author eScrims Team
 */
public class AsignarRolAccion implements AccionOrganizador {

    private static final String TIPO_ACCION = "ASIGNAR_ROL";

    private final String userId;
    private final RolJuego nuevoRol;
    private RolJuego rolAnterior; // Se guarda al ejecutar para poder deshacer

    /**
     * Constructor para crear una acción de asignación de rol.
     * 
     * @param userId   identificador del usuario (username)
     * @param nuevoRol el nuevo rol a asignar
     * @throws IllegalArgumentException si userId o nuevoRol son null/vacíos
     */
    public AsignarRolAccion(String userId, RolJuego nuevoRol) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("El userId no puede ser null o vacío");
        }
        if (nuevoRol == null) {
            throw new IllegalArgumentException("El nuevo rol no puede ser null");
        }

        this.userId = userId;
        this.nuevoRol = nuevoRol;
    }

    /**
     * Ejecuta la asignación del nuevo rol al jugador.
     * Guarda el rol anterior para poder deshacerla si es necesario.
     * 
     * @param organizador el organizador del scrim
     * @throws IllegalArgumentException si el jugador no existe o el rol no es
     *                                  válido
     */
    @Override
    public void ejecutar(ScrimOrganizador organizador) {
        // Buscar al participante
        ParticipanteScrim participante = organizador.buscarParticipante(userId);
        if (participante == null) {
            throw new IllegalArgumentException(
                    "El usuario '" + userId + "' no está en el scrim");
        }

        // Validar que el nuevo rol sea compatible con el juego
        if (!organizador.getScrim().getJuego().esRolValido(nuevoRol)) {
            throw new IllegalArgumentException(
                    String.format("El rol '%s' no es válido para el juego '%s'",
                            nuevoRol.getNombre(),
                            organizador.getScrim().getJuego().getNombre()));
        }

        // Verificar que el nuevo rol no esté ocupado por otro jugador
        ParticipanteScrim ocupante = organizador.buscarParticipantePorRol(nuevoRol);
        if (ocupante != null && !ocupante.getUserId().equals(userId)) {
            throw new IllegalArgumentException(
                    "El rol '" + nuevoRol.getNombre() + "' ya está ocupado por " +
                            ocupante.getUserId());
        }

        // Guardar el rol anterior para poder deshacer
        this.rolAnterior = participante.getRolAsignado();

        // Asignar el nuevo rol
        participante.setRolAsignado(nuevoRol);
    }

    /**
     * Deshace la asignación, restaurando el rol anterior del jugador.
     * 
     * @param organizador el organizador del scrim
     * @throws IllegalStateException si no se ejecutó la acción previamente
     */
    @Override
    public void deshacer(ScrimOrganizador organizador) {
        if (rolAnterior == null) {
            throw new IllegalStateException(
                    "No se puede deshacer: la acción no fue ejecutada");
        }

        ParticipanteScrim participante = organizador.buscarParticipante(userId);
        if (participante != null) {
            participante.setRolAsignado(rolAnterior);
        }
    }

    /**
     * Verifica si la acción puede ejecutarse.
     * 
     * @param organizador el organizador del scrim
     * @return true si el scrim no está bloqueado y el jugador existe
     */
    @Override
    public boolean puedeEjecutarse(ScrimOrganizador organizador) {
        return !organizador.isBloqueado()
                && organizador.buscarParticipante(userId) != null
                && organizador.getScrim().getJuego().esRolValido(nuevoRol);
    }

    @Override
    public String getDescripcion() {
        return String.format("Asignar rol '%s' a '%s'",
                nuevoRol.getNombre(),
                userId);
    }

    @Override
    public String getTipoAccion() {
        return TIPO_ACCION;
    }

    public String getUserId() {
        return userId;
    }

    public RolJuego getNuevoRol() {
        return nuevoRol;
    }

    public RolJuego getRolAnterior() {
        return rolAnterior;
    }
}
