package test;

import java.util.Arrays;
import java.util.List;

import dominio.juegos.Juego;
import dominio.juegos.LeagueOfLegends;
import dominio.modelo.Usuario;
import infraestructura.persistencia.repository.RepositorioFactory;
import infraestructura.persistencia.repository.RepositorioUsuario;

/**
 * Clase de prueba para el RepositorioUsuarioJSON.
 * Ejecuta pruebas de CRUD y funcionalidades del perfil de usuario sin necesidad
 * de JUnit.
 * 
 * Pruebas incluidas:
 * - Guardar y buscar usuario
 * - Actualizar perfil (juego, rango, roles, región, disponibilidad)
 * - Gestión de roles por juego
 * - Eliminar usuario
 * - Listar usuarios
 */
public class RepositorioUsuarioJSONTest {

    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_USERNAME = "testuser";
    private static RepositorioUsuario repositorio;

    public static void main(String[] args) {
        System.out.println("=== Iniciando pruebas de RepositorioUsuarioJSON ===\n");

        // Configurar el repositorio para usar un archivo de prueba
        System.setProperty("usuario.json.file", "test_usuarios.json");
        repositorio = RepositorioFactory.getRepositorioUsuario();

        try {
            testGuardarYBuscarUsuario();
            testActualizarUsuario();
            testRolesPorJuego();
            testEliminarUsuario();
            testListarUsuarios();

            System.out.println("\n=== Todas las pruebas se completaron exitosamente ===");
        } catch (Exception e) {
            System.err.println("Error en las pruebas: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Limpiar el archivo de prueba al finalizar
            java.io.File file = new java.io.File("test_usuarios.json");
            if (file.exists()) {
                file.delete();
            }
        }
    }

    private static void testGuardarYBuscarUsuario() {
        System.out.println("Probando guardar y buscar usuario...");

        // Crear y guardar un usuario
        Usuario usuario = new Usuario(TEST_USERNAME, TEST_EMAIL, "password123");
        repositorio.guardar(usuario);

        // Buscar el usuario por email
        Usuario encontrado = repositorio.buscarPorEmail(TEST_EMAIL);

        // Verificar que los datos coinciden
        if (encontrado == null) {
            throw new AssertionError("El usuario no debería ser nulo");
        }
        if (!TEST_EMAIL.equals(encontrado.getEmail())) {
            throw new AssertionError("El email no coincide");
        }
        if (!TEST_USERNAME.equals(encontrado.getUsername())) {
            throw new AssertionError("El nombre de usuario no coincide");
        }

        System.out.println("✓ Prueba de guardar y buscar completada con éxito");
    }

    private static void testActualizarUsuario() {
        System.out.println("\nProbando actualización de usuario...");

        // Crear y guardar un usuario
        Usuario usuario = new Usuario(TEST_USERNAME, TEST_EMAIL, "password123");
        repositorio.guardar(usuario);

        // Actualizar el usuario con la nueva estructura
        Juego lol = LeagueOfLegends.getInstance();
        usuario.setJuegoPrincipal(lol);
        usuario.setRangoPrincipal("1800"); // Rango numérico
        usuario.setRolesPreferidos(Arrays.asList("TOP", "MID", "JUNGLE"));
        usuario.setRegion("LAS");
        usuario.setDisponibilidad("18:00-23:00");
        repositorio.guardar(usuario);

        // Buscar el usuario actualizado
        Usuario actualizado = repositorio.buscarPorEmail(TEST_EMAIL);

        // Verificar que los cambios se guardaron
        if (actualizado == null) {
            throw new AssertionError("El usuario no debería ser nulo");
        }
        if (!"1800".equals(actualizado.getRangoPrincipal())) {
            throw new AssertionError("El rango no se actualizó correctamente. Esperado: 1800, Obtenido: "
                    + actualizado.getRangoPrincipal());
        }
        if (actualizado.getJuegoPrincipal() == null) {
            throw new AssertionError("El juego principal no debería ser nulo");
        }
        if (!"League of Legends".equals(actualizado.getJuegoPrincipalNombre())) {
            throw new AssertionError(
                    "El juego principal no se actualizó correctamente. Esperado: League of Legends, Obtenido: "
                            + actualizado.getJuegoPrincipalNombre());
        }
        if (actualizado.getRolesPreferidos().size() != 3) {
            throw new AssertionError(
                    "Debería tener 3 roles preferidos. Obtenido: " + actualizado.getRolesPreferidos().size());
        }
        if (!"LAS".equals(actualizado.getRegion())) {
            throw new AssertionError("La región no se actualizó correctamente");
        }
        if (!"18:00-23:00".equals(actualizado.getDisponibilidad())) {
            throw new AssertionError("La disponibilidad no se actualizó correctamente");
        }

        System.out.println("✓ Prueba de actualización completada con éxito");
        System.out.println("  - Rango: " + actualizado.getRangoPrincipal());
        System.out.println("  - Juego: " + actualizado.getJuegoPrincipalNombre());
        System.out.println("  - Roles: " + String.join(", ", actualizado.getRolesPreferidos()));
        System.out.println("  - Región: " + actualizado.getRegion());
        System.out.println("  - Disponibilidad: " + actualizado.getDisponibilidad());
    }

    private static void testRolesPorJuego() {
        System.out.println("\nProbando roles por juego...");

        // Crear usuario
        Usuario usuario = new Usuario("multiGameUser", "multigame@example.com", "pass123");

        // Configurar juego principal y roles
        Juego lol = LeagueOfLegends.getInstance();
        usuario.setJuegoPrincipal(lol);
        usuario.setRolesPreferidos(Arrays.asList("TOP", "JUNGLE"));

        // Agregar roles para otro juego (simulando que en el futuro habrá más juegos)
        usuario.agregarRolPreferido(lol, "MID");

        // Guardar
        repositorio.guardar(usuario);

        // Recuperar y verificar
        Usuario recuperado = repositorio.buscarPorEmail("multigame@example.com");

        if (recuperado == null) {
            throw new AssertionError("El usuario no debería ser nulo");
        }

        List<String> roles = recuperado.getRolesPreferidos();
        if (roles.size() < 2) {
            throw new AssertionError("Debería tener al menos 2 roles. Obtenido: " + roles.size());
        }

        if (!roles.contains("TOP") || !roles.contains("JUNGLE")) {
            throw new AssertionError("Debería contener los roles TOP y JUNGLE");
        }

        // Verificar que puede obtener roles por juego específico
        List<String> rolesLol = recuperado.getRolesPreferidosParaJuego("League of Legends");
        if (rolesLol.isEmpty()) {
            throw new AssertionError("Debería tener roles para League of Legends");
        }

        System.out.println("✓ Prueba de roles por juego completada con éxito");
        System.out.println("  - Roles actuales: " + String.join(", ", roles));
        System.out.println("  - Roles para LoL: " + String.join(", ", rolesLol));

        // Limpiar
        repositorio.eliminar("multigame@example.com");
    }

    private static void testEliminarUsuario() {
        System.out.println("\nProbando eliminación de usuario...");

        // Crear y guardar un usuario
        Usuario usuario = new Usuario(TEST_USERNAME, TEST_EMAIL, "password123");
        repositorio.guardar(usuario);

        // Verificar que el usuario existe
        if (!repositorio.existeEmail(TEST_EMAIL)) {
            throw new AssertionError("El usuario debería existir antes de eliminarlo");
        }

        // Eliminar el usuario
        boolean eliminado = repositorio.eliminar(TEST_EMAIL);

        // Verificar que se eliminó correctamente
        if (!eliminado) {
            throw new AssertionError("El usuario debería haberse eliminado correctamente");
        }
        if (repositorio.existeEmail(TEST_EMAIL)) {
            throw new AssertionError("El usuario no debería existir después de eliminarlo");
        }

        System.out.println("✓ Prueba de eliminación completada con éxito");
    }

    private static void testListarUsuarios() {
        System.out.println("\nProbando listado de usuarios...");

        // Crear y guardar varios usuarios
        Usuario usuario1 = new Usuario("user1", "user1@example.com", "pass1");
        Usuario usuario2 = new Usuario("user2", "user2@example.com", "pass2");

        repositorio.guardar(usuario1);
        repositorio.guardar(usuario2);

        // Obtener la lista de usuarios
        List<Usuario> usuarios = repositorio.listarTodos();

        // Verificar que la lista contiene los usuarios esperados
        if (usuarios.isEmpty()) {
            throw new AssertionError("La lista de usuarios no debería estar vacía");
        }

        boolean encontrado1 = false;
        boolean encontrado2 = false;

        for (Usuario u : usuarios) {
            if ("user1@example.com".equals(u.getEmail())) {
                encontrado1 = true;
            }
            if ("user2@example.com".equals(u.getEmail())) {
                encontrado2 = true;
            }
        }

        if (!encontrado1)
            throw new AssertionError("Falta el usuario 1 en la lista");
        if (!encontrado2)
            throw new AssertionError("Falta el usuario 2 en la lista");

        System.out.println("✓ Prueba de listado completada con éxito");
    }
}
