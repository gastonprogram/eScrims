# 🔄 Reestructuración de Postulaciones y Confirmaciones

## 📋 Resumen de Cambios

Se ha reestructurado completamente el sistema de postulaciones y confirmaciones para separar claramente las responsabilidades entre **Usuario Común** y **Organizador**.

---

## 🎯 Estructura Anterior vs Nueva

### ❌ Antes (Problemas)

```
PostulacionView/Controller
├── Postularse (usuario)
├── Ver mi postulación (usuario)
├── Gestionar postulaciones (ORGANIZADOR) ❌
└── Aceptar/Rechazar (ORGANIZADOR) ❌

ConfirmacionView/Controller
├── Confirmar/Rechazar (usuario)
├── Ver mi confirmación (usuario)
├── Ver todas las confirmaciones (ORGANIZADOR) ❌
└── Ver estadísticas (ORGANIZADOR) ❌
```

**Problema:** Mezclaba funcionalidades de usuario y organizador en las mismas clases.

---

### ✅ Ahora (Solución)

#### **Para Usuario Común**

```
PostulacionViewSimplificada + PostulacionControllerSimplificado
├── ✅ Postularse a un scrim
└── ✅ Ver estado de MI postulación

ConfirmacionViewSimplificada + ConfirmacionControllerSimplificado
├── ✅ Confirmar asistencia
├── ✅ Rechazar asistencia
└── ✅ Ver estado de MI confirmación
```

#### **Para Organizador**

```
OrganizadorView + OrganizadorController
├── ✅ Ver postulaciones pendientes
├── ✅ Aceptar/Rechazar postulaciones
├── ✅ Ver todas las postulaciones
├── ✅ Ver estado de confirmaciones
└── ✅ Ver estadísticas de confirmaciones
```

---

## 📁 Archivos Nuevos Creados

### Views

1. **PostulacionViewSimplificada.java** - Vista para postularse (usuario)
2. **ConfirmacionViewSimplificada.java** - Vista para confirmar (usuario)
3. **OrganizadorView.java** - Vista para gestión (organizador)

### Controllers

4. **PostulacionControllerSimplificado.java** - Controller de postulación (usuario)
5. **ConfirmacionControllerSimplificado.java** - Controller de confirmación (usuario)
6. **OrganizadorController.java** - Controller de gestión (organizador)

### Main.java

7. **Actualizado** - Nueva opción "5. Gestionar mis Scrims (Organizador)"

---

## 🎮 Flujo del Usuario en el Sistema

### 1️⃣ **Flujo del Usuario Común**

```
Login → Menú Principal
  ├── 1. Crear Scrim
  ├── 2. Buscar Scrims
  ├── 3. Postularse a un Scrim ⭐ NUEVO
  │    ├── Ingresar ID del scrim
  │    ├── Ingresar rango
  │    ├── Ingresar latencia
  │    └── ✅ Postulación enviada
  │
  ├── 4. Gestionar Confirmaciones ⭐ NUEVO
  │    ├── 1. Confirmar asistencia
  │    ├── 2. Rechazar asistencia
  │    ├── 3. Ver mi confirmación
  │    └── 0. Volver
  │
  ├── 5. Gestionar mis Scrims (Organizador) ⭐ NUEVO
  ├── 6. Editar Perfil
  └── 7. Cerrar Sesión
```

### 2️⃣ **Flujo del Organizador**

```
5. Gestionar mis Scrims (Organizador)
  ├── 1. Ver postulaciones pendientes
  │    └── Muestra lista de usuarios postulados con rango/latencia
  │
  ├── 2. Gestionar postulaciones
  │    ├── Ver postulaciones pendientes
  │    ├── Aceptar postulación
  │    ├── Rechazar postulación (con motivo)
  │    └── Volver
  │
  ├── 3. Ver estado de confirmaciones
  │    ├── Lista de todas las confirmaciones
  │    ├── Estadísticas (confirmadas/pendientes/rechazadas)
  │    └── Lista de confirmaciones pendientes
  │
  ├── 4. Ver todas las postulaciones
  │    └── Muestra todas (aceptadas/rechazadas/pendientes)
  │
  └── 0. Volver al menú principal
```

