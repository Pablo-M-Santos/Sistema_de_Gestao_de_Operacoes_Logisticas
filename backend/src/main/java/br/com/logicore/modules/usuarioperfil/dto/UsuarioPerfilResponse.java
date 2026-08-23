package br.com.logicore.modules.usuarioperfil.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UsuarioPerfilResponse {

    private Long id;
    private Long usuarioId;
    private Long perfilId;
    private String nomeUsuario;
    private String nomePerfil;
}
