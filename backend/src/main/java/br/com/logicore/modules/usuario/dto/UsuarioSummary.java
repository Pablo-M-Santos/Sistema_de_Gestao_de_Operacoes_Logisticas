package br.com.logicore.modules.usuario.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UsuarioSummary {

    private long total;
    private long active;
    private long inactive;
}
