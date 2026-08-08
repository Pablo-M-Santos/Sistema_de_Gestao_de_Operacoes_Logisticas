package br.com.logicore.modules.cargo.mapper;

import br.com.logicore.modules.cargo.dto.CreateCargoRequest;
import br.com.logicore.modules.cargo.dto.CargoResponse;
import br.com.logicore.modules.cargo.entity.Cargo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CargoMapperTest {

    private CargoMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new CargoMapper();
    }

    @Test
    void shouldConvertCreateRequestToEntity() {
        CreateCargoRequest request = new CreateCargoRequest();
        request.setNome("Analyst");
        request.setCodigo("ANL");
        request.setDescricao("Cargo de análise");

        Cargo result = mapper.toEntity(request);

        assertThat(result).isNotNull();
        assertThat(result.getNome()).isEqualTo("Analyst");
        assertThat(result.getCodigo()).isEqualTo("ANL");
        assertThat(result.getDescricao()).isEqualTo("Cargo de análise");
        assertThat(result.getAtivo()).isTrue();
    }

    @Test
    void shouldConvertEntityToResponse() {
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        Cargo cargo = Cargo.builder()
                .id(1L)
                .nome("Analyst")
                .codigo("ANL")
                .descricao("Cargo de análise")
                .ativo(true)
                .criadoEm(createdAt)
                .atualizadoEm(updatedAt)
                .build();

        CargoResponse result = mapper.toResponse(cargo);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getNome()).isEqualTo("Analyst");
        assertThat(result.getCodigo()).isEqualTo("ANL");
        assertThat(result.getDescricao()).isEqualTo("Cargo de análise");
        assertThat(result.getAtivo()).isTrue();
        assertThat(result.getCriadoEm()).isEqualTo(createdAt);
        assertThat(result.getAtualizadoEm()).isEqualTo(updatedAt);
    }
}

