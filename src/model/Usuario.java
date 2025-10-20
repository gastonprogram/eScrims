package model;

import java.util.Date;
import java.util.List;
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

    private String rangoPrincipal; // Para guardar el rango principal editable (ej: "Oro IV")
    private List<String> rolesPreferidos;
    private String juegoPrincipal; // Juego principal editable
    private String disponibilidad; // HH:MM-HH:MM

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
        this.rangoPrincipal = "Sin Rango";
        this.rolesPreferidos = new java.util.ArrayList<>();
        this.juegoPrincipal = "No definido";
        this.region = "N/A";
        this.disponibilidad = "00:00-23:59";
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getRangoPrincipal() {
        return rangoPrincipal;
    }

    public void setRangoPrincipal(String rangoPrincipal) {
        this.rangoPrincipal = rangoPrincipal;
    }

    public List<String> getRolesPreferidos() {
        return rolesPreferidos;
    }

    public void setRolesPreferidos(List<String> rolesPreferidos) {
        this.rolesPreferidos = rolesPreferidos;
    }

    public String getJuegoPrincipal() {
        return juegoPrincipal;
    }

    public void setJuegoPrincipal(String juegoPrincipal) {
        this.juegoPrincipal = juegoPrincipal;
    }

    public String getDisponibilidad() {
        return disponibilidad;
    }

    public void setDisponibilidad(String disponibilidad) {
        this.disponibilidad = disponibilidad;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Map<String, Integer> getRangoPorJuego() {
        return rangoPorJuego;
    }

    public void setRangoPorJuego(Map<String, Integer> rangoPorJuego) {
        this.rangoPorJuego = rangoPorJuego;
    }

    public String getRango(String juego) {
        return rangoPorJuego.getOrDefault(juego, 0).toString();
    }

}