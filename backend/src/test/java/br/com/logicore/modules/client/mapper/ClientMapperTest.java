package br.com.logicore.modules.client.mapper;

import br.com.logicore.modules.address.entity.Address;
import br.com.logicore.modules.client.dto.ClientResponse;
import br.com.logicore.modules.client.entity.Client;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ClientMapperTest {

    @Test
    void shouldMapClientToResponse() {
        ClientMapper mapper = new ClientMapper();

        Address address = Address.builder()
                .id(1L)
                .cep("01001000")
                .logradouro("Praça da Sé")
                .numero("100")
                .bairro("Sé")
                .cidade("São Paulo")
                .estado("SP")
                .pais("Brasil")
                .build();

        Client client = Client.builder()
                .id(1L)
                .razaoSocial("Tech Corp")
                .nomeFantasia("Tech")
                .cnpj("12345678901234")
                .inscricaoEstadual("123456789")
                .telefone("11999999999")
                .email("contact@tech.com")
                .contatoPrincipal("João Silva")
                .endereco(address)
                .status(br.com.logicore.modules.client.enums.ClientStatus.ACTIVE)
                .build();

        ClientResponse response = mapper.toResponse(client);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getRazaoSocial()).isEqualTo("Tech Corp");
        assertThat(response.getCnpj()).isEqualTo("12345678901234");
        assertThat(response.getEnderecoId()).isEqualTo(1L);
        assertThat(response.getEnderecoCep()).isEqualTo("01001000");
        assertThat(response.getStatus()).isEqualTo(br.com.logicore.modules.client.enums.ClientStatus.ACTIVE);
    }
}
