package view;

import controller.PostulacionController;
import model.Postulacion;
import model.Usuario;

import java.util.List;
import java.util.Scanner;

/**
 * Vista para gestionar las postulaciones a scrims.
 * 
 * Funcionalidades:
 * - Postularse a un scrim (jugador)
 * - Ver postulaciones pendientes (organizador)
 * - Aceptar/rechazar postulaciones (organizador)
 * 
 * Siguiendo el patrón de desacoplamiento:
 * - La vista NO contiene lógica de negocio
 * - Toda la lógica está en PostulacionController
 * - La vista solo orquesta la interacción
 * 
 * @author eScrims Team
 */
public class PostulacionView {
    private final PostulacionController controller;
    private final Scanner scanner;

    public PostulacionView() {
        this.controller = new PostulacionController();
        this.scanner = new Scanner(System.in);
    }

    /**
     * Muestra el menú principal de postulaciones para un jugador.
     * 
     * @param usuario Usuario actual
     */
    public void mostrarMenuPostulaciones(Usuario usuario) {
        boolean continuar = true;

        while (continuar) {
            System.out.println("\n=== GESTIÓN DE POSTULACIONES ===");
            System.out.println("1. Postularse a un scrim");
            System.out.println("2. Ver mis postulaciones");
            System.out.println("0. Volver");
            System.out.print("Seleccione una opción: ");

            String opcion = scanner.nextLine().trim();

            switch (opcion) {
                case "1":
                    formularioPostularse(usuario);
                    break;
                case "2":
                    verMisPostulaciones(usuario);
                    break;
                case "0":
                    continuar = false;
                    break;
                default:
                    System.out.println("Opción inválida");
            }
        }
    }

