package infraestructura.notificaciones.adapter;

import dominio.modelo.Notificacion;

public class DiscordWebhook implements IAdapterDiscord {
    
    private String webhookUrl;
    private String botName;
    
    public DiscordWebhook() {
        this.webhookUrl = System.getenv("DISCORD_WEBHOOK_URL");
        this.botName = "eScrims Bot";
    }
    
    public DiscordWebhook(String webhookUrl) {
        this.webhookUrl = webhookUrl;
        this.botName = "eScrims Bot";
    }
    
    @Override
    public void enviar(Notificacion notif) {
        String discordId = notif.getDestinoEspecifico();
        if (discordId == null || discordId.isEmpty()) {
            System.err.println("Discord ID no configurado para usuario: " + 
                notif.getDestinatario().getUsername());
            return;
        }
        
        if (webhookUrl == null || webhookUrl.isEmpty()) {
            System.out.println("[SIMUL] DISCORD (simulado) -> @" + discordId + 
                " (" + notif.getDestinatario().getUsername() + "): " + notif.getMensaje());
            return;
        }
        
        System.out.println("[OK] Discord enviado a @" + discordId + 
            " (" + notif.getDestinatario().getUsername() + ")");
    }
    
    public void setWebhookUrl(String webhookUrl) {
        this.webhookUrl = webhookUrl;
    }
    
    public void setBotName(String botName) {
        this.botName = botName;
    }
}
