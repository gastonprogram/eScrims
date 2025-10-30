package view;

import model.estadisticas.Comentario;
import java.util.Scanner;

public class ComentarioView {
    private Scanner scanner;

    public ComentarioView() {
        this.scanner = new Scanner(System.in);
    }

    public Comentario solicitarComentario(Long jugadorId, Long partidoId) {
        System.out.println("\n--- Dejar Comentario ---");

        System.out.print("Rating (1-5 estrellas): ");
        int rating = Integer.parseInt(scanner.nextLine());

        System.out.print("Comentario: ");
        String contenido = scanner.nextLine();

        return new Comentario(jugadorId, partidoId, contenido, rating);
    }

    public void mostrarComentariosPendientes(java.util.List<Comentario> comentarios) {
        System.out.println("\n" + "=".repeat(40));
        System.out.println("      COMENTARIOS PENDIENTES");
        System.out.println("=".repeat(40));

        comentarios.forEach(c -> {
            System.out.printf("\nID: %d | Rating: %s%n", c.getId(), "⭐".repeat(c.getRating()));
            System.out.println("Contenido: " + c.getContenido());
            System.out.println("-".repeat(40));
        });
    }

    public boolean aprobarComentario(Long comentarioId) {
        System.out.print("\n¿Aprobar comentario " + comentarioId + "? (s/n): ");
        String respuesta = scanner.nextLine().trim().toLowerCase();
        return respuesta.equals("s");
    }

    public String solicitarMotivoRechazo() {
        System.out.print("Motivo de rechazo: ");
        return scanner.nextLine();
    }
}
