package br.com.logicore.modules.usuario.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UsuarioResponse {

    private Long id;
    private String nome;
    private String email;
    private String status;
    private LocalDateTime ultimoAcesso;
    private Long funcionarioId;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;
}
