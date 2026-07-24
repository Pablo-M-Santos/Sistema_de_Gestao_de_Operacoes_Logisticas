package br.com.logicore.modules.department.mapper;

import br.com.logicore.modules.department.dto.CreateDepartmentRequest;
import br.com.logicore.modules.department.dto.DepartmentResponse;
import br.com.logicore.modules.department.entity.Department;
import br.com.logicore.modules.department.enums.DepartmentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class DepartmentMapperTest {

    private DepartmentMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new DepartmentMapper();
    }

    @Test
    void shouldConvertCreateRequestToEntity() {

        CreateDepartmentRequest request = new CreateDepartmentRequest();

        request.setNome("Technology");
        request.setDescricao("Technology department");
        request.setSigla("TI");

        Department result = mapper.toEntity(request);

        assertThat(result).isNotNull();
        assertThat(result.getNome()).isEqualTo("Technology");
        assertThat(result.getDescricao()).isEqualTo("Technology department");
        assertThat(result.getSigla()).isEqualTo("TI");
    }


    @Test
    void shouldConvertEntityToResponse() {

        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        Department department = Department.builder()
                .id(1L)
                .nome("Technology")
                .descricao("Technology department")
                .sigla("TI")
                .status(DepartmentStatus.ACTIVE)
                .criadoEm(createdAt)
                .atualizadoEm(updatedAt)
                .build();


        DepartmentResponse result = mapper.toResponse(department);


        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getNome()).isEqualTo("Technology");
        assertThat(result.getDescricao()).isEqualTo("Technology department");
        assertThat(result.getSigla()).isEqualTo("TI");
        assertThat(result.getStatus()).isEqualTo(DepartmentStatus.ACTIVE);
        assertThat(result.getCriadoEm()).isEqualTo(createdAt);
        assertThat(result.getAtualizadoEm()).isEqualTo(updatedAt);
    }
}