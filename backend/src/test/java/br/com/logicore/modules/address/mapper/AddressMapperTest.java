package br.com.logicore.modules.address.mapper;

import br.com.logicore.modules.address.dto.AddressResponse;
import br.com.logicore.modules.address.dto.CreateAddressRequest;
import br.com.logicore.modules.address.entity.Address;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class AddressMapperTest {

    private AddressMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new AddressMapper();
    }

    @Test
    void shouldConvertCreateRequestToEntity() {
        CreateAddressRequest request = new CreateAddressRequest();
        request.setCep("01001000");
        request.setLogradouro("Praça da Sé");
        request.setNumero("100");
        request.setBairro("Sé");
        request.setCidade("São Paulo");
        request.setEstado("SP");
        request.setLatitude(new BigDecimal("-23.55052000"));
        request.setLongitude(new BigDecimal("-46.63330800"));

        Address result = mapper.toEntity(request);

        assertThat(result).isNotNull();
        assertThat(result.getCep()).isEqualTo("01001000");
        assertThat(result.getPais()).isEqualTo("Brasil");
    }

    @Test
    void shouldConvertEntityToResponse() {
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        Address address = Address.builder()
                .id(1L)
                .cep("01001000")
                .logradouro("Praça da Sé")
                .numero("100")
                .complemento("Sala 1")
                .bairro("Sé")
                .cidade("São Paulo")
                .estado("SP")
                .pais("Brasil")
                .latitude(new BigDecimal("-23.55052000"))
                .longitude(new BigDecimal("-46.63330800"))
                .criadoEm(createdAt)
                .atualizadoEm(updatedAt)
                .build();

        AddressResponse result = mapper.toResponse(address);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getCep()).isEqualTo("01001000");
        assertThat(result.getPais()).isEqualTo("Brasil");
        assertThat(result.getCriadoEm()).isEqualTo(createdAt);
        assertThat(result.getAtualizadoEm()).isEqualTo(updatedAt);
    }
}

