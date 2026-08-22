package br.com.logicore.modules.vehicle.validator;

import br.com.logicore.common.exception.BusinessException;
import br.com.logicore.common.exception.DuplicateResourceException;
import br.com.logicore.modules.vehicle.repository.VehicleRepository;
import org.springframework.stereotype.Component;

@Component
public class VehicleValidator {

    private final VehicleRepository repository;

    public VehicleValidator(VehicleRepository repository) {
        this.repository = repository;
    }

    public void validateUniquePlaca(String placa) {
        if (repository.existsByPlaca(placa)) {
            throw new DuplicateResourceException("There is already a vehicle with this placa.");
        }
    }

    public void validateUniquePlacaForUpdate(String placa, Long id) {
        if (repository.existsByPlacaAndIdNot(placa, id)) {
            throw new DuplicateResourceException("There is already a vehicle with this placa.");
        }
    }

    public void validateUniqueRenavam(String renavam) {
        if (repository.existsByRenavam(renavam)) {
            throw new DuplicateResourceException("There is already a vehicle with this RENAVAM.");
        }
    }

    public void validateUniqueRenavamForUpdate(String renavam, Long id) {
        if (repository.existsByRenavamAndIdNot(renavam, id)) {
            throw new DuplicateResourceException("There is already a vehicle with this RENAVAM.");
        }
    }

    public void validateStatus(String status) {
        if (status == null || (!br.com.logicore.modules.vehicle.enums.VehicleStatus.ACTIVE.equals(status)
                && !br.com.logicore.modules.vehicle.enums.VehicleStatus.INACTIVE.equals(status))) {
            throw new BusinessException("Status must be ACTIVE or INACTIVE.");
        }
    }
}