---

## 🔄 Flujo de Estados del Scrim

### Estado: **BUSCANDO**

- ✅ Usuarios pueden postularse
- ✅ Si cumplen requisitos → Aceptación AUTOMÁTICA
- ❌ Si NO cumplen → Rechazo AUTOMÁTICO con motivo
- ⏭️ Cuando se llena el cupo → **LOBBY_ARMADO**

### Estado: **LOBBY_ARMADO**

- ✅ Se generan confirmaciones para todos los jugadores
- ✅ Usuarios deben confirmar/rechazar asistencia
- ⏭️ Cuando TODOS confirman → **CONFIRMADO**
- ⏮️ Si alguien rechaza → Vuelve a **BUSCANDO**

### Estado: **CONFIRMADO**

- ✅ Scrim listo para iniciar
- ⏭️ Puede iniciar cuando llegue la hora → **EN_JUEGO**

---

## 🎨 Mejoras en la UI

### Emojis y Mensajes Claros

- ✅ Aceptada
- ❌ Rechazada
- ⏳ Pendiente
- 📋 ID del scrim
- 🎮 Rango
- 📡 Latencia
- 📅 Fecha
- 💬 Motivo de rechazo

### Separadores Visuales

```
==================================================
           MENÚ DE USUARIO - username
==================================================
```

### Mensajes Informativos

- Al postularse con éxito
- Al confirmar asistencia
- Al rechazar asistencia
- Estados del scrim

---

## 🧪 Testing

### Para probar el flujo completo (simulado):

1. **Crear un scrim** (usuario A como organizador)
2. **Postularse** (usuario B como jugador)
3. **Ver postulaciones** (usuario A como organizador)
4. **Esperar que se llene el cupo** (automático o simular más postulaciones)
5. **Confirmar asistencia** (usuario B como jugador)
6. **Ver confirmaciones** (usuario A como organizador)

---

## 📝 Notas Importantes

1. **Los archivos viejos NO fueron eliminados** (PostulacionView, PostulacionController, ConfirmacionView, ConfirmacionController)

   - Puedes eliminarlos si quieres, pero dejé los nuevos con nombres "Simplificada" y Main.java usa las nuevas versiones

2. **Los servicios NO fueron modificados** - La lógica de negocio se mantiene igual

3. **La simulación de otros usuarios** está pendiente - Por ahora el flujo funciona manualmente

4. **El sistema asume que los requisitos (rango/latencia) ya están configurados en el scrim**

---

## ✅ Checklist de Funcionalidades

### Usuario Común

- [x] Postularse a un scrim
- [x] Ver estado de mi postulación
- [x] Confirmar asistencia
- [x] Rechazar asistencia
- [x] Ver estado de mi confirmación

### Organizador

- [x] Ver postulaciones pendientes
- [x] Aceptar postulaciones
- [x] Rechazar postulaciones (con motivo)
- [x] Ver todas las postulaciones
- [x] Ver estado de confirmaciones
- [x] Ver estadísticas de confirmaciones
- [x] Ver confirmaciones pendientes

### Estados

- [x] BUSCANDO → Acepta postulaciones
- [x] LOBBY_ARMADO → Genera confirmaciones
- [x] CONFIRMADO → Listo para iniciar

---

## 🚀 Próximos Pasos

1. **Simular otros usuarios** - Crear un sistema que simule postulaciones/confirmaciones automáticas
2. **Notificaciones** - Avisar al usuario cuando debe confirmar asistencia
3. **Eliminar archivos viejos** - Si las nuevas versiones funcionan bien
4. **Tests unitarios** - Para cada flujo
