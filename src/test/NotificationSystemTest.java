import model.*;
import model.notifications.core.NotificationService;
import model.notifications.types.*;
import model.notifications.channels.NotificationChannel;
import model.notifications.factory.NotificationChannelFactory;
import model.utils.ScrimFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Test básico para verificar el sistema de notificaciones.
 * Demuestra cómo funciona el sistema Observer + Abstract Factory/Adapter.
 */
public class NotificationSystemTest {

    public static void main(String[] args) {
        System.out.println("=== Test del Sistema de Notificaciones ===\n");
        
        // 1. Crear usuarios con preferencias de notificación
        System.out.println("1. Creando usuarios con preferencias...");
        Usuario usuario1 = new Usuario("jugador1", "jugador1@example.com", "password123");
        usuario1.addPreferredChannel(ChannelType.EMAIL, "jugador1@example.com");
        usuario1.addPreferredChannel(ChannelType.DISCORD, "123456789");
        System.out.println("   - Usuario1 creado con canales: EMAIL, DISCORD");
        
        Usuario usuario2 = new Usuario("jugador2", "jugador2@example.com", "password456");
        usuario2.addPreferredChannel(ChannelType.EMAIL, "jugador2@example.com");
        usuario2.subscribeToEvent(NotificationEvent.LOBBY_ARMADO);
        usuario2.subscribeToEvent(NotificationEvent.CONFIRMADO_TODOS);
        System.out.println("   - Usuario2 creado con canal: EMAIL");
        System.out.println("   - Suscrito a: LOBBY_ARMADO, CONFIRMADO_TODOS\n");
        
        // 2. Crear un scrim
        System.out.println("2. Creando un scrim...");
        ScrimFormat formato = new ScrimFormat(5); // 5v5
        Scrim scrim = new ScrimBuilder()
            .withJuego("League of Legends")
            .withFormato(formato)
            .withFechaHora(LocalDateTime.now().plusHours(2))
            .withRango(1000, 2000)
            .withLatenciaMaxima(50)
            .build();
        
        scrim.setCreatedBy("jugador1");
        System.out.println("   - Scrim creado: " + scrim.getJuego());
        System.out.println("   - Estado inicial: " + scrim.getEstado());
        System.out.println("   - Notificación de scrim creado enviada\n");
        
        // 3. Simular postulaciones hasta llenar el lobby
        System.out.println("3. Simulando postulaciones...");
        for (int i = 1; i <= 10; i++) {
            scrim.postular("jugador" + i);
            System.out.println("   - Postulación " + i + "/10");
        }
        System.out.println("   - Estado actual: " + scrim.getEstado());
        System.out.println("   - Notificación LOBBY_ARMADO enviada\n");
        
        // 4. Simular confirmaciones
        System.out.println("4. Simulando confirmaciones...");
        for (int i = 1; i <= 10; i++) {
            scrim.confirmar("jugador" + i);
            System.out.println("   - Confirmación " + i + "/10");
        }
        System.out.println("   - Estado actual: " + scrim.getEstado());
        System.out.println("   - Notificación CONFIRMADO_TODOS enviada\n");
        
        // 5. Iniciar el scrim
        System.out.println("5. Iniciando el scrim...");
        scrim.iniciar();
        System.out.println("   - Estado actual: " + scrim.getEstado());
        System.out.println("   - Notificación EN_JUEGO enviada\n");
        
        // 6. Finalizar el scrim
        System.out.println("6. Finalizando el scrim...");
        scrim.finalizar();
        System.out.println("   - Estado actual: " + scrim.getEstado());
        System.out.println("   - Notificación FINALIZADO enviada\n");
        
        // 7. Probar notificación manual
        System.out.println("7. Probando notificación manual...");
        NotificationService notifService = NotificationService.getInstance();
        Notification testNotif = new Notification(
            NotificationEvent.SCRIM_CREATED_MATCH,
            scrim,
            "jugador1",
            "Test de Notificación",
            "Este es un mensaje de prueba del sistema de notificaciones"
        );
        
        // Probar cada tipo de canal
        NotificationChannelFactory factory = NotificationChannelFactory.getDefaultFactory();
        
        System.out.println("   - Probando canal PUSH:");
        NotificationChannel pushChannel = factory.createChannel(ChannelType.PUSH);
        pushChannel.send(testNotif, "fake-fcm-token");
        
        System.out.println("   - Probando canal EMAIL:");
        NotificationChannel emailChannel = factory.createChannel(ChannelType.EMAIL);
        emailChannel.send(testNotif, "test@example.com");
        
        System.out.println("   - Probando canal DISCORD:");
        NotificationChannel discordChannel = factory.createChannel(ChannelType.DISCORD);
        discordChannel.send(testNotif, "987654321");
        
        System.out.println("   - Probando canal SLACK:");
        NotificationChannel slackChannel = factory.createChannel(ChannelType.SLACK);
        slackChannel.send(testNotif, "U123456");
        
        System.out.println("\n=== Test completado ===");
        System.out.println("\nNOTA: Las notificaciones actuales son MOCK.");
        System.out.println("Para usar notificaciones reales, configura las siguientes variables de entorno:");
        System.out.println("  - FIREBASE_SERVER_KEY (para Push)");
        System.out.println("  - SENDGRID_API_KEY o SMTP_HOST/SMTP_USER/SMTP_PASSWORD (para Email)");
        System.out.println("  - DISCORD_WEBHOOK_URL (para Discord)");
        System.out.println("  - SLACK_WEBHOOK_URL (para Slack)");
    }
}
