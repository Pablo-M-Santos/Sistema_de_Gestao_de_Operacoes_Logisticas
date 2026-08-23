package br.com.logicore.modules.permissao.validator;

import br.com.logicore.common.exception.DuplicateResourceException;
import br.com.logicore.modules.permissao.repository.PermissaoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PermissaoValidatorTest {

    @Mock
    private PermissaoRepository repository;

    @InjectMocks
    private PermissaoValidator validator;

    @Test
    void shouldThrowExceptionWhenNomeAlreadyExists() {
        when(repository.existsByNome("READ")).thenReturn(true);

        assertThatThrownBy(() -> validator.validateUniqueNome("READ"))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void shouldNotThrowExceptionWhenNomeDoesNotExist() {
        when(repository.existsByNome("READ")).thenReturn(false);

        validator.validateUniqueNome("READ");
        verify(repository).existsByNome("READ");
    }

    @Test
    void shouldThrowExceptionWhenNomeAlreadyExistsForUpdate() {
        when(repository.existsByNomeAndIdNot("READ", 1L)).thenReturn(true);

        assertThatThrownBy(() -> validator.validateUniqueNomeForUpdate("READ", 1L))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void shouldNotThrowExceptionWhenNomeDoesNotExistForUpdate() {
        when(repository.existsByNomeAndIdNot("READ", 1L)).thenReturn(false);

        validator.validateUniqueNomeForUpdate("READ", 1L);
        verify(repository).existsByNomeAndIdNot("READ", 1L);
    }
}
