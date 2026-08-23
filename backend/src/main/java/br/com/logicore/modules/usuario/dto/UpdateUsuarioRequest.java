package br.com.logicore.modules.usuario.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUsuarioRequest {

    @Size(max = 150, message = "Name must have at most 150 characters.")
    private String nome;

    @Email(message = "Email must be valid.")
    @Size(max = 150, message = "Email must have at most 150 characters.")
    private String email;

    @Size(min = 6, max = 255, message = "Password must have between 6 and 255 characters.")
    private String senha;

    private Long funcionarioId;

    private String status;
}
