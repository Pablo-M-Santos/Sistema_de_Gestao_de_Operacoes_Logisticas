package br.com.logicore.modules.usuarioperfil.service;

import br.com.logicore.common.dto.PageResponse;
import br.com.logicore.common.exception.ResourceNotFoundException;
import br.com.logicore.modules.perfil.entity.Perfil;
import br.com.logicore.modules.perfil.repository.PerfilRepository;
import br.com.logicore.modules.usuarioperfil.dto.CreateUsuarioPerfilRequest;
import br.com.logicore.modules.usuarioperfil.dto.UsuarioPerfilResponse;
import br.com.logicore.modules.usuarioperfil.dto.UsuarioPerfilSummary;
import br.com.logicore.modules.usuarioperfil.entity.UsuarioPerfil;
import br.com.logicore.modules.usuarioperfil.mapper.UsuarioPerfilMapper;
import br.com.logicore.modules.usuarioperfil.repository.UsuarioPerfilRepository;
import br.com.logicore.modules.usuarioperfil.validator.UsuarioPerfilValidator;
import br.com.logicore.modules.usuario.entity.Usuario;
import br.com.logicore.modules.usuario.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioPerfilServiceTest {

    @Mock
    private UsuarioPerfilRepository repository;

    @Mock
    private UsuarioPerfilMapper mapper;

    @Mock
    private UsuarioPerfilValidator validator;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PerfilRepository perfilRepository;

    @InjectMocks
    private UsuarioPerfilService service;

    @Test
    void shouldCreateAssociationSuccessfully() {
        CreateUsuarioPerfilRequest request = new CreateUsuarioPerfilRequest();
        request.setUsuarioId(1L);
        request.setPerfilId(2L);

        Usuario usuario = Usuario.builder().id(1L).nome("João").build();
        Perfil perfil = Perfil.builder().id(2L).nome("Admin").build();
        UsuarioPerfil entity = UsuarioPerfil.builder().id(1L).usuario(usuario).perfil(perfil).build();
        UsuarioPerfilResponse response = UsuarioPerfilResponse.builder().id(1L).usuarioId(1L).perfilId(2L).nomeUsuario("João").nomePerfil("Admin").build();

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(perfilRepository.findById(2L)).thenReturn(Optional.of(perfil));
        when(repository.save(any(UsuarioPerfil.class))).thenReturn(entity);
        when(mapper.toResponse(entity)).thenReturn(response);

        UsuarioPerfilResponse result = service.create(request);

        assertThat(result).isNotNull();
        assertThat(result.getUsuarioId()).isEqualTo(1L);
        assertThat(result.getPerfilId()).isEqualTo(2L);
        verify(validator).validateUsuarioExists(1L);
        verify(validator).validatePerfilExists(2L);
        verify(validator).validateUniqueAssociation(1L, 2L);
        verify(repository).save(any(UsuarioPerfil.class));
    }

    @Test
    void shouldThrowExceptionWhenUsuarioNotFound() {
        CreateUsuarioPerfilRequest request = new CreateUsuarioPerfilRequest();
        request.setUsuarioId(1L);
        request.setPerfilId(2L);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldThrowExceptionWhenPerfilNotFound() {
        CreateUsuarioPerfilRequest request = new CreateUsuarioPerfilRequest();
        request.setUsuarioId(1L);
        request.setPerfilId(2L);

        Usuario usuario = Usuario.builder().id(1L).nome("João").build();
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(perfilRepository.findById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldFindAssociationById() {
        Usuario usuario = Usuario.builder().id(1L).nome("João").build();
        Perfil perfil = Perfil.builder().id(2L).nome("Admin").build();
        UsuarioPerfil entity = UsuarioPerfil.builder().id(1L).usuario(usuario).perfil(perfil).build();
        UsuarioPerfilResponse response = UsuarioPerfilResponse.builder().id(1L).usuarioId(1L).perfilId(2L).nomeUsuario("João").nomePerfil("Admin").build();

        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(mapper.toResponse(entity)).thenReturn(response);

        UsuarioPerfilResponse result = service.findById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void shouldThrowExceptionWhenAssociationNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(1L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldReturnPaginatedAssociations() {
        Usuario usuario = Usuario.builder().id(1L).nome("João").build();
        Perfil perfil = Perfil.builder().id(2L).nome("Admin").build();
        UsuarioPerfil entity = UsuarioPerfil.builder().id(1L).usuario(usuario).perfil(perfil).build();
        Page<UsuarioPerfil> page = new PageImpl<>(java.util.List.of(entity), PageRequest.of(0, 20), 1);

        when(repository.findAll(org.mockito.ArgumentMatchers.any(org.springframework.data.jpa.domain.Specification.class), any(Pageable.class)))
                .thenReturn(page);

        PageResponse<UsuarioPerfilResponse> result = service.findAll(null, null, null, PageRequest.of(0, 20));

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void shouldReturnSummary() {
        when(repository.count()).thenReturn(5L);

        UsuarioPerfilSummary summary = service.summary();

        assertThat(summary).isNotNull();
        assertThat(summary.getTotal()).isEqualTo(5L);
    }

    @Test
    void shouldDeleteAssociationPhysically() {
        Usuario usuario = Usuario.builder().id(1L).nome("João").build();
        Perfil perfil = Perfil.builder().id(2L).nome("Admin").build();
        UsuarioPerfil entity = UsuarioPerfil.builder().id(1L).usuario(usuario).perfil(perfil).build();

        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        doNothing().when(repository).delete(entity);

        service.delete(1L);

        verify(repository).delete(entity);
    }
}
