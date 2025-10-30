package controller;

import model.Usuario;
import model.utils.RegisterValidator;
import java.util.Map;

public class RegisterController {
    private Map<String, Usuario> usuarios;

    public RegisterController(Map<String, Usuario> usuarios) {
        this.usuarios = usuarios;
    }

    public boolean registrarUsuario(String username, String email, String password,
            String juegoPrincipal, int rango) {
        try {
            RegisterValidator.validarDatosRegistro(username, email, password, juegoPrincipal, rango, usuarios);

            // crear y guardar el nuevo usuario
            Usuario nuevoUsuario = new Usuario(username.trim(), email.trim(), password);
            usuarios.put(username.trim(), nuevoUsuario);

            return true;

        } catch (Exception e) {
            throw new RuntimeException("Error en el registro: " + e.getMessage());
        }
    }
}
