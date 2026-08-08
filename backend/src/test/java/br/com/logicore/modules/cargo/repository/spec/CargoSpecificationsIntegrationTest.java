package br.com.logicore.modules.cargo.repository.spec;

import br.com.logicore.modules.cargo.entity.Cargo;
import br.com.logicore.modules.cargo.repository.CargoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@DataJpaTest
class CargoSpecificationsIntegrationTest {

    @Autowired
    private CargoRepository repository;

    @SuppressWarnings("unchecked")
    private Root<Cargo> rootMock() {
        return (Root<Cargo>) mock(Root.class);
    }

    @SuppressWarnings("unchecked")
    private CriteriaQuery<Cargo> queryMock() {
        return (CriteriaQuery<Cargo>) mock(CriteriaQuery.class);
    }

    private CriteriaBuilder builderMock() {
        return mock(CriteriaBuilder.class);
    }

    @Test
    void shouldFindCargoByName() {
        Cargo cargo = Cargo.builder()
                .nome("Analyst")
                .codigo("ANL")
                .ativo(true)
                .build();

        repository.save(cargo);

        Specification<Cargo> specification = CargoSpecifications.withSearch("anal");

        List<Cargo> result = repository.findAll(specification);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNome()).isEqualTo("Analyst");
    }

    @Test
    void shouldFindCargoByCode() {
        Cargo cargo = Cargo.builder()
                .nome("Finance")
                .codigo("FIN")
                .ativo(true)
                .build();

        repository.save(cargo);

        Specification<Cargo> specification = CargoSpecifications.withSearch("fin");

        List<Cargo> result = repository.findAll(specification);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCodigo()).isEqualTo("FIN");
    }

    @Test
    void shouldReturnNullSpecificationWhenSearchContainsOnlySpaces() {
        Specification<Cargo> specification = CargoSpecifications.withSearch("   ");

        assertThat(specification.toPredicate(rootMock(), queryMock(), builderMock()))
                .isNull();
    }

    @Test
    void shouldReturnNullSpecificationWhenSearchIsEmpty() {
        Specification<Cargo> specification = CargoSpecifications.withSearch("");

        assertThat(specification.toPredicate(rootMock(), queryMock(), builderMock()))
                .isNull();
    }

    @Test
    void shouldReturnNullSpecificationWhenSearchIsNull() {
        Specification<Cargo> specification = CargoSpecifications.withSearch(null);

        assertThat(specification.toPredicate(rootMock(), queryMock(), builderMock()))
                .isNull();
    }

    @Test
    void shouldReturnCargosByActiveStatus() {
        Cargo active = Cargo.builder()
                .nome("Analyst")
                .codigo("ANL")
                .ativo(true)
                .build();

        Cargo inactive = Cargo.builder()
                .nome("Finance")
                .codigo("FIN")
                .ativo(false)
                .build();

        repository.saveAll(List.of(active, inactive));

        Specification<Cargo> specification = CargoSpecifications.withStatus(true);

        List<Cargo> result = repository.findAll(specification);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getAtivo()).isTrue();
    }

    @Test
    void shouldReturnCargosByInactiveStatus() {
        Cargo inactive = Cargo.builder()
                .nome("Finance")
                .codigo("FIN")
                .ativo(false)
                .build();

        repository.save(inactive);

        Specification<Cargo> specification = CargoSpecifications.withStatus(false);

        List<Cargo> result = repository.findAll(specification);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getAtivo()).isFalse();
    }

    @Test
    void shouldReturnNullSpecificationWhenStatusIsNull() {
        Specification<Cargo> specification = CargoSpecifications.withStatus(null);

        assertThat(specification.toPredicate(rootMock(), queryMock(), builderMock()))
                .isNull();
    }
}



