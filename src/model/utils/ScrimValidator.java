package model.utils;

import java.time.LocalDateTime;

public class ScrimValidator {
    public static void validateFechaHora(LocalDateTime fechaHora) {
        if (fechaHora.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("La fecha y hora debe ser posterior a la actual");
        }
    }

    public static void validateRangos(int rangoMin, int rangoMax) {
        if (rangoMin > rangoMax) {
            throw new IllegalArgumentException("El rango mínimo no puede ser mayor al máximo");
        }
    }

    public static void validatePlazas(int plazas) {
        if (plazas <= 0) {
            throw new IllegalArgumentException("Las plazas deben ser un número positivo");
        }
    }
}