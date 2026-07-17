package br.com.logicore.modules.department.mapper;

import br.com.logicore.modules.department.dto.CreateDepartmentRequest;
import br.com.logicore.modules.department.dto.DepartmentResponse;
import br.com.logicore.modules.department.entity.Department;
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