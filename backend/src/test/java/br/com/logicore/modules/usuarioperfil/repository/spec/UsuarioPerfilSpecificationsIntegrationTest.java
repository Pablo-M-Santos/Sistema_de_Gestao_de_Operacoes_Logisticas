package br.com.logicore.modules.usuarioperfil.repository.spec;

import br.com.logicore.modules.perfil.entity.Perfil;
import br.com.logicore.modules.usuarioperfil.entity.UsuarioPerfil;
import br.com.logicore.modules.usuarioperfil.repository.UsuarioPerfilRepository;
import br.com.logicore.modules.usuario.entity.Usuario;
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
        "INSERT INTO employee (id, matricula, nome, cpf, cargo_id, departamento_id, data_admissao, status) VALUES (2, 'EMP002', 'Maria Souza', '10987654321', 1, 1, '2024-02-01', 'ACTIVE')",
        "INSERT INTO usuario (id, nome, email, senha, status, ultimo_acesso, funcionario_id, criado_em, atualizado_em) " +
                "VALUES (1, 'Joao Silva', 'joao@example.com', '123456', 'ACTIVE', null, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)",
        "INSERT INTO usuario (id, nome, email, senha, status, ultimo_acesso, funcionario_id, criado_em, atualizado_em) " +
                "VALUES (2, 'Maria Souza', 'maria@example.com', '123456', 'INACTIVE', null, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)",
        "INSERT INTO perfil (id, nome, descricao, criado_em, atualizado_em) " +
                "VALUES (1, 'Administrador', 'Admin profile', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)",
        "INSERT INTO perfil (id, nome, descricao, criado_em, atualizado_em) " +
                "VALUES (2, 'Usuario', 'User profile', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)"
})
class UsuarioPerfilSpecificationsIntegrationTest {

    @Autowired
    private UsuarioPerfilRepository repository;

    @Test
    void shouldFindAssociationByUsuarioName() {
        Usuario usuario = Usuario.builder().id(1L).nome("Joao Silva").build();
        Perfil perfil = Perfil.builder().id(1L).nome("Administrador").build();
        UsuarioPerfil entity = UsuarioPerfil.builder().usuario(usuario).perfil(perfil).build();
        repository.save(entity);

        Specification<UsuarioPerfil> specification = UsuarioPerfilSpecifications.withSearch("joao");

        List<UsuarioPerfil> result = repository.findAll(specification);

        assertThat(result).hasSize(1);
    }

    @Test
    void shouldFindAssociationByPerfilName() {
        Usuario usuario = Usuario.builder().id(1L).nome("Joao Silva").build();
        Perfil perfil = Perfil.builder().id(1L).nome("Administrador").build();
        UsuarioPerfil entity = UsuarioPerfil.builder().usuario(usuario).perfil(perfil).build();
        repository.save(entity);

        Specification<UsuarioPerfil> specification = UsuarioPerfilSpecifications.withSearch("admin");

        List<UsuarioPerfil> result = repository.findAll(specification);

        assertThat(result).hasSize(1);
    }

    @Test
    void shouldFilterByUsuarioId() {
        Usuario usuario1 = Usuario.builder().id(1L).nome("Joao").build();
        Usuario usuario2 = Usuario.builder().id(2L).nome("Maria").build();
        Perfil perfil = Perfil.builder().id(1L).nome("Admin").build();
        repository.save(UsuarioPerfil.builder().usuario(usuario1).perfil(perfil).build());
        repository.save(UsuarioPerfil.builder().usuario(usuario2).perfil(perfil).build());

        Specification<UsuarioPerfil> specification = UsuarioPerfilSpecifications.withUsuarioId(1L);

        List<UsuarioPerfil> result = repository.findAll(specification);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUsuario().getId()).isEqualTo(1L);
    }

    @Test
    void shouldFilterByPerfilId() {
        Usuario usuario = Usuario.builder().id(1L).nome("Joao").build();
        Perfil perfil1 = Perfil.builder().id(1L).nome("Admin").build();
        Perfil perfil2 = Perfil.builder().id(2L).nome("User").build();
        repository.save(UsuarioPerfil.builder().usuario(usuario).perfil(perfil1).build());
        repository.save(UsuarioPerfil.builder().usuario(usuario).perfil(perfil2).build());

        Specification<UsuarioPerfil> specification = UsuarioPerfilSpecifications.withPerfilId(1L);

        List<UsuarioPerfil> result = repository.findAll(specification);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPerfil().getId()).isEqualTo(1L);
    }
}
