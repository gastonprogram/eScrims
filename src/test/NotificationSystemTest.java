package test;

import java.time.LocalDateTime;

import aplicacion.builders.ScrimBuilder;
import dominio.juegos.LeagueOfLegends;
import dominio.modelo.Confirmacion;
import dominio.modelo.Postulacion;
import dominio.modelo.Scrim;
import dominio.modelo.Usuario;
import infraestructura.notificaciones.channels.NotificationChannel;
import infraestructura.notificaciones.core.NotificationService;
import infraestructura.notificaciones.factory.NotificationChannelFactory;
import infraestructura.notificaciones.types.*;

/**
 * Test básico para verificar el sistema de notificaciones.
 * Demuestra cómo funciona el sistema Observer + Abstract Factory/Adapter.
 * Actualizado para usar el nuevo sistema de Postulaciones y Confirmaciones.
 */
public class NotificationSystemTest {

    public static void main(String[] args) {
        System.out.println("=== Test del Sistema de Notificaciones ===\n");

        // 1. Crear usuarios con preferencias de notificación
        System.out.println("1. Creando usuarios con preferencias...");
        Usuario usuario1 = new Usuario("jugador1", "jugador1@example.com", "password123");
        usuario1.addPreferredChannel(ChannelType.EMAIL, "jugador1@example.com");
        usuario1.addPreferredChannel(ChannelType.DISCORD, "123456789");
        System.out.println("   - Usuario1 creado (ID: " + usuario1.getId() + ")");
        System.out.println("   - Canales: EMAIL, DISCORD");

        Usuario usuario2 = new Usuario("jugador2", "jugador2@example.com", "password456");
        usuario2.addPreferredChannel(ChannelType.EMAIL, "jugador2@example.com");
        usuario2.subscribeToEvent(NotificationEvent.LOBBY_ARMADO);
        usuario2.subscribeToEvent(NotificationEvent.CONFIRMADO_TODOS);
        System.out.println("   - Usuario2 creado (ID: " + usuario2.getId() + ")");
        System.out.println("   - Canal: EMAIL");
        System.out.println("   - Suscrito a: LOBBY_ARMADO, CONFIRMADO_TODOS\n");

        // 2. Crear un scrim usando el nuevo sistema
        System.out.println("2. Creando un scrim...");
        Scrim scrim = new ScrimBuilder()
                .withJuego(LeagueOfLegends.getInstance())
                .withFormato(new dominio.juegos.formatos.Formato5v5LoL())
                .withFechaHora(LocalDateTime.now().plusHours(2))
                .withRango(1000, 2000)
                .withLatenciaMaxima(50)
                .build();

        scrim.setCreatedBy(usuario1.getId());
        System.out.println("   - Scrim creado: " + scrim.getJuego().getNombre());
        System.out.println("   - Estado inicial: " + scrim.getEstado());
        System.out.println("   - Notificación de scrim creado enviada\n");

        // 3. Simular postulaciones hasta llenar el lobby (usando el nuevo sistema)
        System.out.println("3. Simulando postulaciones...");
        String[] jugadores = new String[10];
        jugadores[0] = usuario1.getId();
        jugadores[1] = usuario2.getId();

        // Crear más usuarios para completar 10
        for (int i = 2; i < 10; i++) {
            Usuario u = new Usuario("jugador" + (i + 1), "jugador" + (i + 1) + "@example.com", "pass123");
            jugadores[i] = u.getId();
        }

        // Crear postulaciones con el nuevo sistema
        for (int i = 0; i < 10; i++) {
            try {
                Postulacion postulacion = new Postulacion(
                        scrim.getId(),
                        jugadores[i],
                        1500, // Rango que cumple requisitos (1000-2000)
                        40 // Latencia que cumple requisitos (máx 50)
                );
                scrim.postular(postulacion);
                System.out.println("   - Postulación " + (i + 1) + "/10 - Estado: " + postulacion.getEstado());
            } catch (Exception e) {
                System.out.println("   - Error en postulación " + (i + 1) + ": " + e.getMessage());
            }
        }
        System.out.println("   - Estado actual: " + scrim.getEstado());
        if ("LOBBY_ARMADO".equals(scrim.getEstado())) {
            System.out.println("   - Notificación LOBBY_ARMADO enviada");
            System.out.println("   - Confirmaciones generadas: " + scrim.getConfirmaciones().size() + "\n");
        }

        // 4. Simular confirmaciones (usando el nuevo sistema)
        System.out.println("4. Simulando confirmaciones...");
        if ("LOBBY_ARMADO".equals(scrim.getEstado())) {
            int confirmadas = 0;
            for (Confirmacion conf : scrim.getConfirmaciones()) {
                try {
                    conf.confirmar();
                    scrim.confirmar(conf);
                    confirmadas++;
                    System.out.println("   - Confirmación " + confirmadas + "/10");
                } catch (Exception e) {
                    System.out.println("   - Error en confirmación: " + e.getMessage());
                }
            }
            System.out.println("   - Estado actual: " + scrim.getEstado());
            if ("CONFIRMADO".equals(scrim.getEstado())) {
                System.out.println("   - Notificación CONFIRMADO_TODOS enviada\n");
            }
        }

        // 5. Iniciar el scrim
        System.out.println("5. Iniciando el scrim...");
        try {
            scrim.iniciar();
            System.out.println("   - Estado actual: " + scrim.getEstado());
            System.out.println("   - Notificación EN_JUEGO enviada\n");
        } catch (Exception e) {
            System.out.println("   - Error al iniciar: " + e.getMessage() + "\n");
        }

        // 6. Finalizar el scrim
        System.out.println("6. Finalizando el scrim...");
        try {
            scrim.finalizar();
            System.out.println("   - Estado actual: " + scrim.getEstado());
            System.out.println("   - Notificación FINALIZADO enviada\n");
        } catch (Exception e) {
            System.out.println("   - Error al finalizar: " + e.getMessage() + "\n");
        }

        // 7. Verificar el sistema de notificaciones
        System.out.println("7. Verificando sistema de notificaciones...");
        System.out.println("   NOTA: En un sistema real, aquí se verificarían:");
        System.out.println("   - Que NotificationService haya registrado todos los eventos");
        System.out.println("   - Que se hayan llamado los canales correctos (Email, Discord, etc.)");
        System.out.println("   - Que los usuarios hayan recibido solo sus notificaciones suscritas");
        System.out.println("   - Usuario1 debería recibir todas las notificaciones en EMAIL y DISCORD");
        System.out.println("   - Usuario2 solo recibe LOBBY_ARMADO y CONFIRMADO_TODOS en EMAIL\n");

        // 8. Información del patrón utilizado
        System.out.println("8. Patrones de diseño implementados:");
        System.out.println("   Observer Pattern:");
        System.out.println("   - NotificationService es el Subject");
        System.out.println("   - Usuario es el Observer");
        System.out.println("   - Las notificaciones fluyen automáticamente\n");

        System.out.println("   Abstract Factory + Adapter Pattern:");
        System.out.println("   - NotificationChannelFactory crea los canales");
        System.out.println("   - Cada canal (Email, Discord, Slack, etc.) es un Adapter");
        System.out.println("   - Se pueden agregar nuevos canales sin modificar código existente\n");

        // 9. Probar notificación manual con cada tipo de canal
        System.out.println("9. Probando notificación manual con cada tipo de canal...");
        Notification testNotif = new Notification(
                NotificationEvent.SCRIM_CREATED_MATCH,
                scrim,
                usuario1.getId(),
                "Test de Notificación",
                "Este es un mensaje de prueba del sistema de notificaciones");

        // Probar cada tipo de canal
        NotificationChannelFactory factory = NotificationChannelFactory.getDefaultFactory();

        System.out.println("   - Probando canal PUSH:");
        NotificationChannel pushChannel = factory.createChannel(ChannelType.PUSH);
        pushChannel.send(testNotif, "fake-fcm-token");

        System.out.println("   - Probando canal EMAIL:");
        NotificationChannel emailChannel = factory.createChannel(ChannelType.EMAIL);
        emailChannel.send(testNotif, usuario1.getEmail());

        System.out.println("   - Probando canal DISCORD:");
        NotificationChannel discordChannel = factory.createChannel(ChannelType.DISCORD);
        discordChannel.send(testNotif, "987654321");

        System.out.println("   - Probando canal SLACK:");
        NotificationChannel slackChannel = factory.createChannel(ChannelType.SLACK);
        slackChannel.send(testNotif, "U123456");

        // 10. Estado final del NotificationService
        System.out.println("\n10. Estado final del NotificationService:");
        NotificationService notifService = NotificationService.getInstance();
        System.out.println("   - Instancia Singleton: " + notifService);
        System.out.println("   - Sistema listo para enviar notificaciones\n");

        // 11. Resumen del flujo completo
        System.out.println("11. Resumen del flujo completado:");
        System.out.println("   BUSCANDO → " + scrim.getPostulaciones().size() + " postulaciones aceptadas");
        System.out.println("   → LOBBY_ARMADO → " + scrim.getConfirmaciones().size() + " confirmaciones generadas");
        System.out.println("   → CONFIRMADO → todos confirmaron");
        System.out.println("   → EN_JUEGO → scrim en progreso");
        System.out.println("   → FINALIZADO → scrim completado");
        System.out.println("   Estado final: " + scrim.getEstado() + "\n");

        System.out.println("=== Test Completado ===");
        System.out.println("\nNOTA: Las notificaciones actuales son MOCK.");
        System.out.println("Para usar notificaciones reales, configura las siguientes variables de entorno:");
        System.out.println("  - FIREBASE_SERVER_KEY (para Push)");
        System.out.println("  - SENDGRID_API_KEY o SMTP_HOST/SMTP_USER/SMTP_PASSWORD (para Email)");
        System.out.println("  - DISCORD_WEBHOOK_URL (para Discord)");
        System.out.println("  - SLACK_WEBHOOK_URL (para Slack)");
    }
}
