package br.com.logicore.modules.usuarioperfil.validator;

import br.com.logicore.common.exception.DuplicateResourceException;
import br.com.logicore.common.exception.ResourceNotFoundException;
import br.com.logicore.modules.perfil.repository.PerfilRepository;
import br.com.logicore.modules.usuarioperfil.repository.UsuarioPerfilRepository;
import br.com.logicore.modules.usuario.repository.UsuarioRepository;
import org.springframework.stereotype.Component;

@Component
public class UsuarioPerfilValidator {

    private final UsuarioPerfilRepository repository;
    private final UsuarioRepository usuarioRepository;
    private final PerfilRepository perfilRepository;

    public UsuarioPerfilValidator(UsuarioPerfilRepository repository, UsuarioRepository usuarioRepository, PerfilRepository perfilRepository) {
        this.repository = repository;
        this.usuarioRepository = usuarioRepository;
        this.perfilRepository = perfilRepository;
    }

    public void validateUsuarioExists(Long usuarioId) {
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new ResourceNotFoundException("Usuario not found with ID: " + usuarioId);
        }
    }

    public void validatePerfilExists(Long perfilId) {
        if (!perfilRepository.existsById(perfilId)) {
            throw new ResourceNotFoundException("Perfil not found with ID: " + perfilId);
        }
    }

    public void validateUniqueAssociation(Long usuarioId, Long perfilId) {
        if (repository.existsByUsuarioIdAndPerfilId(usuarioId, perfilId)) {
            throw new DuplicateResourceException("This association already exists.");
        }
    }
}
