package br.com.logicore.modules.department.service;

import br.com.logicore.common.dto.PageResponse;
import br.com.logicore.common.exception.ResourceNotFoundException;
import br.com.logicore.modules.department.dto.CreateDepartmentRequest;
import br.com.logicore.modules.department.dto.DepartmentResponse;
import br.com.logicore.modules.department.dto.DepartmentSummaryResponse;
import br.com.logicore.modules.department.dto.UpdateDepartmentRequest;
import br.com.logicore.modules.department.entity.Department;
import br.com.logicore.modules.department.enums.DepartmentStatus;
import br.com.logicore.modules.department.mapper.DepartmentMapper;
import br.com.logicore.modules.department.repository.DepartmentRepository;
import br.com.logicore.modules.department.validator.DepartmentValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;


import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class DepartmentServiceTest {

    @Mock
    private DepartmentRepository repository;

    @Mock
    private DepartmentMapper mapper;

    @Mock
    private DepartmentValidator validator;

    @InjectMocks
    private DepartmentService service;


    @Test
    void shouldCreateDepartmentSuccessfully() {

        CreateDepartmentRequest request = new CreateDepartmentRequest();
        request.setNome("Technology");


        Department entity = Department.builder()
                .id(1L)
                .nome("Technology")
                .build();


        DepartmentResponse response = DepartmentResponse.builder()
                .id(1L)
                .nome("Technology")
                .build();


        when(mapper.toEntity(request))
                .thenReturn(entity);

        when(repository.save(entity))
                .thenReturn(entity);

        when(mapper.toResponse(entity))
                .thenReturn(response);


        DepartmentResponse result = service.create(request);


        assertThat(result).isNotNull();
        assertThat(result.getNome())
                .isEqualTo("Technology");


        verify(validator)
                .validateUniqueName("Technology");

        verify(repository)
                .save(entity);
    }


    @Test
    void shouldFindDepartmentByIdSuccessfully() {

        Department department = Department.builder()
                .id(1L)
                .nome("Technology")
                .build();


        DepartmentResponse response = DepartmentResponse.builder()
                .id(1L)
                .nome("Technology")
                .build();


        when(repository.findById(1L))
                .thenReturn(Optional.of(department));

        when(mapper.toResponse(department))
                .thenReturn(response);


        DepartmentResponse result =
                service.findById(1L);


        assertThat(result.getId())
                .isEqualTo(1L);
    }


    @Test
    void shouldThrowExceptionWhenDepartmentDoesNotExist() {

        when(repository.findById(1L))
                .thenReturn(Optional.empty());


        assertThatThrownBy(() ->
                service.findById(1L)
        )
                .isInstanceOf(ResourceNotFoundException.class);
    }


    @Test
    void shouldUpdateDepartmentSuccessfully() {

        Department department = Department.builder()
                .id(1L)
                .nome("Old Name")
                .build();


        UpdateDepartmentRequest request =
                new UpdateDepartmentRequest();

        request.setNome("New Name");
        request.setDescricao("Description");
        request.setSigla("NEW");


        DepartmentResponse response =
                DepartmentResponse.builder()
                        .nome("New Name")
                        .build();


        when(repository.findById(1L))
                .thenReturn(Optional.of(department));


        when(repository.save(department))
                .thenReturn(department);


        when(mapper.toResponse(department))
                .thenReturn(response);


        DepartmentResponse result =
                service.update(1L, request);


        assertThat(result.getNome())
                .isEqualTo("New Name");


        verify(repository)
                .save(department);
    }


    @Test
    void shouldActivateDepartment() {

        Department department =
                Department.builder()
                        .id(1L)
                        .status(DepartmentStatus.INACTIVE)
                        .build();


        when(repository.findById(1L))
                .thenReturn(Optional.of(department));


        service.activate(1L);


        assertThat(department.getStatus())
                .isEqualTo(DepartmentStatus.ACTIVE);
    }


    @Test
    void shouldDeactivateDepartment() {

        Department department =
                Department.builder()
                        .id(1L)
                        .status(DepartmentStatus.ACTIVE)
                        .build();


        when(repository.findById(1L))
                .thenReturn(Optional.of(department));


        service.deactivate(1L);


        assertThat(department.getStatus())
                .isEqualTo(DepartmentStatus.INACTIVE);
    }


    @Test
    void shouldReturnDepartmentSummary() {

        when(repository.count())
                .thenReturn(10L);

        when(repository.countByStatus(DepartmentStatus.ACTIVE))
                .thenReturn(7L);

        when(repository.countByStatus(DepartmentStatus.INACTIVE))
                .thenReturn(3L);


        DepartmentSummaryResponse result =
                service.summary();


        assertThat(result.getTotal())
                .isEqualTo(10);

        assertThat(result.getActive())
                .isEqualTo(7);

        assertThat(result.getInactive())
                .isEqualTo(3);
    }

    @Test
    void shouldReturnPagedDepartments() {

        Pageable pageable = PageRequest.of(0, 20);


        Department department = Department.builder()
                .id(1L)
                .nome("Technology")
                .status(DepartmentStatus.ACTIVE)
                .build();


        DepartmentResponse response =
                DepartmentResponse.builder()
                        .id(1L)
                        .nome("Technology")
                        .build();


        Page<Department> page =
                new PageImpl<>(
                        java.util.List.of(department)
                );


        when(repository.findAll(
                any(Specification.class),
                eq(pageable)
        ))
                .thenReturn(page);


        when(mapper.toResponse(department))
                .thenReturn(response);


        PageResponse<DepartmentResponse> result =
                service.findAll(
                        null,
                        null,
                        pageable
                );


        assertThat(result)
                .isNotNull();

        assertThat(result.getContent())
                .hasSize(1);

        assertThat(result.getContent()
                .get(0)
                .getNome())
                .isEqualTo("Technology");
    }

    @Test
    void shouldUpdateDepartmentWithoutValidatingNameWhenNameIsTheSame() {

        Department department =
                Department.builder()
                        .id(1L)
                        .nome("Technology")
                        .build();


        UpdateDepartmentRequest request =
                new UpdateDepartmentRequest();

        request.setNome("technology");
        request.setDescricao("Updated");
        request.setSigla("TEC");


        DepartmentResponse response =
                DepartmentResponse.builder()
                        .nome("technology")
                        .build();


        when(repository.findById(1L))
                .thenReturn(Optional.of(department));


        when(repository.save(department))
                .thenReturn(department);


        when(mapper.toResponse(department))
                .thenReturn(response);


        DepartmentResponse result =
                service.update(1L, request);


        assertThat(result.getNome())
                .isEqualTo("technology");


        verify(validator, never())
                .validateUniqueNameForUpdate(anyString(), anyLong());
    }

    @Test
    void shouldNotChangeStatusWhenAlreadyActive() {

        Department department =
                Department.builder()
                        .id(1L)
                        .status(DepartmentStatus.ACTIVE)
                        .build();


        when(repository.findById(1L))
                .thenReturn(Optional.of(department));


        service.activate(1L);


        assertThat(department.getStatus())
                .isEqualTo(DepartmentStatus.ACTIVE);
    }

    @Test
    void shouldFilterDepartmentsByStatus() {

        Pageable pageable = PageRequest.of(0, 20);


        Page<Department> page =
                new PageImpl<>(java.util.List.of());


        when(repository.findAll(
                any(Specification.class),
                eq(pageable)
        ))
                .thenReturn(page);


        PageResponse<DepartmentResponse> result =
                service.findAll(
                        null,
                        "ACTIVE",
                        pageable
                );


        assertThat(result.getContent())
                .isEmpty();
    }

    @Test
    void shouldIgnoreInvalidStatusFilter() {

        Pageable pageable = PageRequest.of(0, 20);


        Page<Department> page =
                new PageImpl<>(java.util.List.of());


        when(repository.findAll(
                any(Specification.class),
                eq(pageable)
        ))
                .thenReturn(page);


        PageResponse<DepartmentResponse> result =
                service.findAll(
                        null,
                        "INVALID",
                        pageable
                );


        assertThat(result.getContent())
                .isEmpty();
    }

    @Test
    void shouldIgnoreBlankStatusFilter() {

        Pageable pageable = PageRequest.of(0,20);

        Page<Department> page =
                new PageImpl<>(java.util.List.of());


        when(repository.findAll(
                any(Specification.class),
                eq(pageable)
        ))
                .thenReturn(page);


        PageResponse<DepartmentResponse> result =
                service.findAll(
                        null,
                        "",
                        pageable
                );


        assertThat(result.getContent())
                .isEmpty();
    }

    @Test
    void shouldIgnoreAllStatusFilter() {

        Pageable pageable = PageRequest.of(0,20);

        Page<Department> page =
                new PageImpl<>(java.util.List.of());


        when(repository.findAll(
                any(Specification.class),
                eq(pageable)
        ))
                .thenReturn(page);


        PageResponse<DepartmentResponse> result =
                service.findAll(
                        null,
                        "ALL",
                        pageable
                );


        assertThat(result.getContent())
                .isEmpty();
    }


}