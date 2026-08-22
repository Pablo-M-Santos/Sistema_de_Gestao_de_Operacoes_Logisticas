package br.com.logicore.modules.vehicle.service;

import br.com.logicore.common.dto.PageResponse;
import br.com.logicore.common.exception.ResourceNotFoundException;
import br.com.logicore.modules.vehicle.dto.VehicleResponse;
import br.com.logicore.modules.vehicle.dto.VehicleSummaryResponse;
import br.com.logicore.modules.vehicle.dto.CreateVehicleRequest;
import br.com.logicore.modules.vehicle.dto.UpdateVehicleRequest;
import br.com.logicore.modules.vehicle.entity.Vehicle;
import br.com.logicore.modules.vehicle.mapper.VehicleMapper;
import br.com.logicore.modules.vehicle.repository.VehicleRepository;
import br.com.logicore.modules.vehicle.validator.VehicleValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehicleServiceTest {

    @Mock
    private VehicleRepository repository;

    @Mock
    private VehicleMapper mapper;

    @Mock
    private VehicleValidator validator;

    @InjectMocks
    private VehicleService service;

    @Test
    void shouldCreateVehicleSuccessfully() {
        CreateVehicleRequest request = new CreateVehicleRequest();
        request.setPlaca("ABC1234");
        request.setRenavam("12345678901");
        request.setModelo("Model X");
        request.setFabricante("Fabricante Y");
        request.setAnoFabricacao(2020);
        request.setAnoModelo(2021);
        request.setCapacidadePeso(new BigDecimal("1000.50"));
        request.setCapacidadeVolume(new BigDecimal("25.500"));
        request.setQuilometragem(10000);

        Vehicle entity = Vehicle.builder()
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

        VehicleResponse response = VehicleResponse.builder()
                .id(1L)
                .placa("ABC1234")
                .modelo("Model X")
                .build();

        when(mapper.toResponse(entity)).thenReturn(response);
        when(repository.save(any(Vehicle.class))).thenReturn(entity);

        VehicleResponse result = service.create(request);

        assertThat(result).isNotNull();
        assertThat(result.getPlaca()).isEqualTo("ABC1234");
        verify(validator).validateUniquePlaca("ABC1234");
        verify(validator).validateUniqueRenavam("12345678901");
        verify(repository).save(any(Vehicle.class));
    }

    @Test
    void shouldFindVehicleByIdSuccessfully() {
        Vehicle vehicle = Vehicle.builder()
                .id(1L)
                .placa("ABC1234")
                .modelo("Model X")
                .build();

        VehicleResponse response = VehicleResponse.builder()
                .id(1L)
                .placa("ABC1234")
                .modelo("Model X")
                .build();

        when(repository.findById(1L)).thenReturn(Optional.of(vehicle));
        when(mapper.toResponse(vehicle)).thenReturn(response);

        VehicleResponse result = service.findById(1L);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void shouldThrowExceptionWhenVehicleDoesNotExist() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(1L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldUpdateVehicleSuccessfully() {
        Vehicle vehicle = Vehicle.builder()
                .id(1L)
                .placa("ABC1234")
                .modelo("Model X")
                .quilometragem(10000)
                .status("ACTIVE")
                .build();

        UpdateVehicleRequest request = new UpdateVehicleRequest();
        request.setModelo("Model X Updated");
        request.setQuilometragem(15000);

        VehicleResponse response = VehicleResponse.builder()
                .id(1L)
                .placa("ABC1234")
                .modelo("Model X Updated")
                .quilometragem(15000)
                .build();

        when(repository.findById(1L)).thenReturn(Optional.of(vehicle));
        when(repository.save(any(Vehicle.class))).thenReturn(vehicle);
        when(mapper.toResponse(vehicle)).thenReturn(response);

        VehicleResponse result = service.update(1L, request);

        assertThat(result.getModelo()).isEqualTo("Model X Updated");
        assertThat(result.getQuilometragem()).isEqualTo(15000);
        verify(repository).save(any(Vehicle.class));
    }

    @Test
    void shouldDeleteVehicle() {
        Vehicle vehicle = Vehicle.builder()
                .id(1L)
                .status("ACTIVE")
                .build();

        when(repository.findById(1L)).thenReturn(Optional.of(vehicle));

        service.delete(1L);

        assertThat(vehicle.getStatus()).isEqualTo("INACTIVE");
        verify(repository).save(vehicle);
    }

    @Test
    void shouldReturnVehicleSummary() {
        when(repository.count()).thenReturn(10L);

        VehicleSummaryResponse result = service.summary();

        assertThat(result.getTotal()).isEqualTo(10);
        assertThat(result.getActive()).isEqualTo(10);
        assertThat(result.getInactive()).isEqualTo(0);
    }

    @Test
    void shouldReturnPagedVehicles() {
        Pageable pageable = PageRequest.of(0, 20);

        Vehicle vehicle = Vehicle.builder()
                .id(1L)
                .placa("ABC1234")
                .modelo("Model X")
                .build();

        VehicleResponse response = VehicleResponse.builder()
                .id(1L)
                .placa("ABC1234")
                .modelo("Model X")
                .build();

        Page<Vehicle> page = new PageImpl<>(List.of(vehicle));

        when(repository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
        when(mapper.toResponse(vehicle)).thenReturn(response);

        PageResponse<VehicleResponse> result = service.findAll(null, null, null, null, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getPlaca()).isEqualTo("ABC1234");
    }
}
