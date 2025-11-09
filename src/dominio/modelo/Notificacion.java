package dominio.modelo;

public class Notificacion {
    private Usuario destinatario;
    private String mensaje;
    private String destinoEspecifico; // email, discordId o fcmToken según el canal
    
    public Notificacion(Usuario destinatario, String mensaje, String destinoEspecifico) {
        this.destinatario = destinatario;
        this.mensaje = mensaje;
        this.destinoEspecifico = destinoEspecifico;
    }
    
    public Usuario getDestinatario() {
        return destinatario;
    }
    
    public void setDestinatario(Usuario destinatario) {
        this.destinatario = destinatario;
    }
    
    public String getMensaje() {
        return mensaje;
    }
    
    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
    
    public String getDestinoEspecifico() {
        return destinoEspecifico;
    }
    
    public void setDestinoEspecifico(String destinoEspecifico) {
        this.destinoEspecifico = destinoEspecifico;
    }
    
    @Override
    public String toString() {
        return "Notificacion{" +
                "destinatario=" + destinatario.getUsername() +
                ", mensaje='" + mensaje + '\'' +
                ", destino='" + destinoEspecifico + '\'' +
                '}';
    }
}
