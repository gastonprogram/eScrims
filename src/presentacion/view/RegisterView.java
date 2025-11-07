package presentacion.view;

import dominio.juegos.Juego;
import dominio.juegos.JuegosRegistry;

import java.util.List;
import java.util.Scanner;

public class RegisterView {
    private Scanner scanner;
    private JuegosRegistry juegosRegistry;

    public RegisterView() {
        this.scanner = new Scanner(System.in);
        this.juegosRegistry = JuegosRegistry.getInstance();
    }

    public void mostrarTituloRegistro() {
        System.out.println("\n" + "=".repeat(40));
        System.out.println("         REGISTRO DE USUARIO");
        System.out.println("=".repeat(40));
        System.out.println("Complete los siguientes datos:");
    }

    public String solicitarUsername() {
        System.out.print("\nUsername (3-20 caracteres, solo letras, números y _): ");
        return scanner.nextLine().trim();
    }

    public String solicitarEmail() {
        System.out.print("Email: ");
        return scanner.nextLine().trim();
    }

    public String solicitarPassword() {
        System.out.print("Contraseña (mínimo 6 caracteres): ");
        return scanner.nextLine();
    }

    public String confirmarPassword() {
        System.out.print("Confirmar contraseña: ");
        return scanner.nextLine();
    }

    public String solicitarJuegoPrincipal() {
        System.out.println("\nJuegos disponibles:");
        List<Juego> juegos = juegosRegistry.getJuegosDisponibles();

        for (int i = 0; i < juegos.size(); i++) {
            System.out.println((i + 1) + ". " + juegos.get(i).getNombre());
        }

        System.out.print("\nSeleccione el número del juego principal: ");
        try {
            String input = scanner.nextLine().trim();
            int numero = Integer.parseInt(input);

            Juego juegoSeleccionado = juegosRegistry.obtenerPorNumero(numero);
            if (juegoSeleccionado != null) {
                return juegoSeleccionado.getNombre();
            } else {
                return ""; // Número inválido
            }
        } catch (NumberFormatException e) {
            return ""; // Entrada inválida
        }
    }

    public int solicitarRango() {
        System.out.print("Rango (1-100): ");
        try {
            String input = scanner.nextLine().trim();
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return -1; // Valor inválido que será manejado por las validaciones
        }
    }

    public void mostrarRegistroExitoso(String username) {
        System.out.println("\n" + "=".repeat(40));
        System.out.println("✓ ¡Usuario registrado exitosamente!");
        System.out.println("Username: " + username);
        System.out.println("Ya puede iniciar sesión con sus credenciales.");
        System.out.println("=".repeat(40));
    }

    public void mostrarErrorRegistro(String mensaje) {
        System.err.println("\n✗ Error en el registro:");
        System.err.println("  " + mensaje);
    }

    public void mostrarErrorConfirmacion() {
        System.err.println("\n✗ Las contraseñas no coinciden. Intente nuevamente.");
    }

    public boolean confirmarReintento() {
        System.out.print("\n¿Desea intentar registrarse nuevamente? (s/n): ");
        String respuesta = scanner.nextLine().trim().toLowerCase();
        return respuesta.equals("s") || respuesta.equals("si") || respuesta.equals("sí");
    }

    public void mostrarRequisitos() {
        System.out.println("\nRequisitos:");
        System.out.println("• Username: 3-20 caracteres, solo letras, números y guión bajo");
        System.out.println("• Email: formato válido (ej: usuario@dominio.com)");
        System.out.println("• Contraseña: mínimo 6 caracteres, debe contener letra y número");
        System.out.println("• Rango: número entre 1 y 100");
    }

    public void mostrarProgreso(int paso, int total) {
        System.out.println("\n[Paso " + paso + "/" + total + "]");
    }

    public void mostrarMensaje(String mensaje) {
        System.out.println(mensaje);
    }

    public void limpiarPantalla() {
        for (int i = 0; i < 3; i++) {
            System.out.println();
        }
    }

    public void mostrarResumenDatos(String username, String email, String juego, int rango) {
        System.out.println("\n" + "-".repeat(30));
        System.out.println("RESUMEN DE DATOS:");
        System.out.println("Username: " + username);
        System.out.println("Email: " + email);
        System.out.println("Juego principal: " + juego);
        System.out.println("Rango: " + rango);
        System.out.println("-".repeat(30));
    }

    public boolean confirmarRegistro() {
        System.out.print("\n¿Confirma el registro con estos datos? (s/n): ");
        String respuesta = scanner.nextLine().trim().toLowerCase();
        return respuesta.equals("s") || respuesta.equals("si") || respuesta.equals("sí");
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