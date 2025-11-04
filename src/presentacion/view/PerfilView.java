package presentacion.view;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import dominio.juegos.Juego;
import dominio.juegos.LeagueOfLegends;
import dominio.modelo.Usuario;

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

        List<String> roles = perfil.getRolesPreferidos();
        String rolesStr = roles.isEmpty() ? "Sin roles configurados" : String.join(", ", roles);
        System.out.println("Roles Preferidos: " + rolesStr);

        // Usar el método que retorna String para compatibilidad con la vista
        String juego = perfil.getJuegoPrincipalNombre();
        System.out.println("Juego Principal: " + (juego.isEmpty() ? "Sin juego configurado" : juego));

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
        System.out.println("Juegos disponibles:");
        System.out.println("  1. League of Legends");
        System.out.println("  (Más juegos próximamente...)");
        System.out.print("Nuevo Juego Principal (actual: " + actual + ") [Ingrese el nombre]: ");
        return scanner.nextLine();
    }

    /**
     * Convierte el nombre ingresado por el usuario a un objeto Juego.
     * Por ahora solo soporta League of Legends.
     */
    public Juego convertirStringAJuego(String nombreJuego) {
        if (nombreJuego == null || nombreJuego.trim().isEmpty()) {
            return null;
        }

        String nombre = nombreJuego.trim().toLowerCase();
        if (nombre.contains("league") || nombre.contains("lol")) {
            return LeagueOfLegends.getInstance();
        }

        // Si no coincide con ninguno, retornar null
        return null;
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

    // ================== Métodos para el Controller ==================

    /**
     * Muestra el perfil completo del usuario.
     */
    public void mostrarPerfil(Usuario usuario) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("           MI PERFIL");
        System.out.println("=".repeat(50));
        System.out.println("Username:        " + usuario.getUsername());
        System.out.println("Email:           " + usuario.getEmail());
        System.out.println("Rango:           " + usuario.getRangoPrincipal());
        System.out.println("Juego Principal: " + usuario.getJuegoPrincipalNombre());
        System.out.println("Roles:           " + String.join(", ", usuario.getRolesPreferidos()));
        System.out.println("Región:          " + usuario.getRegion());
        System.out.println("Disponibilidad:  " + usuario.getDisponibilidad());
        System.out.println("=".repeat(50));
    }

    public String solicitarRango() {
        System.out.print("\nNuevo rango (1-100): ");
        return scanner.nextLine().trim();
    }

    public Juego solicitarJuegoPrincipal() {
        System.out.println("\n--- Seleccionar Juego Principal ---");
        System.out.println("1. League of Legends");
        System.out.println("0. No cambiar");
        System.out.print("Opción: ");

        String opcion = scanner.nextLine().trim();
        if (opcion.equals("1")) {
            return LeagueOfLegends.getInstance();
        }
        return null;
    }

    public List<String> solicitarRolesPreferidos(Juego juego) {
        if (juego == null) {
            return Arrays.asList();
        }

        System.out.println("\n--- Seleccionar Roles Preferidos ---");
        System.out.println("Roles disponibles para " + juego.getNombre() + ":");

        var rolesDisponibles = juego.getRolesDisponibles();
        for (int i = 0; i < rolesDisponibles.size(); i++) {
            System.out.println((i + 1) + ". " + rolesDisponibles.get(i).getNombre());
        }

        System.out.print("\nIngrese los roles separados por comas (ej: TOP,MID): ");
        String input = scanner.nextLine().trim();

        if (input.isEmpty()) {
            return Arrays.asList();
        }

        return Arrays.stream(input.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    public String solicitarRegion() {
        System.out.print("\nNueva región (ej: LAS, LAN, NA, EUW): ");
        return scanner.nextLine().trim();
    }

    public String solicitarDisponibilidad() {
        System.out.print("\nNueva disponibilidad (ej: 18:00-23:00): ");
        return scanner.nextLine().trim();
    }

    public boolean confirmarCambios(String rango, Juego juego, List<String> roles,
            String region, String disponibilidad) {
        System.out.println("\n--- RESUMEN DE CAMBIOS ---");
        System.out.println("Rango:          " + rango);
        System.out.println("Juego:          " + (juego != null ? juego.getNombre() : "Sin cambios"));
        System.out.println("Roles:          " + String.join(", ", roles));
        System.out.println("Región:         " + region);
        System.out.println("Disponibilidad: " + disponibilidad);
        System.out.print("\n¿Confirmar cambios? (s/n): ");

        String respuesta = scanner.nextLine().trim().toLowerCase();
        return respuesta.equals("s") || respuesta.equals("si") || respuesta.equals("sí");
    }

    public String solicitarIdUsuario() {
        System.out.print("\nID del usuario: ");
        return scanner.nextLine().trim();
    }

    public void mostrarPerfilOtroUsuario(Usuario usuario) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("           PERFIL DE USUARIO");
        System.out.println("=".repeat(50));
        System.out.println("Username:        " + usuario.getUsername());
        System.out.println("Rango:           " + usuario.getRangoPrincipal());
        System.out.println("Juego Principal: " + usuario.getJuegoPrincipalNombre());
        System.out.println("Roles:           " + String.join(", ", usuario.getRolesPreferidos()));
        System.out.println("Región:          " + usuario.getRegion());
        System.out.println("=".repeat(50));
    }

    public String mostrarMenuPerfil() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("           GESTIÓN DE PERFIL");
        System.out.println("=".repeat(50));
        System.out.println("1. Ver Mi Perfil");
        System.out.println("2. Editar Perfil");
        System.out.println("3. Ver Perfil de Otro Usuario");
        System.out.println("0. Volver");
        System.out.println("=".repeat(50));
        System.out.print("Seleccione una opción: ");
        return scanner.nextLine().trim();
    }

    public void mostrarExito(String mensaje) {
        System.out.println("\n✓ " + mensaje);
    }

    public void mostrarInfo(String mensaje) {
        System.out.println("\n" + mensaje);
    }
}