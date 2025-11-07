package model.estadisticas;

import model.Scrim;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class GeneradorDatosRandom {
    private static final String[] EQUIPOS = {"Equipo Azul", "Equipo Rojo", "Equipo Verde", "Equipo Amarillo"};
    private static final String[] USUARIOS = {"usuario1", "usuario2", "usuario3", "usuario4", "usuario5", "usuario6", "usuario7", "usuario8"};
    private static final String[] COMENTARIOS = {
        "Jugador tóxico", "Abandono de partida", "Feed intencional", 
        "Lenguaje inapropiado", "Juego limpio", "Buen compañero", "Muy habilidoso"
    };

    public static EstadisticasScrim generarEstadisticas(Scrim scrim) {
        EstadisticasScrim estadisticas = new EstadisticasScrim(scrim);
        Random random = new Random();

        // Generar resultados aleatorios para los equipos
        for (String equipo : EQUIPOS) {
            if (random.nextBoolean()) {
                estadisticas.registrarVictoria(equipo);
            } else {
                estadisticas.registrarDerrota(equipo);
            }
        }

        // Seleccionar un ganador aleatorio
        String ganador = EQUIPOS[random.nextInt(EQUIPOS.length)];
        estadisticas.finalizarPartida(ganador);

        // Generar algunos abandonos aleatorios
        int abandonos = random.nextInt(USUARIOS.length / 2);
        for (int i = 0; i < abandonos; i++) {
            String usuarioAbandono = USUARIOS[random.nextInt(USUARIOS.length)];
            estadisticas.registrarAbandono(usuarioAbandono);
        }

        // Generar reportes aleatorios
        int numReportes = random.nextInt(5);
        for (int i = 0; i < numReportes; i++) {
            String reportador = USUARIOS[random.nextInt(USUARIOS.length)];
            String reportado;
            do {
                reportado = USUARIOS[random.nextInt(USUARIOS.length)];
            } while (reportado.equals(reportador));

            ReporteConducta.TipoReporte tipo = ReporteConducta.TipoReporte.values()[
                random.nextInt(ReporteConducta.TipoReporte.values().length)];
            
            ReporteConducta.Gravedad gravedad = ReporteConducta.Gravedad.values()[
                random.nextInt(ReporteConducta.Gravedad.values().length)];
            
            String descripcion = COMENTARIOS[random.nextInt(COMENTARIOS.length)];
            
            ReporteConducta reporte = new ReporteConducta(
                scrim.getId(),
                reportado,
                reportador,
                tipo,
                gravedad,
                descripcion
            );
            
            // Marcar algunos reportes como revisados
            if (random.nextBoolean()) {
                reporte.setRevisado(true);
                reporte.setSancionado(random.nextBoolean());
                if (reporte.isSancionado()) {
                    reporte.setComentariosModerador("Se aplicó sanción por " + tipo);
                } else {
                    reporte.setComentariosModerador("Reporte revisado, no se aplicaron sanciones");
                }
            }
            
            estadisticas.agregarReporte(reporte);
        }

        return estadisticas;
    }

    public static List<EstadisticasScrim> generarHistorialScrims(Scrim scrim, int cantidad) {
        List<EstadisticasScrim> historial = new ArrayList<>();
        for (int i = 0; i < cantidad; i++) {
            // Crear una copia de la scrim con un ID diferente para cada una
            Scrim copiaScrim = new Scrim(scrim.getJuego(), scrim.getFormato(), 
                scrim.getFechaHora().minusDays(cantidad - i), 
                scrim.getRangoMin(), scrim.getRangoMax(), 
                new ArrayList<>(scrim.getRolesRequeridos()),
                scrim.getLatenciaMax(), 
                scrim.getListaConfirmaciones().size());
            
            historial.add(generarEstadisticas(copiaScrim));
        }
        return historial;
    }
}
