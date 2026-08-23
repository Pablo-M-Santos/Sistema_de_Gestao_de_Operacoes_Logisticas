package br.com.logicore.modules.usuario.validator;

import br.com.logicore.common.exception.BusinessException;
import br.com.logicore.common.exception.DuplicateResourceException;
import br.com.logicore.modules.usuario.enums.UserStatus;
import br.com.logicore.modules.usuario.repository.UsuarioRepository;
import org.springframework.stereotype.Component;

@Component
public class UsuarioValidator {

    private final UsuarioRepository repository;

    public UsuarioValidator(UsuarioRepository repository) {
        this.repository = repository;
    }

    public void validateUniqueEmail(String email) {
        if (repository.existsByEmail(email)) {
            throw new DuplicateResourceException("There is already a user with this email.");
        }
    }

    public void validateUniqueEmailForUpdate(String email, Long id) {
        if (repository.existsByEmailAndIdNot(email, id)) {
            throw new DuplicateResourceException("Another user is already using this email.");
        }
    }

    public void validateUniqueFuncionarioId(Long funcionarioId) {
        if (repository.existsByFuncionarioId(funcionarioId)) {
            throw new DuplicateResourceException("This employee already has a user account.");
        }
    }

    public void validateUniqueFuncionarioIdForUpdate(Long funcionarioId, Long id) {
        if (repository.existsByFuncionarioIdAndIdNot(funcionarioId, id)) {
            throw new DuplicateResourceException("Another user is already linked to this employee.");
        }
    }

    public void validateStatus(String status) {
        if (status == null || (!UserStatus.ACTIVE.name().equals(status)
                && !UserStatus.INACTIVE.name().equals(status))) {
            throw new BusinessException("Status must be ACTIVE or INACTIVE.");
        }
    }
}
