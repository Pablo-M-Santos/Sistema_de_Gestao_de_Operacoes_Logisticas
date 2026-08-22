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
import br.com.logicore.modules.vehicle.repository.spec.VehicleSpecifications;
import br.com.logicore.modules.vehicle.validator.VehicleValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VehicleService {

    private final VehicleRepository repository;
    private final VehicleMapper mapper;
    private final VehicleValidator validator;

    public VehicleService(VehicleRepository repository, VehicleMapper mapper, VehicleValidator validator) {
        this.repository = repository;
        this.mapper = mapper;
        this.validator = validator;
    }

    @Transactional
    public VehicleResponse create(CreateVehicleRequest request) {
        validator.validateUniquePlaca(request.getPlaca());
        validator.validateUniqueRenavam(request.getRenavam());

        Vehicle vehicle = Vehicle.builder()
                .placa(request.getPlaca())
                .renavam(request.getRenavam())
                .modelo(request.getModelo())
                .fabricante(request.getFabricante())
                .anoFabricacao(request.getAnoFabricacao())
                .anoModelo(request.getAnoModelo())
                .capacidadePeso(request.getCapacidadePeso())
                .capacidadeVolume(request.getCapacidadeVolume())
                .quilometragem(request.getQuilometragem())
                .build();

        return mapper.toResponse(repository.save(vehicle));
    }

    @Transactional(readOnly = true)
    public PageResponse<VehicleResponse> findAll(String search, String status, Integer anoFabricacao, Integer anoModelo, Pageable pageable) {
        Specification<Vehicle> spec = Specification
                .where(VehicleSpecifications.withSearch(search))
                .and(VehicleSpecifications.withStatus(status))
                .and(VehicleSpecifications.withAnoFabricacao(anoFabricacao))
                .and(VehicleSpecifications.withAnoModelo(anoModelo));

        Page<VehicleResponse> page = repository.findAll(spec, pageable)
                .map(mapper::toResponse);

        return new PageResponse<>(page);
    }

    @Transactional(readOnly = true)
    public VehicleSummaryResponse summary() {
        long total = repository.count();
        long active = repository.count();
        long inactive = 0;

        return VehicleSummaryResponse.builder()
                .total(total)
                .active(active)
                .inactive(inactive)
                .build();
    }

    @Transactional(readOnly = true)
    public VehicleResponse findById(Long id) {
        return mapper.toResponse(findVehicleById(id));
    }

    @Transactional
    public VehicleResponse update(Long id, UpdateVehicleRequest request) {
        Vehicle vehicle = findVehicleById(id);

        if (isPresent(request.getPlaca())) {
            if (!vehicle.getPlaca().equalsIgnoreCase(request.getPlaca())) {
                validator.validateUniquePlacaForUpdate(request.getPlaca(), id);
            }
            vehicle.setPlaca(request.getPlaca());
        }

        if (isPresent(request.getRenavam())) {
            if (!vehicle.getRenavam().equalsIgnoreCase(request.getRenavam())) {
                validator.validateUniqueRenavamForUpdate(request.getRenavam(), id);
            }
            vehicle.setRenavam(request.getRenavam());
        }

        if (isPresent(request.getModelo())) {
            vehicle.setModelo(request.getModelo());
        }
        if (isPresent(request.getFabricante())) {
            vehicle.setFabricante(request.getFabricante());
        }
        if (request.getAnoFabricacao() != null) {
            vehicle.setAnoFabricacao(request.getAnoFabricacao());
        }
        if (request.getAnoModelo() != null) {
            vehicle.setAnoModelo(request.getAnoModelo());
        }
        if (request.getCapacidadePeso() != null) {
            vehicle.setCapacidadePeso(request.getCapacidadePeso());
        }
        if (request.getCapacidadeVolume() != null) {
            vehicle.setCapacidadeVolume(request.getCapacidadeVolume());
        }
        if (request.getQuilometragem() != null) {
            vehicle.setQuilometragem(request.getQuilometragem());
        }
        if (request.getStatus() != null) {
            validator.validateStatus(request.getStatus());
            vehicle.setStatus(request.getStatus());
        }

        return mapper.toResponse(repository.save(vehicle));
    }

    @Transactional
    public void delete(Long id) {
        Vehicle vehicle = findVehicleById(id);
        vehicle.setStatus(br.com.logicore.modules.vehicle.enums.VehicleStatus.INACTIVE);
        repository.save(vehicle);
    }

    private Vehicle findVehicleById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with ID: " + id));
    }

    private boolean isPresent(String value) {
        return value != null && !value.isBlank();
    }
}
