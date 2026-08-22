package br.com.logicore.modules.driver.service;

import br.com.logicore.common.dto.PageResponse;
import br.com.logicore.common.exception.ResourceNotFoundException;
import br.com.logicore.modules.driver.dto.DriverResponse;
import br.com.logicore.modules.driver.dto.DriverSummaryResponse;
import br.com.logicore.modules.driver.dto.CreateDriverRequest;
import br.com.logicore.modules.driver.dto.UpdateDriverRequest;
import br.com.logicore.modules.driver.entity.Driver;
import br.com.logicore.modules.driver.mapper.DriverMapper;
import br.com.logicore.modules.driver.repository.DriverRepository;
import br.com.logicore.modules.driver.repository.spec.DriverSpecifications;
import br.com.logicore.modules.driver.validator.DriverValidator;
import br.com.logicore.modules.employee.entity.Employee;
import br.com.logicore.modules.employee.repository.EmployeeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DriverService {

    private final DriverRepository repository;
    private final DriverMapper mapper;
    private final DriverValidator validator;
    private final EmployeeRepository employeeRepository;

    public DriverService(DriverRepository repository, DriverMapper mapper, DriverValidator validator, EmployeeRepository employeeRepository) {
        this.repository = repository;
        this.mapper = mapper;
        this.validator = validator;
        this.employeeRepository = employeeRepository;
    }

    @Transactional
    public DriverResponse create(CreateDriverRequest request) {
        validator.validateUniqueCnh(request.getCnh());
        validator.validateUniqueFuncionarioId(request.getFuncionarioId());
        validator.validateCategoria(request.getCategoria());

        Employee funcionario = employeeRepository.findById(request.getFuncionarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + request.getFuncionarioId()));

        Driver driver = Driver.builder()
                .funcionario(funcionario)
                .cnh(request.getCnh())
                .categoria(request.getCategoria().trim().toUpperCase())
                .validadeCnh(request.getValidadeCnh())
                .observacoes(request.getObservacoes())
                .build();

        return mapper.toResponse(repository.save(driver));
    }

    @Transactional(readOnly = true)
    public PageResponse<DriverResponse> findAll(String search, String categoria, Long funcionarioId, Pageable pageable) {
        Specification<Driver> spec = Specification
                .where(DriverSpecifications.withSearch(search))
                .and(DriverSpecifications.withCategoria(categoria))
                .and(DriverSpecifications.withFuncionarioId(funcionarioId));

        Page<DriverResponse> page = repository.findAll(spec, pageable)
                .map(mapper::toResponse);

        return new PageResponse<>(page);
    }

    @Transactional(readOnly = true)
    public DriverSummaryResponse summary() {
        long total = repository.count();
        return DriverSummaryResponse.builder()
                .total(total)
                .build();
    }

    @Transactional(readOnly = true)
    public DriverResponse findById(Long id) {
        return mapper.toResponse(findDriverById(id));
    }

    @Transactional(readOnly = true)
    public DriverResponse findByFuncionarioId(Long funcionarioId) {
        Driver driver = repository.findAll(DriverSpecifications.withFuncionarioId(funcionarioId))
                .stream()
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found for employee ID: " + funcionarioId));
        return mapper.toResponse(driver);
    }

    @Transactional
    public DriverResponse update(Long id, UpdateDriverRequest request) {
        Driver driver = findDriverById(id);

        if (isPresent(request.getCnh())) {
            if (!driver.getCnh().equalsIgnoreCase(request.getCnh())) {
                validator.validateUniqueCnhForUpdate(request.getCnh(), id);
            }
            driver.setCnh(request.getCnh());
        }

        if (isPresent(request.getCategoria())) {
            validator.validateCategoria(request.getCategoria());
            driver.setCategoria(request.getCategoria().trim().toUpperCase());
        }

        if (request.getValidadeCnh() != null) {
            driver.setValidadeCnh(request.getValidadeCnh());
        }

        if (request.getObservacoes() != null) {
            driver.setObservacoes(request.getObservacoes());
        }

        return mapper.toResponse(repository.save(driver));
    }

    @Transactional
    public void delete(Long id) {
        Driver driver = findDriverById(id);
        repository.delete(driver);
    }

    private Driver findDriverById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with ID: " + id));
    }

    private boolean isPresent(String value) {
        return value != null && !value.isBlank();
    }
}
