package br.com.logicore.modules.cargo.controller;

import br.com.logicore.common.dto.PageResponse;
import br.com.logicore.modules.cargo.dto.CargoResponse;
import br.com.logicore.modules.cargo.dto.CargoSummaryResponse;
import br.com.logicore.modules.cargo.dto.CreateCargoRequest;
import br.com.logicore.modules.cargo.dto.UpdateCargoRequest;
import br.com.logicore.modules.cargo.service.CargoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CargoController.class)
class CargoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CargoService service;

    @Test
    void shouldCreateCargoSuccessfully() throws Exception {
        CreateCargoRequest request = new CreateCargoRequest();
        request.setNome("Analyst");
        request.setCodigo("ANL");
        request.setDescricao("Cargo de análise");

        CargoResponse response = CargoResponse.builder()
                .id(1L)
                .nome("Analyst")
                .codigo("ANL")
                .ativo(true)
                .build();

        when(service.create(any(CreateCargoRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/cargos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Analyst"))
                .andExpect(jsonPath("$.codigo").value("ANL"))
                .andExpect(jsonPath("$.ativo").value(true));

        verify(service).create(any(CreateCargoRequest.class));
    }

    @Test
    void shouldFindAllCargos() throws Exception {
        CargoResponse response = CargoResponse.builder()
                .id(1L)
                .nome("Analyst")
                .codigo("ANL")
                .ativo(true)
                .build();

        PageResponse<CargoResponse> pageResponse = new PageResponse<>(
                new PageImpl<>(List.of(response), PageRequest.of(0, 20), 1)
        );

        when(service.findAll(any(), any(), any())).thenReturn(pageResponse);

        mockMvc.perform(get("/api/v1/cargos")
                        .param("search", "an")
                        .param("active", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].nome").value("Analyst"))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(20));

        verify(service).findAll(any(), any(), any());
    }

    @Test
    void shouldFindCargoById() throws Exception {
        CargoResponse response = CargoResponse.builder()
                .id(1L)
                .nome("Analyst")
                .codigo("ANL")
                .ativo(true)
                .build();

        when(service.findById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/cargos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Analyst"));

        verify(service).findById(1L);
    }

    @Test
    void shouldUpdateCargoSuccessfully() throws Exception {
        UpdateCargoRequest request = new UpdateCargoRequest();
        request.setNome("Analyst Updated");
        request.setCodigo("ANL2");
        request.setDescricao("Cargo atualizado");
        request.setAtivo(false);

        CargoResponse response = CargoResponse.builder()
                .id(1L)
                .nome("Analyst Updated")
                .codigo("ANL2")
                .ativo(false)
                .build();

        when(service.update(eq(1L), any(UpdateCargoRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/cargos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Analyst Updated"))
                .andExpect(jsonPath("$.codigo").value("ANL2"))
                .andExpect(jsonPath("$.ativo").value(false));

        verify(service).update(eq(1L), any(UpdateCargoRequest.class));
    }

    @Test
    void shouldActivateCargo() throws Exception {
        mockMvc.perform(patch("/api/v1/cargos/1/activate"))
                .andExpect(status().isNoContent());

        verify(service).activate(1L);
    }

    @Test
    void shouldDeactivateCargo() throws Exception {
        mockMvc.perform(patch("/api/v1/cargos/1/deactivate"))
                .andExpect(status().isNoContent());

        verify(service).deactivate(1L);
    }

    @Test
    void shouldReturnCargoSummary() throws Exception {
        CargoSummaryResponse response = CargoSummaryResponse.builder()
                .total(10)
                .active(6)
                .inactive(4)
                .build();

        when(service.summary()).thenReturn(response);

        mockMvc.perform(get("/api/v1/cargos/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(10))
                .andExpect(jsonPath("$.active").value(6))
                .andExpect(jsonPath("$.inactive").value(4));

        verify(service).summary();
    }
}

