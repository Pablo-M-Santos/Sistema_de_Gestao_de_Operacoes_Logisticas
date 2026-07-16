package br.com.logicore.modules.department.validator;

import br.com.logicore.modules.department.repository.DepartmentRepository;
import org.springframework.stereotype.Component;

@Component
public class DepartmentValidator {

    private final DepartmentRepository repository;

    public DepartmentValidator(DepartmentRepository repository) {
        this.repository = repository;
    }

    public void validateUniqueName(String nome) {
        if (repository.existsByNome(nome)) {
            throw new IllegalArgumentException("A department with this name is already registered.");
        }
    }

    public void validateUniqueNameForUpdate(String nome, Long id) {
        if (repository.existsByNomeAndIdNot(nome, id)) {
            throw new IllegalArgumentException("Another department is already using this name.");
        }
    }
}