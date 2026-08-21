package br.com.logicore.modules.employee.repository.spec;

import br.com.logicore.modules.cargo.entity.Cargo;
import br.com.logicore.modules.cargo.repository.CargoRepository;
import br.com.logicore.modules.department.entity.Department;
import br.com.logicore.modules.department.enums.DepartmentStatus;
import br.com.logicore.modules.department.repository.DepartmentRepository;
import br.com.logicore.modules.employee.entity.Employee;
import br.com.logicore.modules.employee.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class EmployeeSpecificationsIntegrationTest {

    @Autowired
    private EmployeeRepository repository;

    @Autowired
    private CargoRepository cargoRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    private Employee saveEmployee(String nome, String cpf, String matricula) {
        Cargo cargo = cargoRepository.save(Cargo.builder().nome("Dev").codigo("DEV").ativo(true).build());
        Department department = departmentRepository.save(
                Department.builder().nome("Tech").sigla("TI").status(DepartmentStatus.ACTIVE).build());

        Employee employee = Employee.builder()
                .matricula(matricula)
                .nome(nome)
                .cpf(cpf)
                .cargo(cargo)
                .departamento(department)
                .dataAdmissao(LocalDate.of(2024, 1, 1))
                .status("ACTIVE")
                .build();

        return repository.save(employee);
    }

    @Test
    void shouldFindEmployeeBySearch() {
        saveEmployee("João Silva", "12345678901", "EMP001");

        List<Employee> result = repository.findAll(EmployeeSpecifications.withSearch("silva"));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNome()).isEqualTo("João Silva");
    }

    @Test
    void shouldFindEmployeeByNome() {
        saveEmployee("João Silva", "12345678901", "EMP001");

        List<Employee> result = repository.findAll(EmployeeSpecifications.withNome("silva"));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNome()).isEqualTo("João Silva");
    }

    @Test
    void shouldFindEmployeeByCpf() {
        saveEmployee("João Silva", "12345678901", "EMP001");

        List<Employee> result = repository.findAll(EmployeeSpecifications.withCpf("12345678901"));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCpf()).isEqualTo("12345678901");
    }

    @Test
    void shouldFindEmployeeByCargoId() {
        Employee employee = saveEmployee("João Silva", "12345678901", "EMP001");
        Long cargoId = employee.getCargo().getId();

        List<Employee> result = repository.findAll(EmployeeSpecifications.withCargoId(cargoId));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCargo().getId()).isEqualTo(cargoId);
    }

    @Test
    void shouldFindEmployeeByDepartamentoId() {
        Employee employee = saveEmployee("João Silva", "12345678901", "EMP001");
        Long departamentoId = employee.getDepartamento().getId();

        List<Employee> result = repository.findAll(EmployeeSpecifications.withDepartamentoId(departamentoId));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDepartamento().getId()).isEqualTo(departamentoId);
    }
}
