package br.com.logicore.modules.permissao.mapper;

import br.com.logicore.modules.permissao.dto.CreatePermissaoRequest;
import br.com.logicore.modules.permissao.dto.PermissaoResponse;
import br.com.logicore.modules.permissao.entity.Permissao;
import org.springframework.stereotype.Component;

@Component
public class PermissaoMapper {

    public Permissao toEntity(CreatePermissaoRequest request) {
        return Permissao.builder()
                .nome(request.getNome())
                .descricao(request.getDescricao())
                .build();
    }

    public PermissaoResponse toResponse(Permissao permissao) {
        return PermissaoResponse.builder()
                .id(permissao.getId())
                .nome(permissao.getNome())
                .descricao(permissao.getDescricao())
                .criadoEm(permissao.getCriadoEm())
                .atualizadoEm(permissao.getAtualizadoEm())
                .build();
    }
}
