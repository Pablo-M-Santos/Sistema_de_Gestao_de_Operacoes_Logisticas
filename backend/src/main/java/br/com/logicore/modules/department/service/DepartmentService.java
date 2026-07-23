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
import br.com.logicore.modules.department.validator.DepartmentValidator;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    public PageResponse<DepartmentResponse> findAll(Pageable pageable) {

        Page<DepartmentResponse> page = repository.findAllByOrderByIdAsc(pageable)
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

        validator.validateUniqueNameForUpdate(request.getNome(), id);

        department.setNome(request.getNome());
        department.setDescricao(request.getDescricao());
        department.setSigla(request.getSigla());

        return mapper.toResponse(repository.save(department));
    }

    @Transactional
    public void activate(Long id) {
        Department department = findDepartmentById(id);

        department.setStatus(DepartmentStatus.ACTIVE);
    }

    @Transactional
    public void deactivate(Long id) {
        Department department = findDepartmentById(id);

        department.setStatus(DepartmentStatus.INACTIVE);
    }

    private Department findDepartmentById(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Department not found with ID: " + id));
    }
}