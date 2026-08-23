package br.com.logicore.modules.usuarioperfil.mapper;

import br.com.logicore.modules.usuarioperfil.dto.CreateUsuarioPerfilRequest;
import br.com.logicore.modules.usuarioperfil.dto.UsuarioPerfilResponse;
import br.com.logicore.modules.usuarioperfil.entity.UsuarioPerfil;
import org.springframework.stereotype.Component;

@Component
public class UsuarioPerfilMapper {

    public UsuarioPerfil toEntity(CreateUsuarioPerfilRequest request) {
        return UsuarioPerfil.builder().build();
    }

    public UsuarioPerfilResponse toResponse(UsuarioPerfil entity) {
        return UsuarioPerfilResponse.builder()
                .id(entity.getId())
                .usuarioId(entity.getUsuario() != null ? entity.getUsuario().getId() : null)
                .perfilId(entity.getPerfil() != null ? entity.getPerfil().getId() : null)
                .nomeUsuario(entity.getUsuario() != null ? entity.getUsuario().getNome() : null)
                .nomePerfil(entity.getPerfil() != null ? entity.getPerfil().getNome() : null)
                .build();
    }
}
