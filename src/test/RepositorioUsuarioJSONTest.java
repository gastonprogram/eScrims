import model.Persistencia.RepositorioFactory;
import model.Persistencia.RepositorioUsuario;
import model.Usuario;

import java.util.List;

/**
 * Clase de prueba simple para el RepositorioUsuarioJSON.
 * Ejecuta pruebas básicas de CRUD sin necesidad de JUnit.
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
        
        // Actualizar el usuario
        usuario.setRangoPrincipal("Oro III");
        usuario.setJuegoPrincipal("League of Legends");
        repositorio.guardar(usuario);
        
        // Buscar el usuario actualizado
        Usuario actualizado = repositorio.buscarPorEmail(TEST_EMAIL);
        
        // Verificar que los cambios se guardaron
        if (actualizado == null) {
            throw new AssertionError("El usuario no debería ser nulo");
        }
        if (!"Oro III".equals(actualizado.getRangoPrincipal())) {
            throw new AssertionError("El rango no se actualizó correctamente");
        }
        if (!"League of Legends".equals(actualizado.getJuegoPrincipal())) {
            throw new AssertionError("El juego principal no se actualizó correctamente");
        }
        
        System.out.println("✓ Prueba de actualización completada con éxito");
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
        
        if (!encontrado1) throw new AssertionError("Falta el usuario 1 en la lista");
        if (!encontrado2) throw new AssertionError("Falta el usuario 2 en la lista");
        
        System.out.println("✓ Prueba de listado completada con éxito");
    }
}
