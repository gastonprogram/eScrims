package presentacion.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import dominio.juegos.Juego;
import dominio.juegos.JuegosRegistry;
import dominio.modelo.Usuario;
import dominio.roles.RolJuego;

/**
 * Vista modernizada para gestión de perfiles de usuario.
 * 
 * Funcionalidades:
 * - Ver perfil completo con información organizada por juego
 * - Editar rangos por juego específico
 * - Gestionar roles preferidos por juego
 * - Modificar región y latencia
 * - Selección enumerada para mejor UX
 * 
 * Estructura actual del sistema:
 * - Usuario.rangoPorJuego: Map<String, Integer>
 * - Usuario.rolesPorJuego: Map<String, List<String>>
 * - Usuario.latenciaPromedio: int
 * - Usuario.region: String
 * 
 * @author eScrims Team
 */
public class PerfilView {

    private final Scanner scanner;
    private final JuegosRegistry juegosRegistry;

    public PerfilView() {
        this.scanner = new Scanner(System.in);
        this.juegosRegistry = JuegosRegistry.getInstance();
    }

    // ========== MENÚ PRINCIPAL ==========

    /**
     * Muestra el menú principal de gestión de perfil.
     */
    public String mostrarMenuPerfil() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("               - GESTIÓN DE PERFIL");
        System.out.println("=".repeat(60));
        System.out.println("1. Ver Mi Perfil Completo");
        System.out.println("2. Editar Rango por Juego");
        System.out.println("3. Gestionar Roles Preferidos por Juego");
        System.out.println("4. Cambiar Región");
        System.out.println("5. Ajustar Latencia Promedio");
        System.out.println("6. Ver Perfil de Otro Usuario");
        System.out.println("0. Volver al Menú Principal");
        System.out.println("=".repeat(60));
        System.out.print("Seleccione una opción: ");
        return scanner.nextLine().trim();
    }

    // ========== VER PERFIL ==========

    /**
     * Muestra el perfil completo del usuario con toda la información organizada.
     */
    public void mostrarPerfilCompleto(Usuario usuario) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("                   - MI PERFIL");
        System.out.println("=".repeat(60));

        // Información básica
        System.out.printf("- Username:     %s\n", usuario.getUsername());
        System.out.printf("- Email:        %s\n", usuario.getEmail());
        System.out.printf("- Región:       %s\n",
                usuario.getRegion().isEmpty() ? "No configurada" : usuario.getRegion());
        System.out.printf("- Latencia:     %d ms\n", usuario.getLatenciaPromedio());

        // Rangos por juego
        System.out.println("\n- RANGOS POR JUEGO:");
        Map<String, Integer> rangos = usuario.getRangoPorJuego();
        if (rangos.isEmpty()) {
            System.out.println("   - No hay rangos configurados");
        } else {
            rangos.forEach((juego, rango) -> System.out.printf("   - %s: %d\n", juego, rango));
        }

        // Roles por juego
        System.out.println("\n- ROLES PREFERIDOS POR JUEGO:");
        boolean tieneRoles = false;
        List<Juego> juegosDisponibles = juegosRegistry.getJuegosDisponibles();

        for (Juego juego : juegosDisponibles) {
            List<String> roles = usuario.getRolesPreferidosParaJuego(juego.getNombre());
            if (!roles.isEmpty()) {
                System.out.printf("   - %s: %s\n", juego.getNombre(), String.join(", ", roles));
                tieneRoles = true;
            }
        }

        if (!tieneRoles) {
            System.out.println("   - No hay roles configurados");
        }

        System.out.println("=".repeat(60));
    }

    /**
     * Muestra el perfil de otro usuario (información pública).
     */
    public void mostrarPerfilOtroUsuario(Usuario usuario) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("                 - PERFIL DE USUARIO");
        System.out.println("=".repeat(60));

        System.out.printf("- Username:     %s\n", usuario.getUsername());
        System.out.printf("- Región:       %s\n", usuario.getRegion().isEmpty() ? "No visible" : usuario.getRegion());

        // Mostrar solo rangos (sin roles por privacidad)
        System.out.println("\n- RANGOS:");
        Map<String, Integer> rangos = usuario.getRangoPorJuego();
        if (rangos.isEmpty()) {
            System.out.println("   - No hay rangos públicos");
        } else {
            rangos.forEach((juego, rango) -> System.out.printf("   - %s: %d\n", juego, rango));
        }

        System.out.println("=".repeat(60));
    }

    // ========== EDITAR RANGOS ==========

    /**
     * Solicita al usuario que seleccione un juego para editar el rango.
     */
    public Juego solicitarJuegoParaRango() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("            - EDITAR RANGO POR JUEGO");
        System.out.println("=".repeat(60));

        List<Juego> juegos = juegosRegistry.getJuegosDisponibles();

        System.out.println("Juegos disponibles:");
        for (int i = 0; i < juegos.size(); i++) {
            System.out.printf("%d. %s\n", i + 1, juegos.get(i).getNombre());
        }

        System.out.printf("\nSeleccione el juego (1-%d) o '0' para cancelar: ", juegos.size());

        try {
            int opcion = Integer.parseInt(scanner.nextLine().trim());

            if (opcion == 0) {
                return null; // Cancelar
            }

            if (opcion >= 1 && opcion <= juegos.size()) {
                return juegos.get(opcion - 1);
            } else {
                mostrarError("Opción inválida");
                return null;
            }
        } catch (NumberFormatException e) {
            mostrarError("Debe ingresar un número válido");
            return null;
        }
    }

    /**
     * Solicita el nuevo rango para el juego seleccionado.
     */
    public int solicitarNuevoRango(String nombreJuego, Integer rangoActual) {
        String rangoActualStr = rangoActual != null ? rangoActual.toString() : "No configurado";

        System.out.printf("\n- Juego: %s\n", nombreJuego);
        System.out.printf("- Rango actual: %s\n", rangoActualStr);
        System.out.print("\nIngrese el nuevo rango (1-100) o '0' para cancelar: ");

        try {
            int rango = Integer.parseInt(scanner.nextLine().trim());

            if (rango == 0) {
                return -1; // Cancelar
            }

            if (rango >= 1 && rango <= 100) {
                return rango;
            } else {
                mostrarError("El rango debe estar entre 1 y 100");
                return -1;
            }
        } catch (NumberFormatException e) {
            mostrarError("Debe ingresar un número válido");
            return -1;
        }
    }

    // ========== GESTIONAR ROLES ==========

    /**
     * Solicita al usuario que seleccione un juego para gestionar roles.
     */
    public Juego solicitarJuegoParaRoles() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("          - GESTIONAR ROLES POR JUEGO");
        System.out.println("=".repeat(60));

        List<Juego> juegos = juegosRegistry.getJuegosDisponibles();

        System.out.println("Juegos disponibles:");
        for (int i = 0; i < juegos.size(); i++) {
            System.out.printf("%d. %s\n", i + 1, juegos.get(i).getNombre());
        }

        System.out.printf("\nSeleccione el juego (1-%d) o '0' para cancelar: ", juegos.size());

        try {
            int opcion = Integer.parseInt(scanner.nextLine().trim());

            if (opcion == 0) {
                return null; // Cancelar
            }

            if (opcion >= 1 && opcion <= juegos.size()) {
                return juegos.get(opcion - 1);
            } else {
                mostrarError("Opción inválida");
                return null;
            }
        } catch (NumberFormatException e) {
            mostrarError("Debe ingresar un número válido");
            return null;
        }
    }

    /**
     * Muestra los roles actuales y permite seleccionar nuevos roles.
     */
    public List<String> solicitarNuevosRoles(Juego juego, List<String> rolesActuales) {
        System.out.printf("\n- Juego: %s\n", juego.getNombre());

        // Mostrar roles actuales
        if (rolesActuales.isEmpty()) {
            System.out.println("- Roles actuales: Ninguno configurado");
        } else {
            System.out.printf("- Roles actuales: %s\n", String.join(", ", rolesActuales));
        }

        // Mostrar roles disponibles
        System.out.println("\nRoles disponibles:");
        List<RolJuego> rolesDisponibles = juego.getRolesDisponibles();
        for (int i = 0; i < rolesDisponibles.size(); i++) {
            System.out.printf("%d. %s\n", i + 1, rolesDisponibles.get(i).getNombre());
        }

        System.out.println("\nSeleccione los roles preferidos:");
        System.out.println("• Ingrese los números separados por comas (ej: 1,3,5)");
        System.out.println("• Deje vacío para no cambiar");
        System.out.println("• Ingrese '0' para borrar todos los roles");
        System.out.print("\nOpción: ");

        String input = scanner.nextLine().trim();

        // No cambiar si está vacío
        if (input.isEmpty()) {
            return rolesActuales;
        }

        // Borrar todos si es '0'
        if (input.equals("0")) {
            return new ArrayList<>();
        }

        // Procesar selección múltiple
        List<String> nuevosRoles = new ArrayList<>();
        try {
            String[] numeros = input.split(",");
            for (String numero : numeros) {
                int indice = Integer.parseInt(numero.trim()) - 1;
                if (indice >= 0 && indice < rolesDisponibles.size()) {
                    String nombreRol = rolesDisponibles.get(indice).getNombre();
                    if (!nuevosRoles.contains(nombreRol)) {
                        nuevosRoles.add(nombreRol);
                    }
                }
            }
            return nuevosRoles;
        } catch (NumberFormatException e) {
            mostrarError("Formato inválido. Use números separados por comas");
            return rolesActuales;
        }
    }

    // ========== CAMBIAR REGIÓN ==========

    /**
     * Solicita una nueva región al usuario.
     */
    public String solicitarNuevaRegion(String regionActual) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("               - CAMBIAR REGIÓN");
        System.out.println("=".repeat(60));

        System.out.printf("- Región actual: %s\n", regionActual.isEmpty() ? "No configurada" : regionActual);

        System.out.println("\nRegiones disponibles:");
        System.out.println("1. LAS (Latinoamérica Sur)");
        System.out.println("2. LAN (Latinoamérica Norte)");
        System.out.println("3. NA (Norte América)");
        System.out.println("4. EUW (Europa Oeste)");
        System.out.println("5. EUNE (Europa Noreste)");
        System.out.println("6. BR (Brasil)");
        System.out.println("7. Otra (ingreso manual)");

        System.out.print("\nSeleccione una opción (1-7) o '0' para cancelar: ");

        String opcion = scanner.nextLine().trim();

        switch (opcion) {
            case "0":
                return null; // Cancelar
            case "1":
                return "LAS";
            case "2":
                return "LAN";
            case "3":
                return "NA";
            case "4":
                return "EUW";
            case "5":
                return "EUNE";
            case "6":
                return "BR";
            case "7":
                System.out.print("Ingrese la región manualmente: ");
                return scanner.nextLine().trim().toUpperCase();
            default:
                mostrarError("Opción inválida");
                return null;
        }
    }

    // ========== AJUSTAR LATENCIA ==========

    /**
     * Solicita nueva latencia promedio al usuario.
     */
    public int solicitarNuevaLatencia(int latenciaActual) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("             - AJUSTAR LATENCIA PROMEDIO");
        System.out.println("=".repeat(60));

        System.out.printf("- Latencia actual: %d ms\n", latenciaActual);

        System.out.println("\nReferencia de latencia:");
        System.out.println("• 0-30 ms: Excelente");
        System.out.println("• 31-60 ms: Buena");
        System.out.println("• 61-100 ms: Regular");
        System.out.println("• 101+ ms: Alta");

        System.out.print("\nIngrese la nueva latencia en ms (0-500) o '0' para cancelar: ");

        try {
            int latencia = Integer.parseInt(scanner.nextLine().trim());

            if (latencia == 0) {
                return -1; // Cancelar
            }

            if (latencia >= 1 && latencia <= 500) {
                return latencia;
            } else {
                mostrarError("La latencia debe estar entre 1 y 500 ms");
                return -1;
            }
        } catch (NumberFormatException e) {
            mostrarError("Debe ingresar un número válido");
            return -1;
        }
    }

    // ========== VER PERFIL DE OTRO USUARIO ==========

    /**
     * Solicita el username del usuario a buscar.
     */
    public String solicitarUsernameUsuario() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("            - VER PERFIL DE OTRO USUARIO");
        System.out.println("=".repeat(60));
        System.out.print("\n- Ingrese el username del usuario: ");
        return scanner.nextLine().trim();
    }

    // ========== CONFIRMACIONES ==========

    /**
     * Solicita confirmación para guardar cambios.
     */
    public boolean confirmarCambios(String mensaje) {
        System.out.printf("\n%s\n", mensaje);
        System.out.print("¿Confirma los cambios? (s/n): ");
        String respuesta = scanner.nextLine().trim().toLowerCase();
        return respuesta.equals("s") || respuesta.equals("si") || respuesta.equals("sí");
    }

    // ========== MENSAJES ==========

    /**
     * Muestra un mensaje de éxito.
     */
    public void mostrarExito(String mensaje) {
        System.out.println("\n- " + mensaje);
    }

    /**
     * Muestra un mensaje de error.
     */
    public void mostrarError(String mensaje) {
        System.err.println("\n- " + mensaje);
    }

    /**
     * Muestra un mensaje informativo.
     */
    public void mostrarInfo(String mensaje) {
        System.out.println("\n- " + mensaje);
    }

    /**
     * Pausa para que el usuario pueda leer la información.
     */
    public void pausaParaContinuar() {
        System.out.print("\nPresione Enter para continuar...");
        scanner.nextLine();
    }
}