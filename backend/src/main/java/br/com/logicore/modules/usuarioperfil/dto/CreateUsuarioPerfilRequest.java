package br.com.logicore.modules.usuarioperfil.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUsuarioPerfilRequest {

    @NotNull(message = "Usuario ID is required.")
    private Long usuarioId;

    @NotNull(message = "Perfil ID is required.")
    private Long perfilId;
}
