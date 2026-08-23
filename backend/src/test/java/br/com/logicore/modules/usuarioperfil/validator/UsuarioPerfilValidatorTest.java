package br.com.logicore.modules.usuarioperfil.validator;

import br.com.logicore.common.exception.DuplicateResourceException;
import br.com.logicore.common.exception.ResourceNotFoundException;
import br.com.logicore.modules.perfil.repository.PerfilRepository;
import br.com.logicore.modules.usuarioperfil.repository.UsuarioPerfilRepository;
import br.com.logicore.modules.usuario.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioPerfilValidatorTest {

    @Mock
    private UsuarioPerfilRepository repository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PerfilRepository perfilRepository;

    @InjectMocks
    private UsuarioPerfilValidator validator;

    @Test
    void shouldThrowExceptionWhenUsuarioNotFound() {
        when(usuarioRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> validator.validateUsuarioExists(1L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldNotThrowExceptionWhenUsuarioExists() {
        when(usuarioRepository.existsById(1L)).thenReturn(true);

        validator.validateUsuarioExists(1L);
        verify(usuarioRepository).existsById(1L);
    }

    @Test
    void shouldThrowExceptionWhenPerfilNotFound() {
        when(perfilRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> validator.validatePerfilExists(1L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldNotThrowExceptionWhenPerfilExists() {
        when(perfilRepository.existsById(1L)).thenReturn(true);

        validator.validatePerfilExists(1L);
        verify(perfilRepository).existsById(1L);
    }

    @Test
    void shouldThrowExceptionWhenAssociationAlreadyExists() {
        when(repository.existsByUsuarioIdAndPerfilId(1L, 2L)).thenReturn(true);

        assertThatThrownBy(() -> validator.validateUniqueAssociation(1L, 2L))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void shouldNotThrowExceptionWhenAssociationDoesNotExist() {
        when(repository.existsByUsuarioIdAndPerfilId(1L, 2L)).thenReturn(false);

        validator.validateUniqueAssociation(1L, 2L);
        verify(repository).existsByUsuarioIdAndPerfilId(1L, 2L);
    }
}
