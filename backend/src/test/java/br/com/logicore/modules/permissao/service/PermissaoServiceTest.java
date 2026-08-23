package br.com.logicore.modules.permissao.service;

import br.com.logicore.common.dto.PageResponse;
import br.com.logicore.common.exception.ResourceNotFoundException;
import br.com.logicore.modules.permissao.dto.CreatePermissaoRequest;
import br.com.logicore.modules.permissao.dto.PermissaoResponse;
import br.com.logicore.modules.permissao.dto.PermissaoSummary;
import br.com.logicore.modules.permissao.dto.UpdatePermissaoRequest;
import br.com.logicore.modules.permissao.entity.Permissao;
import br.com.logicore.modules.permissao.mapper.PermissaoMapper;
import br.com.logicore.modules.permissao.repository.PermissaoRepository;
import br.com.logicore.modules.permissao.validator.PermissaoValidator;
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
class PermissaoServiceTest {

    @Mock
    private PermissaoRepository repository;

    @Mock
    private PermissaoMapper mapper;

    @Mock
    private PermissaoValidator validator;

    @InjectMocks
    private PermissaoService service;

    @Test
    void shouldCreatePermissaoSuccessfully() {
        CreatePermissaoRequest request = new CreatePermissaoRequest();
        request.setNome("READ");
        request.setDescricao("Read permission");

        Permissao entity = Permissao.builder().id(1L).nome("READ").descricao("Read permission").build();
        PermissaoResponse response = PermissaoResponse.builder().id(1L).nome("READ").descricao("Read permission").build();

        when(mapper.toEntity(request)).thenReturn(entity);
        when(mapper.toResponse(entity)).thenReturn(response);
        when(repository.save(entity)).thenReturn(entity);

        PermissaoResponse result = service.create(request);

        assertThat(result).isNotNull();
        assertThat(result.getNome()).isEqualTo("READ");
        verify(validator).validateUniqueNome("READ");
        verify(repository).save(entity);
    }

    @Test
    void shouldFindPermissaoById() {
        Permissao permissao = Permissao.builder().id(1L).nome("READ").descricao("Read permission").build();
        PermissaoResponse response = PermissaoResponse.builder().id(1L).nome("READ").descricao("Read permission").build();

        when(repository.findById(1L)).thenReturn(Optional.of(permissao));
        when(mapper.toResponse(permissao)).thenReturn(response);

        PermissaoResponse result = service.findById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getNome()).isEqualTo("READ");
    }

    @Test
    void shouldThrowExceptionWhenPermissaoNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(1L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldReturnPaginatedPermissoes() {
        Permissao permissao = Permissao.builder().id(1L).nome("READ").descricao("Read permission").build();
        Page<Permissao> page = new PageImpl<>(java.util.List.of(permissao), PageRequest.of(0, 20), 1);

        when(repository.findAll(org.mockito.ArgumentMatchers.any(org.springframework.data.jpa.domain.Specification.class), any(Pageable.class)))
                .thenReturn(page);

        PageResponse<PermissaoResponse> result = service.findAll(null, PageRequest.of(0, 20));

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void shouldUpdatePermissao() {
        Permissao permissao = Permissao.builder()
                .id(1L)
                .nome("READ")
                .descricao("Read permission")
                .build();

        UpdatePermissaoRequest request = new UpdatePermissaoRequest();
        request.setNome("READ_UPDATED");
        request.setDescricao("Updated description");

        PermissaoResponse response = PermissaoResponse.builder()
                .id(1L)
                .nome("READ_UPDATED")
                .descricao("Updated description")
                .build();

        when(repository.findById(1L)).thenReturn(Optional.of(permissao));
        when(mapper.toResponse(permissao)).thenReturn(response);
        when(repository.save(permissao)).thenReturn(permissao);

        PermissaoResponse result = service.update(1L, request);

        assertThat(result).isNotNull();
        assertThat(result.getNome()).isEqualTo("READ_UPDATED");
    }

    @Test
    void shouldDeletePermissaoPhysically() {
        Permissao permissao = Permissao.builder().id(1L).nome("READ").build();

        when(repository.findById(1L)).thenReturn(Optional.of(permissao));
        doNothing().when(repository).delete(permissao);

        service.delete(1L);

        verify(repository).delete(permissao);
    }

    @Test
    void shouldReturnSummary() {
        when(repository.count()).thenReturn(5L);

        PermissaoSummary summary = service.summary();

        assertThat(summary).isNotNull();
        assertThat(summary.getTotal()).isEqualTo(5L);
    }
}
