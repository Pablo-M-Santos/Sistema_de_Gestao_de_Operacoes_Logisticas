package br.com.logicore.modules.department.validator;

import br.com.logicore.common.exception.DuplicateResourceException;
import br.com.logicore.modules.department.repository.DepartmentRepository;
import org.springframework.stereotype.Component;

@Component
public class DepartmentValidator {

    private final DepartmentRepository repository;

    public DepartmentValidator(DepartmentRepository repository) {
        this.repository = repository;
    }

    public void validateUniqueName(String nome) {
        if (repository.existsByNomeIgnoreCase(nome)) {
            throw new DuplicateResourceException(
                    "A department with this name is already registered."
            );
        }
    }

    public void validateUniqueNameForUpdate(String nome, Long id) {
        if (repository.existsByNomeIgnoreCaseAndIdNot(nome, id)) {
            throw new DuplicateResourceException(
                    "Another department is already using this name."
            );
        }
    }
}