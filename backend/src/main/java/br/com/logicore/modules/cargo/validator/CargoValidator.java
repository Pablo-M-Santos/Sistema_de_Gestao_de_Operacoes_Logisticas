package br.com.logicore.modules.cargo.validator;

import br.com.logicore.modules.cargo.repository.CargoRepository;
import org.springframework.stereotype.Component;

@Component
public class CargoValidator {

    private final CargoRepository repository;

    public CargoValidator(CargoRepository repository) {
        this.repository = repository;
    }

    public void validateUniqueName(String nome) {

        if (repository.existsByNomeIgnoreCase(nome)) {
            throw new RuntimeException("There is already a position with this name.");
        }

    }

    public void validateUniqueCode(String codigo) {

        if (codigo != null && !codigo.isBlank()
                && repository.existsByCodigoIgnoreCase(codigo)) {

            throw new RuntimeException("There is already a position with this code.");
        }

    }

    public void validateUniqueNameForUpdate(String nome, Long id) {

        repository.findByNomeIgnoreCase(nome)
                .filter(cargo -> !cargo.getId().equals(id))
                .ifPresent(cargo -> {
                    throw new RuntimeException("There is already a position with this name.");
                });

    }

    public void validateUniqueCodeForUpdate(String codigo, Long id) {

        if (codigo == null || codigo.isBlank()) {
            return;
        }

        repository.findByCodigoIgnoreCase(codigo)
                .filter(cargo -> !cargo.getId().equals(id))
                .ifPresent(cargo -> {
                    throw new RuntimeException("There is already a position with this code.");
                });

    }

}