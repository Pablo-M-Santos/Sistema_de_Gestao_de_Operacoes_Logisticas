package br.com.logicore.modules.department.dto;

import br.com.logicore.modules.department.enums.DepartmentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateDepartmentRequest {

    @NotBlank(message = "The name is mandatory.")
    @Size(max = 150, message = "The name cannot exceed 150 characters.")
    private String nome;

    @Size(max = 500, message = "The description cannot exceed 500 characters.")
    private String descricao;

    @Size(max = 20, message = "The abbreviation cannot exceed 20 characters.")
    private String sigla;

    private DepartmentStatus status;
}
