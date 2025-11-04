package presentacion.view;

import java.util.Scanner;

public class LoginView {
    private Scanner scanner;

    public LoginView() {
        this.scanner = new Scanner(System.in);
    }

    public void mostrarTituloLogin() {
        System.out.println("\n" + "=".repeat(40));
        System.out.println("           INICIAR SESIÓN");
        System.out.println("=".repeat(40));
    }

    public String solicitarUsername() {
        System.out.print("Username: ");
        return scanner.nextLine().trim();
    }

    public String solicitarPassword() {
        System.out.print("Contraseña: ");
        return scanner.nextLine();
    }

    public void mostrarLoginExitoso(String username) {
        System.out.println("\n✓ ¡Bienvenido " + username + "!");
        System.out.println("Sesión iniciada correctamente.");
    }

    public void mostrarErrorLogin(String mensaje) {
        System.err.println("\n✗ Error de login: " + mensaje);
    }

    public void mostrarLogoutExitoso() {
        System.out.println("\n✓ Sesión cerrada correctamente.");
        System.out.println("¡Hasta luego!");
    }

    public void mostrarInfoSesion(String info) {
        System.out.println("\n" + "-".repeat(30));
        System.out.println(info);
        System.out.println("-".repeat(30));
    }

    public boolean confirmarReintento() {
        System.out.print("\n¿Desea intentar nuevamente? (s/n): ");
        String respuesta = scanner.nextLine().trim().toLowerCase();
        return respuesta.equals("s") || respuesta.equals("si") || respuesta.equals("sí");
    }

    public void mostrarMensaje(String mensaje) {
        System.out.println(mensaje);
    }

    public void limpiarPantalla() {
        for (int i = 0; i < 3; i++) {
            System.out.println();
        }
    }

    // ================== Métodos para el Controller ==================

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