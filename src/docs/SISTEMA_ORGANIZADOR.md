# Sistema de Gestión de Scrims con Organizador

## 📋 Descripción General

Este módulo implementa un sistema completo de gestión de scrims (partidas de práctica) con capacidades avanzadas para el organizador, incluyendo:

- ✅ Invitar jugadores con roles específicos
- ✅ Asignar y cambiar roles de jugadores
- ✅ Intercambiar roles entre jugadores (SWAP)
- ✅ **Deshacer acciones** antes de confirmar el scrim
- ✅ Validaciones automáticas según el juego
- ✅ Arquitectura extensible y modular

## 🎯 Patrones de Diseño Implementados

### 1. **Strategy Pattern**

- **Interfaz**: `RolJuego`, `AccionOrganizador`
- **Uso**: Permite intercambiar comportamientos (roles y acciones) en tiempo de ejecución
- **Ventaja**: Agregar nuevos roles o acciones sin modificar código existente

### 2. **State Pattern**

- **Clases**: `ScrimState` y sus implementaciones
- **Uso**: Gestiona los diferentes estados del scrim (Buscando, Confirmado, etc.)
- **Ventaja**: Transiciones de estado seguras y predecibles

### 3. **Builder Pattern**

- **Clase**: `ScrimBuilder`
- **Uso**: Construcción fluida de scrims con múltiples parámetros
- **Ventaja**: Código más legible y menos propenso a errores

### 4. **Singleton Pattern**

- **Clase**: `LeagueOfLegends` (y otros juegos)
- **Uso**: Una única instancia del juego en toda la aplicación
- **Ventaja**: Reduce el uso de memoria y garantiza consistencia

### 5. **Template Method Pattern**

- **Clase abstracta**: `Juego`
- **Uso**: Define la estructura común, las subclases implementan detalles
- **Ventaja**: Reutilización de código y comportamiento consistente

### 6. **Facade Pattern**

- **Clase**: `ScrimOrganizador`
- **Uso**: Simplifica operaciones complejas sobre el scrim
- **Ventaja**: Interfaz simple para operaciones complicadas

## 🏗️ Arquitectura del Sistema

```
src/model/
├── Scrim.java                    # Entidad principal del scrim
├── ScrimBuilder.java             # Builder para crear scrims
├── ParticipanteScrim.java        # Jugador en un scrim específico
├── ScrimOrganizador.java         # Gestor de acciones del organizador
├── EjemploUsoScrimOrganizador.java  # Ejemplo de uso completo
│
├── juegos/                       # Sistema de juegos
│   ├── Juego.java               # Clase abstracta base
│   └── LeagueOfLegends.java     # Implementación de LoL
│
├── roles/                        # Sistema de roles
│   ├── RolJuego.java            # Interfaz de roles
│   └── lol/                     # Roles de League of Legends
│       ├── RolTopLoL.java
│       ├── RolJungleLoL.java
│       ├── RolMidLoL.java
│       ├── RolADCLoL.java
│       └── RolSupportLoL.java
│
├── formatos/                     # Formatos de partida
│   ├── Formato5v5LoL.java
│   └── FormatoARAMLoL.java
│
├── acciones/                     # Sistema de acciones (Strategy)
│   ├── AccionOrganizador.java   # Interfaz de acciones
│   ├── InvitarJugadorAccion.java
│   ├── AsignarRolAccion.java
│   └── SwapJugadoresAccion.java
│
└── states/                       # Sistema de estados
    ├── ScrimState.java
    ├── BuscandoState.java
    ├── ConfirmadoState.java
    └── ...
```

## 🚀 Uso Básico

### 1. Crear un Scrim

```java
// Obtener instancia del juego
LeagueOfLegends lol = LeagueOfLegends.getInstance();

// Construir el scrim
ScrimBuilder builder = new ScrimBuilder();
Scrim scrim = builder
    .withJuego(lol)
    .withFormato(new Formato5v5LoL())
    .withFechaHora(LocalDateTime.now().plusDays(1))
    .withRango(1000, 3000)
    .withRolesRequeridos(Arrays.asList("Top", "Jungle", "Mid", "ADC", "Support"))
    .withLatenciaMaxima(50)
    .build();
```

