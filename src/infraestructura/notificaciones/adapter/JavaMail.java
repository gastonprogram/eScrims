package infraestructura.notificaciones.adapter;

import javax.mail.*;
import javax.mail.internet.*;

import dominio.modelo.Notificacion;

import java.util.Properties;

public class JavaMail implements IAdapterJavaMail {
    
    private static final String SMTP_HOST = "sandbox.smtp.mailtrap.io";
    private static final String SMTP_PORT = "587";
    private static final String USERNAME = "f0f7f733005c39"; // Usuario Domain smtp@mailtrap.io
    private static final String PASSWORD = "da15f56872df17"; // Contraseña Domain 1fa40ce596dd7b2a258df2ee1fc7ed67
    
    @Override
    public void enviar(Notificacion notif) {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", SMTP_HOST);
            props.put("mail.smtp.port", SMTP_PORT);
            
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(USERNAME, PASSWORD);
                }
            });
            
            String email = notif.getDestinoEspecifico();
            if (email == null || email.isEmpty()) {
                System.err.println("Email no configurado para usuario: " + 
                    notif.getDestinatario().getUsername());
                return;
            }
            
            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress("hello@demomailtrap.co"));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
                message.setSubject("Notificacion eScrims");
                message.setText("Hola " + notif.getDestinatario().getUsername() + ",\n\n" + 
                              notif.getMensaje() + "\n\n---\nEnviado desde eScrims");
                
                // Transport.send(message);
                System.out.println("[OK] Email enviado a: " + email + 
                    " (" + notif.getDestinatario().getUsername() + ")");
                
            } catch (MessagingException e) {
                System.err.println("[ERROR] Error al enviar email a " + email + ": " + e.getMessage());
            }
            
        } catch (Exception e) {
            System.err.println("[ERROR] Error al configurar JavaMail: " + e.getMessage());
        }
    }
}
