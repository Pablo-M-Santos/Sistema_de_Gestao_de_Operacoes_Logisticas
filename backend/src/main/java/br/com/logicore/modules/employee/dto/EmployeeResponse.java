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

    // Cargo
    private Long cargoId;
    private String cargoNome;
    private String cargoCodigo;

    // Departamento
    private Long departamentoId;
    private String departamentoNome;
    private String departamentoSigla;

    // Endereço
    private Long enderecoId;
    private String enderecoCep;
    private String enderecoLogradouro;
    private String enderecoNumero;
    private String enderecoComplemento;
    private String enderecoBairro;
    private String enderecoCidade;
    private String enderecoEstado;
    private String enderecoPais;
    private BigDecimal enderecoLatitude;
    private BigDecimal enderecoLongitude;

    private LocalDate dataAdmissao;
    private String status;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;

}