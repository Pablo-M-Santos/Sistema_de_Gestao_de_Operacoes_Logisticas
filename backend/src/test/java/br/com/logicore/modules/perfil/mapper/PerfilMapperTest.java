package br.com.logicore.modules.perfil.mapper;

import br.com.logicore.modules.perfil.dto.PerfilResponse;
import br.com.logicore.modules.perfil.entity.Perfil;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class PerfilMapperTest {

    @Test
    void shouldMapPerfilToResponse() {
        PerfilMapper mapper = new PerfilMapper();

        LocalDateTime now = LocalDateTime.now();
        Perfil perfil = Perfil.builder()
                .id(1L)
                .nome("Admin")
                .descricao("Administrator profile")
                .criadoEm(now)
                .atualizadoEm(now)
                .build();

        PerfilResponse response = mapper.toResponse(perfil);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getNome()).isEqualTo("Admin");
        assertThat(response.getDescricao()).isEqualTo("Administrator profile");
        assertThat(response.getCriadoEm()).isEqualTo(now);
        assertThat(response.getAtualizadoEm()).isEqualTo(now);
    }
}
