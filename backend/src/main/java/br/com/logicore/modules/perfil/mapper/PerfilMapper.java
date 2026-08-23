package br.com.logicore.modules.perfil.mapper;

import br.com.logicore.modules.perfil.dto.CreatePerfilRequest;
import br.com.logicore.modules.perfil.dto.PerfilResponse;
import br.com.logicore.modules.perfil.entity.Perfil;
import org.springframework.stereotype.Component;

@Component
public class PerfilMapper {

    public Perfil toEntity(CreatePerfilRequest request) {
        return Perfil.builder()
                .nome(request.getNome())
                .descricao(request.getDescricao())
                .build();
    }

    public PerfilResponse toResponse(Perfil perfil) {
        return PerfilResponse.builder()
                .id(perfil.getId())
                .nome(perfil.getNome())
                .descricao(perfil.getDescricao())
                .criadoEm(perfil.getCriadoEm())
                .atualizadoEm(perfil.getAtualizadoEm())
                .build();
    }
}
