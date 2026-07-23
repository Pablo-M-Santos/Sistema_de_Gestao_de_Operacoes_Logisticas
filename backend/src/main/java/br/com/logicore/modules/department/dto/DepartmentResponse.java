package br.com.logicore.modules.department.dto;

import br.com.logicore.modules.department.enums.DepartmentStatus;
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
    private DepartmentStatus status;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;
}