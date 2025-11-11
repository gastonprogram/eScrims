package presentacion.view;

import java.util.List;
import java.util.Scanner;

import dominio.modelo.Confirmacion;
import dominio.modelo.Postulacion;

/**
 * Vista para gestión de scrims del ORGANIZADOR.
 * 
 * Funcionalidades del organizador:
 * - Ver postulaciones pendientes a sus scrims
 * - Aceptar/rechazar postulaciones manualmente
 * - Ver estado de confirmaciones de sus scrims
 * - Ver estadísticas de confirmaciones
 * 
 * @author eScrims Team
 */
public class OrganizadorView {

    private final Scanner scanner;

    public OrganizadorView() {
        this.scanner = new Scanner(System.in);
    }

    // ========== MENÚ PRINCIPAL ==========

    /**
     * Muestra el menú de gestión del organizador.
     */
    public String mostrarMenuOrganizador() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("           GESTIÓN DE MIS SCRIMS (ORGANIZADOR)");
        System.out.println("=".repeat(60));
        System.out.println("1. Ver postulaciones pendientes de un scrim");
        System.out.println("2. Gestionar postulaciones (aceptar/rechazar)");
        System.out.println("3. Ver estado de confirmaciones de un scrim");
        System.out.println("4. Ver todas las postulaciones de un scrim");
        System.out.println("5. Gestionar estrategias de matchmaking");
        System.out.println("0. Volver al menú principal");
        System.out.println("=".repeat(60));
        System.out.print("Seleccione una opción: ");
        return scanner.nextLine().trim();
    }

    // ========== GESTIÓN DE POSTULACIONES ==========

    /**
     * Muestra los scrims del organizador y permite seleccionar uno por número.
     */
    public void mostrarMisScrims(List<dominio.modelo.Scrim> scrims) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("              MIS SCRIMS COMO ORGANIZADOR");
        System.out.println("=".repeat(60));

        if (scrims.isEmpty()) {
            System.out.println("\n- No tienes scrims como organizador");
            System.out.println("   Puedes crear uno desde el menú principal");
            return;
        }

        System.out.printf("\n- Tienes %d scrim(s) como organizador:\n", scrims.size());

        for (int i = 0; i < scrims.size(); i++) {
            dominio.modelo.Scrim scrim = scrims.get(i);
            System.out.println("\n" + "-".repeat(60));
            System.out.printf("%d. ID: %s\n", i + 1, scrim.getId());
            System.out.printf("   Juego: %s | Formato: %s\n",
                    scrim.getJuego().getNombre(),
                    scrim.getFormato().getFormatName());
            System.out.printf("   Fecha/Hora: %s\n", scrim.getFechaHora());
            System.out.printf("   Estado: %s\n", scrim.getState().getEstado());

            // Mostrar estadísticas rápidas
            int postulaciones = scrim.getPostulaciones().size();
            int confirmaciones = scrim.getConfirmaciones().size();
            System.out.printf("   Postulaciones: %d | Confirmaciones: %d\n", postulaciones, confirmaciones);
        }

        System.out.println("-".repeat(60));
    }

    /**
     * Solicita el número del scrim a gestionar.
     */
    public int solicitarNumeroScrim(int cantidadScrims) {
        System.out.printf("\n- Ingrese el número del scrim (1-%d) o '0' para cancelar: ", cantidadScrims);
        try {
            String input = scanner.nextLine().trim();
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return -1; // Valor inválido
        }
    }

    /**
     * Solicita el ID del scrim a gestionar.
     * 
     * @deprecated Usar selección numerada con mostrarMisScrims() y
     *             solicitarNumeroScrim()
     */
    @Deprecated
    public String solicitarIdScrim() {
        System.out.print("\n- ID del scrim: ");
        return scanner.nextLine().trim();
    }

    /**
     * Muestra la lista de postulaciones pendientes.
     */
    public void mostrarPostulacionesPendientes(List<Postulacion> postulaciones) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("              POSTULACIONES PENDIENTES");
        System.out.println("=".repeat(60));

        if (postulaciones.isEmpty()) {
            System.out.println("\n- No hay postulaciones pendientes");
            return;
        }

        for (int i = 0; i < postulaciones.size(); i++) {
            Postulacion p = postulaciones.get(i);
            System.out.printf("\n%d. Usuario ID: %s\n", i + 1, p.getUserId());
            System.out.printf("   Rango: %d\n", p.getRangoUsuario());
            System.out.printf("   Latencia: %d ms\n", p.getLatenciaUsuario());
            System.out.printf("   Fecha: %s\n", p.getFechaPostulacion());
        }
        System.out.println("=".repeat(60));
    }

    /**
     * Muestra todas las postulaciones (pendientes, aceptadas, rechazadas).
     */
    public void mostrarTodasLasPostulaciones(List<Postulacion> postulaciones) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("              TODAS LAS POSTULACIONES");
        System.out.println("=".repeat(60));

        if (postulaciones.isEmpty()) {
            System.out.println("\n- No hay postulaciones para este scrim");
            return;
        }

        for (Postulacion p : postulaciones) {
            String estadoMarca = getEstadoMarcaPostulacion(p.getEstado());

            System.out.printf("\n%s Usuario ID: %s | Estado: %s\n",
                    estadoMarca, p.getUserId(), p.getEstado());
            System.out.printf("   Rango: %d | Latencia: %d ms\n",
                    p.getRangoUsuario(), p.getLatenciaUsuario());
            System.out.printf("   Fecha: %s\n", p.getFechaPostulacion());

            if (p.getEstado() == Postulacion.EstadoPostulacion.RECHAZADA) {
                System.out.printf("   Motivo rechazo: %s\n", p.getMotivoRechazo());
            }
        }
        System.out.println("=".repeat(60));
    }

    /**
     * Solicita la acción que el organizador quiere realizar.
     */
    public String solicitarAccion() {
        System.out.println("\n¿Qué desea hacer?");
        System.out.println("1. Aceptar postulación");
        System.out.println("2. Rechazar postulación");
        System.out.println("0. Salir");
        System.out.print("Opción: ");

        String opcion = scanner.nextLine().trim();
        switch (opcion) {
            case "1":
                return "aceptar";
            case "2":
                return "rechazar";
            case "0":
                return "salir";
            default:
                return "invalida";
        }
    }

    /**
     * Permite seleccionar un postulante por ID.
     */
    public String seleccionarPostulante(List<Postulacion> postulaciones) {
        System.out.print("\nIngrese el ID del usuario (o número de la lista): ");
        String input = scanner.nextLine().trim();

        // Intentar interpretar como número
        try {
            int numero = Integer.parseInt(input);
            if (numero >= 1 && numero <= postulaciones.size()) {
                return postulaciones.get(numero - 1).getUserId();
            }
        } catch (NumberFormatException e) {
            // No es un número, asumir que es el ID del usuario
        }

        return input;
    }

    /**
     * Solicita el motivo del rechazo.
     */
    public String solicitarMotivo() {
        System.out.print("\n- Ingrese el motivo del rechazo: ");
        String motivo = scanner.nextLine().trim();
        return motivo.isEmpty() ? "Sin motivo especificado" : motivo;
    }

    // ========== GESTIÓN DE CONFIRMACIONES ==========

    /**
     * Muestra la lista de confirmaciones de un scrim.
     */
    public void mostrarListaConfirmaciones(List<Confirmacion> confirmaciones) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("           ESTADO DE CONFIRMACIONES");
        System.out.println("=".repeat(60));

        if (confirmaciones.isEmpty()) {
            System.out.println("\n- No hay confirmaciones para este scrim");
            return;
        }

        for (Confirmacion c : confirmaciones) {
            String estadoMarca = getEstadoMarcaConfirmacion(c.getEstado());

            System.out.printf("\n%s Usuario ID: %s | Estado: %s\n",
                    estadoMarca, c.getUserId(), c.getEstado());
            System.out.printf("   Solicitud: %s\n", c.getFechaSolicitud());

            if (c.getFechaRespuesta() != null) {
                System.out.printf("   Respuesta: %s\n", c.getFechaRespuesta());
            }
        }
        System.out.println("=".repeat(60));
    }

    /**
     * Muestra solo las confirmaciones pendientes.
     */
    public void mostrarConfirmacionesPendientes(List<Confirmacion> pendientes) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("           CONFIRMACIONES PENDIENTES");
        System.out.println("=".repeat(60));

        if (pendientes.isEmpty()) {
            System.out.println("\n[OK] ¡Todos los jugadores han confirmado!");
            return;
        }

        System.out.printf("\n[!] %d jugador(es) aún no han confirmado:\n\n", pendientes.size());

        for (Confirmacion c : pendientes) {
            System.out.printf("[...] Usuario ID: %s\n", c.getUserId());
            System.out.printf("   Solicitud enviada: %s\n", c.getFechaSolicitud());
        }
        System.out.println("=".repeat(60));
    }

    /**
     * Muestra estadísticas de confirmaciones.
     */
    public void mostrarEstadisticas(long confirmadas, long pendientes, long rechazadas) {
        System.out.println("\n" + "-".repeat(60));
        System.out.println("           ESTADÍSTICAS");
        System.out.println("-".repeat(60));
        System.out.printf("[OK] Confirmadas: %d\n", confirmadas);
        System.out.printf("[...] Pendientes:  %d\n", pendientes);
        System.out.printf("[X] Rechazadas:  %d\n", rechazadas);
        System.out.println("-".repeat(60));
    }

    // ========== MENSAJES ==========

    /**
     * Muestra un mensaje de éxito.
     */
    public void mostrarExito(String mensaje) {
        System.out.println("\n[OK] " + mensaje);
    }

    /**
     * Muestra un mensaje de error.
     */
    public void mostrarError(String mensaje) {
        System.err.println("\n[ERROR] " + mensaje);
    }

    /**
     * Muestra un mensaje informativo.
     */
    public void mostrarInfo(String mensaje) {
        System.out.println("\n[INFO] " + mensaje);
    }

    // ========== HELPERS ==========

    private String getEstadoMarcaPostulacion(Postulacion.EstadoPostulacion estado) {
        switch (estado) {
            case ACEPTADA:
                return "[OK]";
            case RECHAZADA:
                return "[X]";
            case PENDIENTE:
                return "[...]";
            default:
                return "[?]";
        }
    }

    private String getEstadoMarcaConfirmacion(Confirmacion.EstadoConfirmacion estado) {
        switch (estado) {
            case CONFIRMADA:
                return "[OK]";
            case RECHAZADA:
                return "[X]";
            case PENDIENTE:
                return "[...]";
            default:
                return "[?]";
        }
    }
}
