package CasosDeUso;

import java.time.LocalDateTime;

import aplicacion.builders.ScrimBuilder;
import dominio.juegos.LeagueOfLegends;
import dominio.juegos.formatos.Formato5v5LoL;
import dominio.juegos.formatos.FormatoARAMLoL;
import dominio.modelo.Confirmacion;
import dominio.modelo.Postulacion;
import dominio.modelo.Scrim;
import dominio.modelo.Usuario;
import infraestructura.notificaciones.channels.NotificationChannel;
import infraestructura.notificaciones.factory.NotificationChannelFactory;
import infraestructura.notificaciones.types.*;

/**
 * Ejemplos de uso del sistema de notificaciones.
 * Actualizado para usar la nueva estructura: Juegos como objetos,
 * Postulaciones, Confirmaciones.
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
                .withJuego(LeagueOfLegends.getInstance())
                .withFormato(new Formato5v5LoL()) // 5v5 League of Legends
                .withFechaHora(LocalDateTime.now().plusHours(3))
                .withRango(1500, 2500)
                .withLatenciaMaxima(60)
                .build();

        System.out.println("Scrim creado: " + scrim.getId());
        System.out.println("Juego: " + scrim.getJuego().getNombre());
        System.out.println("Estado: " + scrim.getEstado());
        System.out.println("→ Notificación SCRIM_CREATED_MATCH enviada a usuarios matching");
    }

    /**
     * Ejemplo 3: Flujo completo de un scrim (con nueva estructura)
     */
    public static void ejemplo3_FlujoCompleto() {
        System.out.println("\n=== Ejemplo 3: Flujo Completo ===\n");

        // Crear scrim (programado para dentro de 5 minutos para poder iniciarlo)
        Scrim scrim = new ScrimBuilder()
                .withJuego(LeagueOfLegends.getInstance())
                .withFormato(new Formato5v5LoL())
                .withFechaHora(LocalDateTime.now().plusMinutes(5))
                .withRango(1000, 2000)
                .withLatenciaMaxima(50)
                .build();

        System.out.println("1. Scrim creado (ID: " + scrim.getId() + ")");
        System.out.println("   Estado: " + scrim.getEstado());
        System.out.println("   Programado para: " + scrim.getFechaHora());
        System.out.println("   Formato: 5v5 - " + scrim.getJuego().getNombre());

        // Postulaciones (ahora con objetos Postulacion)
        System.out.println("\n2. Creando postulaciones...");
        for (int i = 1; i <= 10; i++) {
            try {
                Postulacion postulacion = new Postulacion(
                        scrim.getId(),
                        "jugador" + i,
                        1500, // Rango que cumple requisitos
                        40 // Latencia que cumple requisitos
                );
                scrim.postular(postulacion);
                System.out.println("   - Postulación " + i + "/10 - Estado: " + postulacion.getEstado());
            } catch (Exception e) {
                System.out.println("   - Error en postulación " + i + ": " + e.getMessage());
            }
        }
        System.out.println("   → Estado del scrim: " + scrim.getEstado());
        System.out.println("   → Notificación LOBBY_ARMADO enviada");
        System.out.println("   → Confirmaciones generadas: " + scrim.getConfirmaciones().size());

        // Confirmaciones (ahora con objetos Confirmacion)
        System.out.println("\n3. Confirmando asistencias...");
        int confirmadas = 0;
        for (Confirmacion conf : scrim.getConfirmaciones()) {
            try {
                conf.confirmar();
                scrim.confirmar(conf);
                confirmadas++;
                System.out.println("   - Confirmación " + confirmadas + "/10");
            } catch (Exception e) {
                System.out.println("   - Error: " + e.getMessage());
            }
        }
        System.out.println("   → Estado del scrim: " + scrim.getEstado());
        System.out.println("   → Notificación CONFIRMADO_TODOS enviada");

        // Iniciar
        System.out.println("\n4. Iniciando scrim...");
        scrim.iniciar();
        System.out.println("   → Estado del scrim: " + scrim.getEstado());
        System.out.println("   → Notificación EN_JUEGO enviada");

        // Finalizar
        System.out.println("\n5. Finalizando scrim...");
        scrim.finalizar();
        System.out.println("   → Estado del scrim: " + scrim.getEstado());
        System.out.println("   → Notificación FINALIZADO enviada");
    }

    /**
     * Ejemplo 4: Cancelar un scrim
     */
    public static void ejemplo4_Cancelar() {
        System.out.println("\n=== Ejemplo 4: Cancelar Scrim ===\n");

        Scrim scrim = new ScrimBuilder()
                .withJuego(LeagueOfLegends.getInstance())
                .withFormato(new FormatoARAMLoL()) // ARAM mode
                .withFechaHora(LocalDateTime.now().plusDays(1))
                .withRango(500, 3000)
                .withLatenciaMaxima(80)
                .build();

        System.out.println("Scrim creado (ID: " + scrim.getId() + ")");
        System.out.println("Formato: ARAM - " + scrim.getJuego().getNombre());

        // Algunas postulaciones (con nueva estructura)
        System.out.println("\nCreando postulaciones...");
        for (int i = 1; i <= 3; i++) {
            try {
                Postulacion postulacion = new Postulacion(
                        scrim.getId(),
                        "jugador" + i,
                        1200, // Rango válido
                        50 // Latencia válida
                );
                scrim.postular(postulacion);
                System.out.println("   - Postulación " + i + " creada");
            } catch (Exception e) {
                System.out.println("   - Error: " + e.getMessage());
            }
        }

        // Cancelar
        System.out.println("\nCancelando scrim...");
        scrim.cancelar();
        System.out.println("   → Estado del scrim: " + scrim.getEstado());
        System.out.println("   → Notificación CANCELADO enviada a " + scrim.getPostulaciones().size() + " postulantes");
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
                .withJuego(LeagueOfLegends.getInstance())
                .withFormato(new Formato5v5LoL())
                .withFechaHora(LocalDateTime.now().plusHours(1))
                .withRango(2000, 3000)
                .withLatenciaMaxima(30)
                .build();

        System.out.println("Scrim VIP creado:");
        System.out.println("   - Juego: " + scrim.getJuego().getNombre());
        System.out.println("   - Rango requerido: 2000-3000 (Alto nivel)");

        // Crear notificación custom
        Notification customNotif = new Notification(
                NotificationEvent.SCRIM_CREATED_MATCH,
                scrim,
                "admin",
                "Scrim Especial VIP",
                "Se ha creado un scrim especial para jugadores de alto nivel (Rango 2000-3000). ¡No te lo pierdas!");

        // Obtener factory y crear canal
        NotificationChannelFactory factory = NotificationChannelFactory.getDefaultFactory();
        NotificationChannel emailChannel = factory.createChannel(ChannelType.EMAIL);

        // Enviar manualmente
        System.out.println("\nEnviando notificación manual a VIPs...");
        boolean enviado = emailChannel.send(customNotif, "vip@example.com");
        System.out.println("   → Notificación custom enviada: " + (enviado ? "✓" : "✗"));
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
                .withJuego(LeagueOfLegends.getInstance())
                .withFormato(new Formato5v5LoL())
                .withFechaHora(LocalDateTime.now())
                .build();

        System.out.println("Scrim de prueba creado:");
        System.out.println("   - Juego: " + scrim.getJuego().getNombre());
        System.out.println("   - ID: " + scrim.getId());

        Notification testNotif = new Notification(
                NotificationEvent.SCRIM_CREATED_MATCH,
                scrim,
                "tester",
                "Test de Notificación",
                "Este es un mensaje de prueba del sistema de notificaciones");

        NotificationChannelFactory factory = NotificationChannelFactory.getDefaultFactory();

        // Test Push
        System.out.println("\nTesteando canal PUSH...");
        NotificationChannel pushChannel = factory.createChannel(ChannelType.PUSH);
        boolean pushResult = pushChannel.send(testNotif, "test-token");
        System.out.println("   → Resultado: " + (pushResult ? "✓ Enviado" : "✗ Falló"));

        // Test Email
        System.out.println("\nTesteando canal EMAIL...");
        NotificationChannel emailChannel = factory.createChannel(ChannelType.EMAIL);
        boolean emailResult = emailChannel.send(testNotif, "test@example.com");
        System.out.println("   → Resultado: " + (emailResult ? "✓ Enviado" : "✗ Falló"));

        // Test Discord
        System.out.println("\nTesteando canal DISCORD...");
        NotificationChannel discordChannel = factory.createChannel(ChannelType.DISCORD);
        boolean discordResult = discordChannel.send(testNotif, "123456789");
        System.out.println("   → Resultado: " + (discordResult ? "✓ Enviado" : "✗ Falló"));

        // Test Slack
        System.out.println("\nTesteando canal SLACK...");
        NotificationChannel slackChannel = factory.createChannel(ChannelType.SLACK);
        boolean slackResult = slackChannel.send(testNotif, "U123456");
        System.out.println("   → Resultado: " + (slackResult ? "✓ Enviado" : "✗ Falló"));
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