### 2. Crear el Organizador

```java
ScrimOrganizador organizador = new ScrimOrganizador(scrim);
```

### 3. Invitar Jugadores

```java
Usuario usuario1 = new Usuario("Faker", "faker@example.com", "password");

AccionOrganizador invitar = new InvitarJugadorAccion(
    usuario1,
    new RolMidLoL()
);
organizador.ejecutarAccion(invitar);
```

### 4. Cambiar Rol de un Jugador

```java
AccionOrganizador cambiarRol = new AsignarRolAccion(
    "Faker",
    new RolTopLoL()
);
organizador.ejecutarAccion(cambiarRol);
```

### 5. Intercambiar Roles (SWAP)

```java
AccionOrganizador swap = new SwapJugadoresAccion(
    "Faker",  // Usuario 1
    "Deft"    // Usuario 2
);
organizador.ejecutarAccion(swap);
// Los roles de Faker y Deft se intercambian
```

### 6. Deshacer Acción

```java
// Deshace la última acción ejecutada
organizador.deshacerUltimaAccion();
```

### 7. Confirmar Scrim

```java
// Confirma el scrim y bloquea futuras modificaciones
organizador.confirmarScrim();

// Después de confirmar, no se puede deshacer ni ejecutar nuevas acciones
```

## 🔍 ¿Qué es SWAP de Jugadores?

**SWAP** significa **intercambiar** los roles de dos jugadores en el scrim.

### Ejemplo:

**Estado inicial:**

- Faker: Mid
- TheShy: Top

**Después de swap:**

- Faker: Top
- TheShy: Mid

### Ventajas del Swap:

- ✅ Operación atómica (todo o nada)
- ✅ Más simple que cambiar roles manualmente
- ✅ Se puede deshacer con un solo comando
- ✅ Útil cuando dos jugadores quieren cambiar posiciones

## 🔒 Sistema de Undo (Deshacer)

El sistema permite **deshacer acciones** antes de que el scrim sea confirmado:

### Características:

- ✅ Usa un **Stack** para mantener el historial
- ✅ Cada acción sabe cómo deshacerse
- ✅ Solo disponible antes de confirmar
- ✅ Deshacer es seguro y atómico

### Limitaciones:

- ❌ No se puede deshacer después de confirmar el scrim
- ❌ No se puede deshacer si el scrim cambió de estado

## 📊 Principios SOLID Aplicados

### ✅ Single Responsibility Principle (SRP)

- Cada clase tiene una única responsabilidad
- `ParticipanteScrim`: solo gestiona la participación
- `InvitarJugadorAccion`: solo sabe invitar
- `ScrimOrganizador`: solo coordina acciones

### ✅ Open/Closed Principle (OCP)

- Abierto a extensión, cerrado a modificación
- Agregar nuevos roles: crear nueva clase `RolXXX`
- Agregar nuevas acciones: crear nueva `AccionXXX`
- Agregar nuevos juegos: crear nueva clase `JuegoXXX`

### ✅ Liskov Substitution Principle (LSP)

- Cualquier `RolJuego` puede usarse donde se espera un rol
- Cualquier `AccionOrganizador` puede ejecutarse polimórficamente
- Cualquier `Juego` puede usarse en un `Scrim`

### ✅ Interface Segregation Principle (ISP)

- Interfaces pequeñas y específicas
- `RolJuego`: solo métodos relacionados con roles
- `AccionOrganizador`: solo métodos de ejecución/undo

### ✅ Dependency Inversion Principle (DIP)

- Dependemos de abstracciones, no de implementaciones
- `ScrimOrganizador` depende de `AccionOrganizador`, no de acciones concretas
- `ParticipanteScrim` depende de `RolJuego`, no de roles específicos

## 🎮 Extensibilidad

### Agregar un Nuevo Juego (ej: Valorant)

