package br.com.logicore.modules.perfil.validator;

import br.com.logicore.common.exception.DuplicateResourceException;
import br.com.logicore.modules.perfil.repository.PerfilRepository;
import org.springframework.stereotype.Component;

@Component
public class PerfilValidator {

    private final PerfilRepository repository;

    public PerfilValidator(PerfilRepository repository) {
        this.repository = repository;
    }

    public void validateUniqueNome(String nome) {
        if (repository.existsByNome(nome)) {
            throw new DuplicateResourceException("There is already a profile with this name.");
        }
    }

    public void validateUniqueNomeForUpdate(String nome, Long id) {
        if (repository.existsByNomeAndIdNot(nome, id)) {
            throw new DuplicateResourceException("Another profile is already using this name.");
        }
    }
}
