package presentacion.view;

import dominio.modelo.ParticipanteScrim;
import dominio.modelo.Scrim;

import java.util.List;

/**
 * Vista para las acciones específicas del organizador de scrims.
 * Maneja la presentación de información y la captura de entradas del usuario
 * para operaciones como invitar jugadores, asignar roles, etc.
 */
public class AccionesOrganizadorView {

    /**
     * Muestra el menú principal de acciones del organizador.
     */
    public void mostrarMenuPrincipal() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("        ACCIONES DEL ORGANIZADOR");
        System.out.println("=".repeat(50));
        System.out.println("1. Gestionar Scrims");
        System.out.println("2. Ver Historial de Acciones");
        System.out.println("0. Volver al menú anterior");
        System.out.println("=".repeat(50));
        System.out.print("Selecciona una opción: ");
    }

    /**
     * Muestra el menú de gestión de un scrim específico.
     */
    public void mostrarMenuGestionScrim() {
        System.out.println("\n" + "=".repeat(45));
        System.out.println("        GESTIÓN DE SCRIM");
        System.out.println("=".repeat(45));
        System.out.println("1. Invitar Jugador");
        System.out.println("2. Asignar Rol");
        System.out.println("3. Intercambiar Jugadores (Swap)");
        System.out.println("4. Deshacer Última Acción");
        System.out.println("5. Confirmar Scrim");
        System.out.println("0. Volver");
        System.out.println("=".repeat(45));
        System.out.print("Selecciona una opción: ");
    }

    /**
     * Muestra la lista de scrims del organizador con numeración.
     */
    public void mostrarScrimsDelOrganizador(List<Scrim> scrims) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("                 TUS SCRIMS");
        System.out.println("=".repeat(60));
        
        for (int i = 0; i < scrims.size(); i++) {
            Scrim scrim = scrims.get(i);
            System.out.printf("%d. ID: %-10s | Juego: %-20s | Estado: %s%n",
                    i + 1,
                    scrim.getId(),
                    scrim.getJuego().getNombre(),
                    scrim.getEstado().toString());
        }
        System.out.println("=".repeat(60));
    }    /**
     * Muestra información detallada del scrim actual.
     */
    public void mostrarInformacionScrim(String scrimId, List<ParticipanteScrim> participantes,
            boolean bloqueado, int accionesEnHistorial) {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("                 INFORMACIÓN DEL SCRIM: " + scrimId);
        System.out.println("=".repeat(70));

        System.out.printf("Estado: %s | Acciones en historial: %d%n",
                bloqueado ? "BLOQUEADO" : "EDITABLE", accionesEnHistorial);

        System.out.println("\n--- PARTICIPANTES ---");
        if (participantes.isEmpty()) {
            System.out.println("Sin participantes");
        } else {
            for (ParticipanteScrim participante : participantes) {
                System.out.printf("• %-15s - Rol: %s%n",
                        participante.getUserId(),
                        participante.getRolAsignado() != null ? participante.getRolAsignado().getNombre() : "Sin rol");
            }
        }
        System.out.println("=".repeat(70));
    }

    /**
     * Muestra los roles disponibles para League of Legends.
     */
    public void mostrarRolesDisponibles() {
        System.out.println("\n--- ROLES DISPONIBLES ---");
        System.out.println("1. Top");
        System.out.println("2. Jungle");
        System.out.println("3. Mid");
        System.out.println("4. ADC");
        System.out.println("5. Support");
    }

    /**
     * Muestra la lista de participantes actuales con numeración.
     */
    public void mostrarParticipantes(List<ParticipanteScrim> participantes) {
        System.out.println("\n--- PARTICIPANTES ACTUALES ---");
        for (int i = 0; i < participantes.size(); i++) {
            ParticipanteScrim participante = participantes.get(i);
            System.out.printf("%d. %-15s - Rol: %s%n",
                    i + 1,
                    participante.getUserId(),
                    participante.getRolAsignado() != null ? participante.getRolAsignado().getNombre() : "Sin rol");
        }
    }

    /**
     * Muestra el historial de acciones para un scrim.
     */
    public void mostrarHistorialScrim(String scrimId, String juego, int cantidadAcciones) {
        System.out.printf("Scrim %-10s | %-20s | Acciones: %d%n",
                scrimId, juego, cantidadAcciones);
    }

    /**
     * Solicita una entrada del usuario con un mensaje.
     */
    public void solicitarInput(String mensaje) {
        System.out.print(mensaje);
    }

    /**
     * Solicita confirmación del usuario.
     */
    public void solicitarConfirmacion(String mensaje) {
        System.out.print(mensaje);
    }

    /**
     * Muestra un mensaje informativo.
     */
    public void mostrarMensaje(String mensaje) {
        System.out.println("\nINFO: " + mensaje);
    }

    /**
     * Muestra un mensaje de éxito.
     */
    public void mostrarExito(String mensaje) {
        System.out.println("\n✓ ÉXITO: " + mensaje);
    }

    /**
     * Muestra un mensaje de error.
     */
    public void mostrarError(String mensaje) {
        System.out.println("\n✗ ERROR: " + mensaje);
    }
}