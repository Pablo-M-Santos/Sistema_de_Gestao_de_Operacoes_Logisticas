package br.com.logicore.modules.cargo.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CargoResponse {

    private Long id;

    private String nome;

    private String descricao;

    private String codigo;

    private Boolean ativo;

    private LocalDateTime criadoEm;

    private LocalDateTime atualizadoEm;

}
