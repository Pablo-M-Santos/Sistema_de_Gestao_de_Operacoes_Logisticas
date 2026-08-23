package br.com.logicore.modules.usuario.mapper;

import br.com.logicore.modules.usuario.dto.CreateUsuarioRequest;
import br.com.logicore.modules.usuario.dto.UsuarioResponse;
import br.com.logicore.modules.usuario.entity.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    public Usuario toEntity(CreateUsuarioRequest request) {
        return Usuario.builder()
                .nome(request.getNome())
                .email(request.getEmail())
                .senha(request.getSenha())
                .build();
    }

    public UsuarioResponse toResponse(Usuario usuario) {
        return UsuarioResponse.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .status(usuario.getStatus().name())
                .ultimoAcesso(usuario.getUltimoAcesso())
                .funcionarioId(usuario.getFuncionario() != null ? usuario.getFuncionario().getId() : null)
                .criadoEm(usuario.getCriadoEm())
                .atualizadoEm(usuario.getAtualizadoEm())
                .build();
    }
}