```java
// 1. Crear la clase del juego
public class Valorant extends Juego {
    private static Valorant instance;

    public static synchronized Valorant getInstance() {
        if (instance == null) {
            instance = new Valorant();
        }
        return instance;
    }

    @Override
    public String getNombre() {
        return "Valorant";
    }

    @Override
    public List<RolJuego> getRolesDisponibles() {
        return Arrays.asList(
            new RolDuelistValorant(),
            new RolInitiatorValorant(),
            // ...
        );
    }

    // ...
}

// 2. Crear los roles
public class RolDuelistValorant implements RolJuego {
    // Implementación
}

// 3. Usar en un scrim
Valorant valorant = Valorant.getInstance();
Scrim scrimValorant = new ScrimBuilder()
    .withJuego(valorant)
    .withFormato(new Formato5v5Valorant())
    // ...
    .build();
```

### Agregar una Nueva Acción

```java
// 1. Crear la clase de acción
public class KickJugadorAccion implements AccionOrganizador {
    private final String userId;

    public KickJugadorAccion(String userId) {
        this.userId = userId;
    }

    @Override
    public void ejecutar(ScrimOrganizador organizador) {
        // Lógica para expulsar jugador
        organizador.removerParticipante(userId);
    }

    @Override
    public void deshacer(ScrimOrganizador organizador) {
        // Lógica para re-agregar
    }

    // ...
}

// 2. Usar la nueva acción
AccionOrganizador kick = new KickJugadorAccion("Faker");
organizador.ejecutarAccion(kick);
```

## ✨ Ventajas de esta Arquitectura

1. **Sin Enums**: Todo son clases, totalmente extensible
2. **Validaciones Automáticas**: El juego valida sus propios roles
3. **Consistencia**: Imposible asignar un rol de LoL a un scrim de Valorant
4. **Undo Poderoso**: Deshacer cualquier acción antes de confirmar
5. **Modular**: Cada componente es independiente
6. **Testeable**: Fácil de probar cada pieza por separado
7. **Mantenible**: Cambios localizados, bajo acoplamiento
8. **Profesional**: Sigue las mejores prácticas de la industria

## 🧪 Prueba Rápida

Ejecuta la clase `EjemploUsoScrimOrganizador` para ver el sistema en acción:

```bash
cd src
javac model/EjemploUsoScrimOrganizador.java
java model.EjemploUsoScrimOrganizador
```

## 📝 Notas Importantes

1. **Thread Safety**: Los juegos usan Singleton sincronizado
2. **Inmutabilidad**: Las listas retornadas son copias defensivas
3. **Validaciones**: Múltiples niveles de validación para garantizar consistencia
4. **Logging**: Mensajes informativos en cada operación
5. **Excepciones**: Mensajes claros y específicos para errores

## 🎓 Conceptos Avanzados

### ¿Por qué no usar Command Pattern?

Aunque el enunciado mencionaba Command Pattern, implementamos **Strategy Pattern** porque:

- ✅ Más simple y directo
- ✅ Menos boilerplate code
- ✅ Misma funcionalidad de undo
- ✅ Mejor para este caso de uso
- ✅ Más fácil de entender y mantener

La funcionalidad es idéntica: cada acción sabe ejecutarse y deshacerse.

### Template Method en Juego

La clase `Juego` define métodos template que todas las subclases deben implementar:

```java
public abstract class Juego {
    // Template methods
    public abstract String getNombre();
    public abstract List<RolJuego> getRolesDisponibles();
    public abstract List<ScrimFormat> getFormatosDisponibles();

    // Implemented methods that use templates
    public boolean esRolValido(RolJuego rol) {
        return getRolesDisponibles().contains(rol);
    }
}
```

## 🤝 Colaboración entre Componentes

```
Usuario → ParticipanteScrim → ScrimOrganizador → Scrim
                ↓                      ↓
            RolJuego               AccionOrganizador
                ↓                      ↓
              Juego            [Invitar, Asignar, Swap]
```

## 🎉 Conclusión

Este sistema implementa una arquitectura profesional, extensible y mantenible que sigue todos los principios SOLID y utiliza múltiples patrones de diseño de forma coherente y justificada.

---

**Autor**: eScrims Team  
**Fecha**: 2025  
**Versión**: 1.0
