package br.com.logicore.modules.vehicle.mapper;

import br.com.logicore.modules.vehicle.dto.VehicleResponse;
import br.com.logicore.modules.vehicle.entity.Vehicle;
import org.springframework.stereotype.Component;

@Component
public class VehicleMapper {

    public VehicleResponse toResponse(Vehicle vehicle) {
        return VehicleResponse.builder()
                .id(vehicle.getId())
                .placa(vehicle.getPlaca())
                .renavam(vehicle.getRenavam())
                .modelo(vehicle.getModelo())
                .fabricante(vehicle.getFabricante())
                .anoFabricacao(vehicle.getAnoFabricacao())
                .anoModelo(vehicle.getAnoModelo())
                .capacidadePeso(vehicle.getCapacidadePeso())
                .capacidadeVolume(vehicle.getCapacidadeVolume())
                .quilometragem(vehicle.getQuilometragem())
                .status(vehicle.getStatus())
                .criadoEm(vehicle.getCriadoEm())
                .atualizadoEm(vehicle.getAtualizadoEm())
                .build();
    }
}
