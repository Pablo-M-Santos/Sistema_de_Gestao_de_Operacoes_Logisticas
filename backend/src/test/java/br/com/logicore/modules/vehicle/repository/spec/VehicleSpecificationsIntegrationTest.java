package br.com.logicore.modules.vehicle.repository.spec;

import br.com.logicore.modules.vehicle.entity.Vehicle;
import br.com.logicore.modules.vehicle.repository.VehicleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class VehicleSpecificationsIntegrationTest {

    @Autowired
    private VehicleRepository repository;

    @Test
    void shouldFindVehicleBySearch() {
        Vehicle vehicle = Vehicle.builder()
                .placa("ABC1234")
                .renavam("12345678901")
                .modelo("Model X")
                .fabricante("Fabricante Y")
                .anoFabricacao(2020)
                .anoModelo(2021)
                .capacidadePeso(new java.math.BigDecimal("1000.50"))
                .capacidadeVolume(new java.math.BigDecimal("25.500"))
                .quilometragem(10000)
                .status("ACTIVE")
                .build();
        repository.save(vehicle);

        Specification<Vehicle> specification = VehicleSpecifications.withSearch("abc");

        List<Vehicle> result = repository.findAll(specification);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPlaca()).isEqualTo("ABC1234");
    }

    @Test
    void shouldReturnVehiclesByStatus() {
        Vehicle active = Vehicle.builder()
                .placa("ABC1234")
                .renavam("12345678901")
                .modelo("Model X")
                .fabricante("Fabricante Y")
                .anoFabricacao(2020)
                .anoModelo(2021)
                .capacidadePeso(new java.math.BigDecimal("1000.50"))
                .capacidadeVolume(new java.math.BigDecimal("25.500"))
                .quilometragem(10000)
                .status("ACTIVE")
                .build();

        Vehicle inactive = Vehicle.builder()
                .placa("XYZ9876")
                .renavam("10987654321")
                .modelo("Model Y")
                .fabricante("Fabricante Z")
                .anoFabricacao(2015)
                .anoModelo(2016)
                .capacidadePeso(new java.math.BigDecimal("500.50"))
                .capacidadeVolume(new java.math.BigDecimal("10.500"))
                .quilometragem(50000)
                .status("INACTIVE")
                .build();

        repository.saveAll(List.of(active, inactive));

        Specification<Vehicle> specification = VehicleSpecifications.withStatus("ACTIVE");

        List<Vehicle> result = repository.findAll(specification);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo("ACTIVE");
    }

    @Test
    void shouldReturnNullSpecificationWhenSearchIsNull() {
        Specification<Vehicle> specification = VehicleSpecifications.withSearch(null);
        assertThat(specification.toPredicate(null, null, null)).isNull();
    }

    @Test
    void shouldReturnNullSpecificationWhenStatusIsNull() {
        Specification<Vehicle> specification = VehicleSpecifications.withStatus(null);
        assertThat(specification.toPredicate(null, null, null)).isNull();
    }
}
