package br.com.logicore.modules.permissao.mapper;

import br.com.logicore.modules.permissao.dto.PermissaoResponse;
import br.com.logicore.modules.permissao.entity.Permissao;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class PermissaoMapperTest {

    @Test
    void shouldMapPermissaoToResponse() {
        PermissaoMapper mapper = new PermissaoMapper();

        LocalDateTime now = LocalDateTime.now();
        Permissao permissao = Permissao.builder()
                .id(1L)
                .nome("READ")
                .descricao("Read permission")
                .criadoEm(now)
                .atualizadoEm(now)
                .build();

        PermissaoResponse response = mapper.toResponse(permissao);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getNome()).isEqualTo("READ");
        assertThat(response.getDescricao()).isEqualTo("Read permission");
        assertThat(response.getCriadoEm()).isEqualTo(now);
        assertThat(response.getAtualizadoEm()).isEqualTo(now);
    }
}
