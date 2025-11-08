package compartido.utils;

import dominio.juegos.Juego;
import dominio.valueobjects.formatosScrims.ScrimFormat;
import java.util.Random;

/**
 * Simulador de tiempo de partida y nombres de equipos para generar
 * datos realistas basados en el tipo de juego y formato.
 * 
 * Utiliza rangos de tiempo típicos para cada tipo de juego:
 * - League of Legends 5v5: 25-45 minutos
 * - League of Legends ARAM: 15-25 minutos
 * - Counter-Strike: 20-60 minutos
 * - Valorant: 25-50 minutos
 * 
 * También genera nombres de equipos temáticos según el juego.
 * 
 * @author eScrims Team
 */
public class SimuladorPartida {

    private static final Random random = new Random();

    // Nombres de equipos por categorías
    private static final String[] NOMBRES_ANIMALES = {
            "Dragons", "Tigers", "Eagles", "Wolves", "Lions", "Panthers", "Hawks", "Bears",
            "Sharks", "Phoenix", "Serpents", "Falcons", "Leopards", "Cobras", "Ravens"
    };

    private static final String[] NOMBRES_ELEMENTOS = {
            "Fire", "Ice", "Storm", "Lightning", "Thunder", "Blaze", "Frost", "Inferno",
            "Tempest", "Vortex", "Nova", "Eclipse", "Meteor", "Plasma", "Crystal"
    };

    private static final String[] NOMBRES_GUERREROS = {
            "Warriors", "Knights", "Guardians", "Defenders", "Crusaders", "Sentinels",
            "Champions", "Legends", "Heroes", "Titans", "Gladiators", "Paladins"
    };

    private static final String[] NOMBRES_MISTICOS = {
            "Mystics", "Shadows", "Spirits", "Phantoms", "Wraiths", "Specters",
            "Reapers", "Demons", "Angels", "Seraphim", "Valkyries", "Nemesis"
    };

    private static final String[] COLORES = {
            "Red", "Blue", "Green", "Purple", "Gold", "Silver", "Black", "White",
            "Crimson", "Azure", "Emerald", "Violet", "Amber", "Obsidian"
    };

    private static final String[] NOMBRES_GAMING = {
            "Cyber", "Digital", "Neon", "Pixel", "Matrix", "Binary", "Vector",
            "Alpha", "Beta", "Gamma", "Delta", "Omega", "Prime", "Elite"
    };

    // Nombres específicos por juego
    private static final String[] NOMBRES_LOL = {
            "Rift", "Nexus", "Baron", "Elder", "Herald", "Jungle", "Lane",
            "Gank", "Roam", "Ward", "Carry", "Support", "Tank"
    };

    private static final String[] NOMBRES_CS = {
            "Strike", "Force", "Squad", "Unit", "Team", "Crew", "Gang",
            "Rush", "Clutch", "Ace", "Frag", "Smoke", "Flash"
    };

    private static final String[] NOMBRES_VALORANT = {
            "Agents", "Spike", "Radiant", "Immortal", "Ascendant", "Diamond",
            "Platinum", "Gold", "Silver", "Bronze", "Iron"
    };

