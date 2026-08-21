package br.com.logicore.modules.client.validator;

import br.com.logicore.common.exception.BusinessException;
import br.com.logicore.common.exception.DuplicateResourceException;
import br.com.logicore.modules.client.repository.ClientRepository;
import org.springframework.stereotype.Component;

@Component
public class ClientValidator {

    private final ClientRepository repository;

    public ClientValidator(ClientRepository repository) {
        this.repository = repository;
    }

    public void validateUniqueCnpj(String cnpj) {
        if (repository.existsByCnpj(cnpj)) {
            throw new DuplicateResourceException("There is already a client with this CNPJ.");
        }
    }

    public void validateUniqueCnpjForUpdate(String cnpj, Long id) {
        repository.existsByCnpjAndIdNot(cnpj, id);
    }

    public void validateStatus(String status) {
        if (status == null || (!br.com.logicore.modules.client.enums.ClientStatus.ACTIVE.equals(status)
                && !br.com.logicore.modules.client.enums.ClientStatus.INACTIVE.equals(status))) {
            throw new BusinessException("Status must be ACTIVE or INACTIVE.");
        }
    }
}
