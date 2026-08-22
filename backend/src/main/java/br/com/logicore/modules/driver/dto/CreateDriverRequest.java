package br.com.logicore.modules.driver.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CreateDriverRequest {

    @NotNull(message = "Employee ID is required.")
    private Long funcionarioId;

    @NotBlank(message = "CNH is required.")
    @Size(max = 20, message = "CNH must have at most 20 characters.")
    private String cnh;

    @NotBlank(message = "Category is required.")
    @Size(min = 1, max = 5, message = "Category must have between 1 and 5 characters.")
    private String categoria;

    @NotNull(message = "CNH validity date is required.")
    private LocalDate validadeCnh;

    @Size(max = 500, message = "Observacoes must have at most 500 characters.")
    private String observacoes;
}
