package br.com.logicore.modules.perfil.service;

import br.com.logicore.common.dto.PageResponse;
import br.com.logicore.common.exception.ResourceNotFoundException;
import br.com.logicore.modules.perfil.dto.CreatePerfilRequest;
import br.com.logicore.modules.perfil.dto.PerfilResponse;
import br.com.logicore.modules.perfil.dto.PerfilSummary;
import br.com.logicore.modules.perfil.dto.UpdatePerfilRequest;
import br.com.logicore.modules.perfil.entity.Perfil;
import br.com.logicore.modules.perfil.mapper.PerfilMapper;
import br.com.logicore.modules.perfil.repository.PerfilRepository;
import br.com.logicore.modules.perfil.validator.PerfilValidator;
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
class PerfilServiceTest {

    @Mock
    private PerfilRepository repository;

    @Mock
    private PerfilMapper mapper;

    @Mock
    private PerfilValidator validator;

    @InjectMocks
    private PerfilService service;

    @Test
    void shouldCreatePerfilSuccessfully() {
        CreatePerfilRequest request = new CreatePerfilRequest();
        request.setNome("Admin");
        request.setDescricao("Administrator profile");

        Perfil entity = Perfil.builder().id(1L).nome("Admin").descricao("Administrator profile").build();
        PerfilResponse response = PerfilResponse.builder().id(1L).nome("Admin").descricao("Administrator profile").build();

        when(mapper.toEntity(request)).thenReturn(entity);
        when(mapper.toResponse(entity)).thenReturn(response);
        when(repository.save(entity)).thenReturn(entity);

        PerfilResponse result = service.create(request);

        assertThat(result).isNotNull();
        assertThat(result.getNome()).isEqualTo("Admin");
        verify(validator).validateUniqueNome("Admin");
        verify(repository).save(entity);
    }

    @Test
    void shouldFindPerfilById() {
        Perfil perfil = Perfil.builder().id(1L).nome("Admin").descricao("Administrator profile").build();
        PerfilResponse response = PerfilResponse.builder().id(1L).nome("Admin").descricao("Administrator profile").build();

        when(repository.findById(1L)).thenReturn(Optional.of(perfil));
        when(mapper.toResponse(perfil)).thenReturn(response);

        PerfilResponse result = service.findById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getNome()).isEqualTo("Admin");
    }

    @Test
    void shouldThrowExceptionWhenPerfilNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(1L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldReturnPaginatedPerfis() {
        Perfil perfil = Perfil.builder().id(1L).nome("Admin").descricao("Administrator profile").build();
        Page<Perfil> page = new PageImpl<>(java.util.List.of(perfil), PageRequest.of(0, 20), 1);

        when(repository.findAll(org.mockito.ArgumentMatchers.any(org.springframework.data.jpa.domain.Specification.class), any(Pageable.class)))
                .thenReturn(page);

        PageResponse<PerfilResponse> result = service.findAll(null, PageRequest.of(0, 20));

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void shouldUpdatePerfil() {
        Perfil perfil = Perfil.builder()
                .id(1L)
                .nome("Admin")
                .descricao("Administrator profile")
                .build();

        UpdatePerfilRequest request = new UpdatePerfilRequest();
        request.setNome("Admin Updated");
        request.setDescricao("Updated description");

        PerfilResponse response = PerfilResponse.builder()
                .id(1L)
                .nome("Admin Updated")
                .descricao("Updated description")
                .build();

        when(repository.findById(1L)).thenReturn(Optional.of(perfil));
        when(mapper.toResponse(perfil)).thenReturn(response);
        when(repository.save(perfil)).thenReturn(perfil);

        PerfilResponse result = service.update(1L, request);

        assertThat(result).isNotNull();
        assertThat(result.getNome()).isEqualTo("Admin Updated");
    }

    @Test
    void shouldDeletePerfilPhysically() {
        Perfil perfil = Perfil.builder().id(1L).nome("Admin").build();

        when(repository.findById(1L)).thenReturn(Optional.of(perfil));
        doNothing().when(repository).delete(perfil);

        service.delete(1L);

        verify(repository).delete(perfil);
    }

    @Test
    void shouldReturnSummary() {
        when(repository.count()).thenReturn(5L);

        PerfilSummary summary = service.summary();

        assertThat(summary).isNotNull();
        assertThat(summary.getTotal()).isEqualTo(5L);
    }
}
