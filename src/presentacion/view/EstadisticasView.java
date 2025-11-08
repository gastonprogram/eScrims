package presentacion.view;

import java.util.List;
import java.util.Scanner;

import dominio.estadisticas.Comentario;
import dominio.estadisticas.EstadisticasJugador;
import dominio.estadisticas.EstadisticasScrim;
import dominio.estadisticas.ReporteConducta;
import dominio.estadisticas.SistemaModeracion;

public class EstadisticasView {
    private Scanner scanner;

    public EstadisticasView() {
        this.scanner = new Scanner(System.in);
    }

    public void mostrarTitulo() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("         REGISTRO DE ESTADÍSTICAS DEL PARTIDO");
        System.out.println("=".repeat(50));
    }

    public EstadisticasJugador solicitarEstadisticasJugador(Long jugadorId, String nombreJugador) {
        System.out.println("\n--- Estadísticas de " + nombreJugador + " ---");
        EstadisticasJugador stats = new EstadisticasJugador(jugadorId.toString());

        System.out.print("Kills: ");
        stats.setKills(Integer.parseInt(scanner.nextLine()));

        System.out.print("Assists: ");
        stats.setAssists(Integer.parseInt(scanner.nextLine()));

        System.out.print("Deaths: ");
        stats.setDeaths(Integer.parseInt(scanner.nextLine()));

        System.out.print("Puntuación: ");
        stats.setPuntuacion(Integer.parseInt(scanner.nextLine()));

        return stats;
    }

    public String seleccionarMVP(EstadisticasScrim estadisticas) {
        System.out.println("\n--- Selección de MVP ---");
        estadisticas.getEstadisticasPorJugador().forEach((id, stats) -> {
            System.out.printf("ID: %s | KDA: %.2f | Puntos: %d%n",
                    id, stats.getKDA(), stats.getPuntuacion());
        });

        System.out.print("\nID del MVP: ");
        return scanner.nextLine();
    }

    public void mostrarResumen(EstadisticasScrim estadisticas) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("              RESUMEN DE ESTADÍSTICAS");
        System.out.println("=".repeat(50));
        System.out.printf("Scrim ID: %s%n", estadisticas.getScrimId());
        System.out.printf("Fecha inicio: %s%n", estadisticas.getFechaHoraInicio());

        if (estadisticas.getGanador() != null) {
            System.out.printf("Ganador: %s%n", estadisticas.getGanador());
            System.out.printf("Duración: %d minutos%n", estadisticas.getDuracionMinutos());
        }

        System.out.println("\n--- Estadísticas por Jugador ---");
        estadisticas.getEstadisticasPorJugador().forEach((id, stats) -> {
            String mvpLabel = stats.isEsMVP() ? " ⭐ MVP" : "";
            System.out.printf("\nJugador ID %s%s:%n", id, mvpLabel);
            System.out.printf("  K/D/A: %d/%d/%d (KDA: %.2f)%n",
                    stats.getKills(), stats.getDeaths(), stats.getAssists(), stats.getKDA());
            System.out.printf("  Puntuación: %d%n", stats.getPuntuacion());
        });

        // Mostrar MVP actual si existe
        EstadisticasJugador mvp = estadisticas.obtenerMVP();
        if (mvp != null) {
            System.out.printf("\n🏆 MVP Actual: Jugador %s (KDA: %.2f)%n",
                    mvp.getJugadorId(), mvp.getKDA());
        }
    }

    // ========== FUNCIONALIDAD EXTENDIDA: REPORTES Y MODERACIÓN ==========

    public void mostrarMenuCompleto() {
        System.out.println("\n=== MÓDULO DE ESTADÍSTICAS Y MODERACIÓN ===");
        System.out.println("1. Ver estadísticas de mis scrims");
        System.out.println("2. Finalizar scrim en juego");
        System.out.println("3. Reportar conducta");
        System.out.println("4. Ver reportes de un usuario");
        System.out.println("5. Ver estado de moderación de un usuario");
        System.out.println("6. Gestionar comentarios");
        System.out.println("7. Volver al menú principal");
        System.out.print("Seleccione una opción: ");
    }

    public void mostrarEstadisticasScrim(EstadisticasScrim estadisticas) {
        if (estadisticas == null) {
            System.out.println("No hay estadísticas disponibles para mostrar.");
            return;
        }

        System.out.println("\n🎮 ESTADÍSTICAS COMPLETAS DEL SCRIM");
        System.out.println("=" + "=".repeat(50));
        System.out.println("📅 ID: " + estadisticas.getScrimId());
        System.out.println("🕐 Inicio: " + estadisticas.getFechaHoraInicio());

        if (estadisticas.getFechaHoraFin() != null) {
            System.out.println("🏁 Fin: " + estadisticas.getFechaHoraFin());
            System.out.printf("⏱️ Duración: %d minutos (%s)%n",
                    estadisticas.getDuracionMinutos(),
                    estadisticas.getDescripcionPartida());
        }

        System.out.println(
                "🏆 Ganador: " + (estadisticas.getGanador() != null ? estadisticas.getGanador() : "Sin determinar"));

        // Mostrar formación de equipos si la partida finalizó
        if (estadisticas.getFechaHoraFin() != null && !estadisticas.getEstadisticasPorJugador().isEmpty()) {
            System.out.println("\n👥 FORMACIÓN DE EQUIPOS:");
            System.out.println(estadisticas.getFormacionEquipos());
        }

        // Mostrar estadísticas individuales
        System.out.println("\n📊 ESTADÍSTICAS INDIVIDUALES:");
        if (!estadisticas.getEstadisticasPorJugador().isEmpty()) {
            System.out.println("Jugador | K/D/A | KDA | Puntos | MVP");
            System.out.println("-".repeat(45));
            estadisticas.getEstadisticasPorJugador().values().forEach(stats -> {
                System.out.printf("%-8s | %d/%d/%d | %.2f | %-6d | %s%n",
                        stats.getJugadorId().substring(Math.max(0, stats.getJugadorId().length() - 8)),
                        stats.getKills(), stats.getDeaths(), stats.getAssists(),
                        stats.getKDA(), stats.getPuntuacion(),
                        stats.isEsMVP() ? "⭐" : "");
            });
        } else {
            System.out.println("No hay estadísticas individuales registradas.");
        }

        System.out.println("\n📋 REPORTES DE CONDUCTA:");
        System.out.println("Total de reportes: " + estadisticas.getReportes().size());
        if (!estadisticas.getReportes().isEmpty()) {
            System.out.println("\nReportes:");
            estadisticas.getReportes().forEach(reporte -> System.out.println(
                    "- " + reporte.getTipo() + " (" + reporte.getGravedad() + "): " + reporte.getDescripcion()));
        }
    }

    public void mostrarReportes(List<ReporteConducta> reportes) {
        if (reportes.isEmpty()) {
            System.out.println("No hay reportes para mostrar.");
            return;
        }

        System.out.println("\n=== REPORTES DE CONDUCTA ===");
        System.out.printf("%-10s | %-15s | %-20s | %-15s | %-10s | %-30s%n",
                "ID", "Usuario", "Tipo", "Gravedad", "Estado", "Descripción");
        System.out.println("-".repeat(110));

        for (ReporteConducta reporte : reportes) {
            System.out.printf("%-10s | %-15s | %-20s | %-15s | %-10s | %-30s%n",
                    reporte.getId().substring(0, 8),
                    reporte.getUsuarioReportadoId(),
                    reporte.getTipo(),
                    reporte.getGravedad(),
                    reporte.isRevisado() ? (reporte.isSancionado() ? "Sancionado" : "Revisado") : "Pendiente",
                    reporte.getDescripcion().substring(0, Math.min(30, reporte.getDescripcion().length())));
        }
    }

    public void mostrarEstadoModeracion(String usuarioId, SistemaModeracion moderacion) {
        System.out.println("\n=== ESTADO DE MODERACIÓN ===");
        System.out.println("Usuario: " + usuarioId);
        System.out.println("Strikes actuales: " + moderacion.getStrikes(usuarioId));
        System.out.println("Total reportes: " + moderacion.getReportesUsuario(usuarioId).size());

        if (moderacion.estaPenalizado(usuarioId)) {
            long horasRestantes = moderacion.getTiempoRestantePenalizacion(usuarioId);
            System.out.println("🔴 PENALIZADO - Horas restantes: " + horasRestantes);
        } else if (moderacion.estaEnCooldown(usuarioId)) {
            System.out.println("🟡 EN COOLDOWN");
        } else {
            System.out.println("🟢 Sin penalizaciones activas");
        }
    }

    public String solicitarScrimId() {
        System.out.print("Ingrese el ID del scrim: ");
        return scanner.nextLine().trim();
    }

    /**
     * Permite seleccionar un scrim de una lista enumerada.
     */
    /**
     * Permite al usuario seleccionar un scrim que esté en estado "EN_JUEGO"
     */
    public String seleccionarScrimEnJuego(List<dominio.modelo.Scrim> scrims) {
        // Filtrar solo scrims en juego
        List<dominio.modelo.Scrim> scrimsEnJuego = scrims.stream()
                .filter(scrim -> "EN_JUEGO".equals(scrim.getEstado()))
                .toList();

        if (scrimsEnJuego.isEmpty()) {
            System.out.println("No hay scrims en juego actualmente.");
            return null;
        }

        System.out.println("\n=== SCRIMS EN JUEGO ===");
        for (int i = 0; i < scrimsEnJuego.size(); i++) {
            dominio.modelo.Scrim scrim = scrimsEnJuego.get(i);
            System.out.printf("%d. ID: %s | Juego: %s | Fecha: %s%n",
                    i + 1,
                    scrim.getId().substring(0, Math.min(8, scrim.getId().length())),
                    scrim.getJuego().getNombre(),
                    scrim.getFechaHora());
        }

        System.out.print("\nSeleccione el número del scrim a finalizar (0 para cancelar): ");
        try {
            int opcion = Integer.parseInt(scanner.nextLine());
            if (opcion == 0) {
                return null;
            }
            if (opcion >= 1 && opcion <= scrimsEnJuego.size()) {
                return scrimsEnJuego.get(opcion - 1).getId();
            } else {
                System.out.println("Opción inválida.");
                return null;
            }
        } catch (NumberFormatException e) {
            System.out.println("Por favor, ingrese un número válido.");
            return null;
        }
    }

    public String seleccionarScrimDeNumero(List<dominio.modelo.Scrim> scrims) {
        if (scrims.isEmpty()) {
            System.out.println("No hay scrims disponibles.");
            return null;
        }

        System.out.println("\n=== SCRIMS DISPONIBLES ===");
        for (int i = 0; i < scrims.size(); i++) {
            dominio.modelo.Scrim scrim = scrims.get(i);
            System.out.printf("%d. ID: %s - Estado: %s%n",
                    i + 1,
                    scrim.getId(),
                    scrim.getEstado().getClass().getSimpleName());
        }

        System.out.print("\nSeleccione un scrim (número): ");
        try {
            int seleccion = Integer.parseInt(scanner.nextLine().trim());
            if (seleccion >= 1 && seleccion <= scrims.size()) {
                return scrims.get(seleccion - 1).getId();
            } else {
                System.out.println("Selección inválida.");
                return null;
            }
        } catch (NumberFormatException e) {
            System.out.println("Por favor, ingrese un número válido.");
            return null;
        }
    }

    /**
     * Permite seleccionar un usuario de una lista enumerada.
     */
    public String seleccionarUsuarioDeNumero(java.util.List<dominio.modelo.Usuario> usuarios) {
        if (usuarios.isEmpty()) {
            System.out.println("No hay usuarios disponibles.");
            return null;
        }

        System.out.println("\n=== USUARIOS DISPONIBLES ===");
        for (int i = 0; i < usuarios.size(); i++) {
            dominio.modelo.Usuario usuario = usuarios.get(i);
            System.out.printf("%d. [%s] %s%n",
                    i + 1,
                    usuario.getId(),
                    usuario.getUsername());
        }

        System.out.print("\nSeleccione un usuario (número): ");
        try {
            int seleccion = Integer.parseInt(scanner.nextLine().trim());
            if (seleccion >= 1 && seleccion <= usuarios.size()) {
                return usuarios.get(seleccion - 1).getId();
            } else {
                System.out.println("Selección inválida.");
                return null;
            }
        } catch (NumberFormatException e) {
            System.out.println("Por favor, ingrese un número válido.");
            return null;
        }
    }

    public String solicitarUsuarioId() {
        System.out.print("Ingrese el ID del usuario: ");
        return scanner.nextLine().trim();
    }

    public ReporteConducta.TipoReporte solicitarTipoReporte() {
        System.out.println("\n=== TIPOS DE REPORTE ===");
        ReporteConducta.TipoReporte[] tipos = ReporteConducta.TipoReporte.values();
        for (int i = 0; i < tipos.length; i++) {
            System.out.println((i + 1) + ". " + tipos[i]);
        }
        System.out.print("Seleccione tipo de reporte (1-" + tipos.length + "): ");

        try {
            int opcion = Integer.parseInt(scanner.nextLine());
            if (opcion >= 1 && opcion <= tipos.length) {
                return tipos[opcion - 1];
            }
        } catch (NumberFormatException e) {
            // Fall through to default
        }

        System.out.println("Opción inválida, usando OTRO por defecto.");
        return ReporteConducta.TipoReporte.OTRO;
    }

    public ReporteConducta.Gravedad solicitarGravedad() {
        System.out.println("\n=== GRAVEDAD ===");
        ReporteConducta.Gravedad[] gravedades = ReporteConducta.Gravedad.values();
        for (int i = 0; i < gravedades.length; i++) {
            System.out.println((i + 1) + ". " + gravedades[i]);
        }
        System.out.print("Seleccione gravedad (1-" + gravedades.length + "): ");

        try {
            int opcion = Integer.parseInt(scanner.nextLine());
            if (opcion >= 1 && opcion <= gravedades.length) {
                return gravedades[opcion - 1];
            }
        } catch (NumberFormatException e) {
            // Fall through to default
        }

        System.out.println("Opción inválida, usando LEVE por defecto.");
        return ReporteConducta.Gravedad.LEVE;
    }

    public String solicitarDescripcion() {
        System.out.print("Descripción del reporte: ");
        return scanner.nextLine().trim();
    }

    public String solicitarEquipoGanador() {
        System.out.print("Ingrese el equipo ganador: ");
        return scanner.nextLine().trim();
    }

    public void mostrarMensaje(String mensaje) {
        System.out.println("\n" + mensaje);
    }

    public int leerOpcion() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    // ========== GESTIÓN DE COMENTARIOS ==========

    public void mostrarMenuComentarios() {
        System.out.println("\n=== GESTIÓN DE COMENTARIOS ===");
        System.out.println("1. Crear comentario");
        System.out.println("2. Ver comentarios de scrim");
        System.out.println("3. Ver mis comentarios");
        System.out.println("4. Moderar comentarios (admin)");
        System.out.println("5. Volver");
        System.out.print("Seleccione una opción: ");
    }

    public String solicitarContenidoComentario() {
        System.out.print("Escriba su comentario: ");
        return scanner.nextLine();
    }

    public int solicitarRating() {
        System.out.print("Rating (1-5 estrellas): ");
        try {
            int rating = Integer.parseInt(scanner.nextLine());
            return Math.max(1, Math.min(5, rating)); // Asegurar que esté entre 1-5
        } catch (NumberFormatException e) {
            return 3; // Rating por defecto
        }
    }

    public Long solicitarJugadorId() {
        System.out.print("ID del jugador: ");
        try {
            return Long.parseLong(scanner.nextLine());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public void mostrarComentarios(List<Comentario> comentarios, String titulo) {
        if (comentarios.isEmpty()) {
            System.out.println("No hay comentarios para mostrar.");
            return;
        }

        System.out.println("\n=== " + titulo.toUpperCase() + " ===");
        for (Comentario comentario : comentarios) {
            mostrarComentario(comentario);
            System.out.println("-".repeat(50));
        }
    }

    public void mostrarComentario(Comentario comentario) {
        System.out.println("ID: " + comentario.getId());
        System.out.println("Jugador: " + comentario.getJugadorId());
        System.out.println("Scrim: " + comentario.getScrimId());
        System.out.println("Rating: " + "★".repeat(comentario.getRating()) + "☆".repeat(5 - comentario.getRating()));
        System.out.println("Contenido: " + comentario.getContenido());
        System.out.println("Estado: " + comentario.getEstado());
        System.out.println("Fecha: " + comentario.getFechaCreacion());

        if (comentario.getEstado() == Comentario.EstadoModeracion.RECHAZADO &&
                comentario.getMotivoRechazo() != null) {
            System.out.println("Motivo rechazo: " + comentario.getMotivoRechazo());
        }
    }

    public Long solicitarComentarioId() {
        System.out.print("ID del comentario: ");
        try {
            return Long.parseLong(scanner.nextLine());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public Comentario.EstadoModeracion solicitarEstadoModeracion() {
        System.out.println("Estados disponibles:");
        System.out.println("1. APROBADO");
        System.out.println("2. RECHAZADO");
        System.out.print("Seleccione el estado (1-2): ");

        try {
            int opcion = Integer.parseInt(scanner.nextLine());
            return switch (opcion) {
                case 1 -> Comentario.EstadoModeracion.APROBADO;
                case 2 -> Comentario.EstadoModeracion.RECHAZADO;
                default -> Comentario.EstadoModeracion.PENDIENTE;
            };
        } catch (NumberFormatException e) {
            return Comentario.EstadoModeracion.PENDIENTE;
        }
    }

    public String solicitarMotivoRechazo() {
        System.out.print("Motivo del rechazo: ");
        return scanner.nextLine();
    }
}
