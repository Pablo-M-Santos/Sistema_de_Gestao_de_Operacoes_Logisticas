package br.com.logicore.modules.perfil.repository.spec;

import br.com.logicore.modules.perfil.entity.Perfil;
import br.com.logicore.modules.perfil.repository.PerfilRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PerfilSpecificationsIntegrationTest {

    @Autowired
    private PerfilRepository repository;

    @Test
    void shouldFindPerfilBySearch() {
        Perfil perfil = Perfil.builder()
                .nome("Admin")
                .descricao("Administrator profile")
                .build();
        repository.save(perfil);

        Specification<Perfil> specification = PerfilSpecifications.withSearch("admin");

        List<Perfil> result = repository.findAll(specification);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNome()).isEqualTo("Admin");
    }

    @Test
    void shouldFindPerfilByDescription() {
        Perfil perfil = Perfil.builder()
                .nome("Admin")
                .descricao("Administrator profile")
                .build();
        repository.save(perfil);

        Specification<Perfil> specification = PerfilSpecifications.withSearch("administrator");

        List<Perfil> result = repository.findAll(specification);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNome()).isEqualTo("Admin");
    }
}
