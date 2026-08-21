package br.com.logicore.modules.employee.service;

import br.com.logicore.common.dto.PageResponse;
import br.com.logicore.common.exception.BusinessException;
import br.com.logicore.common.exception.ResourceNotFoundException;
import br.com.logicore.modules.address.entity.Address;
import br.com.logicore.modules.address.repository.AddressRepository;
import br.com.logicore.modules.cargo.entity.Cargo;
import br.com.logicore.modules.cargo.repository.CargoRepository;
import br.com.logicore.modules.department.entity.Department;
import br.com.logicore.modules.department.enums.DepartmentStatus;
import br.com.logicore.modules.department.repository.DepartmentRepository;
import br.com.logicore.modules.employee.dto.CreateEmployeeRequest;
import br.com.logicore.modules.employee.dto.EmployeeResponse;
import br.com.logicore.modules.employee.dto.EmployeeSummaryResponse;
import br.com.logicore.modules.employee.dto.UpdateEmployeeRequest;
import br.com.logicore.modules.employee.entity.Employee;
import br.com.logicore.modules.employee.mapper.EmployeeMapper;
import br.com.logicore.modules.employee.repository.EmployeeRepository;
import br.com.logicore.modules.employee.validator.EmployeeValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository repository;

    @Mock
    private EmployeeMapper mapper;

    @Mock
    private EmployeeValidator validator;

    @Mock
    private CargoRepository cargoRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private AddressRepository addressRepository;

    @InjectMocks
    private EmployeeService service;

    private CreateEmployeeRequest buildCreateRequest() {
        CreateEmployeeRequest request = new CreateEmployeeRequest();
        request.setMatricula("EMP001");
        request.setNome("João Silva");
        request.setCpf("12345678901");
        request.setCargoId(1L);
        request.setDepartamentoId(1L);
        request.setEnderecoId(2L);
        request.setDataAdmissao(LocalDate.of(2024, 1, 1));
        return request;
    }

    @Test
    void shouldCreateEmployeeSuccessfully() {
        CreateEmployeeRequest request = buildCreateRequest();

        Cargo cargo = Cargo.builder().id(1L).nome("Dev").codigo("DEV").build();
        Department department = Department.builder().id(1L).nome("Tech").sigla("TI").status(DepartmentStatus.ACTIVE).build();
        Address address = Address.builder().id(2L).cep("01001000").build();
        Employee saved = Employee.builder()
                .id(1L).matricula("EMP001").nome("João Silva").cpf("12345678901")
                .cargo(cargo).departamento(department).endereco(address)
                .dataAdmissao(LocalDate.of(2024, 1, 1)).build();
        EmployeeResponse response = EmployeeResponse.builder().id(1L).build();

        when(cargoRepository.findById(1L)).thenReturn(Optional.of(cargo));
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(addressRepository.findById(2L)).thenReturn(Optional.of(address));
        when(repository.save(any(Employee.class))).thenReturn(saved);
        when(mapper.toResponse(saved)).thenReturn(response);

        service.create(request);

        verify(validator).validateUniqueCpf("12345678901");
        verify(validator).validateUniqueMatricula("EMP001");
        verify(repository).save(any(Employee.class));
    }

    @Test
    void shouldThrowWhenCargoNotFoundOnCreate() {
        CreateEmployeeRequest request = buildCreateRequest();

        when(cargoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldThrowWhenDepartmentNotFoundOnCreate() {
        CreateEmployeeRequest request = buildCreateRequest();

        when(cargoRepository.findById(1L)).thenReturn(Optional.of(Cargo.builder().id(1L).build()));
        when(departmentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldThrowWhenAddressNotFoundOnCreate() {
        CreateEmployeeRequest request = buildCreateRequest();

        when(cargoRepository.findById(1L)).thenReturn(Optional.of(Cargo.builder().id(1L).build()));
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(Department.builder().id(1L).build()));
        when(addressRepository.findById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldFindEmployeeById() {
        Employee employee = Employee.builder().id(1L).matricula("EMP001").build();
        EmployeeResponse response = EmployeeResponse.builder().id(1L).build();

        when(repository.findById(1L)).thenReturn(Optional.of(employee));
        when(mapper.toResponse(employee)).thenReturn(response);

        service.findById(1L);

        verify(mapper).toResponse(employee);
    }

    @Test
    void shouldThrowWhenEmployeeNotFoundOnFindById() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(1L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldUpdateEmployeePartially() {
        Employee employee = Employee.builder().id(1L).matricula("EMP001").nome("João Silva").cpf("12345678901").build();
        UpdateEmployeeRequest request = new UpdateEmployeeRequest();
        request.setNome("João Silva Updated");
        EmployeeResponse response = EmployeeResponse.builder().id(1L).build();

        when(repository.findById(1L)).thenReturn(Optional.of(employee));
        when(repository.save(employee)).thenReturn(employee);
        when(mapper.toResponse(employee)).thenReturn(response);

        service.update(1L, request);

        assertThat(employee.getNome()).isEqualTo("João Silva Updated");
        verify(validator, never()).validateUniqueCpfForUpdate(anyString(), anyLong());
        verify(repository).save(employee);
    }

    @Test
    void shouldUpdateEmployeeRelationships() {
        Employee employee = Employee.builder().id(1L).matricula("EMP001").nome("João Silva").build();
        Cargo cargo2 = Cargo.builder().id(2L).nome("Mgr").codigo("MGR").build();
        Department department2 = Department.builder().id(2L).nome("Ops").sigla("OP").status(DepartmentStatus.ACTIVE).build();
        Address address3 = Address.builder().id(3L).cep("20040002").build();
        UpdateEmployeeRequest request = new UpdateEmployeeRequest();
        request.setCargoId(2L);
        request.setDepartamentoId(2L);
        request.setEnderecoId(3L);
        EmployeeResponse response = EmployeeResponse.builder().id(1L).build();

        when(repository.findById(1L)).thenReturn(Optional.of(employee));
        when(cargoRepository.findById(2L)).thenReturn(Optional.of(cargo2));
        when(departmentRepository.findById(2L)).thenReturn(Optional.of(department2));
        when(addressRepository.findById(3L)).thenReturn(Optional.of(address3));
        when(repository.save(employee)).thenReturn(employee);
        when(mapper.toResponse(employee)).thenReturn(response);

        service.update(1L, request);

        assertThat(employee.getCargo()).isEqualTo(cargo2);
        assertThat(employee.getDepartamento()).isEqualTo(department2);
        assertThat(employee.getEndereco()).isEqualTo(address3);
    }

    @Test
    void shouldThrowBusinessExceptionWhenUpdateWithInvalidStatus() {
        Employee employee = Employee.builder().id(1L).matricula("EMP001").build();
        UpdateEmployeeRequest request = new UpdateEmployeeRequest();
        request.setStatus("XYZ");

        when(repository.findById(1L)).thenReturn(Optional.of(employee));
        doThrow(new BusinessException("Status must be ACTIVE or INACTIVE."))
                .when(validator).validateStatus("XYZ");

        assertThatThrownBy(() -> service.update(1L, request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Status must be ACTIVE or INACTIVE.");

        verify(repository, never()).save(any(Employee.class));
    }

    @Test
    void shouldSoftDeleteEmployee() {
        Employee employee = Employee.builder().id(1L).matricula("EMP001").status("ACTIVE").build();

        when(repository.findById(1L)).thenReturn(Optional.of(employee));
        when(repository.save(employee)).thenReturn(employee);

        service.delete(1L);

        assertThat(employee.getStatus()).isEqualTo("INACTIVE");
        verify(repository).save(employee);
    }

    @Test
    void shouldReturnEmployeeSummary() {
        when(repository.count()).thenReturn(10L);
        when(repository.countActive()).thenReturn(6L);
        when(repository.countInactive()).thenReturn(3L);
        when(repository.countWithAddress()).thenReturn(4L);

        EmployeeSummaryResponse result = service.summary();

        assertThat(result.getTotal()).isEqualTo(10);
        assertThat(result.getActive()).isEqualTo(6);
        assertThat(result.getInactive()).isEqualTo(3);
        assertThat(result.getWithAddress()).isEqualTo(4);
        assertThat(result.getWithoutAddress()).isEqualTo(6);
    }

    @Test
    void shouldReturnPagedEmployees() {
        Employee employee = Employee.builder().id(1L).matricula("EMP001").build();
        EmployeeResponse response = EmployeeResponse.builder().id(1L).build();
        Page<Employee> page = new PageImpl<>(List.of(employee), PageRequest.of(0, 20), 1);

        when(repository.findAll(any(Specification.class), eq(PageRequest.of(0, 20)))).thenReturn(page);
        when(mapper.toResponse(employee)).thenReturn(response);

        PageResponse<EmployeeResponse> result = service.findAll(null, null, null, null, null, PageRequest.of(0, 20));

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo(1L);
    }
}
