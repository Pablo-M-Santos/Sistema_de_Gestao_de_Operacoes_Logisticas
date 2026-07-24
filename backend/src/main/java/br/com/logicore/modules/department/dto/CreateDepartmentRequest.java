package br.com.logicore.modules.department.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateDepartmentRequest {

    @NotBlank(message = "The name is mandatory.")
    @Size(max = 80, message = "The name cannot exceed 150 characters.")
    private String nome;

    @Size(max = 250, message = "The description cannot exceed 500 characters.")
    private String descricao;

    @NotBlank(message = "The abbreviation is mandatory.")
    @Size(max =10)
    private String sigla;
}