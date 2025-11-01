package CasosDeUso;

import model.*;
import model.notifications.core.NotificationService;
import model.notifications.types.*;
import model.notifications.channels.NotificationChannel;
import model.notifications.factory.NotificationChannelFactory;
import model.utils.ScrimFormat;
import java.time.LocalDateTime;

/**
 * Ejemplos de uso del sistema de notificaciones.
 */
public class NotificationExamples {

    /**
     * Ejemplo 1: Configurar preferencias de notificación de un usuario
     */
    public static void ejemplo1_ConfigurarPreferencias() {
        System.out.println("=== Ejemplo 1: Configurar Preferencias ===\n");
        
        Usuario usuario = new Usuario("jugador123", "jugador@example.com", "password");
        
        // Por defecto, el usuario está suscrito a TODOS los eventos
        // y solo tiene configurado el canal EMAIL con su email
        
        // Cambiar preferencias: solo notificar en eventos importantes
        usuario.unsubscribeFromAllEvents();
        usuario.subscribeToEvent(NotificationEvent.LOBBY_ARMADO);
        usuario.subscribeToEvent(NotificationEvent.CONFIRMADO_TODOS);
        usuario.subscribeToEvent(NotificationEvent.EN_JUEGO);
        
        // Agregar más canales
        usuario.addPreferredChannel(ChannelType.DISCORD, "123456789012345678");
        usuario.addPreferredChannel(ChannelType.PUSH, "fcm-token-123abc");
        
        System.out.println("Usuario configurado con:");
        System.out.println("  Eventos: " + usuario.getSubscribedEvents());
        System.out.println("  Canales: " + usuario.getPreferredChannels());
    }
    
    /**
     * Ejemplo 2: Crear un scrim (dispara notificación automática)
     */
    public static void ejemplo2_CrearScrim() {
        System.out.println("\n=== Ejemplo 2: Crear Scrim ===\n");
        
        // Al crear un scrim, se notifica automáticamente a usuarios interesados
        Scrim scrim = new ScrimBuilder()
            .withJuego("Valorant")
            .withFormato(new ScrimFormat(5)) // 5v5
            .withFechaHora(LocalDateTime.now().plusHours(3))
            .withRango(1500, 2500)
            .withLatenciaMaxima(60)
            .build();
        
        System.out.println("Scrim creado: " + scrim.getId());
        System.out.println("Estado: " + scrim.getEstado());
        System.out.println("→ Notificación SCRIM_CREATED_MATCH enviada a usuarios matching");
    }
    
    /**
     * Ejemplo 3: Flujo completo de un scrim
     */
    public static void ejemplo3_FlujoCompleto() {
        System.out.println("\n=== Ejemplo 3: Flujo Completo ===\n");
        
        // Crear scrim
        Scrim scrim = new ScrimBuilder()
            .withJuego("League of Legends")
            .withFormato(new ScrimFormat(5))
            .withFechaHora(LocalDateTime.now().plusHours(2))
            .build();
        
        System.out.println("1. Scrim creado");
        
        // Postulaciones
        for (int i = 1; i <= 10; i++) {
            scrim.postular("jugador" + i);
        }
        System.out.println("2. Lobby lleno → Notificación LOBBY_ARMADO");
        
        // Confirmaciones
        for (int i = 1; i <= 10; i++) {
            scrim.confirmar("jugador" + i);
        }
        System.out.println("3. Todos confirmaron → Notificación CONFIRMADO_TODOS");
        
        // Iniciar
        scrim.iniciar();
        System.out.println("4. Juego iniciado → Notificación EN_JUEGO");
        
        // Finalizar
        scrim.finalizar();
        System.out.println("5. Juego finalizado → Notificación FINALIZADO");
    }
    
    /**
     * Ejemplo 4: Cancelar un scrim
     */
    public static void ejemplo4_Cancelar() {
        System.out.println("\n=== Ejemplo 4: Cancelar Scrim ===\n");
        
        Scrim scrim = new ScrimBuilder()
            .withJuego("CS:GO")
            .withFormato(new ScrimFormat(5))
            .withFechaHora(LocalDateTime.now().plusDays(1))
            .build();
        
        // Algunas postulaciones
        scrim.postular("jugador1");
        scrim.postular("jugador2");
        scrim.postular("jugador3");
        
        // Cancelar
        scrim.cancelar();
        System.out.println("Scrim cancelado → Notificación CANCELADO enviada a postulantes");
    }
    
