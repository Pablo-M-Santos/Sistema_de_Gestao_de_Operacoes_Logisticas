package br.com.logicore.modules.employee.mapper;

import br.com.logicore.modules.employee.dto.EmployeeResponse;
import br.com.logicore.modules.employee.dto.CreateEmployeeRequest;
import br.com.logicore.modules.employee.entity.Employee;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper {


    public EmployeeResponse toResponse(Employee employee) {

        return EmployeeResponse.builder()

                .id(employee.getId())
                .matricula(employee.getMatricula())
                .nome(employee.getNome())
                .cpf(employee.getCpf())
                .rg(employee.getRg())
                .dataNascimento(employee.getDataNascimento())
                .telefone(employee.getTelefone())
                .email(employee.getEmail())


                .cargoId(
                        employee.getCargo() != null
                                ? employee.getCargo().getId()
                                : null
                )

                .cargoNome(
                        employee.getCargo() != null
                                ? employee.getCargo().getNome()
                                : null
                )


                .departamentoId(
                        employee.getDepartamento() != null
                                ? employee.getDepartamento().getId()
                                : null
                )
                .departamentoNome(
                        employee.getDepartamento() != null
                                ? employee.getDepartamento().getNome()
                                : null
                )
                .enderecoId(
                        employee.getEndereco() != null
                                ? employee.getEndereco().getId()
                                : null
                )
                .dataAdmissao(employee.getDataAdmissao())
                .status(employee.getStatus())
                .criadoEm(employee.getCriadoEm())
                .atualizadoEm(employee.getAtualizadoEm())
                .build();

    }

}