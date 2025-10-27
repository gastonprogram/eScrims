package model;

import model.roles.RolJuego;
import java.util.Objects;

/**
 * Representa a un participante (Usuario) dentro de un Scrim específico.
 * Cada participante tiene un rol asignado en el juego y un estado de
 * confirmación.
 * 
 * Esta clase actúa como un enlace entre Usuario y Scrim, manteniendo
 * información específica del contexto del scrim (rol asignado, confirmación).
 * 
 * Aplica el principio de responsabilidad única: solo gestiona la
 * información de participación de un usuario en un scrim específico.
 * 
 * @author eScrims Team
 */
public class ParticipanteScrim {

    /**
     * Usuario que participa en el scrim.
     * Mantiene la referencia completa al usuario para acceder
     * a sus datos cuando sea necesario.
     */
    private Usuario usuario;

    /**
     * Rol asignado en el juego para este scrim.
     * Por ejemplo: Top, Jungle, Mid, ADC, Support en LoL.
     */
    private RolJuego rolAsignado;

    /**
     * Indica si el participante ha confirmado su asistencia al scrim.
     * Un scrim solo puede iniciarse cuando todos los participantes
     * están confirmados.
     */
    private boolean confirmado;

    /**
     * Constructor para crear un nuevo participante en un scrim.
     * 
     * @param usuario     el usuario que participa
     * @param rolAsignado el rol que desempeñará en el scrim
     * @throws IllegalArgumentException si usuario o rolAsignado son null
     */
    public ParticipanteScrim(Usuario usuario, RolJuego rolAsignado) {
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no puede ser null");
        }
        if (rolAsignado == null) {
            throw new IllegalArgumentException("El rol asignado no puede ser null");
        }

        this.usuario = usuario;
        this.rolAsignado = rolAsignado;
        this.confirmado = false;
    }

    /**
     * Obtiene el usuario participante.
     * 
     * @return instancia del usuario
     */
    public Usuario getUsuario() {
        return usuario;
    }

    /**
     * Obtiene el username del usuario para facilitar la identificación.
     * 
     * @return username del usuario
     */
    public String getUserId() {
        return usuario.getUsername();
    }

    /**
     * Obtiene el rol asignado en el juego.
     * 
     * @return rol del juego asignado
     */
    public RolJuego getRolAsignado() {
        return rolAsignado;
    }

    /**
     * Cambia el rol asignado al participante.
     * Esta operación solo debe permitirse antes de que el scrim
     * sea confirmado.
     * 
     * @param nuevoRol el nuevo rol a asignar
     * @throws IllegalArgumentException si el nuevo rol es null
     */
    public void setRolAsignado(RolJuego nuevoRol) {
        if (nuevoRol == null) {
            throw new IllegalArgumentException("El rol no puede ser null");
        }
        this.rolAsignado = nuevoRol;
    }

    /**
     * Verifica si el participante ha confirmado su asistencia.
     * 
     * @return true si está confirmado, false en caso contrario
     */
    public boolean isConfirmado() {
        return confirmado;
    }

    /**
     * Establece el estado de confirmación del participante.
     * 
     * @param confirmado true para confirmar, false para des-confirmar
     */
    public void setConfirmado(boolean confirmado) {
        this.confirmado = confirmado;
    }

    /**
     * Confirma la participación del usuario en el scrim.
     * Método de conveniencia equivalente a setConfirmado(true).
     */
    public void confirmar() {
        this.confirmado = true;
    }

    /**
     * Cancela la confirmación del usuario.
     * Método de conveniencia equivalente a setConfirmado(false).
     */
    public void desconfirmar() {
        this.confirmado = false;
    }

    /**
     * Dos participantes son iguales si tienen el mismo usuario.
     * El rol puede cambiar, pero la identidad del participante
     * viene determinada por el usuario.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ParticipanteScrim that = (ParticipanteScrim) o;
        return usuario.equals(that.usuario);
    }

    @Override
    public int hashCode() {
        return Objects.hash(usuario);
    }

    @Override
    public String toString() {
        return String.format("ParticipanteScrim{usuario='%s', rol='%s', confirmado=%s}",
                usuario.getUsername(),
                rolAsignado.getNombre(),
                confirmado);
    }
}
