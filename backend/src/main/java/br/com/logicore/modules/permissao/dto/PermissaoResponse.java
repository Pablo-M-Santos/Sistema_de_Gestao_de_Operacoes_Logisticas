package br.com.logicore.modules.permissao.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PermissaoResponse {

    private Long id;
    private String nome;
    private String descricao;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;
}
