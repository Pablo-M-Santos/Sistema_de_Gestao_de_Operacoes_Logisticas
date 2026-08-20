package br.com.logicore.modules.employee.mapper;

import br.com.logicore.modules.employee.dto.EmployeeResponse;
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

                .cargoCodigo(
                        employee.getCargo() != null
                                ? employee.getCargo().getCodigo()
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

                .departamentoSigla(
                        employee.getDepartamento() != null
                                ? employee.getDepartamento().getSigla()
                                : null
                )

                .enderecoId(
                        employee.getEndereco() != null
                                ? employee.getEndereco().getId()
                                : null
                )

                .enderecoCep(
                        employee.getEndereco() != null
                                ? employee.getEndereco().getCep()
                                : null
                )

                .enderecoLogradouro(
                        employee.getEndereco() != null
                                ? employee.getEndereco().getLogradouro()
                                : null
                )

                .enderecoNumero(
                        employee.getEndereco() != null
                                ? employee.getEndereco().getNumero()
                                : null
                )

                .enderecoComplemento(
                        employee.getEndereco() != null
                                ? employee.getEndereco().getComplemento()
                                : null
                )

                .enderecoBairro(
                        employee.getEndereco() != null
                                ? employee.getEndereco().getBairro()
                                : null
                )

                .enderecoCidade(
                        employee.getEndereco() != null
                                ? employee.getEndereco().getCidade()
                                : null
                )

                .enderecoEstado(
                        employee.getEndereco() != null
                                ? employee.getEndereco().getEstado()
                                : null
                )

                .enderecoPais(
                        employee.getEndereco() != null
                                ? employee.getEndereco().getPais()
                                : null
                )

                .enderecoLatitude(
                        employee.getEndereco() != null
                                ? employee.getEndereco().getLatitude()
                                : null
                )

                .enderecoLongitude(
                        employee.getEndereco() != null
                                ? employee.getEndereco().getLongitude()
                                : null
                )

                .dataAdmissao(employee.getDataAdmissao())
                .status(employee.getStatus())
                .criadoEm(employee.getCriadoEm())
                .atualizadoEm(employee.getAtualizadoEm())
                .build();

    }

}