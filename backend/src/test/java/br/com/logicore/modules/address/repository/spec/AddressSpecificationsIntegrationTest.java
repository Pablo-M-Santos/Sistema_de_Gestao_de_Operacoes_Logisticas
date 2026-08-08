package br.com.logicore.modules.address.repository.spec;

import br.com.logicore.modules.address.entity.Address;
import br.com.logicore.modules.address.repository.AddressRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class AddressSpecificationsIntegrationTest {

    @Autowired
    private AddressRepository repository;

    @Test
    void shouldFindAddressBySearch() {
        Address address = Address.builder()
                .cep("01001000")
                .logradouro("Praça da Sé")
                .numero("100")
                .bairro("Sé")
                .cidade("São Paulo")
                .estado("SP")
                .pais("Brasil")
                .latitude(new BigDecimal("-23.55052000"))
                .longitude(new BigDecimal("-46.63330800"))
                .build();

        repository.save(address);

        List<Address> result = repository.findAll(AddressSpecifications.withSearch("praça"));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLogradouro()).isEqualTo("Praça da Sé");
    }

    @Test
    void shouldFindAddressByCep() {
        Address address = Address.builder()
                .cep("01001000")
                .logradouro("Praça da Sé")
                .numero("100")
                .bairro("Sé")
                .cidade("São Paulo")
                .estado("SP")
                .pais("Brasil")
                .build();

        repository.save(address);

        List<Address> result = repository.findAll(AddressSpecifications.withCep("01001000"));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCep()).isEqualTo("01001000");
    }

    @Test
    void shouldFindAddressByCity() {
        Address address = Address.builder()
                .cep("01001000")
                .logradouro("Praça da Sé")
                .numero("100")
                .bairro("Sé")
                .cidade("São Paulo")
                .estado("SP")
                .pais("Brasil")
                .build();

        repository.save(address);

        List<Address> result = repository.findAll(AddressSpecifications.withCidade("são paulo"));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCidade()).isEqualTo("São Paulo");
    }

    @Test
    void shouldFindAddressByState() {
        Address address = Address.builder()
                .cep("01001000")
                .logradouro("Praça da Sé")
                .numero("100")
                .bairro("Sé")
                .cidade("São Paulo")
                .estado("SP")
                .pais("Brasil")
                .build();

        repository.save(address);

        List<Address> result = repository.findAll(AddressSpecifications.withEstado("sp"));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEstado()).isEqualTo("SP");
    }

    @Test
    void shouldFindAddressByCountry() {
        Address address = Address.builder()
                .cep("01001000")
                .logradouro("Praça da Sé")
                .numero("100")
                .bairro("Sé")
                .cidade("São Paulo")
                .estado("SP")
                .pais("Brasil")
                .build();

        repository.save(address);

        List<Address> result = repository.findAll(AddressSpecifications.withPais("brasil"));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPais()).isEqualTo("Brasil");
    }

    @Test
    void shouldReturnAllAddressesWhenSearchIsBlank() {
        Address first = Address.builder()
                .cep("01001000")
                .logradouro("Praça da Sé")
                .numero("100")
                .bairro("Sé")
                .cidade("São Paulo")
                .estado("SP")
                .build();

        Address second = Address.builder()
                .cep("20040002")
                .logradouro("Rua da Assembleia")
                .numero("10")
                .bairro("Centro")
                .cidade("Rio de Janeiro")
                .estado("RJ")
                .build();

        repository.saveAll(List.of(first, second));

        List<Address> result = repository.findAll(AddressSpecifications.withSearch("   "));

        assertThat(result).hasSize(2);
    }
}

