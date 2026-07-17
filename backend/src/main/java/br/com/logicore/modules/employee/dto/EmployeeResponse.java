package br.com.logicore.modules.employee.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class EmployeeResponse {

    private Long id;
    private String matricula;
    private String nome;
    private String cpf;
    private String rg;
    private LocalDate dataNascimento;
    private String telefone;
    private String email;
    private Long cargoId;
    private String cargoNome;
    private Long departamentoId;
    private String departamentoNome;
    private Long enderecoId;
    private LocalDate dataAdmissao;
    private String status;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;

}