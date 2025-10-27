package view;

import model.Usuario;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class PerfilView {
    private Scanner scanner;

    public PerfilView() {
        // Asumo que el scanner ya fue inicializado en el Main, pero lo inicializo aquí
        // por seguridad
        this.scanner = new Scanner(System.in);
    }

    public void mostrarTituloEdicion() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("      EDITAR PERFIL Y PREFERENCIAS");
        System.out.println("=".repeat(50));
        System.out.println("Deje el campo vacío si no desea modificar el valor.");
    }

    public void mostrarPerfilActual(Usuario perfil) {
        System.out.println("\n--- Valores Actuales ---");
        System.out.println("Rango: " + perfil.getRangoPrincipal());
        System.out.println("Roles Preferidos: " + String.join(", ", perfil.getRolesPreferidos()));
        System.out.println("Juego Principal: " + perfil.getJuegoPrincipal());
        System.out.println("Región: " + perfil.getRegion());
        System.out.println("Disponibilidad: " + perfil.getDisponibilidad());
        System.out.println("------------------------\n");
    }

    // Métodos para solicitar datos (devuelven el input o la cadena vacía)

    public String solicitarNuevoRango(String actual) {
        System.out.print("Nuevo Rango (" + actual + "): ");
        return scanner.nextLine();
    }

    public List<String> solicitarNuevosRoles(List<String> actuales) {
        String rolesStr = String.join(", ", actuales);
        System.out.print("Nuevos Roles (ej: TOP, MID). Actuales: [" + rolesStr + "]: ");
        String input = scanner.nextLine();

        if (input.trim().isEmpty()) {
            return actuales;
        }

        // Transforma la entrada de String separado por comas a List<String>
        return Arrays.stream(input.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    public String solicitarNuevoJuegoPrincipal(String actual) {
        System.out.print("Nuevo Juego Principal (" + actual + "): ");
        return scanner.nextLine();
    }

    public String solicitarNuevaRegion(String actual) {
        System.out.print("Nueva Región (" + actual + "): ");
        return scanner.nextLine();
    }

    public String solicitarNuevaDisponibilidad(String actual) {
        System.out.print("Nueva Disponibilidad (Formato HH:MM-HH:MM) (" + actual + "): ");
        return scanner.nextLine();
    }

    public void mostrarExito() {
        System.out.println("\n✓ Perfil actualizado y cambios persistidos correctamente.");
    }

    public void mostrarError(String mensaje) {
        System.err.println("\n✗ Error al editar perfil: " + mensaje);
    }

    public void presionarEnterParaContinuar() {
        System.out.print("\nPresione Enter para volver al menú de usuario...");
        scanner.nextLine();
    }
}