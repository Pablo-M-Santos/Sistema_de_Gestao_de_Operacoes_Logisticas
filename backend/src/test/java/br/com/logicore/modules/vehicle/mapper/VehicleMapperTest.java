package br.com.logicore.modules.vehicle.mapper;

import br.com.logicore.modules.vehicle.dto.VehicleResponse;
import br.com.logicore.modules.vehicle.entity.Vehicle;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class VehicleMapperTest {

    @Test
    void shouldMapVehicleToResponse() {
        VehicleMapper mapper = new VehicleMapper();

        Vehicle vehicle = Vehicle.builder()
                .id(1L)
                .placa("ABC1234")
                .renavam("12345678901")
                .modelo("Model X")
                .fabricante("Fabricante Y")
                .anoFabricacao(2020)
                .anoModelo(2021)
                .capacidadePeso(new BigDecimal("1000.50"))
                .capacidadeVolume(new BigDecimal("25.500"))
                .quilometragem(10000)
                .status("ACTIVE")
                .build();

        VehicleResponse response = mapper.toResponse(vehicle);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getPlaca()).isEqualTo("ABC1234");
        assertThat(response.getRenavam()).isEqualTo("12345678901");
        assertThat(response.getModelo()).isEqualTo("Model X");
        assertThat(response.getFabricante()).isEqualTo("Fabricante Y");
        assertThat(response.getAnoFabricacao()).isEqualTo(2020);
        assertThat(response.getAnoModelo()).isEqualTo(2021);
        assertThat(response.getCapacidadePeso()).isEqualTo(new BigDecimal("1000.50"));
        assertThat(response.getCapacidadeVolume()).isEqualTo(new BigDecimal("25.500"));
        assertThat(response.getQuilometragem()).isEqualTo(10000);
        assertThat(response.getStatus()).isEqualTo("ACTIVE");
    }
}
