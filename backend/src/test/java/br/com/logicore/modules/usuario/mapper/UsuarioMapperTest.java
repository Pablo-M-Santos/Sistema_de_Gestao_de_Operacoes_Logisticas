package br.com.logicore.modules.usuario.mapper;

import br.com.logicore.modules.usuario.dto.UsuarioResponse;
import br.com.logicore.modules.usuario.entity.Usuario;
import br.com.logicore.modules.usuario.enums.UserStatus;
import br.com.logicore.modules.employee.entity.Employee;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class UsuarioMapperTest {

    @Test
    void shouldMapUsuarioToResponse() {
        UsuarioMapper mapper = new UsuarioMapper();

        Employee employee = Employee.builder()
                .id(1L)
                .nome("Joao Silva")
                .build();

        LocalDateTime now = LocalDateTime.now();
        Usuario usuario = Usuario.builder()
                .id(1L)
                .nome("Joao Silva")
                .email("joao@example.com")
                .senha("123456")
                .status(UserStatus.ACTIVE)
                .ultimoAcesso(now)
                .funcionario(employee)
                .criadoEm(now)
                .atualizadoEm(now)
                .build();

        UsuarioResponse response = mapper.toResponse(usuario);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getNome()).isEqualTo("Joao Silva");
        assertThat(response.getEmail()).isEqualTo("joao@example.com");
        assertThat(response.getStatus()).isEqualTo("ACTIVE");
        assertThat(response.getUltimoAcesso()).isEqualTo(now);
        assertThat(response.getFuncionarioId()).isEqualTo(1L);
        assertThat(response.getCriadoEm()).isEqualTo(now);
        assertThat(response.getAtualizadoEm()).isEqualTo(now);
    }

    @Test
    void shouldNotExposeSenhaInResponse() {
        UsuarioMapper mapper = new UsuarioMapper();

        Usuario usuario = Usuario.builder()
                .id(1L)
                .nome("Joao Silva")
                .email("joao@example.com")
                .senha("secret123")
                .status(UserStatus.ACTIVE)
                .build();

        UsuarioResponse response = mapper.toResponse(usuario);

        assertThat(response).isNotNull();
        assertThat(response.getEmail()).isEqualTo("joao@example.com");
    }
}
