package presentacion.controller;

import aplicacion.services.OrganizadorService;
import aplicacion.services.ScrimService;
import dominio.modelo.ParticipanteScrim;
import dominio.modelo.Scrim;
import dominio.roles.RolJuego;
import dominio.roles.lol.*;
import presentacion.view.AccionesOrganizadorView;

import java.util.List;
import java.util.Scanner;

/**
 * Controlador para las acciones específicas del organizador de scrims
 * (invitar, asignar roles, swap, etc.).
 * Maneja la interacción entre la vista y el servicio de organizador.
 */
public class AccionesOrganizadorController {

    private final OrganizadorService organizadorService;
    private final ScrimService scrimService;
    private final AccionesOrganizadorView view;
    private final Scanner scanner;

    public AccionesOrganizadorController(OrganizadorService organizadorService, ScrimService scrimService,
            Scanner scanner) {
        this.organizadorService = organizadorService;
        this.scrimService = scrimService;
        this.view = new AccionesOrganizadorView();
        this.scanner = scanner;
    }

    /**
     * Muestra el menú principal de acciones del organizador y maneja las opciones
     * seleccionadas.
     */
    public void mostrarMenuAccionesOrganizador(String usuarioId) {
        boolean continuar = true;

        while (continuar) {
            try {
                view.mostrarMenuPrincipal();
                int opcion = scanner.nextInt();
                scanner.nextLine(); // Limpiar buffer

                switch (opcion) {
                    case 1:
                        gestionarScrims(usuarioId);
                        break;
                    case 2:
                        mostrarHistorialAcciones(usuarioId);
                        break;
                    case 0:
                        continuar = false;
                        break;
                    default:
                        view.mostrarError("Opción no válida");
                }
            } catch (Exception e) {
                view.mostrarError("Error: " + e.getMessage());
                scanner.nextLine(); // Limpiar buffer en caso de error
            }
        }
    }

    /**
     * Gestiona los scrims del organizador.
     */
    private void gestionarScrims(String usuarioId) {
        List<Scrim> scrims = organizadorService.obtenerScrimsDelOrganizador(usuarioId);

        if (scrims.isEmpty()) {
            view.mostrarMensaje("No tienes scrims creados");
            return;
        }

        view.mostrarScrimsDelOrganizador(scrims);
        view.solicitarInput("Selecciona el número del scrim a gestionar (1-" + scrims.size() + "): ");

        try {
            int seleccion = scanner.nextInt();
            scanner.nextLine(); // Limpiar buffer

            // Verificar que la selección esté en rango
            if (seleccion < 1 || seleccion > scrims.size()) {
                view.mostrarError("Número fuera de rango. Debe estar entre 1 y " + scrims.size());
                return;
            }

            // Obtener el scrim seleccionado
            Scrim scrimSeleccionado = scrims.get(seleccion - 1);

            gestionarScrimEspecifico(scrimSeleccionado.getId(), usuarioId);

        } catch (Exception e) {
            view.mostrarError("Por favor ingresa un número válido");
            scanner.nextLine(); // Limpiar buffer en caso de error
        }
    }

    /**
     * Gestiona un scrim específico con todas sus opciones.
     */
    private void gestionarScrimEspecifico(String scrimId, String usuarioId) {
        boolean continuar = true;

        while (continuar) {
            try {
                // Mostrar información actual del scrim
                mostrarInformacionScrim(scrimId, usuarioId);

                view.mostrarMenuGestionScrim();
                int opcion = scanner.nextInt();
                scanner.nextLine(); // Limpiar buffer

                switch (opcion) {
                    case 1:
                        invitarJugador(scrimId, usuarioId);
                        break;
                    case 2:
                        asignarRol(scrimId, usuarioId);
                        break;
                    case 3:
                        swapJugadores(scrimId, usuarioId);
                        break;
                    case 4:
                        deshacerUltimaAccion(scrimId, usuarioId);
                        break;
                    case 5:
                        confirmarScrim(scrimId, usuarioId);
                        break;
                    case 0:
                        continuar = false;
                        break;
                    default:
                        view.mostrarError("Opción no válida");
                }
            } catch (Exception e) {
                view.mostrarError("Error: " + e.getMessage());
                scanner.nextLine(); // Limpiar buffer en caso de error
            }
        }
    }

