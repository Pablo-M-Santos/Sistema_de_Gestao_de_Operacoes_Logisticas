package br.com.logicore.modules.client.repository.spec;

import br.com.logicore.modules.address.entity.Address;
import br.com.logicore.modules.client.entity.Client;
import br.com.logicore.modules.client.repository.ClientRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ClientSpecificationsIntegrationTest {

    @Autowired
    private ClientRepository repository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void shouldFindClientBySearch() {
        Address address = Address.builder()
                .cep("01001000")
                .logradouro("Praça da Sé")
                .numero("100")
                .bairro("Sé")
                .cidade("São Paulo")
                .estado("SP")
                .pais("Brasil")
                .build();
        entityManager.persist(address);

        Client client = Client.builder()
                .razaoSocial("Tech Corp")
                .nomeFantasia("Tech")
                .cnpj("12345678901234")
                .email("contact@tech.com")
                .telefone("11999999999")
                .contatoPrincipal("João Silva")
                .endereco(address)
                .status("ACTIVE")
                .build();
        repository.save(client);

        Specification<Client> specification = ClientSpecifications.withSearch("tech");

        List<Client> result = repository.findAll(specification);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRazaoSocial()).isEqualTo("Tech Corp");
    }

    @Test
    void shouldFindClientByName() {
        Client client = Client.builder()
                .razaoSocial("Tech Corp")
                .nomeFantasia("Tech")
                .cnpj("12345678901234")
                .status("ACTIVE")
                .build();
        repository.save(client);

        Specification<Client> specification = ClientSpecifications.withSearch("tech corp");

        List<Client> result = repository.findAll(specification);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRazaoSocial()).isEqualTo("Tech Corp");
    }

    @Test
    void shouldReturnClientsByStatus() {
        Client active = Client.builder()
                .razaoSocial("Tech Corp")
                .cnpj("12345678901234")
                .status("ACTIVE")
                .build();

        Client inactive = Client.builder()
                .razaoSocial("Old Corp")
                .cnpj("99999999999999")
                .status("INACTIVE")
                .build();

        repository.saveAll(List.of(active, inactive));

        Specification<Client> specification = ClientSpecifications.withStatus("ACTIVE");

        List<Client> result = repository.findAll(specification);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo("ACTIVE");
    }

    @Test
    void shouldReturnNullSpecificationWhenSearchIsNull() {
        Specification<Client> specification = ClientSpecifications.withSearch(null);
        assertThat(specification.toPredicate(null, null, null)).isNull();
    }

    @Test
    void shouldReturnNullSpecificationWhenSearchIsEmpty() {
        Specification<Client> specification = ClientSpecifications.withSearch("");
        assertThat(specification.toPredicate(null, null, null)).isNull();
    }

    @Test
    void shouldReturnNullSpecificationWhenStatusIsNull() {
        Specification<Client> specification = ClientSpecifications.withStatus(null);
        assertThat(specification.toPredicate(null, null, null)).isNull();
    }
}
