package br.com.logicore.modules.client.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateClientRequest {

    @NotBlank(message = "Razão social is required.")
    @Size(max = 150, message = "Razão social must have at most 150 characters.")
    private String razaoSocial;

    @Size(max = 150, message = "Nome fantasia must have at most 150 characters.")
    private String nomeFantasia;

    @NotBlank(message = "CNPJ is required.")
    @Size(min = 14, max = 14, message = "CNPJ must contain exactly 14 characters.")
    private String cnpj;

    @Size(max = 30, message = "Inscrição estadual must have at most 30 characters.")
    private String inscricaoEstadual;

    @Size(max = 20, message = "Phone must have at most 20 characters.")
    private String telefone;

    @Size(max = 150, message = "Email must have at most 150 characters.")
    private String email;

    @Size(max = 150, message = "Contato principal must have at most 150 characters.")
    private String contatoPrincipal;

    private Long enderecoId;
}
