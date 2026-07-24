package br.com.logicore.modules.department.repository.spec;

import br.com.logicore.modules.department.entity.Department;
import br.com.logicore.modules.department.enums.DepartmentStatus;

import br.com.logicore.modules.department.repository.DepartmentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.jpa.domain.Specification;


import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class DepartmentSpecificationsIntegrationTest {

    @Autowired
    private DepartmentRepository repository;


    @Test
    void shouldFindDepartmentByName() {

        Department department =
                Department.builder()
                        .nome("Technology")
                        .sigla("TI")
                        .status(DepartmentStatus.ACTIVE)
                        .build();


        repository.save(department);


        Specification<Department> specification =
                DepartmentSpecifications.withSearch("tech");


        List<Department> result =
                repository.findAll(specification);


        assertThat(result)
                .hasSize(1);

        assertThat(result.get(0).getNome())
                .isEqualTo("Technology");
    }


    @Test
    void shouldReturnNullSpecificationWhenSearchContainsOnlySpaces() {

        Specification<Department> specification =
                DepartmentSpecifications.withSearch("   ");

        assertThat(specification.toPredicate(null, null, null))
                .isNull();
    }

    @Test
    void shouldFindDepartmentByAbbreviation() {

        Department department =
                Department.builder()
                        .nome("Finance")
                        .sigla("FIN")
                        .status(DepartmentStatus.ACTIVE)
                        .build();


        repository.save(department);


        Specification<Department> specification =
                DepartmentSpecifications.withSearch("fin");


        List<Department> result =
                repository.findAll(specification);


        assertThat(result)
                .hasSize(1);

        assertThat(result.get(0).getSigla())
                .isEqualTo("FIN");
    }


    @Test
    void shouldReturnDepartmentsByStatus() {

        Department active =
                Department.builder()
                        .nome("Technology")
                        .sigla("TI")
                        .status(DepartmentStatus.ACTIVE)
                        .build();


        Department inactive =
                Department.builder()
                        .nome("Finance")
                        .sigla("FIN")
                        .status(DepartmentStatus.INACTIVE)
                        .build();


        repository.saveAll(
                List.of(active, inactive)
        );


        Specification<Department> specification =
                DepartmentSpecifications.withStatus(
                        DepartmentStatus.ACTIVE
                );


        List<Department> result =
                repository.findAll(specification);


        assertThat(result)
                .hasSize(1);

        assertThat(result.get(0).getStatus())
                .isEqualTo(DepartmentStatus.ACTIVE);
    }


    @Test
    void shouldReturnNullSpecificationWhenSearchIsEmpty() {

        Specification<Department> specification =
                DepartmentSpecifications.withSearch("");


        assertThat(specification.toPredicate(null, null, null))
                .isNull();
    }


    @Test
    void shouldReturnNullSpecificationWhenStatusIsNull() {

        Specification<Department> specification =
                DepartmentSpecifications.withStatus(null);


        assertThat(specification.toPredicate(null, null, null))
                .isNull();
    }

    @Test
    void shouldReturnNullSpecificationWhenSearchIsNull() {

        Specification<Department> specification =
                DepartmentSpecifications.withSearch(null);

        assertThat(specification.toPredicate(null, null, null))
                .isNull();
    }


    @Test
    void shouldReturnInactiveDepartmentsByStatus() {

        Department inactive =
                Department.builder()
                        .nome("Finance")
                        .sigla("FIN")
                        .status(DepartmentStatus.INACTIVE)
                        .build();

        repository.save(inactive);


        Specification<Department> specification =
                DepartmentSpecifications.withStatus(
                        DepartmentStatus.INACTIVE
                );


        List<Department> result =
                repository.findAll(specification);


        assertThat(result)
                .hasSize(1);

        assertThat(result.get(0).getStatus())
                .isEqualTo(DepartmentStatus.INACTIVE);
    }
}