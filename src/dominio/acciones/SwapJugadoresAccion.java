package dominio.acciones;

import aplicacion.builders.ScrimOrganizador;
import dominio.modelo.ParticipanteScrim;
import dominio.roles.RolJuego;

/**
 * Acción para intercambiar (swap) los roles de dos jugadores en el scrim.
 * 
 * Esta acción es útil cuando el organizador quiere que dos jugadores
 * intercambien sus posiciones sin necesidad de reasignar manualmente
 * cada rol por separado.
 * 
 * Por ejemplo: si el jugador A tiene el rol Top y el jugador B tiene el
 * rol Jungle, después del swap A tendrá Jungle y B tendrá Top.
 * 
 * Implementa el patrón Strategy para encapsular esta operación.
 * 
 * @author eScrims Team
 */
public class SwapJugadoresAccion implements AccionOrganizador {

    private static final String TIPO_ACCION = "SWAP_JUGADORES";

    private final String userId1;
    private final String userId2;

    // Se guardan los roles originales al ejecutar para poder deshacer
    private RolJuego rolOriginal1;
    private RolJuego rolOriginal2;

    /**
     * Constructor para crear una acción de intercambio de jugadores.
     * 
     * @param userId1 identificador del primer usuario
     * @param userId2 identificador del segundo usuario
     * @throws IllegalArgumentException si alguno de los userId es null/vacío
     *                                  o si son iguales
     */
    public SwapJugadoresAccion(String userId1, String userId2) {
        if (userId1 == null || userId1.trim().isEmpty()) {
            throw new IllegalArgumentException("El userId1 no puede ser null o vacío");
        }
        if (userId2 == null || userId2.trim().isEmpty()) {
            throw new IllegalArgumentException("El userId2 no puede ser null o vacío");
        }
        if (userId1.equals(userId2)) {
            throw new IllegalArgumentException(
                    "No se puede hacer swap de un usuario consigo mismo");
        }

        this.userId1 = userId1;
        this.userId2 = userId2;
    }

    /**
     * Ejecuta el intercambio de roles entre los dos jugadores.
     * Guarda los roles originales para poder deshacer la operación.
     * 
     * @param organizador el organizador del scrim
     * @throws IllegalArgumentException si alguno de los jugadores no existe
     */
    @Override
    public void ejecutar(ScrimOrganizador organizador) {
        // Buscar ambos participantes
        ParticipanteScrim participante1 = organizador.buscarParticipante(userId1);
        ParticipanteScrim participante2 = organizador.buscarParticipante(userId2);

        if (participante1 == null) {
            throw new IllegalArgumentException(
                    "El usuario '" + userId1 + "' no está en el scrim");
        }

        if (participante2 == null) {
            throw new IllegalArgumentException(
                    "El usuario '" + userId2 + "' no está en el scrim");
        }

        // Guardar los roles originales para poder deshacer
        this.rolOriginal1 = participante1.getRolAsignado();
        this.rolOriginal2 = participante2.getRolAsignado();

        // Realizar el intercambio de roles
        RolJuego rolTemp = participante1.getRolAsignado();
        participante1.setRolAsignado(participante2.getRolAsignado());
        participante2.setRolAsignado(rolTemp);
    }

    /**
     * Deshace el intercambio, restaurando los roles originales de ambos jugadores.
     * 
     * @param organizador el organizador del scrim
     * @throws IllegalStateException si la acción no fue ejecutada previamente
     */
    @Override
    public void deshacer(ScrimOrganizador organizador) {
        if (rolOriginal1 == null || rolOriginal2 == null) {
            throw new IllegalStateException(
                    "No se puede deshacer: la acción no fue ejecutada");
        }

        ParticipanteScrim participante1 = organizador.buscarParticipante(userId1);
        ParticipanteScrim participante2 = organizador.buscarParticipante(userId2);

        if (participante1 != null && participante2 != null) {
            participante1.setRolAsignado(rolOriginal1);
            participante2.setRolAsignado(rolOriginal2);
        }
    }

    /**
     * Verifica si la acción puede ejecutarse.
     * 
     * @param organizador el organizador del scrim
     * @return true si el scrim no está bloqueado y ambos jugadores existen
     */
    @Override
    public boolean puedeEjecutarse(ScrimOrganizador organizador) {
        return !organizador.isBloqueado()
                && organizador.buscarParticipante(userId1) != null
                && organizador.buscarParticipante(userId2) != null;
    }

    @Override
    public String getDescripcion() {
        return String.format("Intercambiar roles entre '%s' y '%s'",
                userId1,
                userId2);
    }

    @Override
    public String getTipoAccion() {
        return TIPO_ACCION;
    }

    public String getUserId1() {
        return userId1;
    }

    public String getUserId2() {
        return userId2;
    }

    public RolJuego getRolOriginal1() {
        return rolOriginal1;
    }

    public RolJuego getRolOriginal2() {
        return rolOriginal2;
    }
}
