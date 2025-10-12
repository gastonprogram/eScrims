package model;

import java.util.Date;
import java.util.Map;

import model.utils.PasswordHasher;
import model.utils.Rol;

public class Usuario {

    private int id;
    private String username;
    private String email;
    private String hashedPassword;
    private String salt;

    private Map<String, Integer> rangoPorJuego;
    private Rol rol;
    private String region;

    private boolean disponible;

    // estado mail

    private Date createdAt;
    private Date updatedAt;

    // Constructor
    public Usuario(String username, String email, String password) {
        this.username = username;
        this.email = email;
        setPassword(password);
        this.rol = Rol.USER;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    public void setPassword(String password) {
        this.salt = PasswordHasher.generateSalt();
        this.hashedPassword = PasswordHasher.hashPassword(password, this.salt);
        this.updatedAt = new Date();
    }

    public boolean verifyPassword(String password) {
        return PasswordHasher.verifyPassword(password, this.hashedPassword, this.salt);
    }

    // Getters y setters

    // No incluir setter público para hashedPassword y salt
    // Solo deben modificarse a través de setPassword()
    public String getSalt() {
        return salt;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    // getters y setters

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
        this.updatedAt = new Date();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        this.updatedAt = new Date();
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public boolean esAdmin() {
        return rol == Rol.ADMIN;
    }

    public boolean esModerador() {
        return rol == Rol.MOD;
    }

    public boolean esUsuario() {
        return rol == Rol.USER;
    }

}