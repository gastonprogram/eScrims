package view;

import java.util.Scanner;

public class MenuView {
    private Scanner scanner;

    public MenuView() {
        this.scanner = new Scanner(System.in);
    }

    public void mostrarMenuPrincipal() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("           SISTEMA DE MATCHMAKING");
        System.out.println("=".repeat(50));
        System.out.println("1. Iniciar Sesión");
        System.out.println("2. Registrarse");
        System.out.println("3. Salir");
        System.out.println("=".repeat(50));
        System.out.print("Seleccione una opción: ");
    }

    public int leerOpcion() {
        try {
            String input = scanner.nextLine().trim();
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return -1; // Opción inválida
        }
    }

    public void mostrarOpcionInvalida() {
        System.err.println("\n✗ Opción inválida. Por favor seleccione 1, 2 o 3.");
    }

    public void mostrarSalida() {
        System.out.println("\n" + "=".repeat(40));
        System.out.println("       ¡Gracias por usar el sistema!");
        System.out.println("              ¡Hasta luego!");
        System.out.println("=".repeat(40));
    }

    public void mostrarBienvenida() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("     ¡BIENVENIDO AL SISTEMA DE MATCHMAKING!");
        System.out.println("=".repeat(60));
    }

    public void presionarEnterParaContinuar() {
        System.out.print("\nPresione Enter para continuar...");
        scanner.nextLine();
    }

    public void cerrar() {
        scanner.close();
    }
}