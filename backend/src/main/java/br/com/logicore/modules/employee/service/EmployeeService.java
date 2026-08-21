package br.com.logicore.modules.employee.service;

import br.com.logicore.common.dto.PageResponse;
import br.com.logicore.common.exception.ResourceNotFoundException;
import br.com.logicore.modules.address.entity.Address;
import br.com.logicore.modules.address.repository.AddressRepository;
import br.com.logicore.modules.cargo.entity.Cargo;
import br.com.logicore.modules.cargo.repository.CargoRepository;
import br.com.logicore.modules.department.entity.Department;
import br.com.logicore.modules.department.repository.DepartmentRepository;
import br.com.logicore.modules.employee.dto.EmployeeResponse;
import br.com.logicore.modules.employee.dto.CreateEmployeeRequest;
import br.com.logicore.modules.employee.dto.UpdateEmployeeRequest;
import br.com.logicore.modules.employee.entity.Employee;
import br.com.logicore.modules.employee.mapper.EmployeeMapper;
import br.com.logicore.modules.employee.repository.EmployeeRepository;
import br.com.logicore.modules.employee.repository.spec.EmployeeSpecifications;
import br.com.logicore.modules.employee.validator.EmployeeValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EmployeeService {


    private final EmployeeRepository repository;
    private final EmployeeMapper mapper;
    private final EmployeeValidator validator;

    private final CargoRepository cargoRepository;
    private final DepartmentRepository departmentRepository;
    private final AddressRepository addressRepository;


    public EmployeeService(EmployeeRepository repository, EmployeeMapper mapper, EmployeeValidator validator, CargoRepository cargoRepository, DepartmentRepository departmentRepository, AddressRepository addressRepository) {

        this.repository = repository;
        this.mapper = mapper;
        this.validator = validator;
        this.cargoRepository = cargoRepository;
        this.departmentRepository = departmentRepository;
        this.addressRepository = addressRepository;
    }


    @Transactional
    public EmployeeResponse create(CreateEmployeeRequest request) {
        validator.validateUniqueCpf(request.getCpf());
        validator.validateUniqueMatricula(request.getMatricula());

        Cargo cargo = cargoRepository.findById(request.getCargoId())
                .orElseThrow(() -> new ResourceNotFoundException("Cargo not found with ID: " + request.getCargoId()));

        Department department = departmentRepository.findById(request.getDepartamentoId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with ID: " + request.getDepartamentoId()));

        Address address = null;
        if (request.getEnderecoId() != null) {
            address = addressRepository.findById(request.getEnderecoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Address not found with ID: " + request.getEnderecoId()));
        }

        Employee employee = Employee.builder()
                .matricula(request.getMatricula())
                .nome(request.getNome())
                .cpf(request.getCpf())
                .rg(request.getRg())
                .dataNascimento(request.getDataNascimento())
                .telefone(request.getTelefone())
                .email(request.getEmail())
                .cargo(cargo)
                .departamento(department)
                .endereco(address)
                .dataAdmissao(request.getDataAdmissao())
                .build();

        return mapper.toResponse(repository.save(employee));

    }


    @Transactional(readOnly = true)
    public PageResponse<EmployeeResponse> findAll(
            String search,
            String nome,
            String cpf,
            Long cargoId,
            Long departamentoId,
            Pageable pageable) {

        Specification<Employee> spec = Specification
                .where(EmployeeSpecifications.withSearch(search))
                .and(EmployeeSpecifications.withNome(nome))
                .and(EmployeeSpecifications.withCpf(cpf))
                .and(EmployeeSpecifications.withCargoId(cargoId))
                .and(EmployeeSpecifications.withDepartamentoId(departamentoId));

        Page<EmployeeResponse> page = repository.findAll(spec, pageable)
                .map(mapper::toResponse);

        return new PageResponse<>(page);
    }


    @Transactional(readOnly = true)
    public List<EmployeeResponse> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public br.com.logicore.modules.employee.dto.EmployeeSummaryResponse summary() {
        long total = repository.count();
        long active = repository.countActive();
        long inactive = repository.countInactive();
        long withAddress = repository.countWithAddress();
        long withoutAddress = total - withAddress;

        return br.com.logicore.modules.employee.dto.EmployeeSummaryResponse.builder()
                .total(total)
                .active(active)
                .inactive(inactive)
                .withAddress(withAddress)
                .withoutAddress(withoutAddress)
                .build();
    }


    @Transactional(readOnly = true)
    public EmployeeResponse findById(Long id) {
        Employee employee = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + id));
        return mapper.toResponse(employee);
    }

    @Transactional
    public EmployeeResponse update(Long id, UpdateEmployeeRequest request) {
        Employee employee = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + id));

        if (request.getCpf() != null) {
            validator.validateUniqueCpfForUpdate(request.getCpf(), id);
            employee.setCpf(request.getCpf());
        }

        if (request.getMatricula() != null) {
            validator.validateUniqueMatriculaForUpdate(request.getMatricula(), id);
            employee.setMatricula(request.getMatricula());
        }


        if (request.getNome() != null) employee.setNome(request.getNome());
        if (request.getRg() != null) employee.setRg(request.getRg());
        if (request.getTelefone() != null) employee.setTelefone(request.getTelefone());
        if (request.getEmail() != null) employee.setEmail(request.getEmail());
        if (request.getDataNascimento() != null) employee.setDataNascimento(request.getDataNascimento());

        if (request.getCargoId() != null) {
            Cargo cargo = cargoRepository.findById(request.getCargoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cargo not found."));
            employee.setCargo(cargo);
        }


        if (request.getDepartamentoId() != null) {
            Department department = departmentRepository.findById(request.getDepartamentoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found."));
            employee.setDepartamento(department);
        }


        if (request.getEnderecoId() != null) {
            Address address = addressRepository.findById(request.getEnderecoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Address not found."));
            employee.setEndereco(address);
        }

        if (request.getDataAdmissao() != null) employee.setDataAdmissao(request.getDataAdmissao());
        if (request.getStatus() != null) {
            validator.validateStatus(request.getStatus());
            employee.setStatus(request.getStatus());
        }

        return mapper.toResponse(repository.save(employee));

    }


    @Transactional
    public void delete(Long id) {

        Employee employee = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + id));

        employee.setStatus("INACTIVE");

        repository.save(employee);
    }

}