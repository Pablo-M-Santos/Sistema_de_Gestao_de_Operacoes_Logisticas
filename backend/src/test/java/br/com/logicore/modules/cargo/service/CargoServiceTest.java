package br.com.logicore.modules.cargo.service;

import br.com.logicore.common.dto.PageResponse;
import br.com.logicore.common.exception.ResourceNotFoundException;
import br.com.logicore.modules.cargo.dto.CargoResponse;
import br.com.logicore.modules.cargo.dto.CargoSummaryResponse;
import br.com.logicore.modules.cargo.dto.CreateCargoRequest;
import br.com.logicore.modules.cargo.dto.UpdateCargoRequest;
import br.com.logicore.modules.cargo.entity.Cargo;
import br.com.logicore.modules.cargo.mapper.CargoMapper;
import br.com.logicore.modules.cargo.repository.CargoRepository;
import br.com.logicore.modules.cargo.validator.CargoValidator;
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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CargoServiceTest {

    @Mock
    private CargoRepository repository;

    @Mock
    private CargoMapper mapper;

    @Mock
    private CargoValidator validator;

    @InjectMocks
    private CargoService service;

    @Test
    void shouldCreateCargoSuccessfully() {
        CreateCargoRequest request = new CreateCargoRequest();
        request.setNome("Analyst");
        request.setCodigo("ANL");
        request.setDescricao("Cargo de análise");

        Cargo entity = Cargo.builder()
                .id(1L)
                .nome("Analyst")
                .codigo("ANL")
                .ativo(true)
                .build();

        CargoResponse response = CargoResponse.builder()
                .id(1L)
                .nome("Analyst")
                .codigo("ANL")
                .ativo(true)
                .build();

        when(mapper.toEntity(request)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.toResponse(entity)).thenReturn(response);

        CargoResponse result = service.create(request);

        assertThat(result).isNotNull();
        assertThat(result.getNome()).isEqualTo("Analyst");
        assertThat(result.getCodigo()).isEqualTo("ANL");

        verify(validator).validateUniqueName("Analyst");
        verify(validator).validateUniqueCode("ANL");
        verify(repository).save(entity);
    }

    @Test
    void shouldFindCargoByIdSuccessfully() {
        Cargo cargo = Cargo.builder()
                .id(1L)
                .nome("Analyst").codigo("ANL").ativo(true)
                .build();

        CargoResponse response = CargoResponse.builder()
                .id(1L)
                .nome("Analyst")
                .codigo("ANL")
                .ativo(true)
                .build();

        when(repository.findById(1L)).thenReturn(Optional.of(cargo));
        when(mapper.toResponse(cargo)).thenReturn(response);

        CargoResponse result = service.findById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getAtivo()).isTrue();
    }

    @Test
    void shouldThrowExceptionWhenCargoDoesNotExist() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Cargo not found with ID: 1");
    }

    @Test
    void shouldUpdateCargoSuccessfully() {
        Cargo cargo = Cargo.builder()
                .id(1L)
                .nome("Analyst")
                .codigo("ANL")
                .ativo(true)
                .build();

        UpdateCargoRequest request = new UpdateCargoRequest();
        request.setNome("Analyst Updated");
        request.setCodigo("ANL2");
        request.setDescricao("Cargo atualizado");
        request.setAtivo(false);

        CargoResponse response = CargoResponse.builder()
                .nome("Analyst Updated")
                .codigo("ANL2")
                .ativo(false)
                .build();

        when(repository.findById(1L)).thenReturn(Optional.of(cargo));
        when(repository.save(cargo)).thenReturn(cargo);
        when(mapper.toResponse(cargo)).thenReturn(response);

        CargoResponse result = service.update(1L, request);

        assertThat(result.getNome()).isEqualTo("Analyst Updated");
        assertThat(cargo.getAtivo()).isFalse();

        verify(validator).validateUniqueNameForUpdate("Analyst Updated", 1L);
        verify(validator).validateUniqueCodeForUpdate("ANL2", 1L);
        verify(repository).save(cargo);
    }

    @Test
    void shouldUpdateCargoWithoutValidatingWhenNameAndCodeAreTheSame() {
        Cargo cargo = Cargo.builder()
                .id(1L)
                .nome("Analyst")
                .codigo("ANL")
                .ativo(true)
                .build();

        UpdateCargoRequest request = new UpdateCargoRequest();
        request.setNome("analyst");
        request.setCodigo("anl");
        request.setDescricao("Updated");

        CargoResponse response = CargoResponse.builder()
                .nome("analyst")
                .codigo("anl")
                .ativo(true)
                .build();

        when(repository.findById(1L)).thenReturn(Optional.of(cargo));
        when(repository.save(cargo)).thenReturn(cargo);
        when(mapper.toResponse(cargo)).thenReturn(response);

        CargoResponse result = service.update(1L, request);

        assertThat(result.getNome()).isEqualTo("analyst");
        assertThat(result.getCodigo()).isEqualTo("anl");

        verify(validator, never()).validateUniqueNameForUpdate(anyString(), anyLong());
        verify(validator, never()).validateUniqueCodeForUpdate(anyString(), anyLong());
    }

    @Test
    void shouldActivateCargo() {
        Cargo cargo = Cargo.builder()
                .id(1L)
                .ativo(false)
                .build();

        when(repository.findById(1L)).thenReturn(Optional.of(cargo));

        service.activate(1L);

        assertThat(cargo.getAtivo()).isTrue();
    }

    @Test
    void shouldDeactivateCargo() {
        Cargo cargo = Cargo.builder()
                .id(1L)
                .ativo(true)
                .build();

        when(repository.findById(1L)).thenReturn(Optional.of(cargo));

        service.deactivate(1L);

        assertThat(cargo.getAtivo()).isFalse();
    }

    @Test
    void shouldReturnCargoSummary() {
        when(repository.count()).thenReturn(10L);
        when(repository.countByAtivoTrue()).thenReturn(6L);
        when(repository.countByAtivoFalse()).thenReturn(4L);

        CargoSummaryResponse result = service.summary();

        assertThat(result.getTotal()).isEqualTo(10);
        assertThat(result.getActive()).isEqualTo(6);
        assertThat(result.getInactive()).isEqualTo(4);
    }

    @Test
    void shouldReturnPagedCargos() {
        Pageable pageable = PageRequest.of(0, 20);

        Cargo cargo = Cargo.builder()
                .id(1L)
                .nome("Analyst")
                .codigo("ANL")
                .ativo(true)
                .build();

        CargoResponse response = CargoResponse.builder()
                .id(1L)
                .nome("Analyst")
                .codigo("ANL")
                .ativo(true)
                .build();

        Page<Cargo> page = new PageImpl<>(List.of(cargo), pageable, 1);

        when(repository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
        when(mapper.toResponse(cargo)).thenReturn(response);

        PageResponse<CargoResponse> result = service.findAll(null, null, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getNome()).isEqualTo("Analyst");
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    void shouldFilterCargosByActiveStatus() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<Cargo> page = new PageImpl<>(List.of());

        when(repository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

        PageResponse<CargoResponse> result = service.findAll(null, true, pageable);

        assertThat(result.getContent()).isEmpty();
    }

    @Test
    void shouldIgnoreBlankSearchFilter() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<Cargo> page = new PageImpl<>(List.of());

        when(repository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

        PageResponse<CargoResponse> result = service.findAll("   ", null, pageable);

        assertThat(result.getContent()).isEmpty();
    }
}