    /**
     * Muestra la información actual del scrim y sus participantes.
     */
    private void mostrarInformacionScrim(String scrimId, String usuarioId) {
        try {
            List<ParticipanteScrim> participantes = organizadorService.obtenerParticipantes(scrimId, usuarioId);
            boolean bloqueado = organizadorService.estaBloqueado(scrimId, usuarioId);
            int accionesEnHistorial = organizadorService.getCantidadAccionesEnHistorial(scrimId, usuarioId);

            view.mostrarInformacionScrim(scrimId, participantes, bloqueado, accionesEnHistorial);
        } catch (Exception e) {
            view.mostrarError("Error al obtener información del scrim: " + e.getMessage());
        }
    }

    /**
     * Maneja la invitación de un jugador.
     */
    private void invitarJugador(String scrimId, String usuarioId) {
        try {
            view.solicitarInput("Ingresa el ID del jugador a invitar: ");
            String jugadorId = scanner.nextLine();

            // Mostrar roles disponibles (por ahora solo LoL)
            view.mostrarRolesDisponibles();
            view.solicitarInput("Selecciona el rol (1-5): ");
            int rolOpcion = scanner.nextInt();
            scanner.nextLine(); // Limpiar buffer

            RolJuego rol = obtenerRolPorOpcion(rolOpcion);
            if (rol == null) {
                view.mostrarError("Rol no válido");
                return;
            }

            organizadorService.invitarJugador(scrimId, usuarioId, jugadorId, rol);
            view.mostrarExito("Jugador " + jugadorId + " invitado con rol " + rol.getNombre());

        } catch (Exception e) {
            view.mostrarError("Error al invitar jugador: " + e.getMessage());
        }
    }

    /**
     * Maneja la asignación de rol a un participante.
     */
    private void asignarRol(String scrimId, String usuarioId) {
        try {
            List<ParticipanteScrim> participantes = organizadorService.obtenerParticipantes(scrimId, usuarioId);

            if (participantes.isEmpty()) {
                view.mostrarMensaje("No hay participantes en el scrim");
                return;
            }

            view.mostrarParticipantes(participantes);
            view.solicitarInput("Selecciona el número del participante (1-" + participantes.size() + "): ");
            int seleccion = scanner.nextInt();
            scanner.nextLine(); // Limpiar buffer

            if (seleccion < 1 || seleccion > participantes.size()) {
                view.mostrarError("Número fuera de rango. Debe estar entre 1 y " + participantes.size());
                return;
            }

            ParticipanteScrim participanteSeleccionado = participantes.get(seleccion - 1);
            String participanteId = participanteSeleccionado.getUserId();

            view.mostrarRolesDisponibles();
            view.solicitarInput("Selecciona el nuevo rol (1-5): ");
            int rolOpcion = scanner.nextInt();
            scanner.nextLine(); // Limpiar buffer

            RolJuego nuevoRol = obtenerRolPorOpcion(rolOpcion);
            if (nuevoRol == null) {
                view.mostrarError("Rol no válido");
                return;
            }

            organizadorService.asignarRol(scrimId, usuarioId, participanteId, nuevoRol);
            view.mostrarExito("Rol asignado exitosamente");

        } catch (Exception e) {
            view.mostrarError("Error al asignar rol: " + e.getMessage());
        }
    }

    /**
     * Maneja el intercambio de roles entre dos jugadores.
     */
    private void swapJugadores(String scrimId, String usuarioId) {
        try {
            List<ParticipanteScrim> participantes = organizadorService.obtenerParticipantes(scrimId, usuarioId);

            if (participantes.size() < 2) {
                view.mostrarMensaje("Se necesitan al menos 2 participantes para hacer swap");
                return;
            }

            view.mostrarParticipantes(participantes);
            view.solicitarInput("Número del primer jugador (1-" + participantes.size() + "): ");
            int seleccion1 = scanner.nextInt();
            scanner.nextLine(); // Limpiar buffer

            if (seleccion1 < 1 || seleccion1 > participantes.size()) {
                view.mostrarError("Número fuera de rango para el primer jugador");
                return;
            }

            view.solicitarInput("Número del segundo jugador (1-" + participantes.size() + "): ");
            int seleccion2 = scanner.nextInt();
            scanner.nextLine(); // Limpiar buffer

            if (seleccion2 < 1 || seleccion2 > participantes.size()) {
                view.mostrarError("Número fuera de rango para el segundo jugador");
                return;
            }

            if (seleccion1 == seleccion2) {
                view.mostrarError("No puedes intercambiar un jugador consigo mismo");
                return;
            }

            String jugador1Id = participantes.get(seleccion1 - 1).getUserId();
            String jugador2Id = participantes.get(seleccion2 - 1).getUserId();

            organizadorService.swapJugadores(scrimId, usuarioId, jugador1Id, jugador2Id);
            view.mostrarExito("Roles intercambiados exitosamente");

        } catch (Exception e) {
            view.mostrarError("Error al hacer swap: " + e.getMessage());
        }
    }

