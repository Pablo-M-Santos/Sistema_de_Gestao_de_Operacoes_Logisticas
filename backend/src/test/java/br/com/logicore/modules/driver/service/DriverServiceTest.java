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
import br.com.logicore.modules.driver.validator.DriverValidator;
import br.com.logicore.modules.employee.entity.Employee;
import br.com.logicore.modules.employee.repository.EmployeeRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DriverServiceTest {

    @Mock
    private DriverRepository repository;

    @Mock
    private DriverMapper mapper;

    @Mock
    private DriverValidator validator;

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private DriverService service;

    @Test
    void shouldCreateDriverSuccessfully() {
        CreateDriverRequest request = new CreateDriverRequest();
        request.setFuncionarioId(1L);
        request.setCnh("12345678901");
        request.setCategoria("D");
        request.setValidadeCnh(LocalDate.of(2025, 12, 31));
        request.setObservacoes("Test");

        Employee employee = Employee.builder().id(1L).nome("Joao").matricula("EMP001").build();
        Driver entity = Driver.builder().id(1L).cnh("12345678901").categoria("D").funcionario(employee).build();
        DriverResponse response = DriverResponse.builder().id(1L).cnh("12345678901").build();

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(mapper.toResponse(entity)).thenReturn(response);
        when(repository.save(any(Driver.class))).thenReturn(entity);

        DriverResponse result = service.create(request);

        assertThat(result).isNotNull();
        assertThat(result.getCnh()).isEqualTo("12345678901");
        verify(validator).validateUniqueCnh("12345678901");
        verify(validator).validateUniqueFuncionarioId(1L);
        verify(validator).validateCategoria("D");
        verify(repository).save(any(Driver.class));
    }

    @Test
    void shouldThrowExceptionWhenEmployeeNotFound() {
        CreateDriverRequest request = new CreateDriverRequest();
        request.setFuncionarioId(99L);
        request.setCnh("12345678901");
        request.setCategoria("D");
        request.setValidadeCnh(LocalDate.of(2025, 12, 31));

        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldFindDriverByIdSuccessfully() {
        Driver driver = Driver.builder().id(1L).cnh("12345678901").build();
        DriverResponse response = DriverResponse.builder().id(1L).cnh("12345678901").build();

        when(repository.findById(1L)).thenReturn(Optional.of(driver));
        when(mapper.toResponse(driver)).thenReturn(response);

        DriverResponse result = service.findById(1L);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void shouldThrowExceptionWhenDriverDoesNotExist() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(1L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldFindDriverByFuncionarioId() {
        Driver driver = Driver.builder().id(1L).funcionario(Employee.builder().id(1L).build()).build();
        DriverResponse response = DriverResponse.builder().id(1L).funcionarioId(1L).build();

        when(repository.findAll(any(Specification.class))).thenReturn(List.of(driver));
        when(mapper.toResponse(driver)).thenReturn(response);

        DriverResponse result = service.findByFuncionarioId(1L);

        assertThat(result.getFuncionarioId()).isEqualTo(1L);
    }

    @Test
    void shouldUpdateDriverSuccessfully() {
        Driver driver = Driver.builder()
                .id(1L)
                .cnh("12345678901")
                .categoria("D")
                .observacoes("Old")
                .build();

        UpdateDriverRequest request = new UpdateDriverRequest();
        request.setCategoria("E");
        request.setObservacoes("New");

        DriverResponse response = DriverResponse.builder()
                .id(1L)
                .categoria("E")
                .observacoes("New")
                .build();

        when(repository.findById(1L)).thenReturn(Optional.of(driver));
        when(repository.save(any(Driver.class))).thenReturn(driver);
        when(mapper.toResponse(driver)).thenReturn(response);

        DriverResponse result = service.update(1L, request);

        assertThat(result.getCategoria()).isEqualTo("E");
        assertThat(result.getObservacoes()).isEqualTo("New");
        verify(repository).save(any(Driver.class));
    }

    @Test
    void shouldDeleteDriver() {
        Driver driver = Driver.builder().id(1L).build();
        when(repository.findById(1L)).thenReturn(Optional.of(driver));

        service.delete(1L);

        verify(repository).delete(driver);
    }

    @Test
    void shouldReturnDriverSummary() {
        when(repository.count()).thenReturn(5L);

        DriverSummaryResponse result = service.summary();

        assertThat(result.getTotal()).isEqualTo(5);
    }

    @Test
    void shouldReturnPagedDrivers() {
        Pageable pageable = PageRequest.of(0, 20);

        Driver driver = Driver.builder().id(1L).cnh("12345678901").build();
        DriverResponse response = DriverResponse.builder().id(1L).cnh("12345678901").build();

        Page<Driver> page = new PageImpl<>(List.of(driver));

        when(repository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
        when(mapper.toResponse(driver)).thenReturn(response);

        PageResponse<DriverResponse> result = service.findAll(null, null, null, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getCnh()).isEqualTo("12345678901");
    }
}
