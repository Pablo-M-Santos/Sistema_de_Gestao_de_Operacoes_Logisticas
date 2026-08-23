package br.com.logicore.modules.usuario.validator;

import br.com.logicore.common.exception.BusinessException;
import br.com.logicore.common.exception.DuplicateResourceException;
import br.com.logicore.modules.usuario.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioValidatorTest {

    @Mock
    private UsuarioRepository repository;

    @InjectMocks
    private UsuarioValidator validator;

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        when(repository.existsByEmail("joao@example.com")).thenReturn(true);

        assertThatThrownBy(() -> validator.validateUniqueEmail("joao@example.com"))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void shouldNotThrowExceptionWhenEmailDoesNotExist() {
        when(repository.existsByEmail("joao@example.com")).thenReturn(false);

        validator.validateUniqueEmail("joao@example.com");
        verify(repository).existsByEmail("joao@example.com");
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExistsForUpdate() {
        when(repository.existsByEmailAndIdNot("joao@example.com", 1L)).thenReturn(true);

        assertThatThrownBy(() -> validator.validateUniqueEmailForUpdate("joao@example.com", 1L))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void shouldNotThrowExceptionWhenEmailDoesNotExistForUpdate() {
        when(repository.existsByEmailAndIdNot("joao@example.com", 1L)).thenReturn(false);

        validator.validateUniqueEmailForUpdate("joao@example.com", 1L);
        verify(repository).existsByEmailAndIdNot("joao@example.com", 1L);
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
    void shouldThrowExceptionForInvalidStatus() {
        assertThatThrownBy(() -> validator.validateStatus("INVALID"))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void shouldAcceptValidStatus() {
        validator.validateStatus("ACTIVE");
        validator.validateStatus("INACTIVE");
    }
}
