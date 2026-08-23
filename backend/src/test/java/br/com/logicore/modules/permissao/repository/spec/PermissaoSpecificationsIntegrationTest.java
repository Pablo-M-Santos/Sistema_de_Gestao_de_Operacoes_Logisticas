package br.com.logicore.modules.permissao.repository.spec;

import br.com.logicore.modules.permissao.entity.Permissao;
import br.com.logicore.modules.permissao.repository.PermissaoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PermissaoSpecificationsIntegrationTest {

    @Autowired
    private PermissaoRepository repository;

    @Test
    void shouldFindPermissaoBySearch() {
        Permissao permissao = Permissao.builder()
                .nome("READ")
                .descricao("Read permission")
                .build();
        repository.save(permissao);

        Specification<Permissao> specification = PermissaoSpecifications.withSearch("read");

        List<Permissao> result = repository.findAll(specification);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNome()).isEqualTo("READ");
    }

    @Test
    void shouldFindPermissaoByDescription() {
        Permissao permissao = Permissao.builder()
                .nome("READ")
                .descricao("Read permission")
                .build();
        repository.save(permissao);

        Specification<Permissao> specification = PermissaoSpecifications.withSearch("read permission");

        List<Permissao> result = repository.findAll(specification);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNome()).isEqualTo("READ");
    }
}
