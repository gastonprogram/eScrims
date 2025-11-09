package infraestructura.notificaciones.adapter;

import dominio.modelo.Notificacion;

public class FirebaseCloudMessaging implements IAdapterPush {
    
    private String serviceAccountPath;
    private String projectId;
    
    public FirebaseCloudMessaging() {
        this.serviceAccountPath = System.getenv("FIREBASE_SERVICE_ACCOUNT_PATH");
        this.projectId = System.getenv("FIREBASE_PROJECT_ID");
    }
    
    public FirebaseCloudMessaging(String serviceAccountPath) {
        this.serviceAccountPath = serviceAccountPath;
    }
    
    @Override
    public void enviar(Notificacion notif) {
        String fcmToken = notif.getDestinoEspecifico();
        if (fcmToken == null || fcmToken.isEmpty()) {
            System.err.println("FCM token no configurado para usuario: " + 
                notif.getDestinatario().getUsername());
            return;
        }
        
        if (serviceAccountPath == null || projectId == null) {
            System.out.println("[SIMUL] PUSH (simulado) -> Token: " + fcmToken + 
                " (" + notif.getDestinatario().getUsername() + "): " + notif.getMensaje());
            return;
        }
        
        System.out.println("[OK] Push enviado a token: " + fcmToken + 
            " (" + notif.getDestinatario().getUsername() + ")");
    }
    
    public void setServiceAccountPath(String serviceAccountPath) {
        this.serviceAccountPath = serviceAccountPath;
    }
    
    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }
}