    /**
     * Formulario para que un jugador se postule a un scrim.
     * 
     * @param usuario Usuario que se postula
     */
    public void formularioPostularse(Usuario usuario) {
        System.out.println("\n=== POSTULARSE A UN SCRIM ===");

        System.out.print("ID del scrim: ");
        String scrimId = scanner.nextLine().trim();

        if (scrimId.isEmpty()) {
            System.out.println("El ID del scrim no puede estar vacío");
            return;
        }

        System.out.print("Tu rango actual (valor numérico, ej: 1000): ");
        String rangoStr = scanner.nextLine().trim();

        int rango;
        try {
            rango = Integer.parseInt(rangoStr);
            if (rango < 0) {
                System.out.println("El rango debe ser un número positivo");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("El rango debe ser un número válido");
            return;
        }

        System.out.print("Tu latencia promedio (ms): ");
        String latenciaStr = scanner.nextLine().trim();

        int latencia;
        try {
            latencia = Integer.parseInt(latenciaStr);
            if (latencia < 0) {
                System.out.println("La latencia debe ser un número positivo");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("La latencia debe ser un número válido");
            return;
        }

        // Llamar al controlador
        String resultado = controller.postularAScrim(scrimId, usuario.getId(), rango, latencia);
        System.out.println("\n" + resultado);
    }

    /**
     * Muestra las postulaciones del usuario actual.
     * 
     * @param usuario Usuario actual
     */
    private void verMisPostulaciones(Usuario usuario) {
        System.out.println("\n=== MIS POSTULACIONES ===");
        System.out.println("(Funcionalidad pendiente de implementación)");
        System.out.println("Requiere un método en el controlador para buscar scrims por usuario postulante");
    }

    /**
     * Muestra el menú de gestión de postulaciones para el organizador.
     * 
     * @param scrimId       ID del scrim
     * @param organizadorId ID del organizador
     */
    public void gestionarPostulaciones(String scrimId, String organizadorId) {
        boolean continuar = true;

        while (continuar) {
            System.out.println("\n=== GESTIÓN DE POSTULACIONES (ORGANIZADOR) ===");
            System.out.println("Scrim ID: " + scrimId);
            System.out.println("1. Ver postulaciones pendientes");
            System.out.println("2. Ver todas las postulaciones");
            System.out.println("3. Aceptar postulación");
            System.out.println("4. Rechazar postulación");
            System.out.println("0. Volver");
            System.out.print("Seleccione una opción: ");

            String opcion = scanner.nextLine().trim();

            switch (opcion) {
                case "1":
                    verPostulacionesPendientes(scrimId, organizadorId);
                    break;
                case "2":
                    verTodasLasPostulaciones(scrimId, organizadorId);
                    break;
                case "3":
                    aceptarPostulacion(scrimId, organizadorId);
                    break;
                case "4":
                    rechazarPostulacion(scrimId, organizadorId);
                    break;
                case "0":
                    continuar = false;
                    break;
                default:
                    System.out.println("Opción inválida");
            }
        }
    }

    /**
     * Muestra las postulaciones pendientes de un scrim.
     * 
     * @param scrimId       ID del scrim
     * @param organizadorId ID del organizador
     */
    private void verPostulacionesPendientes(String scrimId, String organizadorId) {
        try {
            List<Postulacion> postulaciones = controller.listarPostulacionesPendientes(scrimId, organizadorId);

            if (postulaciones.isEmpty()) {
                System.out.println("\nNo hay postulaciones pendientes");
                return;
            }

            System.out.println("\n=== POSTULACIONES PENDIENTES ===");
            for (int i = 0; i < postulaciones.size(); i++) {
                Postulacion p = postulaciones.get(i);
                System.out.println("\n" + (i + 1) + ". Usuario: " + p.getUserId());
                System.out.println("   Rango: " + p.getRangoUsuario());
                System.out.println("   Latencia: " + p.getLatenciaUsuario() + " ms");
                System.out.println("   Estado: " + p.getEstado());
                System.out.println("   Fecha: " + p.getFechaPostulacion());
            }

        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Muestra todas las postulaciones de un scrim.
     * 
     * @param scrimId       ID del scrim
     * @param organizadorId ID del organizador
     */
    private void verTodasLasPostulaciones(String scrimId, String organizadorId) {
        try {
            List<Postulacion> postulaciones = controller.listarTodasLasPostulaciones(scrimId, organizadorId);

            if (postulaciones.isEmpty()) {
                System.out.println("\nNo hay postulaciones");
                return;
            }

            System.out.println("\n=== TODAS LAS POSTULACIONES ===");
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

        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Permite al organizador aceptar una postulación.
     * 
     * @param scrimId       ID del scrim
     * @param organizadorId ID del organizador
     */
    private void aceptarPostulacion(String scrimId, String organizadorId) {
        System.out.println("\n=== ACEPTAR POSTULACIÓN ===");
        System.out.print("ID del usuario a aceptar: ");
        String userId = scanner.nextLine().trim();

        if (userId.isEmpty()) {
            System.out.println("El ID del usuario no puede estar vacío");
            return;
        }

        String resultado = controller.aceptarPostulacion(scrimId, userId, organizadorId);
        System.out.println("\n" + resultado);
    }

    /**
     * Permite al organizador rechazar una postulación.
     * 
     * @param scrimId       ID del scrim
     * @param organizadorId ID del organizador
     */
    private void rechazarPostulacion(String scrimId, String organizadorId) {
        System.out.println("\n=== RECHAZAR POSTULACIÓN ===");
        System.out.print("ID del usuario a rechazar: ");
        String userId = scanner.nextLine().trim();

        if (userId.isEmpty()) {
            System.out.println("El ID del usuario no puede estar vacío");
            return;
        }

        System.out.print("Motivo del rechazo: ");
        String motivo = scanner.nextLine().trim();

        if (motivo.isEmpty()) {
            motivo = "Sin motivo especificado";
        }

        String resultado = controller.rechazarPostulacion(scrimId, userId, organizadorId, motivo);
        System.out.println("\n" + resultado);
    }
}
