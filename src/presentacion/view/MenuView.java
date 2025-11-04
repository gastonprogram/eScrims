package presentacion.view;

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

    // ================== Métodos para el Controller ==================

    /**
     * Muestra el menú inicial (antes de login).
     */
    public String mostrarMenuInicial() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("           SISTEMA DE MATCHMAKING - eScrims");
        System.out.println("=".repeat(50));
        System.out.println("1. Iniciar Sesión");
        System.out.println("2. Registrarse");
        System.out.println("0. Salir");
        System.out.println("=".repeat(50));
        System.out.print("Seleccione una opción: ");
        return scanner.nextLine().trim();
    }

    /**
     * Muestra el menú principal (después de login).
     */
    public String mostrarMenuPrincipal(String username) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("    MENÚ PRINCIPAL - Usuario: " + username);
        System.out.println("=".repeat(50));
        System.out.println("1. Gestión de Scrims");
        System.out.println("2. Postulaciones");
        System.out.println("3. Confirmaciones");
        System.out.println("4. Mi Perfil");
        System.out.println("5. Info de Sesión");
        System.out.println("0. Cerrar Sesión");
        System.out.println("=".repeat(50));
        System.out.print("Seleccione una opción: ");
        return scanner.nextLine().trim();
    }

    /**
     * Muestra el submenú de scrims.
     */
    public String mostrarMenuScrims() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("           GESTIÓN DE SCRIMS");
        System.out.println("=".repeat(50));
        System.out.println("1. Crear Scrim");
        System.out.println("2. Buscar Scrims");
        System.out.println("3. Listar Todos los Scrims");
        System.out.println("4. Ver Detalle de Scrim");
        System.out.println("5. Eliminar Scrim");
        System.out.println("6. Estadísticas");
        System.out.println("0. Volver");
        System.out.println("=".repeat(50));
        System.out.print("Seleccione una opción: ");
        return scanner.nextLine().trim();
    }

    /**
     * Muestra el submenú de postulaciones.
     */
    public String mostrarMenuPostulaciones() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("           POSTULACIONES");
        System.out.println("=".repeat(50));
        System.out.println("1. Postularme a un Scrim");
        System.out.println("2. Gestionar Postulaciones (Organizador)");
        System.out.println("3. Ver Todas las Postulaciones");
        System.out.println("4. Ver Mi Postulación");
        System.out.println("0. Volver");
        System.out.println("=".repeat(50));
        System.out.print("Seleccione una opción: ");
        return scanner.nextLine().trim();
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