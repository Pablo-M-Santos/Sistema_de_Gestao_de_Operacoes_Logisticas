package br.com.logicore.modules.permissao.service;

import br.com.logicore.common.dto.PageResponse;
import br.com.logicore.common.exception.ResourceNotFoundException;
import br.com.logicore.modules.permissao.dto.CreatePermissaoRequest;
import br.com.logicore.modules.permissao.dto.PermissaoResponse;
import br.com.logicore.modules.permissao.dto.PermissaoSummary;
import br.com.logicore.modules.permissao.dto.UpdatePermissaoRequest;
import br.com.logicore.modules.permissao.entity.Permissao;
import br.com.logicore.modules.permissao.mapper.PermissaoMapper;
import br.com.logicore.modules.permissao.repository.PermissaoRepository;
import br.com.logicore.modules.permissao.repository.spec.PermissaoSpecifications;
import br.com.logicore.modules.permissao.validator.PermissaoValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PermissaoService {

    private final PermissaoRepository repository;
    private final PermissaoMapper mapper;
    private final PermissaoValidator validator;

    public PermissaoService(PermissaoRepository repository, PermissaoMapper mapper, PermissaoValidator validator) {
        this.repository = repository;
        this.mapper = mapper;
        this.validator = validator;
    }

    @Transactional
    public PermissaoResponse create(CreatePermissaoRequest request) {
        validator.validateUniqueNome(request.getNome());

        Permissao permissao = mapper.toEntity(request);

        return mapper.toResponse(repository.save(permissao));
    }

    @Transactional(readOnly = true)
    public PageResponse<PermissaoResponse> findAll(String search, Pageable pageable) {
        Specification<Permissao> spec = Specification
                .where(PermissaoSpecifications.withSearch(search));

        Page<PermissaoResponse> page = repository.findAll(spec, pageable)
                .map(mapper::toResponse);

        return new PageResponse<>(page);
    }

    @Transactional(readOnly = true)
    public PermissaoSummary summary() {
        long total = repository.count();

        return PermissaoSummary.builder()
                .total(total)
                .build();
    }

    @Transactional(readOnly = true)
    public PermissaoResponse findById(Long id) {
        return mapper.toResponse(findPermissaoById(id));
    }

    @Transactional
    public PermissaoResponse update(Long id, UpdatePermissaoRequest request) {
        Permissao permissao = findPermissaoById(id);

        if (request.getNome() != null) {
            if (!permissao.getNome().equalsIgnoreCase(request.getNome())) {
                validator.validateUniqueNomeForUpdate(request.getNome(), id);
            }
            permissao.setNome(request.getNome());
        }

        if (request.getDescricao() != null) {
            permissao.setDescricao(request.getDescricao());
        }

        return mapper.toResponse(repository.save(permissao));
    }

    @Transactional
    public void delete(Long id) {
        Permissao permissao = findPermissaoById(id);
        repository.delete(permissao);
    }

    private Permissao findPermissaoById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permissao not found with ID: " + id));
    }
}
