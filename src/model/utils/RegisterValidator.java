package model.utils;

import model.Usuario;
import java.util.Map;
import java.util.regex.Pattern;

public class RegisterValidator {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    // Validar todos los datos de registro
    public static void validarDatosRegistro(
            String username, String email, String password, String juegoPrincipal, int rango,
            Map<String, Usuario> usuarios) {

        validarUsername(username);
        validarEmail(email);
        validarPassword(password);
        validarJuegoPrincipal(juegoPrincipal);
        validarRango(rango);
        verificarDisponibilidad(username, email, usuarios);
    }

    private static void validarUsername(String username) {
        if (username == null || username.trim().isEmpty())
            throw new IllegalArgumentException("El username es requerido");
        if (username.trim().length() < 3)
            throw new IllegalArgumentException("El username debe tener al menos 3 caracteres");
        if (username.trim().length() > 20)
            throw new IllegalArgumentException("El username no puede tener más de 20 caracteres");
        if (!username.trim().matches("^[a-zA-Z0-9_]+$"))
            throw new IllegalArgumentException("El username solo puede contener letras, números y guiones bajos");
    }

    private static void validarEmail(String email) {
        if (email == null || email.trim().isEmpty())
            throw new IllegalArgumentException("El email es requerido");
        if (!EMAIL_PATTERN.matcher(email.trim()).matches())
            throw new IllegalArgumentException("El formato del email no es válido");
    }

    private static void validarPassword(String password) {
        if (password == null || password.isEmpty())
            throw new IllegalArgumentException("La contraseña es requerida");
        if (password.length() < 6)
            throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres");
        if (password.length() > 50)
            throw new IllegalArgumentException("La contraseña no puede tener más de 50 caracteres");
        if (!tieneCaracteresSeguroPassword(password))
            throw new IllegalArgumentException("La contraseña debe contener al menos una letra y un número");
    }

    private static boolean tieneCaracteresSeguroPassword(String password) {
        boolean tieneLetra = password.chars().anyMatch(Character::isLetter);
        boolean tieneNumero = password.chars().anyMatch(Character::isDigit);
        return tieneLetra && tieneNumero;
    }

    private static void validarJuegoPrincipal(String juego) {
        if (juego == null || juego.trim().isEmpty())
            throw new IllegalArgumentException("El juego principal es requerido");
        if (juego.trim().length() > 50)
            throw new IllegalArgumentException("El nombre del juego es demasiado largo");
    }

    private static void validarRango(int rango) {
        if (rango < 1 || rango > 100)
            throw new IllegalArgumentException("El rango debe estar entre 1 y 100");
    }

    private static void verificarDisponibilidad(String username, String email, Map<String, Usuario> usuarios) {
        if (usuarios.containsKey(username.trim()))
            throw new IllegalArgumentException("El username ya está en uso");
        boolean emailYaExiste = usuarios.values().stream()
                .anyMatch(usuario -> usuario.getEmail().equalsIgnoreCase(email.trim()));
        if (emailYaExiste)
            throw new IllegalArgumentException("El email ya está registrado");
    }
}
