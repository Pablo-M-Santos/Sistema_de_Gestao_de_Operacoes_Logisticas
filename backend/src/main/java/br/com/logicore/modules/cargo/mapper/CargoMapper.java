package br.com.logicore.modules.cargo.mapper;

import br.com.logicore.modules.cargo.dto.CargoResponse;
import br.com.logicore.modules.cargo.dto.CreateCargoRequest;
import br.com.logicore.modules.cargo.entity.Cargo;
import org.springframework.stereotype.Component;

@Component
public class CargoMapper {

    public Cargo toEntity(CreateCargoRequest request) {
        return Cargo.builder()
                .nome(request.getNome())
                .descricao(request.getDescricao())
                .codigo(request.getCodigo())
                .build();
    }

    public CargoResponse toResponse(Cargo cargo) {
        return CargoResponse.builder()
                .id(cargo.getId())
                .nome(cargo.getNome())
                .descricao(cargo.getDescricao())
                .codigo(cargo.getCodigo())
                .ativo(cargo.getAtivo())
                .criadoEm(cargo.getCriadoEm())
                .atualizadoEm(cargo.getAtualizadoEm())
                .build();
    }

}
