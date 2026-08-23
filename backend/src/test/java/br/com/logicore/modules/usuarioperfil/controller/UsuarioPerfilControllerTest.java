package br.com.logicore.modules.usuarioperfil.controller;

import br.com.logicore.common.dto.PageResponse;
import br.com.logicore.modules.usuarioperfil.dto.CreateUsuarioPerfilRequest;
import br.com.logicore.modules.usuarioperfil.dto.UsuarioPerfilResponse;
import br.com.logicore.modules.usuarioperfil.service.UsuarioPerfilService;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UsuarioPerfilController.class)
class UsuarioPerfilControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UsuarioPerfilService service;

    @Test
    void shouldCreateAssociationSuccessfully() throws Exception {
        CreateUsuarioPerfilRequest request = new CreateUsuarioPerfilRequest();
        request.setUsuarioId(1L);
        request.setPerfilId(2L);

        UsuarioPerfilResponse response = UsuarioPerfilResponse.builder()
                .id(1L)
                .usuarioId(1L)
                .perfilId(2L)
                .nomeUsuario("João")
                .nomePerfil("Admin")
                .build();

        when(service.create(any(CreateUsuarioPerfilRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/usuarios-perfis")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.usuarioId").value(1))
                .andExpect(jsonPath("$.perfilId").value(2))
                .andExpect(jsonPath("$.nomeUsuario").value("João"))
                .andExpect(jsonPath("$.nomePerfil").value("Admin"));
    }

    @Test
    void shouldFindAssociationById() throws Exception {
        UsuarioPerfilResponse response = UsuarioPerfilResponse.builder()
                .id(1L)
                .usuarioId(1L)
                .perfilId(2L)
                .nomeUsuario("João")
                .nomePerfil("Admin")
                .build();

        when(service.findById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/usuarios-perfis/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.usuarioId").value(1))
                .andExpect(jsonPath("$.perfilId").value(2));
    }

    @Test
    void shouldReturnAllAssociations() throws Exception {
        PageResponse<UsuarioPerfilResponse> page = new PageResponse<>(
                new PageImpl<>(List.of(
                        UsuarioPerfilResponse.builder().id(1L).usuarioId(1L).perfilId(2L).nomeUsuario("João").nomePerfil("Admin").build()
                ), PageRequest.of(0, 20), 1)
        );

        when(service.findAll(any(), any(), any(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/usuarios-perfis"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].usuarioId").value(1));
    }

    @Test
    void shouldDeleteAssociation() throws Exception {
        mockMvc.perform(delete("/api/v1/usuarios-perfis/1"))
                .andExpect(status().isNoContent());

        verify(service).delete(1L);
    }
}
