package br.com.logicore.modules.usuario.service;

import br.com.logicore.common.dto.PageResponse;
import br.com.logicore.common.exception.ResourceNotFoundException;
import br.com.logicore.modules.employee.entity.Employee;
import br.com.logicore.modules.employee.repository.EmployeeRepository;
import br.com.logicore.modules.usuario.dto.CreateUsuarioRequest;
import br.com.logicore.modules.usuario.dto.UpdateUsuarioRequest;
import br.com.logicore.modules.usuario.dto.UsuarioResponse;
import br.com.logicore.modules.usuario.dto.UsuarioSummary;
import br.com.logicore.modules.usuario.entity.Usuario;
import br.com.logicore.modules.usuario.enums.UserStatus;
import br.com.logicore.modules.usuario.mapper.UsuarioMapper;
import br.com.logicore.modules.usuario.repository.UsuarioRepository;
import br.com.logicore.modules.usuario.repository.spec.UsuarioSpecifications;
import br.com.logicore.modules.usuario.validator.UsuarioValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {

    private final UsuarioRepository repository;
    private final UsuarioMapper mapper;
    private final UsuarioValidator validator;
    private final EmployeeRepository employeeRepository;

    public UsuarioService(UsuarioRepository repository, UsuarioMapper mapper, UsuarioValidator validator, EmployeeRepository employeeRepository) {
        this.repository = repository;
        this.mapper = mapper;
        this.validator = validator;
        this.employeeRepository = employeeRepository;
    }

    @Transactional
    public UsuarioResponse create(CreateUsuarioRequest request) {
        validator.validateUniqueEmail(request.getEmail());
        validator.validateUniqueFuncionarioId(request.getFuncionarioId());

        Employee funcionario = employeeRepository.findById(request.getFuncionarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + request.getFuncionarioId()));

        Usuario usuario = Usuario.builder()
                .nome(request.getNome())
                .email(request.getEmail())
                .senha(request.getSenha())
                .funcionario(funcionario)
                .build();

        return mapper.toResponse(repository.save(usuario));
    }

    @Transactional(readOnly = true)
    public PageResponse<UsuarioResponse> findAll(String search, String email, String status, Long funcionarioId, Pageable pageable) {
        Specification<Usuario> spec = Specification
                .where(UsuarioSpecifications.withSearch(search))
                .and(UsuarioSpecifications.withEmail(email))
                .and(UsuarioSpecifications.withStatus(status))
                .and(UsuarioSpecifications.withFuncionarioId(funcionarioId));

        Page<UsuarioResponse> page = repository.findAll(spec, pageable)
                .map(mapper::toResponse);

        return new PageResponse<>(page);
    }

    @Transactional(readOnly = true)
    public UsuarioSummary summary() {
        long total = repository.count();
        long active = repository.countByStatus(UserStatus.ACTIVE);
        long inactive = repository.countByStatus(UserStatus.INACTIVE);

        return UsuarioSummary.builder()
                .total(total)
                .active(active)
                .inactive(inactive)
                .build();
    }

    @Transactional(readOnly = true)
    public UsuarioResponse findById(Long id) {
        return mapper.toResponse(findUsuarioById(id));
    }

    @Transactional
    public UsuarioResponse update(Long id, UpdateUsuarioRequest request) {
        Usuario usuario = findUsuarioById(id);

        if (isPresent(request.getNome())) {
            usuario.setNome(request.getNome());
        }

        if (isPresent(request.getEmail())) {
            if (!usuario.getEmail().equalsIgnoreCase(request.getEmail())) {
                validator.validateUniqueEmailForUpdate(request.getEmail(), id);
            }
            usuario.setEmail(request.getEmail());
        }

        if (isPresent(request.getSenha())) {
            usuario.setSenha(request.getSenha());
        }

        if (request.getFuncionarioId() != null) {
            if (!usuario.getFuncionario().getId().equals(request.getFuncionarioId())) {
                validator.validateUniqueFuncionarioIdForUpdate(request.getFuncionarioId(), id);
            }
            Employee funcionario = employeeRepository.findById(request.getFuncionarioId())
                    .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + request.getFuncionarioId()));
            usuario.setFuncionario(funcionario);
        }

        if (request.getStatus() != null) {
            validator.validateStatus(request.getStatus());
            usuario.setStatus(UserStatus.valueOf(request.getStatus().toUpperCase()));
        }

        return mapper.toResponse(repository.save(usuario));
    }

    @Transactional
    public void delete(Long id) {
        Usuario usuario = findUsuarioById(id);
        usuario.setStatus(UserStatus.INACTIVE);
        repository.save(usuario);
    }

    @Transactional
    public void activate(Long id) {
        changeStatus(id, UserStatus.ACTIVE);
    }

    @Transactional
    public void deactivate(Long id) {
        changeStatus(id, UserStatus.INACTIVE);
    }

    private Usuario findUsuarioById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario not found with ID: " + id));
    }

    private void changeStatus(Long id, UserStatus status) {
        Usuario usuario = findUsuarioById(id);
        if (usuario.getStatus() != status) {
            usuario.setStatus(status);
            repository.save(usuario);
        }
    }

    private boolean isPresent(String value) {
        return value != null && !value.isBlank();
    }
}
