# Refactorización: Desacoplamiento de Controllers y Views

## 🎯 Objetivo

Desacoplar completamente los controladores de las vistas, siguiendo el mismo patrón usado en `Login` y `Register`, donde:

- **Controllers**: Solo contienen lógica de negocio pura, NO conocen las vistas
- **Views**: Orquestan el flujo de la interfaz de usuario y usan los controllers

---

## 📋 Cambios Realizados

### 1. **CrearScrimController.java** - Refactorizado

#### ❌ Antes (Acoplado):

```java
public class CrearScrimController {
    private CrearScrimView view;  // ❌ Dependencia de la vista
    private RepositorioScrim repositorio;
    private String currentUserId;

    public CrearScrimController(String currentUserId) {
        this.view = new CrearScrimView();  // ❌ Crea la vista
        this.repositorio = RepositorioScrimMemoria.getInstance();
        this.currentUserId = currentUserId;
    }

    public void iniciar() {
        view.mostrarTitulo();  // ❌ Llama métodos de la vista
        Juego juego = view.solicitarJuego();  // ❌ La vista controla el flujo
        // ...
    }
}
```

#### ✅ Después (Desacoplado):

```java
public class CrearScrimController {
    private RepositorioScrim repositorio;  // ✅ Solo lógica de negocio
    private String currentUserId;

    public CrearScrimController(String currentUserId) {
        this.repositorio = RepositorioScrimMemoria.getInstance();
        this.currentUserId = currentUserId;
    }

    /**
     * Crea un scrim con validaciones.
     * @return El scrim creado
     * @throws IllegalArgumentException Si hay errores de validación
     * @throws RuntimeException Si no se puede guardar
     */
    public Scrim crearScrim(Juego juego, ScrimFormat formato,
                           LocalDateTime fechaHora, int rangoMin,
                           int rangoMax, int latenciaMax) {
        // Solo lógica de negocio pura
        validarParametros(...);
        Scrim scrim = construirScrim(...);
        repositorio.guardar(scrim);
        return scrim;
    }

    private void validarParametros(...) {
        // Validaciones con excepciones
    }
}
```

**Cambios clave:**

- ❌ Eliminada dependencia de `CrearScrimView`
- ❌ Eliminado método `iniciar()` que orquestaba el flujo
- ✅ Método `crearScrim()` recibe todos los parámetros necesarios
- ✅ Lanza excepciones en lugar de mostrar mensajes
- ✅ Validaciones centralizadas en `validarParametros()`

---

### 2. **BuscarScrimController.java** - Refactorizado

#### ❌ Antes (Acoplado):

```java
public class BuscarScrimController {
    private BuscarScrimView view;  // ❌ Dependencia de la vista
    private RepositorioScrim repositorio;

    public void iniciar() {
        view.mostrarTitulo();  // ❌ Llama métodos de la vista
        FiltrosScrim filtros = construirFiltros();  // ❌ Usa la vista internamente
        List<Scrim> resultados = repositorio.buscarConFiltros(filtros);
        view.mostrarResultados(resultados);  // ❌ Llama métodos de la vista
    }

    private FiltrosScrim construirFiltros() {
        String juego = view.solicitarJuego();  // ❌ Depende de la vista
        // ...
    }
}
```

#### ✅ Después (Desacoplado):

```java
public class BuscarScrimController {
    private RepositorioScrim repositorio;  // ✅ Solo lógica de negocio

    /**
     * Busca scrims según filtros.
     * @return Lista de scrims encontrados
     * @throws IllegalArgumentException Si filtros son inválidos
     */
    public List<Scrim> buscarScrims(FiltrosScrim filtros) {
        if (filtros == null) {
            throw new IllegalArgumentException("Los filtros no pueden ser nulos");
        }
        return repositorio.buscarConFiltros(filtros);
    }

    public List<Scrim> obtenerTodos() {
        return repositorio.obtenerTodos();
    }

    public Scrim buscarPorId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID es requerido");
        }
        return repositorio.buscarPorId(id.trim());
    }

    public boolean eliminarScrim(String id) {
        // ...
    }

    public int contarScrims() {
        // ...
    }
}
```

**Cambios clave:**

- ❌ Eliminada dependencia de `BuscarScrimView`
- ❌ Eliminado método `iniciar()` y `construirFiltros()`
- ✅ Métodos públicos para operaciones CRUD sobre scrims
- ✅ Recibe objetos construidos (FiltrosScrim) en lugar de pedirlos a la vista
- ✅ API completa: buscar, obtenerTodos, buscarPorId, eliminar, contar

---

