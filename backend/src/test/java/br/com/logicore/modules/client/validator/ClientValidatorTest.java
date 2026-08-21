package br.com.logicore.modules.client.validator;

import br.com.logicore.common.exception.BusinessException;
import br.com.logicore.common.exception.DuplicateResourceException;
import br.com.logicore.modules.client.repository.ClientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientValidatorTest {

    @Mock
    private ClientRepository repository;

    @InjectMocks
    private ClientValidator validator;

    @Test
    void shouldThrowExceptionWhenCnpjAlreadyExists() {
        when(repository.existsByCnpj("12345678901234")).thenReturn(true);

        assertThatThrownBy(() -> validator.validateUniqueCnpj("12345678901234"))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void shouldNotThrowExceptionWhenCnpjDoesNotExist() {
        when(repository.existsByCnpj("12345678901234")).thenReturn(false);

        validator.validateUniqueCnpj("12345678901234");
        verify(repository).existsByCnpj("12345678901234");
    }

    @Test
    void shouldThrowExceptionWhenStatusIsInvalid() {
        assertThatThrownBy(() -> validator.validateStatus("INVALID"))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void shouldNotThrowExceptionWhenStatusIsValid() {
        validator.validateStatus("ACTIVE");
        validator.validateStatus("INACTIVE");
    }
}
