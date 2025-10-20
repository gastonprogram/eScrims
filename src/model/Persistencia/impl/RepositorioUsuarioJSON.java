package model.Persistencia.impl;

import model.Persistencia.RepositorioUsuario;
import model.Usuario;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Implementación de RepositorioUsuario que guarda los datos en un archivo JSON.
 */
public class RepositorioUsuarioJSON implements RepositorioUsuario {
    
    private static final String ARCHIVO_JSON = "data/usuarios.json";
    private final Gson gson;
    private List<Usuario> usuarios;
    
    public RepositorioUsuarioJSON() {
        this.gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();
        this.usuarios = cargarUsuarios();
    }
    
    private List<Usuario> cargarUsuarios() {
        try {
            // Crear el directorio si no existe
            Path dir = Paths.get("data");
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
            }
            
            // Si el archivo no existe, devolver lista vacía
            if (!Files.exists(Paths.get(ARCHIVO_JSON))) {
                return new ArrayList<>();
            }
            
            try (FileReader reader = new FileReader(ARCHIVO_JSON)) {
                Type tipoListaUsuarios = new TypeToken<List<Usuario>>() {}.getType();
                List<Usuario> usuariosCargados = gson.fromJson(reader, tipoListaUsuarios);
                return usuariosCargados != null ? usuariosCargados : new ArrayList<>();
            }
        } catch (IOException e) {
            System.err.println("Error al cargar usuarios: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    private void guardarUsuarios() {
        try (FileWriter writer = new FileWriter(ARCHIVO_JSON)) {
            gson.toJson(usuarios, writer);
        } catch (IOException e) {
            System.err.println("Error al guardar usuarios: " + e.getMessage());
        }
    }
    
    @Override
    public void guardar(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo");
        }
        
        // Si el usuario ya existe (mismo email), actualizarlo
        for (int i = 0; i < usuarios.size(); i++) {
            if (usuarios.get(i).getEmail().equals(usuario.getEmail())) {
                usuarios.set(i, usuario);
                guardarUsuarios();
                return;
            }
        }
        
        // Si no existe, agregarlo
        usuarios.add(usuario);
        guardarUsuarios();
    }
    
    @Override
    public Usuario buscarPorEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }
        return usuarios.stream()
            .filter(u -> email.equalsIgnoreCase(u.getEmail()))
            .findFirst()
            .orElse(null);
    }
    
    @Override
    public Usuario buscarPorUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return null;
        }
        return usuarios.stream()
            .filter(u -> username.equalsIgnoreCase(u.getUsername()))
            .findFirst()
            .orElse(null);
    }
    
    @Override
    public List<Usuario> listarTodos() {
        return new ArrayList<>(usuarios);
    }
    
    @Override
    public boolean eliminar(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        boolean eliminado = usuarios.removeIf(u -> email.equalsIgnoreCase(u.getEmail()));
        if (eliminado) {
            guardarUsuarios();
        }
        return eliminado;
    }
    
    @Override
    public boolean existeEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return usuarios.stream()
            .anyMatch(u -> email.equalsIgnoreCase(u.getEmail()));
    }
    
    @Override
    public boolean existeUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        return usuarios.stream()
            .anyMatch(u -> username.equalsIgnoreCase(u.getUsername()));
    }
}
