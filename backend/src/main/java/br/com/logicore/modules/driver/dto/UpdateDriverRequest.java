package br.com.logicore.modules.driver.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UpdateDriverRequest {

    private String cnh;

    @Size(min = 1, max = 5, message = "Category must have between 1 and 5 characters.")
    private String categoria;

    private LocalDate validadeCnh;

    @Size(max = 500, message = "Observacoes must have at most 500 characters.")
    private String observacoes;
}
