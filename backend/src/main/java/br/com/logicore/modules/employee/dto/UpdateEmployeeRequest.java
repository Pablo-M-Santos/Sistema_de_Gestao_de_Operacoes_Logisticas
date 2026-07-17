package br.com.logicore.modules.employee.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class UpdateEmployeeRequest {


    @Size(max = 20, message = "Registration number must have at most 20 characters.")
    private String matricula;


    @Size(max = 150, message = "Name must have at most 150 characters.")
    private String nome;


    @Size(min = 11, max = 11, message = "CPF must contain exactly 11 characters.")
    private String cpf;


    @Size(max = 20, message = "RG must have at most 20 characters.")
    private String rg;


    private LocalDate dataNascimento;


    @Size(max = 20, message = "Phone must have at most 20 characters.")
    private String telefone;


    @Email(message = "Email must be valid.")
    @Size(max = 150, message = "Email must have at most 150 characters.")
    private String email;


    private Long cargoId;


    private Long departamentoId;


    private Long enderecoId;


    private LocalDate dataAdmissao;


    private String status;

}