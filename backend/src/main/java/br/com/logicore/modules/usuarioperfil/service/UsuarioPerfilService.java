package br.com.logicore.modules.usuarioperfil.service;

import br.com.logicore.common.dto.PageResponse;
import br.com.logicore.common.exception.ResourceNotFoundException;
import br.com.logicore.modules.perfil.entity.Perfil;
import br.com.logicore.modules.perfil.repository.PerfilRepository;
import br.com.logicore.modules.usuarioperfil.dto.CreateUsuarioPerfilRequest;
import br.com.logicore.modules.usuarioperfil.dto.UsuarioPerfilResponse;
import br.com.logicore.modules.usuarioperfil.dto.UsuarioPerfilSummary;
import br.com.logicore.modules.usuarioperfil.entity.UsuarioPerfil;
import br.com.logicore.modules.usuarioperfil.mapper.UsuarioPerfilMapper;
import br.com.logicore.modules.usuarioperfil.repository.UsuarioPerfilRepository;
import br.com.logicore.modules.usuarioperfil.repository.spec.UsuarioPerfilSpecifications;
import br.com.logicore.modules.usuarioperfil.validator.UsuarioPerfilValidator;
import br.com.logicore.modules.usuario.entity.Usuario;
import br.com.logicore.modules.usuario.repository.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioPerfilService {

    private final UsuarioPerfilRepository repository;
    private final UsuarioPerfilMapper mapper;
    private final UsuarioPerfilValidator validator;
    private final UsuarioRepository usuarioRepository;
    private final PerfilRepository perfilRepository;

    public UsuarioPerfilService(UsuarioPerfilRepository repository, UsuarioPerfilMapper mapper, UsuarioPerfilValidator validator, UsuarioRepository usuarioRepository, PerfilRepository perfilRepository) {
        this.repository = repository;
        this.mapper = mapper;
        this.validator = validator;
        this.usuarioRepository = usuarioRepository;
        this.perfilRepository = perfilRepository;
    }

    @Transactional
    public UsuarioPerfilResponse create(CreateUsuarioPerfilRequest request) {
        validator.validateUsuarioExists(request.getUsuarioId());
        validator.validatePerfilExists(request.getPerfilId());
        validator.validateUniqueAssociation(request.getUsuarioId(), request.getPerfilId());

        Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario not found with ID: " + request.getUsuarioId()));
        Perfil perfil = perfilRepository.findById(request.getPerfilId())
                .orElseThrow(() -> new ResourceNotFoundException("Perfil not found with ID: " + request.getPerfilId()));

        UsuarioPerfil entity = UsuarioPerfil.builder()
                .usuario(usuario)
                .perfil(perfil)
                .build();

        return mapper.toResponse(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public PageResponse<UsuarioPerfilResponse> findAll(String search, Long usuarioId, Long perfilId, Pageable pageable) {
        Specification<UsuarioPerfil> spec = Specification
                .where(UsuarioPerfilSpecifications.withSearch(search))
                .and(UsuarioPerfilSpecifications.withUsuarioId(usuarioId))
                .and(UsuarioPerfilSpecifications.withPerfilId(perfilId));

        Page<UsuarioPerfilResponse> page = repository.findAll(spec, pageable)
                .map(mapper::toResponse);

        return new PageResponse<>(page);
    }

    @Transactional(readOnly = true)
    public UsuarioPerfilResponse findById(Long id) {
        return mapper.toResponse(findUsuarioPerfilById(id));
    }

    @Transactional(readOnly = true)
    public UsuarioPerfilSummary summary() {
        long total = repository.count();
        return UsuarioPerfilSummary.builder().total(total).build();
    }

    @Transactional
    public void delete(Long id) {
        UsuarioPerfil entity = findUsuarioPerfilById(id);
        repository.delete(entity);
    }

    private UsuarioPerfil findUsuarioPerfilById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("UsuarioPerfil association not found with ID: " + id));
    }
}
