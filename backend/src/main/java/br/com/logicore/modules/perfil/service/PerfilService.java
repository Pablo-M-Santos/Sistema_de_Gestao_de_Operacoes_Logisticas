package br.com.logicore.modules.perfil.service;

import br.com.logicore.common.dto.PageResponse;
import br.com.logicore.common.exception.ResourceNotFoundException;
import br.com.logicore.modules.perfil.dto.CreatePerfilRequest;
import br.com.logicore.modules.perfil.dto.PerfilResponse;
import br.com.logicore.modules.perfil.dto.PerfilSummary;
import br.com.logicore.modules.perfil.dto.UpdatePerfilRequest;
import br.com.logicore.modules.perfil.entity.Perfil;
import br.com.logicore.modules.perfil.mapper.PerfilMapper;
import br.com.logicore.modules.perfil.repository.PerfilRepository;
import br.com.logicore.modules.perfil.repository.spec.PerfilSpecifications;
import br.com.logicore.modules.perfil.validator.PerfilValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PerfilService {

    private final PerfilRepository repository;
    private final PerfilMapper mapper;
    private final PerfilValidator validator;

    public PerfilService(PerfilRepository repository, PerfilMapper mapper, PerfilValidator validator) {
        this.repository = repository;
        this.mapper = mapper;
        this.validator = validator;
    }

    @Transactional
    public PerfilResponse create(CreatePerfilRequest request) {
        validator.validateUniqueNome(request.getNome());

        Perfil perfil = mapper.toEntity(request);

        return mapper.toResponse(repository.save(perfil));
    }

    @Transactional(readOnly = true)
    public PageResponse<PerfilResponse> findAll(String search, Pageable pageable) {
        Specification<Perfil> spec = Specification
                .where(PerfilSpecifications.withSearch(search));

        Page<PerfilResponse> page = repository.findAll(spec, pageable)
                .map(mapper::toResponse);

        return new PageResponse<>(page);
    }

    @Transactional(readOnly = true)
    public PerfilSummary summary() {
        long total = repository.count();

        return PerfilSummary.builder()
                .total(total)
                .build();
    }

    @Transactional(readOnly = true)
    public PerfilResponse findById(Long id) {
        return mapper.toResponse(findPerfilById(id));
    }

    @Transactional
    public PerfilResponse update(Long id, UpdatePerfilRequest request) {
        Perfil perfil = findPerfilById(id);

        if (request.getNome() != null) {
            if (!perfil.getNome().equalsIgnoreCase(request.getNome())) {
                validator.validateUniqueNomeForUpdate(request.getNome(), id);
            }
            perfil.setNome(request.getNome());
        }

        if (request.getDescricao() != null) {
            perfil.setDescricao(request.getDescricao());
        }

        return mapper.toResponse(repository.save(perfil));
    }

    @Transactional
    public void delete(Long id) {
        Perfil perfil = findPerfilById(id);
        repository.delete(perfil);
    }

    private Perfil findPerfilById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil not found with ID: " + id));
    }
}
