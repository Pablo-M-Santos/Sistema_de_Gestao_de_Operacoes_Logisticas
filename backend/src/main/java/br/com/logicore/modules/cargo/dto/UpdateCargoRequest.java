package br.com.logicore.modules.cargo.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCargoRequest {

    @Size(max = 100, message = "Name must have at most 100 characters.")
    private String nome;

    @Size(max = 20, message = "Code must have at most 20 characters.")
    private String codigo;

    @Size(max = 255, message = "Description must have at most 255 characters.")
    private String descricao;

    private Boolean ativo;

}