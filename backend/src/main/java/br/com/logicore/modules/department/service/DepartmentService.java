package br.com.logicore.modules.department.service;

import br.com.logicore.common.dto.PageResponse;
import br.com.logicore.common.exception.ResourceNotFoundException;
import br.com.logicore.modules.department.dto.CreateDepartmentRequest;
import br.com.logicore.modules.department.dto.DepartmentResponse;
import br.com.logicore.modules.department.dto.DepartmentSummaryResponse;
import br.com.logicore.modules.department.dto.UpdateDepartmentRequest;
import br.com.logicore.modules.department.entity.Department;
import br.com.logicore.modules.department.enums.DepartmentStatus;
import br.com.logicore.modules.department.mapper.DepartmentMapper;
import br.com.logicore.modules.department.repository.DepartmentRepository;
import br.com.logicore.modules.department.repository.spec.DepartmentSpecifications;
import br.com.logicore.modules.department.validator.DepartmentValidator;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DepartmentService {

    private final DepartmentRepository repository;
    private final DepartmentMapper mapper;
    private final DepartmentValidator validator;

    public DepartmentService(DepartmentRepository repository, DepartmentMapper mapper, DepartmentValidator validator) {
        this.repository = repository;
        this.mapper = mapper;
        this.validator = validator;
    }

    @Transactional
    public DepartmentResponse create(CreateDepartmentRequest request) {
        validator.validateUniqueName(request.getNome());

        Department department = mapper.toEntity(request);

        return mapper.toResponse(repository.save(department));
    }

    @Transactional(readOnly = true)
    public PageResponse<DepartmentResponse> findAll(String search, String statusFilter, Pageable pageable) {
        DepartmentStatus status = parseStatus(statusFilter);

        Specification<Department> spec = Specification
                .where(DepartmentSpecifications.withSearch(search))
                .and(DepartmentSpecifications.withStatus(status));

        Page<DepartmentResponse> page = repository.findAll(spec, pageable)
                .map(mapper::toResponse);

        return new PageResponse<>(page);
    }

    @Transactional(readOnly = true)
    public DepartmentSummaryResponse summary() {

        long total = repository.count();

        long active = repository.countByStatus(
                DepartmentStatus.ACTIVE
        );

        long inactive = repository.countByStatus(
                DepartmentStatus.INACTIVE
        );

        return DepartmentSummaryResponse.builder()
                .total(total)
                .active(active)
                .inactive(inactive)
                .build();
    }

    @Transactional(readOnly = true)
    public DepartmentResponse findById(Long id) {
        return mapper.toResponse(findDepartmentById(id));
    }

    @Transactional
    public DepartmentResponse update(Long id, UpdateDepartmentRequest request) {

        Department department = findDepartmentById(id);

        if (!department.getNome().equalsIgnoreCase(request.getNome())) {
            validator.validateUniqueNameForUpdate(request.getNome(), id);
        }

        department.setNome(request.getNome());
        department.setDescricao(request.getDescricao());
        department.setSigla(request.getSigla());

        return mapper.toResponse(repository.save(department));
    }

    @Transactional
    public void activate(Long id) {
        changeStatus(id, DepartmentStatus.ACTIVE);
    }

    @Transactional
    public void deactivate(Long id) {
        changeStatus(id, DepartmentStatus.INACTIVE);
    }

    private Department findDepartmentById(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Department not found with ID: " + id));
    }

    private DepartmentStatus parseStatus(String status) {
        if (status == null || status.isBlank() || "ALL".equalsIgnoreCase(status)) {
            return null;
        }
        try {
            return DepartmentStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private void changeStatus(Long id, DepartmentStatus status) {
        Department department = findDepartmentById(id);

        if (department.getStatus() != status) {
            department.setStatus(status);
        }
    }
}