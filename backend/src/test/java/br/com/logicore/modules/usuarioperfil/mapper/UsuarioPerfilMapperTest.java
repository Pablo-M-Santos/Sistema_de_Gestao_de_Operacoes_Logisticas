package br.com.logicore.modules.usuarioperfil.mapper;

import br.com.logicore.modules.usuarioperfil.dto.UsuarioPerfilResponse;
import br.com.logicore.modules.usuarioperfil.entity.UsuarioPerfil;
import br.com.logicore.modules.perfil.entity.Perfil;
import br.com.logicore.modules.usuario.entity.Usuario;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class UsuarioPerfilMapperTest {

    @Test
    void shouldMapUsuarioPerfilToResponse() {
        UsuarioPerfilMapper mapper = new UsuarioPerfilMapper();

        Usuario usuario = Usuario.builder().id(1L).nome("João").build();
        Perfil perfil = Perfil.builder().id(2L).nome("Admin").build();
        UsuarioPerfil entity = UsuarioPerfil.builder()
                .id(1L)
                .usuario(usuario)
                .perfil(perfil)
                .build();

        UsuarioPerfilResponse response = mapper.toResponse(entity);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getUsuarioId()).isEqualTo(1L);
        assertThat(response.getPerfilId()).isEqualTo(2L);
        assertThat(response.getNomeUsuario()).isEqualTo("João");
        assertThat(response.getNomePerfil()).isEqualTo("Admin");
    }

    @Test
    void shouldHandleNullUsuarioAndPerfil() {
        UsuarioPerfilMapper mapper = new UsuarioPerfilMapper();

        UsuarioPerfil entity = UsuarioPerfil.builder()
                .id(1L)
                .usuario(null)
                .perfil(null)
                .build();

        UsuarioPerfilResponse response = mapper.toResponse(entity);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getUsuarioId()).isNull();
        assertThat(response.getPerfilId()).isNull();
        assertThat(response.getNomeUsuario()).isNull();
        assertThat(response.getNomePerfil()).isNull();
    }
}
