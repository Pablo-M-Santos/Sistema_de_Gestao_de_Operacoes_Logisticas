package br.com.logicore.modules.permissao.validator;

import br.com.logicore.common.exception.DuplicateResourceException;
import br.com.logicore.modules.permissao.repository.PermissaoRepository;
import org.springframework.stereotype.Component;

@Component
public class PermissaoValidator {

    private final PermissaoRepository repository;

    public PermissaoValidator(PermissaoRepository repository) {
        this.repository = repository;
    }

    public void validateUniqueNome(String nome) {
        if (repository.existsByNome(nome)) {
            throw new DuplicateResourceException("There is already a permission with this name.");
        }
    }

    public void validateUniqueNomeForUpdate(String nome, Long id) {
        if (repository.existsByNomeAndIdNot(nome, id)) {
            throw new DuplicateResourceException("Another permission is already using this name.");
        }
    }
}
