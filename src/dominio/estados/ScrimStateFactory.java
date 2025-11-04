package dominio.estados;

/**
 * Factory para crear instancias de ScrimState basadas en el nombre del estado.
 * Usado principalmente para reconstruir estados después de deserialización.
 * 
 * @author eScrims Team
 */
public class ScrimStateFactory {

    /**
     * Crea una instancia de ScrimState basada en el nombre del estado.
     * 
     * @param estadoNombre nombre del estado (BUSCANDO, LOBBY_ARMADO, etc.)
     * @return instancia del estado correspondiente
     */
    public static ScrimState crearEstado(String estadoNombre) {
        if (estadoNombre == null) {
            return new BuscandoState();
        }

        switch (estadoNombre.toUpperCase()) {
            case "BUSCANDO":
                return new BuscandoState();
            case "LOBBY_ARMADO":
                return new LobbyArmadoState();
            case "CONFIRMADO":
                return new ConfirmadoState();
            case "EN_JUEGO":
                return new EnJuegoState();
            case "FINALIZADO":
                return new FinalizadoState();
            case "CANCELADO":
                return new CanceladoState();
            default:
                System.err.println("Estado desconocido: " + estadoNombre + ". Usando BUSCANDO por defecto.");
                return new BuscandoState();
        }
    }
}
