package aplicacion.services;

import dominio.modelo.Scrim;
import dominio.modelo.Usuario;
import infraestructura.notificaciones.observer.ScrimNotificationObserver;
import infraestructura.persistencia.repository.RepositorioFactory;
import infraestructura.persistencia.repository.RepositorioUsuario;

import java.util.List;
import java.util.stream.Collectors;

public class ScrimMatchingService {
    
    private ScrimNotificationObserver observer;
    
    public ScrimMatchingService() {
        this.observer = new ScrimNotificationObserver();
    }
    
    public void notificarScrimCreado(Scrim scrim) {
        List<Usuario> usuariosInteresados = buscarUsuariosInteresados(scrim);
        
        if (!usuariosInteresados.isEmpty()) {
            observer.notificarBuscando(scrim, usuariosInteresados);
        }
    }
    
    private List<Usuario> buscarUsuariosInteresados(Scrim scrim) {
        RepositorioUsuario repo = RepositorioFactory.getRepositorioUsuario();
        List<Usuario> todosLosUsuarios = repo.listarTodos();
        
        return todosLosUsuarios.stream()
                .filter(usuario -> coincideConPreferencias(usuario, scrim))
                .collect(Collectors.toList());
    }
    
    private boolean coincideConPreferencias(Usuario usuario, Scrim scrim) {
        if (usuario.getId().equals(scrim.getCreatedBy())) {
            return false;
        }
        
        return true;
    }
}
