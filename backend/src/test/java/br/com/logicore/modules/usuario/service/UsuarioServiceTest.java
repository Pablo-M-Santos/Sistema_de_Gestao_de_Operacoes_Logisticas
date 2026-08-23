package br.com.logicore.modules.usuario.service;

import br.com.logicore.common.dto.PageResponse;
import br.com.logicore.common.exception.ResourceNotFoundException;
import br.com.logicore.modules.employee.entity.Employee;
import br.com.logicore.modules.employee.repository.EmployeeRepository;
import br.com.logicore.modules.usuario.dto.CreateUsuarioRequest;
import br.com.logicore.modules.usuario.dto.UpdateUsuarioRequest;
import br.com.logicore.modules.usuario.dto.UsuarioResponse;
import br.com.logicore.modules.usuario.dto.UsuarioSummary;
import br.com.logicore.modules.usuario.entity.Usuario;
import br.com.logicore.modules.usuario.enums.UserStatus;
import br.com.logicore.modules.usuario.mapper.UsuarioMapper;
import br.com.logicore.modules.usuario.repository.UsuarioRepository;
import br.com.logicore.modules.usuario.validator.UsuarioValidator;
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
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository repository;

    @Mock
    private UsuarioMapper mapper;

    @Mock
    private UsuarioValidator validator;

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private UsuarioService service;

    @Test
    void shouldCreateUsuarioSuccessfully() {
        CreateUsuarioRequest request = new CreateUsuarioRequest();
        request.setNome("Joao Silva");
        request.setEmail("joao@example.com");
        request.setSenha("123456");
        request.setFuncionarioId(1L);

        Employee employee = Employee.builder().id(1L).nome("Joao").build();
        Usuario entity = Usuario.builder().id(1L).nome("Joao Silva").email("joao@example.com").funcionario(employee).build();
        UsuarioResponse response = UsuarioResponse.builder().id(1L).nome("Joao Silva").email("joao@example.com").build();

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(mapper.toResponse(entity)).thenReturn(response);
        when(repository.save(any(Usuario.class))).thenReturn(entity);

        UsuarioResponse result = service.create(request);

        assertThat(result).isNotNull();
        assertThat(result.getNome()).isEqualTo("Joao Silva");
        verify(validator).validateUniqueEmail("joao@example.com");
        verify(validator).validateUniqueFuncionarioId(1L);
        verify(repository).save(any(Usuario.class));
    }

    @Test
    void shouldThrowExceptionWhenEmployeeNotFound() {
        CreateUsuarioRequest request = new CreateUsuarioRequest();
        request.setNome("Joao Silva");
        request.setEmail("joao@example.com");
        request.setSenha("123456");
        request.setFuncionarioId(999L);

        when(employeeRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldFindUsuarioById() {
        Usuario usuario = Usuario.builder().id(1L).nome("Joao").email("joao@example.com").build();
        UsuarioResponse response = UsuarioResponse.builder().id(1L).nome("Joao").email("joao@example.com").build();

        when(repository.findById(1L)).thenReturn(Optional.of(usuario));
        when(mapper.toResponse(usuario)).thenReturn(response);

        UsuarioResponse result = service.findById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getNome()).isEqualTo("Joao");
    }

    @Test
    void shouldThrowExceptionWhenUsuarioNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(1L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldReturnPaginatedUsuarios() {
        Usuario usuario = Usuario.builder().id(1L).nome("Joao").email("joao@example.com").build();
        Page<Usuario> page = new PageImpl<>(java.util.List.of(usuario), PageRequest.of(0, 20), 1);

        when(repository.findAll(org.mockito.ArgumentMatchers.any(org.springframework.data.jpa.domain.Specification.class), any(Pageable.class)))
                .thenReturn(page);

        PageResponse<UsuarioResponse> result = service.findAll(null, null, null, null, PageRequest.of(0, 20));

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void shouldUpdateUsuario() {
        Usuario usuario = Usuario.builder()
                .id(1L)
                .nome("Joao")
                .email("joao@example.com")
                .senha("123456")
                .funcionario(Employee.builder().id(1L).build())
                .build();

        UpdateUsuarioRequest request = new UpdateUsuarioRequest();
        request.setNome("Joao Updated");
        request.setEmail("joao.updated@example.com");

        UsuarioResponse response = UsuarioResponse.builder()
                .id(1L)
                .nome("Joao Updated")
                .email("joao.updated@example.com")
                .build();

        when(repository.findById(1L)).thenReturn(Optional.of(usuario));
        when(mapper.toResponse(usuario)).thenReturn(response);
        when(repository.save(usuario)).thenReturn(usuario);

        UsuarioResponse result = service.update(1L, request);

        assertThat(result).isNotNull();
        assertThat(result.getNome()).isEqualTo("Joao Updated");
    }

    @Test
    void shouldSoftDeleteUsuario() {
        Usuario usuario = Usuario.builder()
                .id(1L)
                .status(UserStatus.ACTIVE)
                .build();

        when(repository.findById(1L)).thenReturn(Optional.of(usuario));
        when(repository.save(usuario)).thenReturn(usuario);

        service.delete(1L);

        assertThat(usuario.getStatus()).isEqualTo(UserStatus.INACTIVE);
        verify(repository).save(usuario);
    }

    @Test
    void shouldActivateUsuario() {
        Usuario usuario = Usuario.builder()
                .id(1L)
                .status(UserStatus.INACTIVE)
                .build();

        when(repository.findById(1L)).thenReturn(Optional.of(usuario));
        when(repository.save(usuario)).thenReturn(usuario);

        service.activate(1L);

        assertThat(usuario.getStatus()).isEqualTo(UserStatus.ACTIVE);
        verify(repository).save(usuario);
    }

    @Test
    void shouldDeactivateUsuario() {
        Usuario usuario = Usuario.builder()
                .id(1L)
                .status(UserStatus.ACTIVE)
                .build();

        when(repository.findById(1L)).thenReturn(Optional.of(usuario));
        when(repository.save(usuario)).thenReturn(usuario);

        service.deactivate(1L);

        assertThat(usuario.getStatus()).isEqualTo(UserStatus.INACTIVE);
        verify(repository).save(usuario);
    }

    @Test
    void shouldReturnSummary() {
        when(repository.count()).thenReturn(10L);
        when(repository.countByStatus(UserStatus.ACTIVE)).thenReturn(8L);
        when(repository.countByStatus(UserStatus.INACTIVE)).thenReturn(2L);

        UsuarioSummary summary = service.summary();

        assertThat(summary).isNotNull();
        assertThat(summary.getTotal()).isEqualTo(10L);
        assertThat(summary.getActive()).isEqualTo(8L);
        assertThat(summary.getInactive()).isEqualTo(2L);
    }
}
