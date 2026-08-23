package br.com.logicore.modules.perfil.validator;

import br.com.logicore.common.exception.DuplicateResourceException;
import br.com.logicore.modules.perfil.repository.PerfilRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PerfilValidatorTest {

    @Mock
    private PerfilRepository repository;

    @InjectMocks
    private PerfilValidator validator;

    @Test
    void shouldThrowExceptionWhenNomeAlreadyExists() {
        when(repository.existsByNome("Admin")).thenReturn(true);

        assertThatThrownBy(() -> validator.validateUniqueNome("Admin"))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void shouldNotThrowExceptionWhenNomeDoesNotExist() {
        when(repository.existsByNome("Admin")).thenReturn(false);

        validator.validateUniqueNome("Admin");
        verify(repository).existsByNome("Admin");
    }

    @Test
    void shouldThrowExceptionWhenNomeAlreadyExistsForUpdate() {
        when(repository.existsByNomeAndIdNot("Admin", 1L)).thenReturn(true);

        assertThatThrownBy(() -> validator.validateUniqueNomeForUpdate("Admin", 1L))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void shouldNotThrowExceptionWhenNomeDoesNotExistForUpdate() {
        when(repository.existsByNomeAndIdNot("Admin", 1L)).thenReturn(false);

        validator.validateUniqueNomeForUpdate("Admin", 1L);
        verify(repository).existsByNomeAndIdNot("Admin", 1L);
    }
}
