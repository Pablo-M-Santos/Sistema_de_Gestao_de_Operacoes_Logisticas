package br.com.logicore.modules.permissao.controller;

import br.com.logicore.common.dto.PageResponse;
import br.com.logicore.modules.permissao.dto.CreatePermissaoRequest;
import br.com.logicore.modules.permissao.dto.UpdatePermissaoRequest;
import br.com.logicore.modules.permissao.dto.PermissaoResponse;
import br.com.logicore.modules.permissao.dto.PermissaoSummary;
import br.com.logicore.modules.permissao.service.PermissaoService;
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

@WebMvcTest(PermissaoController.class)
class PermissaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PermissaoService service;

    @Test
    void shouldCreatePermissaoSuccessfully() throws Exception {
        CreatePermissaoRequest request = new CreatePermissaoRequest();
        request.setNome("READ");
        request.setDescricao("Read permission");

        PermissaoResponse response = PermissaoResponse.builder()
                .id(1L)
                .nome("READ")
                .descricao("Read permission")
                .build();

        when(service.create(any(CreatePermissaoRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/permissoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("READ"))
                .andExpect(jsonPath("$.descricao").value("Read permission"));
    }

    @Test
    void shouldFindPermissaoById() throws Exception {
        PermissaoResponse response = PermissaoResponse.builder()
                .id(1L)
                .nome("READ")
                .descricao("Read permission")
                .build();

        when(service.findById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/permissoes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("READ"));
    }

    @Test
    void shouldReturnAllPermissoes() throws Exception {
        PageResponse<PermissaoResponse> page = new PageResponse<>(
                new PageImpl<>(java.util.List.of(
                        PermissaoResponse.builder().id(1L).nome("READ").descricao("Read permission").build()
                ), PageRequest.of(0, 20), 1)
        );

        when(service.findAll(any(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/permissoes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].nome").value("READ"));
    }

    @Test
    void shouldUpdatePermissao() throws Exception {
        UpdatePermissaoRequest request = new UpdatePermissaoRequest();
        request.setNome("READ_UPDATED");
        request.setDescricao("Updated description");

        PermissaoResponse response = PermissaoResponse.builder()
                .id(1L)
                .nome("READ_UPDATED")
                .descricao("Updated description")
                .build();

        when(service.update(eq(1L), any(UpdatePermissaoRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/permissoes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("READ_UPDATED"));
    }

    @Test
    void shouldDeletePermissao() throws Exception {
        mockMvc.perform(delete("/api/v1/permissoes/1"))
                .andExpect(status().isNoContent());

        verify(service).delete(1L);
    }
}
