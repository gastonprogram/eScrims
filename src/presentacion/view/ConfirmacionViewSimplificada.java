package presentacion.view;

import java.util.List;
import java.util.Scanner;

import dominio.modelo.Confirmacion;
import dominio.modelo.Scrim;

/**
 * Vista simplificada para confirmaciones (SOLO PARA USUARIO COMÚN).
 * 
 * Flujo del usuario:
 * 1. Confirmar asistencia a un scrim
 * 2. Rechazar asistencia a un scrim
 * 3. Ver el estado de MI confirmación
 * 
 * Las funcionalidades del organizador están en OrganizadorView.
 * 
 * @author eScrims Team
 */
public class ConfirmacionViewSimplificada {

    private final Scanner scanner;

    public ConfirmacionViewSimplificada() {
        this.scanner = new Scanner(System.in);
    }

    // ========== MENÚ DE CONFIRMACIONES ==========

    /**
     * Muestra el menú de confirmaciones y retorna la opción seleccionada.
     */
    public String mostrarMenuConfirmaciones() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("            ✅ GESTIÓN DE CONFIRMACIONES");
        System.out.println("=".repeat(60));
        System.out.println("1. Confirmar asistencia a un scrim");
        System.out.println("2. Rechazar asistencia a un scrim");
        System.out.println("3. Ver estado de mi confirmación");
        System.out.println("0. Volver al menú principal");
        System.out.println("=".repeat(60));
        System.out.print("Seleccione una opción: ");
        return scanner.nextLine().trim();
    }

    // ========== CONFIRMAR ASISTENCIA ==========

    /**
     * Muestra la lista de scrims con confirmaciones pendientes.
     */
    public void mostrarScrimsConConfirmacionPendiente(List<Scrim> scrims) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("         ⏳ SCRIMS CON CONFIRMACIÓN PENDIENTE");
        System.out.println("=".repeat(60));

        if (scrims.isEmpty()) {
            System.out.println("\n📭 No tienes confirmaciones pendientes");
            System.out.println("   Las confirmaciones se generan cuando un scrim se llena");
            return;
        }

        System.out.printf("\n✅ Tienes %d confirmación(es) pendiente(s):\n", scrims.size());

        for (int i = 0; i < scrims.size(); i++) {
            Scrim scrim = scrims.get(i);
            System.out.println("\n" + "-".repeat(60));
            System.out.printf("%d. 📋 ID: %s\n", i + 1, scrim.getId());
            System.out.printf("   🎮 Juego: %s | Formato: %s\n",
                    scrim.getJuego().getNombre(),
                    scrim.getFormato().getFormatName());
            System.out.printf("   📅 Fecha/Hora: %s\n", scrim.getFechaHora());
            System.out.printf("   📊 Estado: %s\n", scrim.getState().getEstado());

            // Contar confirmaciones
            long confirmadas = scrim.getConfirmaciones().stream()
                    .filter(c -> c.getEstado() == Confirmacion.EstadoConfirmacion.CONFIRMADA)
                    .count();
            long totales = scrim.getConfirmaciones().size();

            System.out.printf("   ✅ Confirmaciones: %d/%d\n", confirmadas, totales);
        }

        System.out.println("-".repeat(60));
    }

    /**
     * Solicita el número del scrim para confirmar o rechazar asistencia.
     */
    public int solicitarNumeroScrim(int cantidadScrims) {
        System.out.printf("\n📋 Ingrese el número del scrim (1-%d) o '0' para cancelar: ", cantidadScrims);
        try {
            String input = scanner.nextLine().trim();
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return -1; // Valor inválido
        }
    }

    /**
     * Solicita el ID del scrim para confirmar asistencia.
     * 
     * @deprecated Usar solicitarNumeroScrim() en su lugar
     */
    @Deprecated
    public String solicitarIdScrim() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("              ✅ CONFIRMAR ASISTENCIA");
        System.out.println("=".repeat(60));
        System.out.print("\n📋 ID del scrim: ");
        return scanner.nextLine().trim();
    }

    /**
     * Solicita confirmación final del usuario antes de confirmar asistencia.
     */
    public boolean confirmarAsistenciaUsuario() {
        System.out.print("\n¿Confirmas tu asistencia a este scrim? (s/n): ");
        String respuesta = scanner.nextLine().trim().toLowerCase();
        return respuesta.equals("s") || respuesta.equals("si") || respuesta.equals("sí");
    }

    /**
     * Solicita confirmación final del usuario antes de rechazar asistencia.
     */
    public boolean confirmarRechazoUsuario() {
        System.out.println("\n⚠️  ATENCIÓN: Al rechazar, perderás tu lugar en el scrim.");
        System.out.print("¿Estás seguro de rechazar tu asistencia? (s/n): ");
        String respuesta = scanner.nextLine().trim().toLowerCase();
        return respuesta.equals("s") || respuesta.equals("si") || respuesta.equals("sí");
    }

    // ========== VER MI CONFIRMACIÓN ==========

    /**
     * Muestra la lista de scrims donde tengo confirmaciones para seleccionar uno.
     */
    public void mostrarScrimsConMisConfirmaciones(List<Scrim> scrims, String miUserId) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("         📊 MIS CONFIRMACIONES EN SCRIMS");
        System.out.println("=".repeat(60));

        if (scrims.isEmpty()) {
            System.out.println("\n📭 No tienes confirmaciones en ningún scrim");
            System.out.println("   Las confirmaciones aparecen cuando te postulan a un scrim que se llena");
            return;
        }

        System.out.printf("\n📋 Tienes confirmaciones en %d scrim(s):\n", scrims.size());

        for (int i = 0; i < scrims.size(); i++) {
            Scrim scrim = scrims.get(i);
            System.out.println("\n" + "-".repeat(60));
            System.out.printf("%d. 📋 ID: %s\n", i + 1, scrim.getId());
            System.out.printf("   🎮 Juego: %s | Formato: %s\n",
                    scrim.getJuego().getNombre(),
                    scrim.getFormato().getFormatName());
            System.out.printf("   📅 Fecha/Hora: %s\n", scrim.getFechaHora());
            System.out.printf("   📊 Estado del scrim: %s\n", scrim.getState().getEstado());

            // Buscar mi confirmación en este scrim
            scrim.getConfirmaciones().stream()
                    .filter(c -> c.getUserId().equals(miUserId))
                    .findFirst()
                    .ifPresent(miConfirmacion -> {
                        String estadoEmoji = getEstadoEmoji(miConfirmacion.getEstado());
                        System.out.printf("   %s Mi estado: %s\n", estadoEmoji, miConfirmacion.getEstado());
                    });
        }

        System.out.println("-".repeat(60));
    }

    /**
     * Solicita el número del scrim para ver mi confirmación.
     */
    public int solicitarNumeroScrimParaVer(int cantidadScrims) {
        System.out.printf("\n📋 Ingrese el número del scrim (1-%d) o '0' para cancelar: ", cantidadScrims);
        try {
            String input = scanner.nextLine().trim();
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return -1; // Valor inválido
        }
    }

    /**
     * Solicita el ID del scrim para ver el estado de mi confirmación.
     * 
     * @deprecated Usar selección numerada con mostrarScrimsConMisConfirmaciones() y
     *             solicitarNumeroScrimParaVer()
     */
    @Deprecated
    public String solicitarIdScrimParaVer() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("           📊 VER ESTADO DE MI CONFIRMACIÓN");
        System.out.println("=".repeat(60));
        System.out.print("\n📋 ID del scrim: ");
        return scanner.nextLine().trim();
    }

    /**
     * Muestra los detalles de MI confirmación.
     */
    public void mostrarMiConfirmacion(Confirmacion confirmacion) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("                 ✅ MI CONFIRMACIÓN");
        System.out.println("=".repeat(60));

        String estadoEmoji = getEstadoEmoji(confirmacion.getEstado());

        System.out.printf("\n%s Estado: %s\n", estadoEmoji, confirmacion.getEstado());
        System.out.printf("📋 Scrim ID: %s\n", confirmacion.getScrimId());
        System.out.printf("📅 Fecha de solicitud: %s\n", confirmacion.getFechaSolicitud());

        if (confirmacion.getFechaRespuesta() != null) {
            System.out.printf("📅 Fecha de respuesta: %s\n", confirmacion.getFechaRespuesta());
        }

        // Mensajes según el estado
        switch (confirmacion.getEstado()) {
            case CONFIRMADA:
                System.out.println("\n✅ ASISTENCIA CONFIRMADA");
                System.out.println("   Esperando que los demás jugadores confirmen.");
                System.out.println("   Recibirás una notificación cuando todos confirmen.");
                break;

            case RECHAZADA:
                System.out.println("\n❌ ASISTENCIA RECHAZADA");
                System.out.println("   Has liberado tu lugar en el scrim.");
                break;

            case PENDIENTE:
                System.out.println("\n⏳ CONFIRMACIÓN PENDIENTE");
                System.out.println("   Por favor confirma o rechaza tu asistencia lo antes posible.");
                System.out.println("   El scrim no puede iniciar hasta que todos confirmen.");
                break;
        }

        System.out.println("=".repeat(60));
    }

    // ========== MENSAJES ==========

    /**
     * Muestra un mensaje de éxito.
     */
    public void mostrarExito(String mensaje) {
        System.out.println("\n✅ " + mensaje);
    }

    /**
     * Muestra un mensaje de error.
     */
    public void mostrarError(String mensaje) {
        System.err.println("\n❌ " + mensaje);
    }

    /**
     * Muestra un mensaje informativo.
     */
    public void mostrarInfo(String mensaje) {
        System.out.println("\nℹ️  " + mensaje);
    }

    // ========== HELPERS ==========

    private String getEstadoEmoji(Confirmacion.EstadoConfirmacion estado) {
        switch (estado) {
            case CONFIRMADA:
                return "✅";
            case RECHAZADA:
                return "❌";
            case PENDIENTE:
                return "⏳";
            default:
                return "❓";
        }
    }
}
