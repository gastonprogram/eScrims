package model.acciones;

import model.ScrimOrganizador;

/**
 * Interfaz que define el contrato para las acciones que el organizador
 * puede realizar sobre un scrim.
 * 
 * Este diseño implementa el patrón Strategy, permitiendo encapsular
 * diferentes comportamientos (invitar, asignar rol, swap) en clases
 * independientes que pueden ser intercambiadas en tiempo de ejecución.
 * 
 * Cada acción es una estrategia que sabe cómo ejecutarse y deshacerse,
 * aplicando el principio Open/Closed: podemos agregar nuevas acciones
 * sin modificar el código existente.
 * 
 * Aunque no usamos el patrón Command explícitamente, esta interfaz
 * proporciona funcionalidad similar con menos complejidad.
 * 
 * @author eScrims Team
 */
public interface AccionOrganizador {

    /**
     * Ejecuta la acción sobre el scrim a través del organizador.
     * Cada implementación define su propia lógica de ejecución.
     * 
     * @param organizador el organizador del scrim que ejecuta la acción
     * @throws IllegalStateException    si la acción no puede ejecutarse
     *                                  en el estado actual del scrim
     * @throws IllegalArgumentException si los parámetros de la acción
     *                                  no son válidos
     */
    void ejecutar(ScrimOrganizador organizador);

    /**
     * Deshace la acción previamente ejecutada, restaurando el estado
     * anterior del scrim.
     * 
     * Este método solo puede invocarse si la acción fue ejecutada
     * exitosamente y el scrim aún no ha sido confirmado.
     * 
     * @param organizador el organizador del scrim sobre el que deshacer
     * @throws IllegalStateException si la acción no puede deshacerse
     */
    void deshacer(ScrimOrganizador organizador);

    /**
     * Verifica si la acción puede ejecutarse en el estado actual del scrim.
     * Permite validar precondiciones antes de intentar ejecutar.
     * 
     * @param organizador el organizador del scrim
     * @return true si la acción puede ejecutarse, false en caso contrario
     */
    boolean puedeEjecutarse(ScrimOrganizador organizador);

    /**
     * Obtiene una descripción legible de la acción para logging
     * y notificaciones al usuario.
     * 
     * @return descripción de la acción
     */
    String getDescripcion();

    /**
     * Obtiene el tipo de acción para facilitar el logging y auditoría.
     * 
     * @return tipo de acción (ej: "INVITAR_JUGADOR", "ASIGNAR_ROL")
     */
    String getTipoAccion();
}
