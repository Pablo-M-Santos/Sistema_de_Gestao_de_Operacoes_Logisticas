package br.com.logicore.modules.client.controller;

import br.com.logicore.common.dto.PageResponse;
import br.com.logicore.modules.client.dto.ClientResponse;
import br.com.logicore.modules.client.dto.ClientSummaryResponse;
import br.com.logicore.modules.client.dto.CreateClientRequest;
import br.com.logicore.modules.client.dto.UpdateClientRequest;
import br.com.logicore.modules.client.service.ClientService;
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

@WebMvcTest(ClientController.class)
class ClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ClientService service;

    @Test
    void shouldCreateClientSuccessfully() throws Exception {
        CreateClientRequest request = new CreateClientRequest();
        request.setRazaoSocial("Tech Corp");
        request.setNomeFantasia("Tech");
        request.setCnpj("12345678901234");
        request.setTelefone("11999999999");
        request.setEmail("contact@tech.com");

        ClientResponse response = ClientResponse.builder()
                .id(1L)
                .razaoSocial("Tech Corp")
                .cnpj("12345678901234")
                .build();

        when(service.create(any(CreateClientRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.razaoSocial").value("Tech Corp"));

        verify(service).create(any(CreateClientRequest.class));
    }

    @Test
    void shouldFindAllClients() throws Exception {
        ClientResponse response = ClientResponse.builder()
                .id(1L)
                .razaoSocial("Tech Corp")
                .build();

        PageResponse<ClientResponse> pageResponse = new PageResponse<>(
                new PageImpl<>(List.of(response), PageRequest.of(0, 20), 1)
        );

        when(service.findAll(any(), any(), any(), any())).thenReturn(pageResponse);

        mockMvc.perform(get("/api/v1/clients")
                        .param("search", "tech")
                        .param("status", "ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].razaoSocial").value("Tech Corp"))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(20));

        verify(service).findAll(any(), any(), any(), any());
    }

    @Test
    void shouldFindClientById() throws Exception {
        ClientResponse response = ClientResponse.builder()
                .id(1L)
                .razaoSocial("Tech Corp")
                .build();

        when(service.findById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/clients/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.razaoSocial").value("Tech Corp"));

        verify(service).findById(1L);
    }

    @Test
    void shouldUpdateClientSuccessfully() throws Exception {
        UpdateClientRequest request = new UpdateClientRequest();
        request.setRazaoSocial("Tech Corp Updated");
        request.setEmail("updated@tech.com");

        ClientResponse response = ClientResponse.builder()
                .id(1L)
                .razaoSocial("Tech Corp Updated")
                .build();

        when(service.update(eq(1L), any(UpdateClientRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/clients/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.razaoSocial").value("Tech Corp Updated"));

        verify(service).update(eq(1L), any(UpdateClientRequest.class));
    }

    @Test
    void shouldDeleteClient() throws Exception {
        mockMvc.perform(delete("/api/v1/clients/1"))
                .andExpect(status().isNoContent());

        verify(service).delete(1L);
    }

    @Test
    void shouldReturnClientSummary() throws Exception {
        ClientSummaryResponse response = ClientSummaryResponse.builder()
                .total(10)
                .active(8)
                .inactive(2)
                .withAddress(7)
                .withoutAddress(3)
                .build();

        when(service.summary()).thenReturn(response);

        mockMvc.perform(get("/api/v1/clients/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(10))
                .andExpect(jsonPath("$.active").value(8))
                .andExpect(jsonPath("$.withAddress").value(7));

        verify(service).summary();
    }
}
