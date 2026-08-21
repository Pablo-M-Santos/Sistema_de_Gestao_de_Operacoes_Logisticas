package br.com.logicore.modules.employee.mapper;

import br.com.logicore.modules.address.entity.Address;
import br.com.logicore.modules.cargo.entity.Cargo;
import br.com.logicore.modules.department.entity.Department;
import br.com.logicore.modules.employee.dto.EmployeeResponse;
import br.com.logicore.modules.employee.entity.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class EmployeeMapperTest {

    private EmployeeMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new EmployeeMapper();
    }

    @Test
    void shouldConvertEntityToResponseWithRelations() {
        Cargo cargo = Cargo.builder().id(5L).nome("Dev").codigo("DEV").build();
        Department department = Department.builder().id(7L).nome("Tech").sigla("TI").build();
        Address address = Address.builder()
                .id(9L).cep("01001000").logradouro("Praça da Sé").numero("100")
                .complemento("Sala 1").bairro("Sé").cidade("São Paulo").estado("SP")
                .pais("Brasil").latitude(new BigDecimal("-23.55052000"))
                .longitude(new BigDecimal("-46.63330800")).build();

        Employee employee = Employee.builder()
                .id(1L).matricula("EMP001").nome("João Silva").cpf("12345678901")
                .cargo(cargo).departamento(department).endereco(address)
                .build();

        EmployeeResponse response = mapper.toResponse(employee);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getCargoId()).isEqualTo(5L);
        assertThat(response.getCargoNome()).isEqualTo("Dev");
        assertThat(response.getCargoCodigo()).isEqualTo("DEV");
        assertThat(response.getDepartamentoId()).isEqualTo(7L);
        assertThat(response.getDepartamentoNome()).isEqualTo("Tech");
        assertThat(response.getDepartamentoSigla()).isEqualTo("TI");
        assertThat(response.getEnderecoId()).isEqualTo(9L);
        assertThat(response.getEnderecoCep()).isEqualTo("01001000");
        assertThat(response.getEnderecoLogradouro()).isEqualTo("Praça da Sé");
        assertThat(response.getEnderecoNumero()).isEqualTo("100");
        assertThat(response.getEnderecoComplemento()).isEqualTo("Sala 1");
        assertThat(response.getEnderecoBairro()).isEqualTo("Sé");
        assertThat(response.getEnderecoCidade()).isEqualTo("São Paulo");
        assertThat(response.getEnderecoEstado()).isEqualTo("SP");
        assertThat(response.getEnderecoPais()).isEqualTo("Brasil");
        assertThat(response.getEnderecoLatitude()).isEqualByComparingTo(new BigDecimal("-23.55052000"));
        assertThat(response.getEnderecoLongitude()).isEqualByComparingTo(new BigDecimal("-46.63330800"));
    }

    @Test
    void shouldConvertEntityToResponseWithNullRelations() {
        Employee employee = Employee.builder()
                .id(1L).matricula("EMP001").nome("João Silva").cpf("12345678901")
                .build();

        EmployeeResponse response = mapper.toResponse(employee);

        assertThat(response).isNotNull();
        assertThat(response.getCargoId()).isNull();
        assertThat(response.getCargoNome()).isNull();
        assertThat(response.getCargoCodigo()).isNull();
        assertThat(response.getDepartamentoId()).isNull();
        assertThat(response.getDepartamentoNome()).isNull();
        assertThat(response.getDepartamentoSigla()).isNull();
        assertThat(response.getEnderecoId()).isNull();
        assertThat(response.getEnderecoCep()).isNull();
    }
}
