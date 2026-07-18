package br.com.logicore.modules.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class DepartmentResponse {
    private Long id;
    private String nome;
    private String descricao;
    private String sigla;
    private Boolean ativo;
    private LocalDateTime criado_em;
    private LocalDateTime atualizado_em;
}