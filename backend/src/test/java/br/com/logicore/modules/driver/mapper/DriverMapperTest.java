package br.com.logicore.modules.driver.mapper;

import br.com.logicore.modules.driver.dto.DriverResponse;
import br.com.logicore.modules.driver.entity.Driver;
import br.com.logicore.modules.employee.entity.Employee;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class DriverMapperTest {

    @Test
    void shouldMapDriverToResponse() {
        DriverMapper mapper = new DriverMapper();

        Employee employee = Employee.builder()
                .id(1L)
                .nome("Joao Silva")
                .matricula("EMP001")
                .build();

        Driver driver = Driver.builder()
                .id(1L)
                .funcionario(employee)
                .cnh("12345678901")
                .categoria("D")
                .validadeCnh(LocalDate.of(2025, 12, 31))
                .observacoes("Test driver")
                .build();

        DriverResponse response = mapper.toResponse(driver);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getFuncionarioId()).isEqualTo(1L);
        assertThat(response.getFuncionarioNome()).isEqualTo("Joao Silva");
        assertThat(response.getFuncionarioMatricula()).isEqualTo("EMP001");
        assertThat(response.getCnh()).isEqualTo("12345678901");
        assertThat(response.getCategoria()).isEqualTo("D");
        assertThat(response.getValidadeCnh()).isEqualTo(LocalDate.of(2025, 12, 31));
        assertThat(response.getObservacoes()).isEqualTo("Test driver");
    }
}