    /**
     * Ejemplo 5: Uso avanzado - diferentes perfiles de notificación
     */
    public static void ejemplo5_PerfilesNotificacion() {
        System.out.println("\n=== Ejemplo 5: Perfiles de Notificación ===\n");
        
        // Perfil "Casual": Solo notificaciones críticas por email
        Usuario casual = new Usuario("casual_player", "casual@example.com", "pass");
        casual.unsubscribeFromAllEvents();
        casual.subscribeToEvent(NotificationEvent.LOBBY_ARMADO);
        casual.subscribeToEvent(NotificationEvent.CANCELADO);
        casual.removePreferredChannel(ChannelType.EMAIL);
        casual.addPreferredChannel(ChannelType.EMAIL, "casual@example.com");
        System.out.println("Perfil Casual: eventos mínimos, solo email");
        
        // Perfil "Competitivo": Todas las notificaciones, múltiples canales
        Usuario competitivo = new Usuario("pro_player", "pro@example.com", "pass");
        competitivo.subscribeToAllEvents();
        competitivo.addPreferredChannel(ChannelType.EMAIL, "pro@example.com");
        competitivo.addPreferredChannel(ChannelType.PUSH, "fcm-token-pro");
        competitivo.addPreferredChannel(ChannelType.DISCORD, "999888777666555444");
        System.out.println("Perfil Competitivo: todos los eventos, todos los canales");
        
        // Perfil "Solo Discord": Todo por Discord
        Usuario discorder = new Usuario("discord_user", "discord@example.com", "pass");
        discorder.subscribeToAllEvents();
        discorder.removePreferredChannel(ChannelType.EMAIL);
        discorder.addPreferredChannel(ChannelType.DISCORD, "111222333444555666");
        System.out.println("Perfil Discord: todos los eventos, solo Discord");
    }
    
    /**
     * Ejemplo 6: Envío manual de notificaciones (caso especial)
     */
    public static void ejemplo6_EnvioManual() {
        System.out.println("\n=== Ejemplo 6: Envío Manual ===\n");
        
        // Crear scrim para contexto
        Scrim scrim = new ScrimBuilder()
            .withJuego("Dota 2")
            .withFormato(new ScrimFormat(5))
            .withFechaHora(LocalDateTime.now().plusHours(1))
            .build();
        
        // Crear notificación custom
        Notification customNotif = new Notification(
            NotificationEvent.SCRIM_CREATED_MATCH,
            scrim,
            "admin",
            "Scrim Especial VIP",
            "Se ha creado un scrim especial para jugadores VIP. ¡No te lo pierdas!"
        );
        
        // Obtener factory y crear canal
        NotificationChannelFactory factory = NotificationChannelFactory.getDefaultFactory();
        NotificationChannel emailChannel = factory.createChannel(ChannelType.EMAIL);
        
        // Enviar manualmente
        boolean enviado = emailChannel.send(customNotif, "vip@example.com");
        System.out.println("Notificación custom enviada: " + enviado);
    }
    
    /**
     * Ejemplo 7: Verificar disponibilidad de canales
     */
    public static void ejemplo7_VerificarCanales() {
        System.out.println("\n=== Ejemplo 7: Verificar Canales ===\n");
        
        NotificationChannelFactory factory = NotificationChannelFactory.getDefaultFactory();
        
        ChannelType[] channels = ChannelType.values();
        for (ChannelType type : channels) {
            NotificationChannel channel = factory.createChannel(type);
            boolean disponible = channel.isAvailable();
            System.out.println(type + ": " + (disponible ? "✓ Disponible" : "✗ No configurado"));
        }
        
        System.out.println("\nPara habilitar canales, configura las variables de entorno:");
        System.out.println("Ver NOTIFICACIONES_CONFIG.md para instrucciones");
    }
    
    /**
     * Ejemplo 8: Testing de canales individuales
     */
    public static void ejemplo8_TestearCanales() {
        System.out.println("\n=== Ejemplo 8: Testear Canales ===\n");
        
        Scrim scrim = new ScrimBuilder()
            .withJuego("Test Game")
            .withFormato(new ScrimFormat(1))
            .withFechaHora(LocalDateTime.now())
            .build();
        
        Notification testNotif = new Notification(
            NotificationEvent.SCRIM_CREATED_MATCH,
            scrim,
            "tester",
            "Test de Notificación",
            "Este es un mensaje de prueba del sistema"
        );
        
        NotificationChannelFactory factory = NotificationChannelFactory.getDefaultFactory();
        
        // Test Push
        System.out.println("Testeando Push...");
        NotificationChannel pushChannel = factory.createChannel(ChannelType.PUSH);
        pushChannel.send(testNotif, "test-token");
        
        // Test Email
        System.out.println("Testeando Email...");
        NotificationChannel emailChannel = factory.createChannel(ChannelType.EMAIL);
        emailChannel.send(testNotif, "test@example.com");
        
        // Test Discord
        System.out.println("Testeando Discord...");
        NotificationChannel discordChannel = factory.createChannel(ChannelType.DISCORD);
        discordChannel.send(testNotif, "123456789");
        
        // Test Slack
        System.out.println("Testeando Slack...");
        NotificationChannel slackChannel = factory.createChannel(ChannelType.SLACK);
        slackChannel.send(testNotif, "U123456");
    }
    
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║       EJEMPLOS DE USO - SISTEMA DE NOTIFICACIONES         ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");
        
        ejemplo1_ConfigurarPreferencias();
        ejemplo2_CrearScrim();
        ejemplo3_FlujoCompleto();
        ejemplo4_Cancelar();
        ejemplo5_PerfilesNotificacion();
        ejemplo6_EnvioManual();
        ejemplo7_VerificarCanales();
        ejemplo8_TestearCanales();
        
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║                    EJEMPLOS COMPLETADOS                    ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
    }
}
