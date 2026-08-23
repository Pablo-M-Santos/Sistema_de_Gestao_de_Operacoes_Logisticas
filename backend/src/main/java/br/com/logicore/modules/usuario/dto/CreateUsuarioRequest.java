package br.com.logicore.modules.usuario.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUsuarioRequest {

    @NotBlank(message = "Name is required.")
    @Size(max = 150, message = "Name must have at most 150 characters.")
    private String nome;

    @NotBlank(message = "Email is required.")
    @Email(message = "Email must be valid.")
    @Size(max = 150, message = "Email must have at most 150 characters.")
    private String email;

    @NotBlank(message = "Password is required.")
    @Size(min = 6, max = 255, message = "Password must have between 6 and 255 characters.")
    private String senha;

    @NotNull(message = "Employee ID is required.")
    private Long funcionarioId;
}
