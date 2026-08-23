package br.com.logicore.modules.permissao.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePermissaoRequest {

    @NotBlank(message = "The name is mandatory.")
    @Size(max = 100, message = "The name cannot exceed 100 characters.")
    private String nome;

    @Size(max = 255, message = "The description cannot exceed 255 characters.")
    private String descricao;
}
