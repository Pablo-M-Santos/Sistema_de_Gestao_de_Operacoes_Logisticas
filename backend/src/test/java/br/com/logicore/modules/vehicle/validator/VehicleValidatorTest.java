package br.com.logicore.modules.vehicle.validator;

import br.com.logicore.common.exception.BusinessException;
import br.com.logicore.common.exception.DuplicateResourceException;
import br.com.logicore.modules.vehicle.repository.VehicleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehicleValidatorTest {

    @Mock
    private VehicleRepository repository;

    @InjectMocks
    private VehicleValidator validator;

    @Test
    void shouldThrowExceptionWhenPlacaAlreadyExists() {
        when(repository.existsByPlaca("ABC1234")).thenReturn(true);

        assertThatThrownBy(() -> validator.validateUniquePlaca("ABC1234"))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void shouldNotThrowExceptionWhenPlacaDoesNotExist() {
        when(repository.existsByPlaca("ABC1234")).thenReturn(false);

        validator.validateUniquePlaca("ABC1234");
        verify(repository).existsByPlaca("ABC1234");
    }

    @Test
    void shouldThrowExceptionWhenRenavamAlreadyExists() {
        when(repository.existsByRenavam("12345678901")).thenReturn(true);

        assertThatThrownBy(() -> validator.validateUniqueRenavam("12345678901"))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void shouldNotThrowExceptionWhenRenavamDoesNotExist() {
        when(repository.existsByRenavam("12345678901")).thenReturn(false);

        validator.validateUniqueRenavam("12345678901");
        verify(repository).existsByRenavam("12345678901");
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
