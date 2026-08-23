package br.com.logicore.modules.perfil.controller;

import br.com.logicore.common.dto.PageResponse;
import br.com.logicore.modules.perfil.dto.CreatePerfilRequest;
import br.com.logicore.modules.perfil.dto.UpdatePerfilRequest;
import br.com.logicore.modules.perfil.dto.PerfilResponse;
import br.com.logicore.modules.perfil.dto.PerfilSummary;
import br.com.logicore.modules.perfil.service.PerfilService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
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

@WebMvcTest(PerfilController.class)
class PerfilControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PerfilService service;

    @Test
    void shouldCreatePerfilSuccessfully() throws Exception {
        CreatePerfilRequest request = new CreatePerfilRequest();
        request.setNome("Admin");
        request.setDescricao("Administrator profile");

        PerfilResponse response = PerfilResponse.builder()
                .id(1L)
                .nome("Admin")
                .descricao("Administrator profile")
                .build();

        when(service.create(any(CreatePerfilRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/perfis")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Admin"))
                .andExpect(jsonPath("$.descricao").value("Administrator profile"));
    }

    @Test
    void shouldFindPerfilById() throws Exception {
        PerfilResponse response = PerfilResponse.builder()
                .id(1L)
                .nome("Admin")
                .descricao("Administrator profile")
                .build();

        when(service.findById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/perfis/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Admin"));
    }

    @Test
    void shouldReturnAllPerfis() throws Exception {
        PageResponse<PerfilResponse> page = new PageResponse<>(
                new PageImpl<>(java.util.List.of(
                        PerfilResponse.builder().id(1L).nome("Admin").descricao("Admin profile").build()
                ), PageRequest.of(0, 20), 1)
        );

        when(service.findAll(any(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/perfis"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].nome").value("Admin"));
    }

    @Test
    void shouldUpdatePerfil() throws Exception {
        UpdatePerfilRequest request = new UpdatePerfilRequest();
        request.setNome("Admin Updated");
        request.setDescricao("Updated description");

        PerfilResponse response = PerfilResponse.builder()
                .id(1L)
                .nome("Admin Updated")
                .descricao("Updated description")
                .build();

        when(service.update(eq(1L), any(UpdatePerfilRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/perfis/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Admin Updated"));
    }

    @Test
    void shouldDeletePerfil() throws Exception {
        mockMvc.perform(delete("/api/v1/perfis/1"))
                .andExpect(status().isNoContent());

        verify(service).delete(1L);
    }
}
