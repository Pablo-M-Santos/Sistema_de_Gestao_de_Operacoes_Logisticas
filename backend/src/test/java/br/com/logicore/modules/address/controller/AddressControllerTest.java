package br.com.logicore.modules.address.controller;

import br.com.logicore.common.dto.PageResponse;
import br.com.logicore.modules.address.dto.AddressResponse;
import br.com.logicore.modules.address.dto.AddressSummaryResponse;
import br.com.logicore.modules.address.dto.CreateAddressRequest;
import br.com.logicore.modules.address.dto.UpdateAddressRequest;
import br.com.logicore.modules.address.service.AddressService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AddressController.class)
class AddressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AddressService service;

    @Test
    void shouldCreateAddressSuccessfully() throws Exception {
        CreateAddressRequest request = new CreateAddressRequest();
        request.setCep("01001000");
        request.setLogradouro("Praça da Sé");
        request.setNumero("100");
        request.setBairro("Sé");
        request.setCidade("São Paulo");
        request.setEstado("SP");
        request.setPais("Brasil");
        request.setLatitude(new BigDecimal("-23.55052000"));
        request.setLongitude(new BigDecimal("-46.63330800"));

        AddressResponse response = AddressResponse.builder()
                .id(1L)
                .cep("01001000")
                .logradouro("Praça da Sé")
                .numero("100")
                .bairro("Sé")
                .cidade("São Paulo")
                .estado("SP")
                .pais("Brasil")
                .build();

        when(service.create(any(CreateAddressRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/addresses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.cep").value("01001000"))
                .andExpect(jsonPath("$.logradouro").value("Praça da Sé"));

        verify(service).create(any(CreateAddressRequest.class));
    }

    @Test
    void shouldFindAllAddressesUsingVersionedEndpoint() throws Exception {
        AddressResponse response = AddressResponse.builder()
                .id(1L)
                .cep("01001000")
                .logradouro("Praça da Sé")
                .numero("100")
                .bairro("Sé")
                .cidade("São Paulo")
                .estado("SP")
                .pais("Brasil")
                .build();

        PageResponse<AddressResponse> pageResponse = new PageResponse<>(
                new PageImpl<>(List.of(response), PageRequest.of(0, 20), 1)
        );

        when(service.findAll(any(), any(), any(), any(), any(), any())).thenReturn(pageResponse);

        mockMvc.perform(get("/api/v1/addresses")
                        .param("search", "sé")
                        .param("cidade", "São Paulo")
                        .param("estado", "SP"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].cep").value("01001000"))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(20));

        verify(service).findAll(any(), any(), any(), any(), any(), any());
    }

    @Test
    void shouldFindAddressById() throws Exception {
        AddressResponse response = AddressResponse.builder()
                .id(1L)
                .cep("01001000")
                .logradouro("Praça da Sé")
                .build();

        when(service.findById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/addresses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(service).findById(1L);
    }

    @Test
    void shouldUpdateAddressSuccessfully() throws Exception {
        UpdateAddressRequest request = new UpdateAddressRequest();
        request.setCidade("Campinas");
        request.setEstado("SP");
        request.setPais("Brasil");

        AddressResponse response = AddressResponse.builder()
                .id(1L)
                .cep("01001000")
                .logradouro("Praça da Sé")
                .cidade("Campinas")
                .estado("SP")
                .pais("Brasil")
                .build();

        when(service.update(eq(1L), any(UpdateAddressRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/addresses/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cidade").value("Campinas"))
                .andExpect(jsonPath("$.estado").value("SP"));

        verify(service).update(eq(1L), any(UpdateAddressRequest.class));
    }

    @Test
    void shouldDeleteAddress() throws Exception {
        mockMvc.perform(delete("/api/v1/addresses/1"))
                .andExpect(status().isNoContent());

        verify(service).delete(1L);
    }

    @Test
    void shouldReturnAddressSummary() throws Exception {
        AddressSummaryResponse response = AddressSummaryResponse.builder()
                .total(10)
                .withCoordinates(7)
                .withoutCoordinates(3)
                .build();

        when(service.summary()).thenReturn(response);

        mockMvc.perform(get("/api/v1/addresses/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(10))
                .andExpect(jsonPath("$.withCoordinates").value(7))
                .andExpect(jsonPath("$.withoutCoordinates").value(3));

        verify(service).summary();
    }
}

