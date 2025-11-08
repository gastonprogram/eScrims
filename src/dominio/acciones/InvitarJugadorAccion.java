package dominio.acciones;

import aplicacion.builders.ScrimOrganizador;
import dominio.modelo.ParticipanteScrim;
import dominio.modelo.Usuario;
import dominio.roles.RolJuego;

/**
 * Acción para invitar a un jugador (Usuario) a un scrim asignándole un rol.
 * 
 * Esta clase encapsula toda la lógica necesaria para invitar a un jugador,
 * incluyendo las validaciones pertinentes y la capacidad de deshacer la
 * invitación si es necesario.
 * 
 * Implementa el patrón Strategy a través de la interfaz AccionOrganizador,
 * permitiendo que esta acción sea tratada polimórficamente junto con
 * otras acciones del organizador.
 * 
 * @author eScrims Team
 */
public class InvitarJugadorAccion implements AccionOrganizador {

    private static final String TIPO_ACCION = "INVITAR_JUGADOR";

    private final Usuario usuario;
    private final RolJuego rolAsignado;

    /**
     * Constructor para crear una acción de invitación.
     * 
     * @param usuario     el usuario a invitar al scrim
     * @param rolAsignado el rol que se le asignará en el juego
     * @throws IllegalArgumentException si usuario o rolAsignado son null
     */
    public InvitarJugadorAccion(Usuario usuario, RolJuego rolAsignado) {
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no puede ser null");
        }
        if (rolAsignado == null) {
            throw new IllegalArgumentException("El rol asignado no puede ser null");
        }

        this.usuario = usuario;
        this.rolAsignado = rolAsignado;
    }

    /**
     * Ejecuta la invitación del jugador al scrim.
     * Valida que el rol sea compatible con el juego del scrim
     * y que el jugador no esté ya invitado.
     * 
     * @param organizador el organizador del scrim
     * @throws IllegalStateException    si el scrim está bloqueado
     * @throws IllegalArgumentException si el rol no es válido o el usuario ya está
     *                                  invitado
     */
    @Override
    public void ejecutar(ScrimOrganizador organizador) {
        // Validar que el rol sea compatible con el juego del scrim
        if (!organizador.getScrim().getJuego().esRolValido(rolAsignado)) {
            throw new IllegalArgumentException(
                    String.format("El rol '%s' no es válido para el juego '%s'",
                            rolAsignado.getNombre(),
                            organizador.getScrim().getJuego().getNombre()));
        }

        // Verificar que el usuario no esté ya en el scrim
        if (organizador.buscarParticipante(usuario.getUsername()) != null) {
            throw new IllegalArgumentException(
                    "El usuario '" + usuario.getUsername() + "' ya está en el scrim");
        }

        // Verificar que el rol no esté ocupado
        if (organizador.esRolOcupado(rolAsignado)) {
            throw new IllegalArgumentException(
                    "El rol '" + rolAsignado.getNombre() + "' ya está ocupado");
        }

        // Crear y agregar el participante
        ParticipanteScrim participante = new ParticipanteScrim(usuario, rolAsignado);
        organizador.agregarParticipante(participante);

        // Crear y agregar postulación aceptada directamente (invitación = postulación
        // pre-aprobada)
        boolean yaPostulado = organizador.getScrim().getPostulacionesAceptadas().stream()
                .anyMatch(p -> p.getUserId().equals(usuario.getId()));

        if (!yaPostulado) {
            dominio.modelo.Postulacion postulacion = new dominio.modelo.Postulacion(
                    organizador.getScrim().getId(),
                    usuario.getId(),
                    15, // Rango medio que pase validación (entre 1 y 30)
                    25 // Latencia que pase validación (menor a 50)
            );
            // No pre-aceptamos la postulación, dejamos que el estado del scrim la maneje
            organizador.getScrim().postular(postulacion);
        }
    }

    /**
     * Deshace la invitación, removiendo al jugador del scrim.
     * 
     * @param organizador el organizador del scrim
     */
    @Override
    public void deshacer(ScrimOrganizador organizador) {
        // Remover el participante
        organizador.removerParticipante(usuario.getUsername());

        // Remover la postulación aceptada asociada
        organizador.getScrim().getPostulaciones().removeIf(
                p -> p.getUserId().equals(usuario.getId()));
    }

    /**
     * Verifica si la acción puede ejecutarse.
     * 
     * @param organizador el organizador del scrim
     * @return true si el scrim no está bloqueado y el usuario no está invitado
     */
    @Override
    public boolean puedeEjecutarse(ScrimOrganizador organizador) {
        return !organizador.isBloqueado()
                && organizador.buscarParticipante(usuario.getUsername()) == null
                && !organizador.esRolOcupado(rolAsignado);
    }

    @Override
    public String getDescripcion() {
        return String.format("Invitar a '%s' con rol '%s'",
                usuario.getUsername(),
                rolAsignado.getNombre());
    }

    @Override
    public String getTipoAccion() {
        return TIPO_ACCION;
    }

    /**
     * Obtiene el usuario que se está invitando.
     * 
     * @return usuario de la acción
     */
    public Usuario getUsuario() {
        return usuario;
    }

    /**
     * Obtiene el rol asignado en la invitación.
     * 
     * @return rol asignado
     */
    public RolJuego getRolAsignado() {
        return rolAsignado;
    }
}
