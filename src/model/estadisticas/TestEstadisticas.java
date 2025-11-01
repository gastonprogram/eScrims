package model.estadisticas;

import java.util.Map;

public class TestEstadisticas {
    public static void main(String[] args) {
        // ==========================================
        // 1) Simular un partido
        // ==========================================
        EstadisticasPartido partido = new EstadisticasPartido(1001L);
        partido.setId(1L); // probamos el setter de id

        // ==========================================
        // 2) Simular jugadores y sus stats
        // ==========================================
        EstadisticasJugador jugador1 = new EstadisticasJugador(10L);
        jugador1.setKills(12);
        jugador1.setAssists(7);
        jugador1.setDeaths(3);
        jugador1.setPuntuacion(1850);
        jugador1.setEsMVP(true); // lo marcamos como MVP

        EstadisticasJugador jugador2 = new EstadisticasJugador(11L);
        jugador2.setKills(5);
        jugador2.setAssists(9);
        jugador2.setDeaths(2);
        jugador2.setPuntuacion(1600);
        jugador2.setEsMVP(false);

        EstadisticasJugador jugador3 = new EstadisticasJugador(12L);
        jugador3.setKills(0);
        jugador3.setAssists(2);
        jugador3.setDeaths(6);
        jugador3.setPuntuacion(600);
        jugador3.setEsMVP(false);

        // ==========================================
        // 3) Meterlos en el partido
        //    (el partido no tiene metodo add, así que usamos el Map directamente)
        // ==========================================
        Map<Long, EstadisticasJugador> mapa = partido.getEstadisticasPorJugador();
        mapa.put(jugador1.getJugadorId(), jugador1);
        mapa.put(jugador2.getJugadorId(), jugador2);
        mapa.put(jugador3.getJugadorId(), jugador3);

        // seteamos el MVP en el partido también
        partido.setMvpJugadorId(jugador1.getJugadorId());

        // ==========================================
        // 4) Mostrar lo que tenemos
        // ==========================================
        System.out.println("==========================================");
        System.out.println("PARTIDO ID: " + partido.getPartidoId());
        System.out.println("Registro creado en: " + partido.getFechaRegistro());
        System.out.println("ID interno registro: " + partido.getId());
        System.out.println("MVP (jugadorId): " + partido.getMvpJugadorId());
        System.out.println("==========================================");
        System.out.println("ESTADÍSTICAS POR JUGADOR");
        System.out.println("==========================================");

        for (Map.Entry<Long, EstadisticasJugador> entry : mapa.entrySet()) {
            EstadisticasJugador sj = entry.getValue();
            double kda = sj.getKDA(); // queremos probar este método
            // el usuario pidió no redondear de más, mínimo 4 decimales
            System.out.printf(
                    "Jugador %d%s -> Kills: %d, Assists: %d, Deaths: %d, KDA: %.4f, Puntuación: %d%n",
                    sj.getJugadorId(),
                    sj.isEsMVP() ? " (MVP)" : "",
                    sj.getKills(),
                    sj.getAssists(),
                    sj.getDeaths(),
                    kda,
                    sj.getPuntuacion()
            );
        }

        // ==========================================
        // 5) Probar Comentario
        // ==========================================

        // Comentario pendiente
        Comentario c1 = new Comentario(10L, 1001L,
                "Muy buena actuación del jugador 10, se notó el impacto.",
                5);
        c1.setId(100L); // probamos setter de id

        // Comentario que vamos a aprobar
        Comentario c2 = new Comentario(11L, 1001L,
                "Buen soporte, pero podría haber rotado antes.",
                4);
        c2.setId(101L);
        c2.setEstado(Comentario.EstadoModeracion.APROBADO);

        // Comentario que vamos a rechazar
        Comentario c3 = new Comentario(12L, 1001L,
                "Comentario fuera de lugar.",
                2);
        c3.setId(102L);
        c3.setEstado(Comentario.EstadoModeracion.RECHAZADO);
        c3.setMotivoRechazo("Lenguaje inapropiado");

        // ==========================================
        // 6) Mostrar comentarios
        // ==========================================
        System.out.println();
        System.out.println("==========================================");
        System.out.println("COMENTARIOS");
        System.out.println("==========================================");
        imprimirComentario(c1);
        imprimirComentario(c2);
        imprimirComentario(c3);
    }

    private static void imprimirComentario(Comentario c) {
        System.out.println("Comentario ID: " + c.getId());
        System.out.println(" - jugadorId: " + c.getJugadorId());
        System.out.println(" - partidoId: " + c.getPartidoId());
        System.out.println(" - contenido: " + c.getContenido());
        System.out.println(" - rating: " + c.getRating());
        System.out.println(" - estado: " + c.getEstado());
        System.out.println(" - fechaCreacion: " + c.getFechaCreacion());
        if (c.getEstado() == Comentario.EstadoModeracion.RECHAZADO) {
            System.out.println(" - motivoRechazo: " + c.getMotivoRechazo());
        }
        System.out.println();
    }
}