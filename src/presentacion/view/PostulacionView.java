package presentacion.view;

import dominio.modelo.Postulacion;

import java.util.List;
import java.util.Scanner;

/**
 * Vista para gestionar las postulaciones a scrims.
 * Refactorizada para usar el nuevo PostulacionController.
 * 
 * @author eScrims Team
 */
public class PostulacionView {
    private final Scanner scanner;

    public PostulacionView() {
        this.scanner = new Scanner(System.in);
    }

    // ================== Métodos para el Controller ==================

    public String solicitarIdScrim() {
        System.out.print("\nID del scrim: ");
        return scanner.nextLine().trim();
    }

    public int solicitarRango() {
        System.out.print("Tu rango actual (1-100): ");
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.err.println("✗ Número inválido");
            return solicitarRango();
        }
    }

    public int solicitarLatencia() {
        System.out.print("Tu latencia promedio (ms): ");
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.err.println("✗ Número inválido");
            return solicitarLatencia();
        }
    }

    public String solicitarAccion() {
        System.out.println("\n--- Acción ---");
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

    public String seleccionarPostulante(List<Postulacion> postulaciones) {
        System.out.print("\nIngrese el ID del usuario: ");
        return scanner.nextLine().trim();
    }

    public String solicitarMotivo() {
        System.out.print("\nMotivo del rechazo: ");
        String motivo = scanner.nextLine().trim();
        return motivo.isEmpty() ? "Sin motivo especificado" : motivo;
    }

    public void mostrarPostulacionesPendientes(List<Postulacion> postulaciones) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("       POSTULACIONES PENDIENTES");
        System.out.println("=".repeat(50));

        for (int i = 0; i < postulaciones.size(); i++) {
            Postulacion p = postulaciones.get(i);
            System.out.println("\n" + (i + 1) + ". Usuario: " + p.getUserId());
            System.out.println("   Rango: " + p.getRangoUsuario());
            System.out.println("   Latencia: " + p.getLatenciaUsuario() + " ms");
            System.out.println("   Estado: " + p.getEstado());
            System.out.println("   Fecha: " + p.getFechaPostulacion());
        }
        System.out.println("=".repeat(50));
    }

    public void mostrarTodasLasPostulaciones(List<Postulacion> postulaciones) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("       TODAS LAS POSTULACIONES");
        System.out.println("=".repeat(50));

        for (int i = 0; i < postulaciones.size(); i++) {
            Postulacion p = postulaciones.get(i);
            System.out.println("\n" + (i + 1) + ". Usuario: " + p.getUserId());
            System.out.println("   Rango: " + p.getRangoUsuario());
            System.out.println("   Latencia: " + p.getLatenciaUsuario() + " ms");
            System.out.println("   Estado: " + p.getEstado());

            if (p.getEstado() == Postulacion.EstadoPostulacion.RECHAZADA) {
                System.out.println("   Motivo rechazo: " + p.getMotivoRechazo());
            }

            System.out.println("   Fecha: " + p.getFechaPostulacion());
        }
        System.out.println("=".repeat(50));
    }

    public void mostrarDetallePostulacion(Postulacion postulacion) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("       DETALLE DE POSTULACIÓN");
        System.out.println("=".repeat(50));
        System.out.println("Scrim:    " + postulacion.getScrimId());
        System.out.println("Usuario:  " + postulacion.getUserId());
        System.out.println("Rango:    " + postulacion.getRangoUsuario());
        System.out.println("Latencia: " + postulacion.getLatenciaUsuario() + " ms");
        System.out.println("Estado:   " + postulacion.getEstado());
        System.out.println("Fecha:    " + postulacion.getFechaPostulacion());

        if (postulacion.getEstado() == Postulacion.EstadoPostulacion.RECHAZADA) {
            System.out.println("Motivo:   " + postulacion.getMotivoRechazo());
        }
        System.out.println("=".repeat(50));
    }

    public void mostrarExito(String mensaje) {
        System.out.println("\n✓ " + mensaje);
    }

    public void mostrarError(String mensaje) {
        System.err.println("\n✗ " + mensaje);
    }

    public void mostrarInfo(String mensaje) {
        System.out.println("\n" + mensaje);
    }

}