    /**
     * Deshace la última acción realizada.
     */
    private void deshacerUltimaAccion(String scrimId, String usuarioId) {
        try {
            if (!organizadorService.puedeDeshacer(scrimId, usuarioId)) {
                view.mostrarMensaje("No hay acciones para deshacer");
                return;
            }

            organizadorService.deshacerUltimaAccion(scrimId, usuarioId);
            view.mostrarExito("Última acción deshecha exitosamente");

        } catch (Exception e) {
            view.mostrarError("Error al deshacer acción: " + e.getMessage());
        }
    }

    /**
     * Confirma el scrim y lo bloquea para futuras modificaciones.
     * Los roles asignados se persisten en las confirmaciones.
     */
    private void confirmarScrim(String scrimId, String usuarioId) {
        try {
            if (organizadorService.estaBloqueado(scrimId, usuarioId)) {
                view.mostrarError("El scrim ya está confirmado y bloqueado");
                return;
            }

            // Mostrar participantes actuales y sus roles
            List<ParticipanteScrim> participantes = organizadorService.obtenerParticipantes(scrimId, usuarioId);
            view.mostrarParticipantes(participantes);

            // Mostrar advertencia sobre la confirmación
            view.mostrarMensaje("\n-  CONFIRMACIÓN DEL SCRIM");
            view.mostrarMensaje("Los roles asignados se guardarán permanentemente.");
            view.mostrarMensaje("Después de confirmar NO podrás hacer más cambios.");

            view.solicitarConfirmacion(
                    "¿Confirmar scrim con roles asignados? (s/n): ");
            String confirmacion = scanner.nextLine();

            if (confirmacion.toLowerCase().equals("s") || confirmacion.toLowerCase().equals("si")) {
                // Usar el nuevo método que persiste roles
                organizadorService.confirmarScrimConRoles(scrimId, usuarioId);

                view.mostrarExito("- ¡Scrim confirmado exitosamente con roles persistidos!");

                // Mostrar resumen de roles guardados
                String resumenRoles = organizadorService.obtenerResumenRoles(scrimId);
                view.mostrarMensaje("\n" + resumenRoles);

            } else {
                view.mostrarMensaje("Confirmación cancelada");
            }

        } catch (Exception e) {
            view.mostrarError("Error al confirmar scrim: " + e.getMessage());
        }
    }

    /**
     * Muestra el historial de acciones de todos los scrims del organizador.
     */
    private void mostrarHistorialAcciones(String usuarioId) {
        try {
            List<Scrim> scrims = organizadorService.obtenerScrimsDelOrganizador(usuarioId);

            if (scrims.isEmpty()) {
                view.mostrarMensaje("No tienes scrims creados");
                return;
            }

            for (Scrim scrim : scrims) {
                int acciones = organizadorService.getCantidadAccionesEnHistorial(scrim.getId(), usuarioId);
                view.mostrarHistorialScrim(scrim.getId(), scrim.getJuego().getNombre(), acciones);
            }

        } catch (Exception e) {
            view.mostrarError("Error al mostrar historial: " + e.getMessage());
        }
    }

    /**
     * Convierte la opción numérica en el rol correspondiente.
     */
    private RolJuego obtenerRolPorOpcion(int opcion) {
        switch (opcion) {
            case 1:
                return new RolTopLoL();
            case 2:
                return new RolJungleLoL();
            case 3:
                return new RolMidLoL();
            case 4:
                return new RolADCLoL();
            case 5:
                return new RolSupportLoL();
            default:
                return null;
        }
    }
}