package presentacion.view;

import java.util.List;
import java.util.Scanner;

import dominio.modelo.Postulacion;
import dominio.modelo.Scrim;

/**
 * Vista simplificada para postulaciones (SOLO PARA USUARIO COMÚN).
 * 
 * Flujo del usuario:
 * 1. Ver scrims disponibles
 * 2. Postularse a un scrim (ingresar ID, rango, latencia)
 * 3. Ver el estado de MI postulación
 * 
 * Las funcionalidades del organizador están en OrganizadorView.
 * 
 * @author eScrims Team
 */
public class PostulacionViewSimplificada {

    private final Scanner scanner;

    public PostulacionViewSimplificada() {
        this.scanner = new Scanner(System.in);
    }

    // ========== MOSTRAR SCRIMS DISPONIBLES ==========

    /**
     * Muestra todos los scrims disponibles para postularse.
     */
    public void mostrarScrimsDisponibles(List<Scrim> scrims) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("           🎮 SCRIMS DISPONIBLES PARA POSTULARSE");
        System.out.println("=".repeat(60));

        if (scrims.isEmpty()) {
            System.out.println("\n📭 No hay scrims disponibles en este momento");
            System.out.println("   Los scrims deben estar en estado BUSCANDO para postularse");
            return;
        }

        System.out.printf("\n✅ Encontrados %d scrim(s) disponible(s):\n", scrims.size());

        for (int i = 0; i < scrims.size(); i++) {
            Scrim scrim = scrims.get(i);
            System.out.println("\n" + "-".repeat(60));
            System.out.printf("%d. 📋 ID: %s\n", i + 1, scrim.getId());
            System.out.printf("   🎮 Juego: %s | Formato: %s\n",
                    scrim.getJuego().getNombre(),
                    scrim.getFormato().getFormatName());
            System.out.printf("   📅 Fecha/Hora: %s\n", scrim.getFechaHora());
            System.out.printf("   👥 Plazas: %d/%d ocupadas\n",
                    scrim.getPostulacionesAceptadas().size(),
                    scrim.getPlazas());
            System.out.printf("   🎯 Rango: %d - %d\n", scrim.getRangoMin(), scrim.getRangoMax());
            System.out.printf("   📡 Latencia máx: %d ms\n", scrim.getLatenciaMax());
            System.out.printf("   📊 Estado: %s\n", scrim.getState().getEstado());
        }

        System.out.println("-".repeat(60));
    }

    // ========== POSTULARSE A UN SCRIM ==========

    /**
     * Solicita el número del scrim al que se quiere postular.
     * Nota: Los scrims disponibles ya fueron mostrados antes de llamar a este
     * método.
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
     * Muestra los datos del usuario que se usarán para la postulación.
     */
    public void mostrarDatosPostulacion(int rango, int latencia, String nombreJuego) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("           📝 DATOS DE TU POSTULACIÓN");
        System.out.println("=".repeat(60));
        System.out.printf("🎮 Rango en %s: %d\n", nombreJuego, rango);
        System.out.printf("📡 Latencia promedio: %d ms\n", latencia);
        System.out.println("=".repeat(60));
    }

    /**
     * Solicita confirmación para postularse con los datos del perfil.
     */
    public boolean confirmarPostulacion() {
        System.out.print("\n¿Confirmas tu postulación con estos datos? (s/n): ");
        String respuesta = scanner.nextLine().trim().toLowerCase();
        return respuesta.equals("s") || respuesta.equals("si") || respuesta.equals("sí");
    }

    // ========== VER MI POSTULACIÓN ==========

    /**
     * Solicita el ID del scrim para ver el estado de mi postulación.
     */
    public String solicitarIdScrimParaVer() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("           📊 VER ESTADO DE MI POSTULACIÓN");
        System.out.println("=".repeat(60));
        System.out.print("\n📋 ID del scrim: ");
        return scanner.nextLine().trim();
    }

    /**
     * Muestra los detalles de MI postulación.
     */
    public void mostrarMiPostulacion(Postulacion postulacion) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("                  📋 MI POSTULACIÓN");
        System.out.println("=".repeat(60));

        String estadoEmoji = getEstadoEmoji(postulacion.getEstado());

        System.out.printf("\n%s Estado: %s\n", estadoEmoji, postulacion.getEstado());
        System.out.printf("📋 Scrim ID: %s\n", postulacion.getScrimId());
        System.out.printf("🎮 Rango enviado: %d\n", postulacion.getRangoUsuario());
        System.out.printf("📡 Latencia enviada: %d ms\n", postulacion.getLatenciaUsuario());
        System.out.printf("📅 Fecha de postulación: %s\n", postulacion.getFechaPostulacion());

        // Mensajes según el estado
        switch (postulacion.getEstado()) {
            case RECHAZADA:
                System.out.println("\n❌ POSTULACIÓN RECHAZADA");
                System.out.printf("💬 Motivo: %s\n", postulacion.getMotivoRechazo());
                break;

            case ACEPTADA:
                System.out.println("\n✅ ¡POSTULACIÓN ACEPTADA!");
                System.out.println("   Esperando que se complete el cupo del scrim.");
                System.out.println("   Recibirás una notificación cuando debas confirmar tu asistencia.");
                break;

            case PENDIENTE:
                System.out.println("\n⏳ POSTULACIÓN PENDIENTE");
                System.out.println("   El organizador está revisando tu postulación.");
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

    private String getEstadoEmoji(Postulacion.EstadoPostulacion estado) {
        switch (estado) {
            case ACEPTADA:
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
