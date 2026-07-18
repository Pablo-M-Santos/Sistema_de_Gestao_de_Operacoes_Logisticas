package br.com.logicore.modules.mapper;

import br.com.logicore.modules.dto.CreateDepartmentRequest;
import br.com.logicore.modules.dto.DepartmentResponse;
import br.com.logicore.modules.entity.Department;
import org.springframework.stereotype.Component;

@Component
public class DepartmentMapper {

    public Department toEntity(CreateDepartmentRequest request) {
        return Department.builder()
                .nome(request.getNome())
                .descricao(request.getDescricao())
                .sigla(request.getSigla())
                .build();
    }

    public DepartmentResponse toResponse(Department entity) {
        return DepartmentResponse.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .descricao(entity.getDescricao())
                .sigla(entity.getSigla())
                .ativo(entity.getAtivo())
                .criado_em(entity.getCriado_em())
                .atualizado_em(entity.getAtualizado_em())
                .build();
    }
}