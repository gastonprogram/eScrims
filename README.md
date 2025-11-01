# eScrims

Sistema de gestión de scrims (partidas de práctica) con sistema de notificaciones integrado.

## Getting Started

Welcome to the VS Code Java world. Here is a guideline to help you get started to write Java code in Visual Studio Code.

## Folder Structure

The workspace contains two folders by default, where:

- `src`: the folder to maintain sources
  - `model/`: Modelos de dominio (Usuario, Scrim, estados)
  - `model/notifications/`: Sistema de notificaciones (Observer + Abstract Factory/Adapter)
  - `controller/`: Controladores de la aplicación
  - `view/`: Vistas de la interfaz
  - `test/`: Tests y demos
- `lib`: the folder to maintain dependencies
- `data`: folder para almacenamiento de datos (JSON)

Meanwhile, the compiled output files will be generated in the `bin` folder by default.

> If you want to customize the folder structure, open `.vscode/settings.json` and update the related settings there.

## Dependency Management

The `JAVA PROJECTS` view allows you to manage your dependencies. More details can be found [here](https://github.com/microsoft/vscode-java-dependency#manage-dependencies).

---

## Sistema de Notificaciones

El sistema implementa los patrones **Observer**, **Abstract Factory** y **Adapter** para enviar notificaciones a través de múltiples canales.

### Eventos que Disparan Notificaciones

1. **SCRIM_CREATED_MATCH**: Scrim creado que coincide con preferencias del usuario
2. **LOBBY_ARMADO**: Cambio a Lobby armado (cupo completo)
3. **CONFIRMADO_TODOS**: Confirmado por todos los participantes
4. **EN_JUEGO**: Cambio a En Juego
5. **FINALIZADO**: Scrim finalizado
6. **CANCELADO**: Scrim cancelado

### Canales de Notificación Disponibles

- **PUSH**: Firebase Cloud Messaging (FCM)
- **EMAIL**: JavaMail API / SendGrid
- **DISCORD**: Webhook de Discord
- **SLACK**: Webhook de Slack

### Arquitectura

```
NotificationService (Observer/Singleton)
    ↓
NotificationChannelFactory (Abstract Factory)
    ↓
NotificationChannel (Adapter Interface)
    ├── PushNotificationChannel (Firebase adapter)
    ├── EmailNotificationChannel (Email adapter)
    ├── DiscordNotificationChannel (Discord webhook adapter)
    └── SlackNotificationChannel (Slack webhook adapter)
```

### Configuración de Canales

#### 1. Notificaciones Push (Firebase)

**Dependencias necesarias:**
```xml
<!-- Firebase Admin SDK -->
<dependency>
    <groupId>com.google.firebase</groupId>
    <artifactId>firebase-admin</artifactId>
    <version>9.2.0</version>
</dependency>
```

**Variables de entorno:**
```bash
export FIREBASE_SERVER_KEY="tu_server_key_aqui"
# O configurar Firebase Admin SDK con archivo de credenciales:
export GOOGLE_APPLICATION_CREDENTIALS="path/to/serviceAccountKey.json"
```

**Integración en código:**
1. Descargar el archivo `serviceAccountKey.json` de Firebase Console
2. Inicializar Firebase Admin en `PushNotificationChannel`:
```java
FileInputStream serviceAccount = new FileInputStream("path/to/serviceAccountKey.json");
FirebaseOptions options = new FirebaseOptions.Builder()
    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
    .build();
FirebaseApp.initializeApp(options);
```
3. Reemplazar el código mock con:
```java
Message message = Message.builder()
    .setToken(fcmToken)
    .setNotification(Notification.builder()
        .setTitle(notification.getTitle())
        .setBody(notification.getMessage())
        .build())
    .build();
String response = FirebaseMessaging.getInstance().send(message);
```

#### 2. Notificaciones por Email

**Opción A: SendGrid**

**Dependencias:**
```xml
<dependency>
    <groupId>com.sendgrid</groupId>
    <artifactId>sendgrid-java</artifactId>
    <version>4.9.3</version>
</dependency>
```

**Variables de entorno:**
```bash
export SENDGRID_API_KEY="tu_api_key_aqui"
```

**Opción B: JavaMail (SMTP)**

**Dependencias:**
```xml
<dependency>
    <groupId>com.sun.mail</groupId>
    <artifactId>javax.mail</artifactId>
    <version>1.6.2</version>
</dependency>
```

**Variables de entorno:**
```bash
export SMTP_HOST="smtp.gmail.com"
export SMTP_PORT="587"
export SMTP_USER="tu_email@gmail.com"
export SMTP_PASSWORD="tu_app_password"
```

**Nota de seguridad:** Para Gmail, crear una App Password específica en lugar de usar tu contraseña principal.

#### 3. Notificaciones por Discord

**Configuración:**
1. Crear un webhook en Discord:
   - Ir a Server Settings → Integrations → Webhooks
   - Crear un nuevo webhook
   - Copiar la URL del webhook

**Variables de entorno:**
```bash
export DISCORD_WEBHOOK_URL="https://discord.com/api/webhooks/YOUR_WEBHOOK_ID/YOUR_WEBHOOK_TOKEN"
```

**No requiere dependencias adicionales** (usa HttpURLConnection nativo de Java).

#### 4. Notificaciones por Slack

**Configuración:**
1. Crear un webhook en Slack:
   - Ir a https://api.slack.com/apps
   - Crear una app → Incoming Webhooks
   - Activar Incoming Webhooks
   - Agregar un nuevo webhook al workspace
   - Copiar la URL del webhook

**Variables de entorno:**
```bash
export SLACK_WEBHOOK_URL="https://hooks.slack.com/services/YOUR/WEBHOOK/URL"
```

**No requiere dependencias adicionales** (usa HttpURLConnection nativo de Java).

### Uso del Sistema

#### 1. Configurar Preferencias de Usuario

```java
Usuario usuario = new Usuario("username", "email@example.com", "password");

// Suscribirse a eventos específicos
usuario.subscribeToEvent(NotificationEvent.LOBBY_ARMADO);
usuario.subscribeToEvent(NotificationEvent.CONFIRMADO_TODOS);

// O suscribirse a todos los eventos
usuario.subscribeToAllEvents();

// Configurar canales preferidos
usuario.addPreferredChannel(ChannelType.EMAIL, "user@example.com");
usuario.addPreferredChannel(ChannelType.DISCORD, "discord_user_id");
usuario.addPreferredChannel(ChannelType.PUSH, "firebase_fcm_token");
```

#### 2. Las Notificaciones se Disparan Automáticamente

Las notificaciones se envían automáticamente cuando:
- Se crea un scrim (vía `ScrimBuilder.build()`)
- Cambian los estados del scrim (vía `scrim.postular()`, `scrim.confirmar()`, etc.)

No es necesario llamar manualmente a `NotificationService` desde tu código de negocio.

#### 3. Ejecutar el Test de Demo

```bash
cd src
javac test/NotificationSystemTest.java
java test.NotificationSystemTest
```

Este test demuestra:
- Creación de usuarios con preferencias
- Ciclo completo de un scrim (creación → postulaciones → confirmaciones → inicio → fin)
- Envío de notificaciones por todos los canales

### Consideraciones de Seguridad

1. **Variables de Entorno**: Nunca incluir claves API o tokens directamente en el código
2. **Secrets Management**: En producción, usar un secret manager (AWS Secrets Manager, Azure Key Vault, etc.)
3. **Rate Limiting**: Implementar rate limiting para evitar spam de notificaciones
4. **Validación**: Validar todos los recipients antes de enviar (emails válidos, tokens activos, etc.)
5. **Logging**: Las notificaciones se loguean automáticamente para auditoría
6. **Errores**: Los fallos en el envío de notificaciones no bloquean las operaciones del scrim

### Desactivar Canales

Si no se configuran las variables de entorno, los canales simplemente quedarán deshabilitados y no se enviarán notificaciones por esos medios. El sistema detecta automáticamente qué canales están disponibles.

### Testing sin Configuración Real

El sistema incluye implementaciones mock que permiten testear sin configurar proveedores reales. Los mensajes se loguean en consola mostrando qué se habría enviado.

### Próximos Pasos / Mejoras Futuras

- [ ] Implementar filtrado por preferencias de usuario para `SCRIM_CREATED_MATCH`
- [ ] Agregar templates personalizables para mensajes
- [ ] Implementar cola de notificaciones asíncrona (background processing)
- [ ] Agregar soporte para notificaciones in-app
- [ ] Implementar retry logic para fallos temporales
- [ ] Agregar métricas y monitoreo de notificaciones
- [ ] Soporte para notificaciones agrupadas (digest)
