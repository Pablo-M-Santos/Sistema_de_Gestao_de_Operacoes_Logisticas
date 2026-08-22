package br.com.logicore.modules.driver.repository.spec;

import br.com.logicore.modules.driver.entity.Driver;
import br.com.logicore.modules.driver.repository.DriverRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Sql(statements = {
        "INSERT INTO cargo (ativo, atualizado_em, codigo, criado_em, descricao, nome, id) VALUES (true, CURRENT_TIMESTAMP, 'DEV', CURRENT_TIMESTAMP, null, 'Developer', 1)",
        "INSERT INTO department (atualizado_em, criado_em, descricao, nome, sigla, status, id) VALUES (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, null, 'Tech', 'TI', 'ACTIVE', 1)",
        "INSERT INTO employee (id, matricula, nome, cpf, cargo_id, departamento_id, data_admissao, status) VALUES (1, 'EMP001', 'Joao Silva', '12345678901', 1, 1, '2024-01-01', 'ACTIVE')",
        "INSERT INTO employee (id, matricula, nome, cpf, cargo_id, departamento_id, data_admissao, status) VALUES (2, 'EMP002', 'Maria Souza', '10987654321', 1, 1, '2024-02-01', 'ACTIVE')"
})
class DriverSpecificationsIntegrationTest {

    @Autowired
    private DriverRepository repository;

    @Test
    void shouldFindDriverBySearch() {
        Driver driver = Driver.builder()
                .funcionario(new br.com.logicore.modules.employee.entity.Employee())
                .cnh("12345678901")
                .categoria("D")
                .validadeCnh(LocalDate.of(2025, 12, 31))
                .build();
        driver.getFuncionario().setId(1L);
        repository.save(driver);

        Specification<Driver> specification = DriverSpecifications.withSearch("joao");

        List<Driver> result = repository.findAll(specification);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCnh()).isEqualTo("12345678901");
    }

    @Test
    void shouldReturnDriversByCategoria() {
        Driver driver1 = Driver.builder()
                .funcionario(new br.com.logicore.modules.employee.entity.Employee())
                .cnh("11111111111")
                .categoria("D")
                .validadeCnh(LocalDate.of(2025, 12, 31))
                .build();
        driver1.getFuncionario().setId(1L);

        Driver driver2 = Driver.builder()
                .funcionario(new br.com.logicore.modules.employee.entity.Employee())
                .cnh("22222222222")
                .categoria("C")
                .validadeCnh(LocalDate.of(2025, 12, 31))
                .build();
        driver2.getFuncionario().setId(2L);

        repository.saveAll(List.of(driver1, driver2));

        Specification<Driver> specification = DriverSpecifications.withCategoria("D");

        List<Driver> result = repository.findAll(specification);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCategoria()).isEqualTo("D");
    }

    @Test
    void shouldReturnNullSpecificationWhenSearchIsNull() {
        Specification<Driver> specification = DriverSpecifications.withSearch(null);
        assertThat(specification.toPredicate(null, null, null)).isNull();
    }

    @Test
    void shouldReturnNullSpecificationWhenCategoriaIsNull() {
        Specification<Driver> specification = DriverSpecifications.withCategoria(null);
        assertThat(specification.toPredicate(null, null, null)).isNull();
    }
}