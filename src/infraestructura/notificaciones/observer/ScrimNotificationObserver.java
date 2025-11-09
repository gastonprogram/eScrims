package infraestructura.notificaciones.observer;

import dominio.modelo.Scrim;
import dominio.modelo.Usuario;
import dominio.modelo.Confirmacion;
import dominio.modelo.Postulacion;
import infraestructura.notificaciones.NotificationManager;
import infraestructura.persistencia.repository.RepositorioFactory;
import infraestructura.persistencia.repository.RepositorioUsuario;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import compartido.utils.NotificationEvent;

public class ScrimNotificationObserver {
    
    private NotificationManager notificationManager;
    
    public ScrimNotificationObserver() {
        this.notificationManager = new NotificationManager();
    }
    
    public ScrimNotificationObserver(NotificationManager notificationManager) {
        this.notificationManager = notificationManager;
    }
    
    public void notificarCambioEstado(Scrim scrim, String estadoAnterior, List<Usuario> participantes) {
        String estadoActual = scrim.getEstado();
        NotificationEvent evento = mapearEstadoAEvento(estadoActual);
        String titulo = getTituloScrim(scrim);
        String mensaje = generarMensajePorEstado(estadoActual, titulo);
        
        notificationManager.notificarUsuarios(participantes, mensaje, evento);
    }
    
    public void notificarLobbyArmado(Scrim scrim, List<Usuario> participantes) {
        String titulo = getTituloScrim(scrim);
        String mensaje = "El lobby del scrim '" + titulo + "' esta completo. Confirma tu participacion!";
        notificationManager.notificarUsuarios(participantes, mensaje, NotificationEvent.LOBBY_ARMADO);
    }
    
    public void notificarConfirmadoTodos(Scrim scrim, List<Usuario> participantes) {
        String titulo = getTituloScrim(scrim);
        String mensaje = "Todos han confirmado para el scrim '" + titulo + "'. Preparate para jugar!";
        notificationManager.notificarUsuarios(participantes, mensaje, NotificationEvent.CONFIRMADO);
    }
    
    public void notificarEnJuego(Scrim scrim, List<Usuario> participantes) {
        String titulo = getTituloScrim(scrim);
        String mensaje = "El scrim '" + titulo + "' ha comenzado. Buena suerte!";
        notificationManager.notificarUsuarios(participantes, mensaje, NotificationEvent.EN_JUEGO);
    }
    
    public void notificarFinalizado(Scrim scrim, List<Usuario> participantes) {
        String titulo = getTituloScrim(scrim);
        String mensaje = "El scrim '" + titulo + "' ha finalizado.";
        notificationManager.notificarUsuarios(participantes, mensaje, NotificationEvent.FINALIZADO);
    }
    
    public void notificarCancelado(Scrim scrim, List<Usuario> participantes, String motivo) {
        String titulo = getTituloScrim(scrim);
        String mensaje = "El scrim '" + titulo + "' ha sido cancelado. Motivo: " + motivo;
        notificationManager.notificarUsuarios(participantes, mensaje, NotificationEvent.CANCELADO);
    }
    
    public void notificarBuscando(Scrim scrim, List<Usuario> usuariosInteresados) {
        String titulo = getTituloScrim(scrim);
        String mensaje = "Nuevo scrim disponible: " + titulo + ". Revisa si coincide con tus preferencias!";
        notificationManager.notificarUsuarios(usuariosInteresados, mensaje, NotificationEvent.BUSCANDO);
    }
    
    public static void notificarScrimCreado(Scrim scrim) {
        try {
            RepositorioUsuario repo = RepositorioFactory.getRepositorioUsuario();
            List<Usuario> todosLosUsuarios = repo.listarTodos();
            
            List<Usuario> usuariosInteresados = todosLosUsuarios.stream()
                    .filter(u -> !u.getId().equals(scrim.getCreatedBy()))
                    .collect(Collectors.toList());
            
            if (!usuariosInteresados.isEmpty()) {
                ScrimNotificationObserver observer = new ScrimNotificationObserver();
                observer.notificarBuscando(scrim, usuariosInteresados);
            }
        } catch (Exception e) {
            System.err.println("Error al notificar scrim creado: " + e.getMessage());
        }
    }
    
    private NotificationEvent mapearEstadoAEvento(String estado) {
        switch (estado) {
            case "BUSCANDO":
                return NotificationEvent.BUSCANDO;
            case "LOBBY_ARMADO":
                return NotificationEvent.LOBBY_ARMADO;
            case "CONFIRMADO":
                return NotificationEvent.CONFIRMADO;
            case "EN_JUEGO":
                return NotificationEvent.EN_JUEGO;
            case "FINALIZADO":
                return NotificationEvent.FINALIZADO;
            case "CANCELADO":
                return NotificationEvent.CANCELADO;
            default:
                return NotificationEvent.BUSCANDO;
        }
    }
    
    private String generarMensajePorEstado(String estado, String titulo) {
        switch (estado) {
            case "BUSCANDO":
                return "Scrim '" + titulo + "' esta buscando jugadores!";
            case "LOBBY_ARMADO":
                return "Lobby completo para '" + titulo + "'. Confirma tu asistencia!";
            case "CONFIRMADO":
                return "Todos confirmaron para '" + titulo + "'. Listos para jugar!";
            case "EN_JUEGO":
                return "Scrim '" + titulo + "' en curso. Buena suerte!";
            case "FINALIZADO":
                return "Scrim '" + titulo + "' finalizado.";
            case "CANCELADO":
                return "Scrim '" + titulo + "' cancelado.";
            default:
                return "Actualizacion de scrim '" + titulo + "'.";
        }
    }
    
    private String getTituloScrim(Scrim scrim) {
        String juego = scrim.getJuego() != null ? scrim.getJuego().getNombre() : "Scrim";
        String formato = scrim.getFormato() != null ? scrim.getFormato().toString() : "";
        return juego + " " + formato;
    }
    
    public List<String> obtenerUserIdsConfirmados(Scrim scrim) {
        if (scrim.getConfirmaciones() == null) {
            return new ArrayList<>();
        }
        
        return scrim.getConfirmaciones().stream()
                .filter(Confirmacion::isConfirmada)
                .map(Confirmacion::getUserId)
                .collect(Collectors.toList());
    }
    
    public List<String> obtenerUserIdsPostulados(Scrim scrim) {
        if (scrim.getPostulaciones() == null) {
            return new ArrayList<>();
        }
        
        return scrim.getPostulaciones().stream()
                .map(Postulacion::getUserId)
                .collect(Collectors.toList());
    }
}


