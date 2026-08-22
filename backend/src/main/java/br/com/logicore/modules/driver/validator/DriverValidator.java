package br.com.logicore.modules.driver.validator;

import br.com.logicore.common.exception.BusinessException;
import br.com.logicore.common.exception.DuplicateResourceException;
import br.com.logicore.modules.driver.repository.DriverRepository;
import br.com.logicore.modules.driver.enums.DriverCnhCategory;
import org.springframework.stereotype.Component;

@Component
public class DriverValidator {

    private final DriverRepository repository;

    public DriverValidator(DriverRepository repository) {
        this.repository = repository;
    }

    public void validateUniqueCnh(String cnh) {
        if (repository.existsByCnh(cnh)) {
            throw new DuplicateResourceException("There is already a driver with this CNH.");
        }
    }

    public void validateUniqueCnhForUpdate(String cnh, Long id) {
        if (repository.existsByCnhAndIdNot(cnh, id)) {
            throw new DuplicateResourceException("There is already a driver with this CNH.");
        }
    }

    public void validateUniqueFuncionarioId(Long funcionarioId) {
        if (repository.existsByFuncionarioId(funcionarioId)) {
            throw new DuplicateResourceException("This employee is already registered as a driver.");
        }
    }

    public void validateUniqueFuncionarioIdForUpdate(Long funcionarioId, Long id) {
        if (repository.existsByFuncionarioIdAndIdNot(funcionarioId, id)) {
            throw new DuplicateResourceException("This employee is already registered as a driver.");
        }
    }

    public void validateCategoria(String categoria) {
        if (categoria == null) {
            return;
        }

        String upper = categoria.trim().toUpperCase();
        boolean valid = switch (upper) {
            case DriverCnhCategory.A, DriverCnhCategory.B, DriverCnhCategory.C,
                 DriverCnhCategory.D, DriverCnhCategory.E, DriverCnhCategory.AB,
                 DriverCnhCategory.AC, DriverCnhCategory.AD, DriverCnhCategory.AE -> true;
            default -> false;
        };

        if (!valid) {
            throw new BusinessException("Invalid CNH category. Allowed: A, B, C, D, E, AB, AC, AD, AE.");
        }
    }
}
