package br.com.logicore.modules.client.dto;

import br.com.logicore.modules.address.entity.Address;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClientResponse {

    private Long id;
    private String razaoSocial;
    private String nomeFantasia;
    private String cnpj;
    private String inscricaoEstadual;
    private String telefone;
    private String email;
    private String contatoPrincipal;

    private Long enderecoId;
    private String enderecoCep;
    private String enderecoLogradouro;
    private String enderecoNumero;
    private String enderecoComplemento;
    private String enderecoBairro;
    private String enderecoCidade;
    private String enderecoEstado;
    private String enderecoPais;

    private String status;
    private java.time.LocalDateTime criadoEm;
    private java.time.LocalDateTime atualizadoEm;
}
