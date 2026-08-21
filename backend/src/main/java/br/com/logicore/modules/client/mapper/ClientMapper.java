package br.com.logicore.modules.client.mapper;

import br.com.logicore.modules.address.entity.Address;
import br.com.logicore.modules.client.dto.ClientResponse;
import br.com.logicore.modules.client.entity.Client;
import org.springframework.stereotype.Component;

@Component
public class ClientMapper {

    public ClientResponse toResponse(Client client) {
        Address endereco = client.getEndereco();

        return ClientResponse.builder()
                .id(client.getId())
                .razaoSocial(client.getRazaoSocial())
                .nomeFantasia(client.getNomeFantasia())
                .cnpj(client.getCnpj())
                .inscricaoEstadual(client.getInscricaoEstadual())
                .telefone(client.getTelefone())
                .email(client.getEmail())
                .contatoPrincipal(client.getContatoPrincipal())
                .enderecoId(endereco != null ? endereco.getId() : null)
                .enderecoCep(endereco != null ? endereco.getCep() : null)
                .enderecoLogradouro(endereco != null ? endereco.getLogradouro() : null)
                .enderecoNumero(endereco != null ? endereco.getNumero() : null)
                .enderecoComplemento(endereco != null ? endereco.getComplemento() : null)
                .enderecoBairro(endereco != null ? endereco.getBairro() : null)
                .enderecoCidade(endereco != null ? endereco.getCidade() : null)
                .enderecoEstado(endereco != null ? endereco.getEstado() : null)
                .enderecoPais(endereco != null ? endereco.getPais() : null)
                .status(client.getStatus())
                .criadoEm(client.getCriadoEm())
                .atualizadoEm(client.getAtualizadoEm())
                .build();
    }
}
