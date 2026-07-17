package br.com.logicore.modules.cargo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCargoRequest {

    @NotBlank(message = "Name is required.")
    @Size(max = 150, message = "Name must have at most 150 characters.")
    private String nome;

    @Size(max = 500, message = "Description must have at most 500 characters.")
    private String descricao;

    @Size(max = 20, message = "Code must have at most 20 characters.")
    private String codigo;

}
