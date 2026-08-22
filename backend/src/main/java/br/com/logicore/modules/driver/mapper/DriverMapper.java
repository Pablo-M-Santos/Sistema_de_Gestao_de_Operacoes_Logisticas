package br.com.logicore.modules.driver.mapper;

import br.com.logicore.modules.driver.dto.DriverResponse;
import br.com.logicore.modules.driver.entity.Driver;
import org.springframework.stereotype.Component;

@Component
public class DriverMapper {

    public DriverResponse toResponse(Driver driver) {
        return DriverResponse.builder()
                .id(driver.getId())
                .funcionarioId(
                        driver.getFuncionario() != null
                                ? driver.getFuncionario().getId()
                                : null
                )
                .funcionarioNome(
                        driver.getFuncionario() != null
                                ? driver.getFuncionario().getNome()
                                : null
                )
                .funcionarioMatricula(
                        driver.getFuncionario() != null
                                ? driver.getFuncionario().getMatricula()
                                : null
                )
                .cnh(driver.getCnh())
                .categoria(driver.getCategoria())
                .validadeCnh(driver.getValidadeCnh())
                .observacoes(driver.getObservacoes())
                .criadoEm(driver.getCriadoEm())
                .atualizadoEm(driver.getAtualizadoEm())
                .build();
    }
}
