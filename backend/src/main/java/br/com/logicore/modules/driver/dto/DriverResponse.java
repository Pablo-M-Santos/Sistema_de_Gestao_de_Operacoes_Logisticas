package br.com.logicore.modules.driver.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class DriverResponse {

    private Long id;
    private Long funcionarioId;
    private String funcionarioNome;
    private String funcionarioMatricula;
    private String cnh;
    private String categoria;
    private LocalDate validadeCnh;
    private String observacoes;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;
}
