package br.com.logicore.modules.department.service;

import br.com.logicore.modules.department.dto.CreateDepartmentRequest;
import br.com.logicore.modules.department.dto.DepartmentResponse;
import br.com.logicore.modules.department.dto.UpdateDepartmentRequest;
import br.com.logicore.modules.department.entity.Department;
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

    @Transactional(readOnly = true)
    public Page<DepartmentResponse> findAll(Pageable pageable) {
        return repository.findAll(pageable)
                .map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public DepartmentResponse findById(Long id) {
        Department department = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found with the ID: " + id));
        return mapper.toResponse(department);
    }

    @Transactional
    public DepartmentResponse update(Long id, UpdateDepartmentRequest request) {
        Department department = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found with the ID: " + id));

        validator.validateUniqueNameForUpdate(request.getNome(), id);

        department.setNome(request.getNome());
        department.setDescricao(request.getDescricao());
        department.setSigla(request.getSigla());
        if (request.getAtivo() != null) {
            department.setAtivo(request.getAtivo());
        }

        return mapper.toResponse(repository.save(department));
    }

    @Transactional
    public void delete(Long id) {
        Department department = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found with the ID: " + id));

        department.setAtivo(false);
        repository.save(department);
    }
}