### 3. **CrearScrimView.java** - Ahora orquesta el flujo

#### ✅ Nuevo método agregado:

```java
/**
 * Inicia el flujo de creación usando el controller.
 * La VISTA orquesta, el CONTROLLER provee lógica de negocio.
 */
public void iniciarCreacion(controller.CrearScrimController controller) {
    mostrarTitulo();

    try {
        // 1. Solicitar juego (la vista maneja la UI)
        Juego juego = solicitarJuego();
        if (juego == null) {
            mostrarCancelacion();
            return;
        }

        // 2. Solicitar formato
        ScrimFormat formato = solicitarFormato(juego);
        if (formato == null) {
            mostrarCancelacion();
            return;
        }

        // 3-5. Recopilar más datos...
        LocalDateTime fechaHora = solicitarFechaHora();
        int rangoMin = solicitarRangoMin();
        int rangoMax = solicitarRangoMax(rangoMin);
        int latenciaMax = solicitarLatenciaMax();

        // 6. Confirmar
        if (!confirmarCreacion(...)) {
            mostrarCancelacion();
            return;
        }

        // 7. Usar el CONTROLLER para crear (lógica de negocio)
        Scrim scrim = controller.crearScrim(juego, formato, fechaHora,
                                           rangoMin, rangoMax, latenciaMax);

        // 8. Mostrar resultado (la vista maneja la UI)
        mostrarExito(scrim.getId());

    } catch (IllegalArgumentException e) {
        mostrarError(e.getMessage());
    } catch (RuntimeException e) {
        mostrarError(e.getMessage());
    }
}
```

**Responsabilidades de la vista:**

- ✅ Maneja toda la interacción con el usuario (Scanner)
- ✅ Decide el flujo (cuándo continuar, cuándo cancelar)
- ✅ Muestra mensajes (éxito, error, cancelación)
- ✅ **USA** el controller pero **NO** es usada por él

---

### 4. **BuscarScrimView.java** - Ahora orquesta el flujo

#### ✅ Nuevo método agregado:

```java
/**
 * Inicia el flujo de búsqueda usando el controller.
 * La VISTA orquesta, el CONTROLLER provee lógica de negocio.
 */
public void iniciarBusqueda(controller.BuscarScrimController controller) {
    mostrarTitulo();

    try {
        // 1. Construir filtros (la vista recopila los datos)
        FiltrosScrim.Builder builder = new FiltrosScrim.Builder();

        String juego = solicitarJuego();
        if (juego != null) builder.conJuego(juego);

        String formato = solicitarFormato();
        if (formato != null) builder.conFormato(formato);

        // ... más filtros opcionales

        builder.conEstado("BUSCANDO");
        FiltrosScrim filtros = builder.build();

        // 2. Usar el CONTROLLER para buscar (lógica de negocio)
        List<Scrim> resultados = controller.buscarScrims(filtros);

        // 3. Mostrar resultados (la vista maneja la UI)
        mostrarResultados(resultados);

    } catch (IllegalArgumentException e) {
        System.err.println("✗ Error de validación: " + e.getMessage());
    } catch (RuntimeException e) {
        System.err.println("✗ Error al buscar: " + e.getMessage());
    }
}
```

**Responsabilidades de la vista:**

- ✅ Solicita cada filtro al usuario (todos opcionales)
- ✅ Construye el objeto `FiltrosScrim`
- ✅ **USA** el controller para buscar
- ✅ Formatea y muestra los resultados

---

### 5. **Main.java** - Actualizado para usar el nuevo patrón

#### ✅ Creación de Scrims:

```java
private static void manejarCrearScrim() {
    try {
        Usuario usuario = loginController.getUsuarioLogueado();

        // 1. Crear controller desacoplado (solo lógica de negocio)
        CrearScrimController controller = new CrearScrimController(usuario.getUsername());

        // 2. La VISTA orquesta el flujo
        view.CrearScrimView vista = new view.CrearScrimView();
        vista.iniciarCreacion(controller);

        menuView.presionarEnterParaContinuar();
    } catch (Exception e) {
        System.err.println("✗ Error: " + e.getMessage());
        menuView.presionarEnterParaContinuar();
    }
}
```

#### ✅ Búsqueda de Scrims:

```java
private static void manejarBuscarScrims() {
    try {
        // 1. Crear controller desacoplado
        BuscarScrimController controller = new BuscarScrimController();

        // 2. La VISTA orquesta el flujo
        view.BuscarScrimView vista = new view.BuscarScrimView();
        vista.iniciarBusqueda(controller);

        menuView.presionarEnterParaContinuar();
    } catch (Exception e) {
        System.err.println("✗ Error: " + e.getMessage());
        menuView.presionarEnterParaContinuar();
    }
}
```

