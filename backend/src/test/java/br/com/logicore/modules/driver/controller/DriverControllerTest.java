package br.com.logicore.modules.driver.controller;

import br.com.logicore.common.dto.PageResponse;
import br.com.logicore.modules.driver.dto.CreateDriverRequest;
import br.com.logicore.modules.driver.dto.UpdateDriverRequest;
import br.com.logicore.modules.driver.dto.DriverResponse;
import br.com.logicore.modules.driver.dto.DriverSummaryResponse;
import br.com.logicore.modules.driver.service.DriverService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DriverController.class)
class DriverControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private DriverService service;

    @Test
    void shouldCreateDriverSuccessfully() throws Exception {
        CreateDriverRequest request = new CreateDriverRequest();
        request.setFuncionarioId(1L);
        request.setCnh("12345678901");
        request.setCategoria("D");
        request.setValidadeCnh(LocalDate.of(2025, 12, 31));
        request.setObservacoes("Test driver");

        DriverResponse response = DriverResponse.builder()
                .id(1L)
                .funcionarioId(1L)
                .funcionarioNome("Joao Silva")
                .cnh("12345678901")
                .categoria("D")
                .build();

        when(service.create(any(CreateDriverRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/motoristas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.cnh").value("12345678901"));

        verify(service).create(any(CreateDriverRequest.class));
    }

    @Test
    void shouldFindAllDrivers() throws Exception {
        DriverResponse response = DriverResponse.builder()
                .id(1L)
                .cnh("12345678901")
                .categoria("D")
                .build();

        PageResponse<DriverResponse> pageResponse = new PageResponse<>(
                new PageImpl<>(java.util.List.of(response), PageRequest.of(0, 20), 1)
        );

        when(service.findAll(any(), any(), any(), any())).thenReturn(pageResponse);

        mockMvc.perform(get("/api/v1/motoristas")
                        .param("search", "12345678901")
                        .param("categoria", "D"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].cnh").value("12345678901"))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(20));

        verify(service).findAll(any(), any(), any(), any());
    }

    @Test
    void shouldFindDriverById() throws Exception {
        DriverResponse response = DriverResponse.builder()
                .id(1L)
                .cnh("12345678901")
                .categoria("D")
                .build();

        when(service.findById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/motoristas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.cnh").value("12345678901"));

        verify(service).findById(1L);
    }

    @Test
    void shouldFindDriverByFuncionarioId() throws Exception {
        DriverResponse response = DriverResponse.builder()
                .id(1L)
                .funcionarioId(1L)
                .funcionarioNome("Joao Silva")
                .build();

        when(service.findByFuncionarioId(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/motoristas/employee/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.funcionarioId").value(1));

        verify(service).findByFuncionarioId(1L);
    }

    @Test
    void shouldUpdateDriverSuccessfully() throws Exception {
        UpdateDriverRequest request = new UpdateDriverRequest();
        request.setCategoria("E");
        request.setObservacoes("Updated");

        DriverResponse response = DriverResponse.builder()
                .id(1L)
                .cnh("12345678901")
                .categoria("E")
                .observacoes("Updated")
                .build();

        when(service.update(eq(1L), any(UpdateDriverRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/motoristas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoria").value("E"));

        verify(service).update(eq(1L), any(UpdateDriverRequest.class));
    }

    @Test
    void shouldDeleteDriver() throws Exception {
        mockMvc.perform(delete("/api/v1/motoristas/1"))
                .andExpect(status().isNoContent());

        verify(service).delete(1L);
    }

    @Test
    void shouldReturnDriverSummary() throws Exception {
        DriverSummaryResponse response = DriverSummaryResponse.builder()
                .total(5)
                .build();

        when(service.summary()).thenReturn(response);

        mockMvc.perform(get("/api/v1/motoristas/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(5));

        verify(service).summary();
    }
}
