package aplicacion.services;

import java.util.List;

import aplicacion.builders.ScrimOrganizador;
import dominio.modelo.Confirmacion;
import dominio.modelo.ParticipanteScrim;
import dominio.modelo.Scrim;
import dominio.roles.RolJuego;

/**
 * Servicio para manejar la persistencia y sincronización de roles
 * entre ParticipanteScrim (temporal) y Confirmacion (persistente).
 * 
 * Este servicio se encarga de:
 * - Transferir roles de ParticipanteScrim a Confirmacion al confirmar scrim
 * - Sincronizar cambios de roles en tiempo real
 * - Mantener consistencia entre ambas representaciones
 * 
 * @author eScrims Team
 */
public class RolPersistenceService {

    /**
     * Transfiere todos los roles asignados desde ParticipanteScrim 
     * hacia las Confirmaciones del Scrim para persistirlos.
     * 
     * @param organizador el organizador con los participantes y roles asignados
     * @param scrim       el scrim que contiene las confirmaciones
     * @return cantidad de roles transferidos exitosamente
     */
    public static int transferirRolesAConfirmaciones(ScrimOrganizador organizador, Scrim scrim) {
        List<ParticipanteScrim> participantes = organizador.getParticipantes();
        int rolesTransferidos = 0;

        for (ParticipanteScrim participante : participantes) {
            if (participante.getRolAsignado() != null) {
                boolean transferido = transferirRolParticipante(participante, scrim);
                if (transferido) {
                    rolesTransferidos++;
                }
            }
        }

        System.out.println("[RolPersistenceService] " + rolesTransferidos + 
                          " roles transferidos a confirmaciones para persistencia.");
        return rolesTransferidos;
    }

    /**
     * Transfiere el rol de un participante específico a su confirmación.
     * 
     * @param participante el participante con rol asignado
     * @param scrim        el scrim que contiene las confirmaciones
     * @return true si se transfirió exitosamente, false si no
     */
    private static boolean transferirRolParticipante(ParticipanteScrim participante, Scrim scrim) {
        String userId = participante.getUserId();
        RolJuego rolAsignado = participante.getRolAsignado();

        // Buscar la confirmación correspondiente
        for (Confirmacion confirmacion : scrim.getConfirmaciones()) {
            if (confirmacion.getUserId().equals(userId) && 
                confirmacion.getEstado() == Confirmacion.EstadoConfirmacion.PENDIENTE) {
                
                confirmacion.setRolAsignado(rolAsignado);
                System.out.println("[RolPersistenceService] Rol " + rolAsignado.getNombre() + 
                                  " asignado a confirmación de " + userId);
                return true;
            }
        }

        System.err.println("[RolPersistenceService] No se encontró confirmación pendiente para " + userId);
        return false;
    }

    /**
     * Sincroniza los roles de ParticipanteScrim con las confirmaciones existentes.
     * Útil para mantener consistencia durante modificaciones (swap, asignación).
     * 
     * @param organizador el organizador con los participantes
     * @param scrim       el scrim con las confirmaciones
     */
    public static void sincronizarRoles(ScrimOrganizador organizador, Scrim scrim) {
        List<ParticipanteScrim> participantes = organizador.getParticipantes();

        for (ParticipanteScrim participante : participantes) {
            if (participante.getRolAsignado() != null) {
                actualizarRolEnConfirmacion(participante, scrim);
            }
        }
    }

    /**
     * Actualiza el rol en la confirmación de un participante específico.
     * 
     * @param participante el participante con el rol actualizado
     * @param scrim        el scrim que contiene las confirmaciones
     */
    private static void actualizarRolEnConfirmacion(ParticipanteScrim participante, Scrim scrim) {
        String userId = participante.getUserId();
        RolJuego nuevoRol = participante.getRolAsignado();

        // Buscar y actualizar la confirmación
        scrim.getConfirmaciones().stream()
                .filter(conf -> conf.getUserId().equals(userId))
                .findFirst()
                .ifPresent(confirmacion -> {
                    confirmacion.setRolAsignado(nuevoRol);
                    System.out.println("[RolPersistenceService] Rol actualizado en confirmación: " + 
                                      userId + " -> " + nuevoRol.getNombre());
                });
    }

    /**
     * Obtiene un resumen de los roles asignados en un scrim confirmado.
     * 
     * @param scrim el scrim confirmado
     * @return resumen con formato: "username: rolName"
     */
    public static String obtenerResumenRoles(Scrim scrim) {
        StringBuilder resumen = new StringBuilder();
        resumen.append("=== ROLES ASIGNADOS ===\n");

        List<Confirmacion> confirmacionesConRoles = scrim.getConfirmacionesConRoles();
        
        if (confirmacionesConRoles.isEmpty()) {
            resumen.append("No hay roles asignados.\n");
        } else {
            for (Confirmacion confirmacion : confirmacionesConRoles) {
                resumen.append(confirmacion.getUserId())
                       .append(": ")
                       .append(confirmacion.getRolAsignado().getNombre())
                       .append("\n");
            }
        }

        return resumen.toString();
    }

    /**
     * Verifica si todos los participantes confirmados tienen roles asignados.
     * 
     * @param scrim el scrim a verificar
     * @return true si todos tienen roles, false si alguno no tiene
     */
    public static boolean todosLosParticipantesTienenRoles(Scrim scrim) {
        List<Confirmacion> confirmadas = scrim.getConfirmacionesConfirmadas();
        return confirmadas.stream().allMatch(Confirmacion::tieneRolAsignado);
    }

    /**
     * Cuenta cuántos roles están asignados en el scrim confirmado.
     * 
     * @param scrim el scrim a analizar
     * @return cantidad de roles asignados
     */
    public static int contarRolesAsignados(Scrim scrim) {
        return (int) scrim.getConfirmacionesConfirmadas().stream()
                          .filter(Confirmacion::tieneRolAsignado)
                          .count();
    }
}