    /**
     * Simula la duración de una partida basada en el juego y formato.
     * 
     * @param juego   el juego que se está jugando
     * @param formato el formato del scrim
     * @return duración en minutos
     */
    public static int simularDuracionPartida(Juego juego, ScrimFormat formato) {
        if (juego == null) {
            return simularDuracionGeneral();
        }

        String nombreJuego = juego.getNombre().toLowerCase();
        String nombreFormato = formato != null ? formato.getFormatName().toLowerCase() : "";

        // League of Legends
        if (nombreJuego.contains("league") || nombreJuego.contains("lol")) {
            if (nombreFormato.contains("aram")) {
                return generarTiempoAleatorio(15, 25); // ARAM: 15-25 min
            } else {
                return generarTiempoAleatorio(25, 45); // 5v5 Summoner's Rift: 25-45 min
            }
        }

        // Counter-Strike
        if (nombreJuego.contains("counter") || nombreJuego.contains("cs")) {
            if (nombreFormato.contains("competitive")) {
                return generarTiempoAleatorio(30, 60); // Competitivo: 30-60 min
            } else {
                return generarTiempoAleatorio(20, 40); // Casual: 20-40 min
            }
        }

        // Valorant
        if (nombreJuego.contains("valorant")) {
            if (nombreFormato.contains("ranked") || nombreFormato.contains("competitive")) {
                return generarTiempoAleatorio(25, 50); // Ranked: 25-50 min
            } else {
                return generarTiempoAleatorio(15, 35); // Unrated: 15-35 min
            }
        }

        // Tiempo genérico para juegos no reconocidos
        return simularDuracionGeneral();
    }

    /**
     * Simula una duración general para juegos no específicos.
     * 
     * @return duración entre 20-40 minutos
     */
    public static int simularDuracionGeneral() {
        return generarTiempoAleatorio(20, 40);
    }

    /**
     * Simula diferentes tipos de finales de partida que afectan la duración.
     * 
     * @param duracionBase duración base calculada
     * @return duración ajustada según el tipo de final
     */
    public static int aplicarVariacionPorTipoFinal(int duracionBase) {
        int tipoFinal = random.nextInt(100);

        if (tipoFinal < 10) {
            // Final muy rápido (surrender temprano, stomp)
            return (int) (duracionBase * 0.6);
        } else if (tipoFinal < 25) {
            // Final rápido
            return (int) (duracionBase * 0.8);
        } else if (tipoFinal < 75) {
            // Final normal
            return duracionBase;
        } else if (tipoFinal < 90) {
            // Final largo
            return (int) (duracionBase * 1.2);
        } else {
            // Final muy largo (overtime, partida muy reñida)
            return (int) (duracionBase * 1.5);
        }
    }

    /**
     * Genera un tiempo aleatorio dentro de un rango específico.
     * 
     * @param min tiempo mínimo en minutos
     * @param max tiempo máximo en minutos
     * @return tiempo aleatorio en el rango
     */
    private static int generarTiempoAleatorio(int min, int max) {
        return min + random.nextInt(max - min + 1);
    }

    /**
     * Simula una duración completa de partida con variaciones realistas.
     * 
     * @param juego   el juego que se está jugando
     * @param formato el formato del scrim
     * @return duración final en minutos
     */
    public static int simularPartidaCompleta(Juego juego, ScrimFormat formato) {
        int duracionBase = simularDuracionPartida(juego, formato);
        return aplicarVariacionPorTipoFinal(duracionBase);
    }

    /**
     * Genera un mensaje descriptivo del tipo de partida basado en la duración.
     * 
     * @param duracionMinutos duración de la partida
     * @return descripción del tipo de partida
     */
    public static String obtenerDescripcionPartida(int duracionMinutos) {
        if (duracionMinutos < 15) {
            return "Partida muy rápida (surrender temprano)";
        } else if (duracionMinutos < 25) {
            return "Partida rápida";
        } else if (duracionMinutos < 40) {
            return "Partida normal";
        } else if (duracionMinutos < 55) {
            return "Partida larga";
        } else {
            return "Partida muy larga (overtime)";
        }
    }

    // ========== MÉTODOS PARA GENERAR NOMBRES DE EQUIPOS ==========

    /**
     * Genera nombres de equipos aleatorios basados en el juego.
     * 
     * @param juego el juego que se está jugando
     * @return array con dos nombres de equipos [equipo1, equipo2]
     */
    public static String[] generarNombresEquipos(Juego juego) {
        if (juego == null) {
            return generarNombresGenericos();
        }

        String nombreJuego = juego.getNombre().toLowerCase();

        if (nombreJuego.contains("league") || nombreJuego.contains("lol")) {
            return generarNombresLoL();
        } else if (nombreJuego.contains("counter") || nombreJuego.contains("cs")) {
            return generarNombresCS();
        } else if (nombreJuego.contains("valorant")) {
            return generarNombresValorant();
        }

        return generarNombresGenericos();
    }

