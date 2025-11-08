package test;

import aplicacion.services.EstadisticasService;
import dominio.estadisticas.EstadisticasScrim;
import dominio.estadisticas.ReporteConducta;
import dominio.estadisticas.SistemaModeracion;
import dominio.juegos.LeagueOfLegends;
import dominio.juegos.formatos.Formato5v5LoL;
import dominio.modelo.Scrim;

import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Test simple para verificar integración de estadísticas y moderación.
 */
public class TestEstadisticasIntegracion {

    public static void main(String[] args) {
        System.out.println("--- TEST ESTADÍSTICAS Y MODERACIÓN ---");

        EstadisticasService estadisticasService = new EstadisticasService();

        // Crear un scrim de prueba
        Scrim scrim = new Scrim(
                LeagueOfLegends.getInstance(),
                new Formato5v5LoL(),
                LocalDateTime.now().plusHours(1),
                40, 80,
                new ArrayList<>(),
                200, 10);

        System.out.println("Scrim creado: " + scrim.getId());

        // 1. Registrar resultado
        estadisticasService.registrarResultado(scrim, "Equipo Azul");
        EstadisticasScrim stats = estadisticasService.obtenerEstadisticasParaScrim(scrim);
        System.out.println("Ganador registrado: " + stats.getGanador());

        // 2. Reportar conducta tóxica
        estadisticasService.reportarConducta(
                scrim.getId(),
                ReporteConducta.TipoReporte.ABUSO_VERBAL,
                ReporteConducta.Gravedad.MODERADO,
                "usuario123",
                "reportador456",
                "Lenguaje inapropiado durante la partida");

        // 3. Verificar reporte
        System.out.println("Reportes totales en scrim: " + stats.getTotalReportes());

        // 4. Verificar sistema de moderación
        SistemaModeracion moderacion = estadisticasService.getSistemaModeracion();
        int strikes = moderacion.getStrikes("usuario123");
        System.out.println("Strikes para usuario123: " + strikes);

        // 5. Reportar otro incidente grave para superar strikes
        estadisticasService.reportarConducta(
                scrim.getId(),
                ReporteConducta.TipoReporte.FEED_INTENCIONAL,
                ReporteConducta.Gravedad.GRAVE,
                "usuario123",
                "reportador789",
                "Feed intencional durante toda la partida");

        estadisticasService.reportarConducta(
                scrim.getId(),
                ReporteConducta.TipoReporte.ABANDONO,
                ReporteConducta.Gravedad.GRAVE,
                "usuario123",
                "reportador101",
                "Abandonó la partida sin avisar");

        strikes = moderacion.getStrikes("usuario123");
        boolean penalizado = moderacion.estaPenalizado("usuario123");
        System.out.println("Strikes después de 3 reportes: " + strikes);
        System.out.println("¿Está penalizado?: " + penalizado);

        if (penalizado) {
            long horasRestantes = moderacion.getTiempoRestantePenalizacion("usuario123");
            System.out.println("Horas restantes de penalización: " + horasRestantes);
        }

        System.out.println("\n--- TEST COMPLETADO ---");
    }
}