---

## 🔄 Comparación con Login/Register

### Login (Patrón Original)

```java
// Controller: Solo lógica de negocio
public class Login {
    public boolean autenticar(String username, String password) {
        // Validaciones
        // Autenticación
        // Retorna boolean o lanza excepción
    }
}

// Main: La vista orquesta
loginView.mostrarTituloLogin();
String username = loginView.solicitarUsername();
String password = loginView.solicitarPassword();

try {
    if (loginController.autenticar(username, password)) {
        loginView.mostrarLoginExitoso(username);
    }
} catch (RuntimeException e) {
    loginView.mostrarErrorLogin(e.getMessage());
}
```

### CrearScrim (Después de refactorización)

```java
// Controller: Solo lógica de negocio
public class CrearScrimController {
    public Scrim crearScrim(Juego juego, ScrimFormat formato, ...) {
        // Validaciones
        // Creación
        // Retorna Scrim o lanza excepción
    }
}

// Vista: Orquesta el flujo
vista.mostrarTitulo();
Juego juego = vista.solicitarJuego();
ScrimFormat formato = vista.solicitarFormato(juego);
// ... recopilar datos

try {
    Scrim scrim = controller.crearScrim(juego, formato, ...);
    vista.mostrarExito(scrim.getId());
} catch (IllegalArgumentException e) {
    vista.mostrarError(e.getMessage());
}
```

**¡Mismo patrón!** ✅

---

## ✅ Beneficios del Desacoplamiento

### 1. **Testabilidad**

- Controllers pueden testearse sin UI
- No se necesita simular Scanners ni System.out

```java
@Test
public void testCrearScrim() {
    CrearScrimController controller = new CrearScrimController("user123");

    Scrim scrim = controller.crearScrim(
        juego, formato, fecha, 10, 90, 80
    );

    assertNotNull(scrim);
    assertEquals("user123", scrim.getCreatedBy());
}
```

### 2. **Reutilización**

- El mismo controller puede usarse en:
  - Consola CLI
  - Interfaz gráfica (Swing/JavaFX)
  - API REST
  - Tests automatizados

### 3. **Mantenibilidad**

- Cambios en la UI NO afectan la lógica de negocio
- Cambios en validaciones están centralizados
- Separación clara de responsabilidades (SRP)

### 4. **Escalabilidad**

- Fácil agregar nuevas vistas (web, móvil, etc.)
- Fácil agregar nuevas operaciones al controller
- Patrón consistente en toda la aplicación

---

## 📊 Estructura Final

```
Controller (Lógica de Negocio)
    ↑
    | usa (pero no conoce)
    |
View (Orquestación + UI)
    ↓
Usuario (Entrada/Salida)
```

### Flujo de Dependencias:

```
Main.java
  ├─→ Crea Controller (sin vista)
  ├─→ Crea Vista (independiente)
  └─→ Vista.iniciar(controller)
        ├─→ Vista recopila datos del usuario
        ├─→ Vista llama controller.metodo(datos)
        ├─→ Controller procesa (valida, guarda)
        ├─→ Controller retorna resultado o lanza excepción
        └─→ Vista muestra resultado al usuario
```

---

## 🎯 Principios SOLID Aplicados

### **S** - Single Responsibility

- **Controller**: Solo lógica de negocio y validaciones
- **View**: Solo interacción con el usuario

### **O** - Open/Closed

- Controllers pueden extenderse sin modificar las vistas
- Vistas pueden reemplazarse sin modificar los controllers

### **D** - Dependency Inversion

- Controller NO depende de abstracciones de UI (View)
- View depende de la interfaz pública del Controller

---

## ✨ Resumen de Cambios

| Componente                | Antes                        | Después                  |
| ------------------------- | ---------------------------- | ------------------------ |
| **CrearScrimController**  | Crea y usa vista             | Solo lógica de negocio   |
| **BuscarScrimController** | Crea y usa vista             | Solo lógica de negocio   |
| **CrearScrimView**        | Usada por controller         | Orquesta con controller  |
| **BuscarScrimView**       | Usada por controller         | Orquesta con controller  |
| **Main.java**             | Llama `controller.iniciar()` | Crea ambos y los conecta |

---

**Refactorización completada exitosamente** ✅

Los controllers ahora siguen exactamente el mismo patrón que `Login` y `Register`:

- ✅ Sin dependencias de vistas
- ✅ Lógica de negocio pura
- ✅ Testeable independientemente
- ✅ Reutilizable en cualquier UI
