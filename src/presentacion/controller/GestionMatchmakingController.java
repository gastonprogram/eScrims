package presentacion.controller;

import java.util.List;

import aplicacion.services.ScrimService;
import dominio.modelo.Scrim;
import presentacion.view.GestionMatchmakingView;

/**
 * Controller para gestionar estrategias de matchmaking de scrims.
 * Permite al organizador cambiar la estrategia de matchmaking de sus scrims.
 * 
 * @author eScrims Team
 */
public class GestionMatchmakingController {

    private final ScrimService scrimService;
    private final GestionMatchmakingView view;
    private final String usuarioActualId;

    public GestionMatchmakingController(ScrimService scrimService,
            GestionMatchmakingView view,
            String usuarioActualId) {
        this.scrimService = scrimService;
        this.view = view;
        this.usuarioActualId = usuarioActualId;
    }

    /**
     * Inicia el flujo de gestión de estrategias de matchmaking.
     */
    public void gestionarEstrategias() {
        try {
            view.mostrarTitulo();

            // 1. Obtener scrims del organizador
            List<Scrim> scrimsOrganizador = scrimService.obtenerScrimsPorOrganizador(usuarioActualId);

            if (scrimsOrganizador.isEmpty()) {
                view.mostrarInfo("No tienes scrims creados.");
                return;
            }

            // 2. Mostrar scrims y solicitar selección
            view.mostrarScrimsOrganizador(scrimsOrganizador);
            int numeroScrim = view.solicitarNumeroScrim(scrimsOrganizador.size());

            if (numeroScrim == 0) {
                view.mostrarCancelacion();
                return;
            }

            // 3. Obtener scrim seleccionado
            Scrim scrimSeleccionado = scrimsOrganizador.get(numeroScrim - 1);
            view.mostrarDetalleScrim(scrimSeleccionado);

            // 4. Solicitar nueva estrategia
            String estrategiaActual = scrimSeleccionado.getEstrategiaMatchmaking();
            String nuevaEstrategia = view.solicitarNuevaEstrategia(estrategiaActual);

            if (nuevaEstrategia == null) {
                view.mostrarCancelacion();
                return;
            }

            // 5. Verificar si hay cambios
            if (nuevaEstrategia.equals(estrategiaActual)) {
                view.mostrarInfo("La estrategia seleccionada es la misma que la actual. No se realizaron cambios.");
                return;
            }

            // 6. Confirmar cambio
            boolean confirmar = view.confirmarCambioEstrategia(estrategiaActual, nuevaEstrategia);
            if (!confirmar) {
                view.mostrarCancelacion();
                return;
            }

            // 7. Realizar el cambio
            scrimService.cambiarEstrategiaMatchmaking(scrimSeleccionado.getId(), nuevaEstrategia);

            // 8. Mostrar resultado
            view.mostrarExito("Estrategia de matchmaking cambiada exitosamente de '" +
                    estrategiaActual + "' a '" + nuevaEstrategia + "'");

        } catch (IllegalArgumentException e) {
            view.mostrarError("Error: " + e.getMessage());
        } catch (Exception e) {
            view.mostrarError("Error inesperado: " + e.getMessage());
        }
    }

    /**
     * Permite al usuario volver a intentar con otro scrim.
     */
    public void repetirGestion() {
        view.pausaParaContinuar();
        gestionarEstrategias();
    }
}