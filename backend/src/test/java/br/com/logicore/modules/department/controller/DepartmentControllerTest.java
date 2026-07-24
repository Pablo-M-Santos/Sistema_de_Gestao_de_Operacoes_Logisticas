package br.com.logicore.modules.department.controller;

import br.com.logicore.modules.department.dto.CreateDepartmentRequest;
import br.com.logicore.modules.department.dto.DepartmentResponse;
import br.com.logicore.modules.department.dto.DepartmentSummaryResponse;
import br.com.logicore.modules.department.dto.UpdateDepartmentRequest;
import br.com.logicore.modules.department.service.DepartmentService;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(DepartmentController.class)
class DepartmentControllerTest {


    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private ObjectMapper objectMapper;


    @MockitoBean
    private DepartmentService service;



    @Test
    void shouldCreateDepartmentSuccessfully() throws Exception {

        CreateDepartmentRequest request = new CreateDepartmentRequest();

        request.setNome("Technology");
        request.setDescricao("IT Department");
        request.setSigla("TI");


        DepartmentResponse response =
                DepartmentResponse.builder()
                        .id(1L)
                        .nome("Technology")
                        .sigla("TI")
                        .build();


        when(service.create(any(CreateDepartmentRequest.class)))
                .thenReturn(response);



        mockMvc.perform(post("/api/v1/departments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))

                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Technology"));


        verify(service)
                .create(any(CreateDepartmentRequest.class));
    }



    @Test
    void shouldFindAllDepartments() throws Exception {

        when(service.findAll(any(), any(), any()))
                .thenReturn(null);


        mockMvc.perform(get("/api/v1/departments"))

                .andExpect(status().isOk());


        verify(service)
                .findAll(any(), any(), any());
    }



    @Test
    void shouldFindDepartmentById() throws Exception {

        DepartmentResponse response =
                DepartmentResponse.builder()
                        .id(1L)
                        .nome("Technology")
                        .build();


        when(service.findById(1L))
                .thenReturn(response);



        mockMvc.perform(get("/api/v1/departments/1"))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));


        verify(service)
                .findById(1L);
    }



    @Test
    void shouldUpdateDepartmentSuccessfully() throws Exception {

        UpdateDepartmentRequest request =
                new UpdateDepartmentRequest();

        request.setNome("Technology Updated");
        request.setSigla("TI");



        DepartmentResponse response =
                DepartmentResponse.builder()
                        .id(1L)
                        .nome("Technology Updated")
                        .build();


        when(service.update(eq(1L), any(UpdateDepartmentRequest.class)))
                .thenReturn(response);



        mockMvc.perform(put("/api/v1/departments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome")
                        .value("Technology Updated"));


        verify(service)
                .update(eq(1L), any(UpdateDepartmentRequest.class));
    }



    @Test
    void shouldActivateDepartment() throws Exception {


        mockMvc.perform(patch("/api/v1/departments/1/activate"))

                .andExpect(status().isNoContent());


        verify(service)
                .activate(1L);
    }



    @Test
    void shouldDeactivateDepartment() throws Exception {


        mockMvc.perform(patch("/api/v1/departments/1/deactivate"))

                .andExpect(status().isNoContent());


        verify(service)
                .deactivate(1L);
    }



    @Test
    void shouldReturnDepartmentSummary() throws Exception {


        DepartmentSummaryResponse response =
                DepartmentSummaryResponse.builder()
                        .total(10)
                        .active(8)
                        .inactive(2)
                        .build();


        when(service.summary())
                .thenReturn(response);



        mockMvc.perform(get("/api/v1/departments/summary"))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(10))
                .andExpect(jsonPath("$.active").value(8))
                .andExpect(jsonPath("$.inactive").value(2));


        verify(service)
                .summary();
    }
}