    /**
     * Genera nombres específicos para League of Legends.
     */
    private static String[] generarNombresLoL() {
        String equipo1 = combinarNombres(COLORES, NOMBRES_LOL);
        String equipo2 = combinarNombres(NOMBRES_ELEMENTOS, NOMBRES_GUERREROS);
        return new String[] { equipo1, equipo2 };
    }

    /**
     * Genera nombres específicos para Counter-Strike.
     */
    private static String[] generarNombresCS() {
        String equipo1 = combinarNombres(NOMBRES_GAMING, NOMBRES_CS);
        String equipo2 = combinarNombres(COLORES, NOMBRES_GUERREROS);
        return new String[] { equipo1, equipo2 };
    }

    /**
     * Genera nombres específicos para Valorant.
     */
    private static String[] generarNombresValorant() {
        String equipo1 = combinarNombres(NOMBRES_ELEMENTOS, NOMBRES_VALORANT);
        String equipo2 = combinarNombres(NOMBRES_GAMING, NOMBRES_ANIMALES);
        return new String[] { equipo1, equipo2 };
    }

    /**
     * Genera nombres genéricos para cualquier juego.
     */
    private static String[] generarNombresGenericos() {
        String equipo1 = combinarNombres(COLORES, NOMBRES_ANIMALES);
        String equipo2 = combinarNombres(NOMBRES_ELEMENTOS, NOMBRES_GUERREROS);
        return new String[] { equipo1, equipo2 };
    }

    /**
     * Combina aleatoriamente nombres de dos arrays.
     */
    private static String combinarNombres(String[] array1, String[] array2) {
        String parte1 = array1[random.nextInt(array1.length)];
        String parte2 = array2[random.nextInt(array2.length)];
        return parte1 + " " + parte2;
    }

    /**
     * Genera un equipo ganador aleatorio entre los dos equipos dados.
     * 
     * @param equipo1 nombre del primer equipo
     * @param equipo2 nombre del segundo equipo
     * @return nombre del equipo ganador
     */
    public static String determinarGanadorAleatorio(String equipo1, String equipo2) {
        return random.nextBoolean() ? equipo1 : equipo2;
    }

    /**
     * Genera una partida completa con equipos y ganador.
     * 
     * @param juego   el juego que se está jugando
     * @param formato el formato del scrim
     * @return objeto PartidaSimulada con toda la información
     */
    public static PartidaSimulada simularPartidaCompletaConEquipos(Juego juego, ScrimFormat formato) {
        String[] equipos = generarNombresEquipos(juego);
        int duracion = simularPartidaCompleta(juego, formato);
        String ganador = determinarGanadorAleatorio(equipos[0], equipos[1]);
        String descripcion = obtenerDescripcionPartida(duracion);

        return new PartidaSimulada(equipos[0], equipos[1], ganador, duracion, descripcion);
    }

    /**
     * Clase interna para encapsular los datos de una partida simulada.
     */
    public static class PartidaSimulada {
        public final String equipo1;
        public final String equipo2;
        public final String ganador;
        public final int duracionMinutos;
        public final String descripcion;

        public PartidaSimulada(String equipo1, String equipo2, String ganador,
                int duracionMinutos, String descripcion) {
            this.equipo1 = equipo1;
            this.equipo2 = equipo2;
            this.ganador = ganador;
            this.duracionMinutos = duracionMinutos;
            this.descripcion = descripcion;
        }

        @Override
        public String toString() {
            return String.format("%s vs %s - Ganador: %s (%d min, %s)",
                    equipo1, equipo2, ganador, duracionMinutos, descripcion);
        }
    }
}