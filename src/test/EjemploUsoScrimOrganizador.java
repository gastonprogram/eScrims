package test;

import java.time.LocalDateTime;
import java.util.Arrays;

import aplicacion.builders.ScrimBuilder;
import aplicacion.builders.ScrimOrganizador;
import dominio.acciones.AccionOrganizador;
import dominio.acciones.AsignarRolAccion;
import dominio.acciones.InvitarJugadorAccion;
import dominio.acciones.SwapJugadoresAccion;
import dominio.juegos.LeagueOfLegends;
import dominio.juegos.formatos.Formato5v5LoL;
import dominio.modelo.ParticipanteScrim;
import dominio.modelo.Scrim;
import dominio.modelo.Usuario;
import dominio.roles.lol.*;

/**
 * Clase de ejemplo que demuestra el uso completo del sistema de scrims
 * con el organizador y las acciones.
 * 
 * Este ejemplo muestra:
 * 1. Cómo crear un scrim con el nuevo sistema de Juego
 * 2. Cómo usar el ScrimOrganizador para gestionar participantes
 * 3. Cómo ejecutar y deshacer acciones
 * 4. Cómo confirmar el scrim
 * 
 * @author eScrims Team
 */
public class EjemploUsoScrimOrganizador {

    public static void main(String[] args) {
        System.out.println("=== Ejemplo de uso del ScrimOrganizador ===\n");

        // 1. Crear usuarios de ejemplo
        Usuario usuario1 = new Usuario("Faker", "faker@example.com", "password123");
        Usuario usuario2 = new Usuario("Deft", "deft@example.com", "password123");
        Usuario usuario3 = new Usuario("TheShy", "theshy@example.com", "password123");
        Usuario usuario4 = new Usuario("Rookie", "rookie@example.com", "password123");
        Usuario usuario5 = new Usuario("Mata", "mata@example.com", "password123");

        System.out.println("Usuarios creados");

        // 2. Crear un scrim de League of Legends
        LeagueOfLegends lol = LeagueOfLegends.getInstance();

        ScrimBuilder builder = new ScrimBuilder();
        Scrim scrim = builder
                .withJuego(lol)
                .withFormato(new Formato5v5LoL())
                .withFechaHora(LocalDateTime.now().plusDays(1))
                .withRango(1000, 3000)
                .withRolesRequeridos(Arrays.asList("Top", "Jungle", "Mid", "ADC", "Support"))
                .withLatenciaMaxima(50)
                .build();

        System.out.println("Scrim creado: " + lol.getNombre() + " - " + scrim.getFormato().getFormatName());
        System.out.println();

        // 3. Crear el organizador del scrim
        ScrimOrganizador organizador = new ScrimOrganizador(scrim);
        System.out.println("Organizador creado");
        System.out.println();

        // 4. Invitar jugadores con sus roles
        System.out.println("--- Invitando jugadores ---");

        AccionOrganizador invitar1 = new InvitarJugadorAccion(usuario1, new RolMidLoL());
        organizador.ejecutarAccion(invitar1);

        AccionOrganizador invitar2 = new InvitarJugadorAccion(usuario2, new RolADCLoL());
        organizador.ejecutarAccion(invitar2);

        AccionOrganizador invitar3 = new InvitarJugadorAccion(usuario3, new RolTopLoL());
        organizador.ejecutarAccion(invitar3);

        AccionOrganizador invitar4 = new InvitarJugadorAccion(usuario4, new RolJungleLoL());
        organizador.ejecutarAccion(invitar4);

        AccionOrganizador invitar5 = new InvitarJugadorAccion(usuario5, new RolSupportLoL());
        organizador.ejecutarAccion(invitar5);

        System.out.println();
        mostrarParticipantes(organizador);

        // 5. Cambiar el rol de un jugador
        System.out.println("\n--- Cambiando rol de Faker de Mid a Top ---");
        AccionOrganizador cambiarRol = new AsignarRolAccion("Faker", new RolTopLoL());
        organizador.ejecutarAccion(cambiarRol);
        mostrarParticipantes(organizador);

        // 6. Intercambiar roles entre dos jugadores (SWAP)
        System.out.println("\n--- Intercambiando roles: TheShy (Top) <-> Rookie (Jungle) ---");
        AccionOrganizador swap = new SwapJugadoresAccion("TheShy", "Rookie");
        organizador.ejecutarAccion(swap);
        mostrarParticipantes(organizador);

        // 7. Deshacer la última acción (el swap)
        System.out.println("\n--- Deshaciendo última acción (swap) ---");
        organizador.deshacerUltimaAccion();
        mostrarParticipantes(organizador);

        // 8. Deshacer otra acción (cambio de rol de Faker)
        System.out.println("\n--- Deshaciendo otra acción (cambio de rol de Faker) ---");
        organizador.deshacerUltimaAccion();
        mostrarParticipantes(organizador);

        // 9. Confirmar el scrim (esto bloquea futuras modificaciones)
        System.out.println("\n--- Confirmando scrim ---");
        organizador.confirmarScrim();
        System.out.println("Scrim confirmado. Estado: " + scrim.getEstado());
        System.out.println("Bloqueado: " + organizador.isBloqueado());

        // 10. Intentar hacer cambios después de confirmar (debe fallar)
        System.out.println("\n--- Intentando cambiar rol después de confirmar ---");
        try {
            AccionOrganizador cambioInvalido = new AsignarRolAccion("Faker", new RolTopLoL());
            organizador.ejecutarAccion(cambioInvalido);
        } catch (IllegalStateException e) {
            System.out.println("Error esperado: " + e.getMessage());
        }

        System.out.println("\n=== Ejemplo completado exitosamente ===");
    }

    /**
     * Método helper para mostrar los participantes actuales del scrim.
     */
    private static void mostrarParticipantes(ScrimOrganizador organizador) {
        System.out.println("Participantes actuales:");
        for (ParticipanteScrim p : organizador.getParticipantes()) {
            System.out.println("  - " + p.getUserId() + ": " + p.getRolAsignado().getNombre() +
                    " (confirmado: " + p.isConfirmado() + ")");
        }
        System.out.println("Acciones en historial: " + organizador.getCantidadAccionesEnHistorial());
    }
}
