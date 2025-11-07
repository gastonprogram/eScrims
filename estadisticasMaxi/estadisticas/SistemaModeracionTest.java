import model.estadisticas.ReporteConducta;
import model.estadisticas.SistemaModeracion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

public class SistemaModeracionTest {
    
    private SistemaModeracion sistemaModeracion;
    private static final String USUARIO_1 = "usuario1";
    private static final String USUARIO_2 = "usuario2";
    private static final String SCRIM_ID = "scrim-123";
    
    @BeforeEach
    void setUp() {
        sistemaModeracion = new SistemaModeracion();
    }
    
    private ReporteConducta crearReporte(String usuarioReportado, ReporteConducta.Gravedad gravedad) {
        return new ReporteConducta(
            SCRIM_ID,
            usuarioReportado,
            "reportador",
            ReporteConducta.TipoReporte.ABUSO_VERBAL,
            gravedad,
            "Comportamiento inapropiado"
        );
    }
    
    @Test
    void testRegistrarReporte() {
        // Reporte de gravedad MODERADO (debería sumar un strike)
        ReporteConducta reporte = crearReporte(USUARIO_1, ReporteConducta.Gravedad.MODERADO);
        sistemaModeracion.registrarReporte(reporte);
        
        assertEquals(1, sistemaModeracion.getStrikes(USUARIO_1), "Debería tener 1 strike");
        assertEquals(1, sistemaModeracion.getReportesUsuario(USUARIO_1).size(), "Debería tener 1 reporte");
    }
    
    @Test
    void testTresStrikesGeneranPenalizacion() {
        // Tres reportes de gravedad MODERADO o superior
        for (int i = 0; i < 3; i++) {
            sistemaModeracion.registrarReporte(crearReporte(USUARIO_1, ReporteConducta.Gravedad.MODERADO));
        }
        
        assertTrue(sistemaModeracion.estaPenalizado(USUARIO_1), "El usuario debería estar penalizado");
        assertTrue(sistemaModeracion.getTiempoRestantePenalizacion(USUARIO_1) > 0, "Debería tener tiempo de penalización restante");
    }
    
    @Test
    void testReporteLeveNoGeneraStrike() {
        sistemaModeracion.registrarReporte(crearReporte(USUARIO_1, ReporteConducta.Gravedad.LEVE));
        
        assertEquals(0, sistemaModeracion.getStrikes(USUARIO_1), "Los reportes LEVE no deberían generar strikes");
        assertEquals(1, sistemaModeracion.getReportesUsuario(USUARIO_1).size(), "Debería registrar el reporte");
    }
    
    @Test
    void testEstaEnCooldown() {
        sistemaModeracion.aplicarCooldown(USUARIO_1);
        
        assertTrue(sistemaModeracion.estaEnCooldown(USUARIO_1), "El usuario debería estar en cooldown");
    }
    
    @Test
    void testLimpiarReportesAntiguos() {
        // Crear un reporte reciente
        ReporteConducta reporteReciente = crearReporte(USUARIO_1, ReporteConducta.Gravedad.LEVE);
        
        // Registrar los reportes
        sistemaModeracion.registrarReporte(reporteReciente);
        
        // Limpiar reportes con más de 30 días (no debería afectar al reciente)
        sistemaModeracion.limpiarReportesAntiguos(30);
        
        // Debería quedar el reporte reciente
        List<ReporteConducta> reportes = sistemaModeracion.getReportesUsuario(USUARIO_1);
        assertEquals(1, reportes.size(), "Debería quedar 1 reporte");
        assertEquals("Comportamiento inapropiado", reportes.get(0).getDescripcion(), "Debería ser el reporte reciente");
    }
}
