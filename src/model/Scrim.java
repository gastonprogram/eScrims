package model;

import model.utils.ScrimFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Scrim {

    private String id;
    private String juego;
    private ScrimFormat formato;
    private int rangoMin;
    private int rangoMax;
    private int latenciaMax;
    private LocalDateTime fechaHora;
    private int plazas;
    private List<String> rolesRequeridos;
    private List<String> listaPostulaciones;
    private List<String> listaConfirmaciones;
    private String createdBy;
    private LocalDateTime createdAt;

    // Constructor protegido - usar ScrimBuilder para crear instancias
    protected Scrim(String juego, ScrimFormat formato, LocalDateTime fechaHora,
                 int rangoMin, int rangoMax, List<String> rolesRequeridos,
                 int latenciaMax, int plazas) {
        this.id = UUID.randomUUID().toString();
        this.juego = juego;
        this.formato = formato;
        this.fechaHora = fechaHora;
        this.rangoMin = rangoMin;
        this.rangoMax = rangoMax;
        this.rolesRequeridos = rolesRequeridos;
        this.latenciaMax = latenciaMax;
        this.plazas = plazas;
        this.listaPostulaciones = new ArrayList<>();
        this.listaConfirmaciones = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
    }
}
