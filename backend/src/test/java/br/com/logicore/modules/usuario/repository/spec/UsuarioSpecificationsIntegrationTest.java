package br.com.logicore.modules.usuario.repository.spec;

import br.com.logicore.modules.usuario.entity.Usuario;
import br.com.logicore.modules.usuario.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Sql(statements = {
        "INSERT INTO cargo (ativo, atualizado_em, codigo, criado_em, descricao, nome, id) VALUES (true, CURRENT_TIMESTAMP, 'DEV', CURRENT_TIMESTAMP, null, 'Developer', 1)",
        "INSERT INTO department (atualizado_em, criado_em, descricao, nome, sigla, status, id) VALUES (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, null, 'Tech', 'TI', 'ACTIVE', 1)",
        "INSERT INTO employee (id, matricula, nome, cpf, cargo_id, departamento_id, data_admissao, status) VALUES (1, 'EMP001', 'Joao Silva', '12345678901', 1, 1, '2024-01-01', 'ACTIVE')",
        "INSERT INTO employee (id, matricula, nome, cpf, cargo_id, departamento_id, data_admissao, status) VALUES (2, 'EMP002', 'Maria Souza', '10987654321', 1, 1, '2024-02-01', 'ACTIVE')"
})
class UsuarioSpecificationsIntegrationTest {

    @Autowired
    private UsuarioRepository repository;

    @Test
    void shouldFindUsuarioBySearch() {
        Usuario usuario = Usuario.builder()
                .nome("Joao Silva")
                .email("joao@example.com")
                .senha("123456")
                .status(br.com.logicore.modules.usuario.enums.UserStatus.ACTIVE)
                .funcionario(new br.com.logicore.modules.employee.entity.Employee())
                .build();
        usuario.getFuncionario().setId(1L);
        repository.save(usuario);

        Specification<Usuario> specification = UsuarioSpecifications.withSearch("joao");

        List<Usuario> result = repository.findAll(specification);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNome()).isEqualTo("Joao Silva");
    }

    @Test
    void shouldReturnUsuariosByStatus() {
        Usuario usuario1 = Usuario.builder()
                .nome("Joao Silva")
                .email("joao1@example.com")
                .senha("123456")
                .status(br.com.logicore.modules.usuario.enums.UserStatus.ACTIVE)
                .funcionario(new br.com.logicore.modules.employee.entity.Employee())
                .build();
        usuario1.getFuncionario().setId(1L);

        Usuario usuario2 = Usuario.builder()
                .nome("Maria Souza")
                .email("maria@example.com")
                .senha("123456")
                .status(br.com.logicore.modules.usuario.enums.UserStatus.INACTIVE)
                .funcionario(new br.com.logicore.modules.employee.entity.Employee())
                .build();
        usuario2.getFuncionario().setId(2L);

        repository.save(usuario1);
        repository.save(usuario2);

        Specification<Usuario> specification = UsuarioSpecifications.withStatus("ACTIVE");

        List<Usuario> result = repository.findAll(specification);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNome()).isEqualTo("Joao Silva");
    }

    @Test
    void shouldReturnUsuariosByFuncionarioId() {
        Usuario usuario = Usuario.builder()
                .nome("Joao Silva")
                .email("joao@example.com")
                .senha("123456")
                .status(br.com.logicore.modules.usuario.enums.UserStatus.ACTIVE)
                .funcionario(new br.com.logicore.modules.employee.entity.Employee())
                .build();
        usuario.getFuncionario().setId(1L);
        repository.save(usuario);

        Specification<Usuario> specification = UsuarioSpecifications.withFuncionarioId(1L);

        List<Usuario> result = repository.findAll(specification);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNome()).isEqualTo("Joao Silva");
    }
}
