package br.com.logicore.modules.driver.validator;

import br.com.logicore.common.exception.BusinessException;
import br.com.logicore.common.exception.DuplicateResourceException;
import br.com.logicore.modules.driver.repository.DriverRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DriverValidatorTest {

    @Mock
    private DriverRepository repository;

    @InjectMocks
    private DriverValidator validator;

    @Test
    void shouldThrowExceptionWhenCnhAlreadyExists() {
        when(repository.existsByCnh("12345678901")).thenReturn(true);

        assertThatThrownBy(() -> validator.validateUniqueCnh("12345678901"))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void shouldNotThrowExceptionWhenCnhDoesNotExist() {
        when(repository.existsByCnh("12345678901")).thenReturn(false);

        validator.validateUniqueCnh("12345678901");
        verify(repository).existsByCnh("12345678901");
    }

    @Test
    void shouldThrowExceptionWhenFuncionarioIdAlreadyExists() {
        when(repository.existsByFuncionarioId(1L)).thenReturn(true);

        assertThatThrownBy(() -> validator.validateUniqueFuncionarioId(1L))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void shouldNotThrowExceptionWhenFuncionarioIdDoesNotExist() {
        when(repository.existsByFuncionarioId(1L)).thenReturn(false);

        validator.validateUniqueFuncionarioId(1L);
        verify(repository).existsByFuncionarioId(1L);
    }

    @Test
    void shouldThrowExceptionForInvalidCategoria() {
        assertThatThrownBy(() -> validator.validateCategoria("INVALID"))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void shouldNotThrowExceptionForValidCategoria() {
        validator.validateCategoria("A");
        validator.validateCategoria("B");
        validator.validateCategoria("C");
        validator.validateCategoria("D");
        validator.validateCategoria("E");
        validator.validateCategoria("AB");
        validator.validateCategoria("AC");
        validator.validateCategoria("AD");
        validator.validateCategoria("AE");
    }

    @Test
    void shouldNotThrowExceptionForNullCategoria() {
        validator.validateCategoria(null);
    }
}
