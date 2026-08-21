package br.com.logicore.modules.employee.controller;

import br.com.logicore.common.dto.PageResponse;
import br.com.logicore.modules.employee.dto.CreateEmployeeRequest;
import br.com.logicore.modules.employee.dto.EmployeeResponse;
import br.com.logicore.modules.employee.dto.EmployeeSummaryResponse;
import br.com.logicore.modules.employee.dto.UpdateEmployeeRequest;
import br.com.logicore.modules.employee.service.EmployeeService;
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

@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private EmployeeService service;

    @Test
    void shouldCreateEmployeeSuccessfully() throws Exception {
        CreateEmployeeRequest request = new CreateEmployeeRequest();
        request.setMatricula("EMP001");
        request.setNome("João Silva");
        request.setCpf("12345678901");
        request.setCargoId(1L);
        request.setDepartamentoId(1L);
        request.setDataAdmissao(java.time.LocalDate.of(2024, 1, 1));

        EmployeeResponse response = EmployeeResponse.builder()
                .id(1L)
                .matricula("EMP001")
                .nome("João Silva")
                .build();

        when(service.create(any(CreateEmployeeRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.matricula").value("EMP001"));

        verify(service).create(any(CreateEmployeeRequest.class));
    }

    @Test
    void shouldFindAllEmployees() throws Exception {
        EmployeeResponse response = EmployeeResponse.builder()
                .id(1L)
                .matricula("EMP001")
                .nome("João Silva")
                .build();

        PageResponse<EmployeeResponse> pageResponse = new PageResponse<>(
                new PageImpl<>(List.of(response), PageRequest.of(0, 20), 1)
        );

        when(service.findAll(any(), any(), any(), any(), any(), any())).thenReturn(pageResponse);

        mockMvc.perform(get("/api/v1/employees")
                        .param("search", "joao")
                        .param("cargoId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].matricula").value("EMP001"))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(20));

        verify(service).findAll(any(), any(), any(), any(), any(), any());
    }

    @Test
    void shouldReturnEmployeeSummary() throws Exception {
        EmployeeSummaryResponse response = EmployeeSummaryResponse.builder()
                .total(10)
                .active(8)
                .inactive(2)
                .withAddress(7)
                .withoutAddress(3)
                .build();

        when(service.summary()).thenReturn(response);

        mockMvc.perform(get("/api/v1/employees/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(10))
                .andExpect(jsonPath("$.active").value(8))
                .andExpect(jsonPath("$.withAddress").value(7));

        verify(service).summary();
    }

    @Test
    void shouldFindEmployeeById() throws Exception {
        EmployeeResponse response = EmployeeResponse.builder()
                .id(1L)
                .matricula("EMP001")
                .nome("João Silva")
                .build();

        when(service.findById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/employees/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(service).findById(1L);
    }

    @Test
    void shouldUpdateEmployeeSuccessfully() throws Exception {
        UpdateEmployeeRequest request = new UpdateEmployeeRequest();
        request.setNome("João Silva Updated");

        EmployeeResponse response = EmployeeResponse.builder()
                .id(1L)
                .nome("João Silva Updated")
                .build();

        when(service.update(eq(1L), any(UpdateEmployeeRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/employees/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("João Silva Updated"));

        verify(service).update(eq(1L), any(UpdateEmployeeRequest.class));
    }

    @Test
    void shouldDeleteEmployee() throws Exception {
        mockMvc.perform(delete("/api/v1/employees/1"))
                .andExpect(status().isNoContent());

        verify(service).delete(1L);
    }
}
