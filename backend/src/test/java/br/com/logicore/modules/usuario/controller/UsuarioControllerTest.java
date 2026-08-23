package br.com.logicore.modules.usuario.controller;

import br.com.logicore.common.dto.PageResponse;
import br.com.logicore.modules.usuario.dto.CreateUsuarioRequest;
import br.com.logicore.modules.usuario.dto.UpdateUsuarioRequest;
import br.com.logicore.modules.usuario.dto.UsuarioResponse;
import br.com.logicore.modules.usuario.dto.UsuarioSummary;
import br.com.logicore.modules.usuario.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

@WebMvcTest(UsuarioController.class)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UsuarioService service;

    @Test
    void shouldCreateUsuarioSuccessfully() throws Exception {
        CreateUsuarioRequest request = new CreateUsuarioRequest();
        request.setNome("Joao Silva");
        request.setEmail("joao@example.com");
        request.setSenha("123456");
        request.setFuncionarioId(1L);

        UsuarioResponse response = UsuarioResponse.builder()
                .id(1L)
                .nome("Joao Silva")
                .email("joao@example.com")
                .status("ACTIVE")
                .funcionarioId(1L)
                .build();

        when(service.create(any(CreateUsuarioRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Joao Silva"))
                .andExpect(jsonPath("$.email").value("joao@example.com"));
    }

    @Test
    void shouldFindUsuarioById() throws Exception {
        UsuarioResponse response = UsuarioResponse.builder()
                .id(1L)
                .nome("Joao Silva")
                .email("joao@example.com")
                .status("ACTIVE")
                .funcionarioId(1L)
                .build();

        when(service.findById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/usuarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Joao Silva"));
    }

    @Test
    void shouldReturnAllUsuarios() throws Exception {
        PageResponse<UsuarioResponse> page = new PageResponse<>(
                new PageImpl<>(java.util.List.of(
                        UsuarioResponse.builder().id(1L).nome("Joao").email("joao@example.com").build()
                ), PageRequest.of(0, 20), 1)
        );

        when(service.findAll(any(), any(), any(), any(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].nome").value("Joao"));
    }

    @Test
    void shouldUpdateUsuario() throws Exception {
        UpdateUsuarioRequest request = new UpdateUsuarioRequest();
        request.setNome("Joao Updated");
        request.setEmail("joao.updated@example.com");

        UsuarioResponse response = UsuarioResponse.builder()
                .id(1L)
                .nome("Joao Updated")
                .email("joao.updated@example.com")
                .status("ACTIVE")
                .funcionarioId(1L)
                .build();

        when(service.update(eq(1L), any(UpdateUsuarioRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/usuarios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Joao Updated"));
    }

    @Test
    void shouldDeleteUsuario() throws Exception {
        mockMvc.perform(delete("/api/v1/usuarios/1"))
                .andExpect(status().isNoContent());

        verify(service).delete(1L);
    }

    @Test
    void shouldActivateUsuario() throws Exception {
        mockMvc.perform(patch("/api/v1/usuarios/1/activate"))
                .andExpect(status().isNoContent());

        verify(service).activate(1L);
    }

    @Test
    void shouldDeactivateUsuario() throws Exception {
        mockMvc.perform(patch("/api/v1/usuarios/1/deactivate"))
                .andExpect(status().isNoContent());

        verify(service).deactivate(1L);
    }
}
