package br.com.logicore.modules.vehicle.controller;

import br.com.logicore.common.dto.PageResponse;
import br.com.logicore.modules.vehicle.dto.CreateVehicleRequest;
import br.com.logicore.modules.vehicle.dto.UpdateVehicleRequest;
import br.com.logicore.modules.vehicle.dto.VehicleResponse;
import br.com.logicore.modules.vehicle.dto.VehicleSummaryResponse;
import br.com.logicore.modules.vehicle.service.VehicleService;
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

@WebMvcTest(VehicleController.class)
class VehicleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private VehicleService service;

    @Test
    void shouldCreateVehicleSuccessfully() throws Exception {
        CreateVehicleRequest request = new CreateVehicleRequest();
        request.setPlaca("ABC1234");
        request.setRenavam("12345678901");
        request.setModelo("Model X");
        request.setFabricante("Fabricante Y");
        request.setAnoFabricacao(2020);
        request.setAnoModelo(2021);
        request.setCapacidadePeso(new BigDecimal("1000.50"));
        request.setCapacidadeVolume(new BigDecimal("25.500"));
        request.setQuilometragem(10000);

        VehicleResponse response = VehicleResponse.builder()
                .id(1L)
                .placa("ABC1234")
                .renavam("12345678901")
                .modelo("Model X")
                .fabricante("Fabricante Y")
                .status("ACTIVE")
                .build();

        when(service.create(any(CreateVehicleRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.placa").value("ABC1234"));

        verify(service).create(any(CreateVehicleRequest.class));
    }

    @Test
    void shouldFindAllVehicles() throws Exception {
        VehicleResponse response = VehicleResponse.builder()
                .id(1L)
                .placa("ABC1234")
                .modelo("Model X")
                .build();

        PageResponse<VehicleResponse> pageResponse = new PageResponse<>(
                new PageImpl<>(List.of(response), PageRequest.of(0, 20), 1)
        );

        when(service.findAll(any(), any(), any(), any(), any())).thenReturn(pageResponse);

        mockMvc.perform(get("/api/v1/vehicles")
                        .param("search", "ABC1234")
                        .param("status", "ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].placa").value("ABC1234"))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(20));

        verify(service).findAll(any(), any(), any(), any(), any());
    }

    @Test
    void shouldFindVehicleById() throws Exception {
        VehicleResponse response = VehicleResponse.builder()
                .id(1L)
                .placa("ABC1234")
                .modelo("Model X")
                .build();

        when(service.findById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/vehicles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.placa").value("ABC1234"));

        verify(service).findById(1L);
    }

    @Test
    void shouldUpdateVehicleSuccessfully() throws Exception {
        UpdateVehicleRequest request = new UpdateVehicleRequest();
        request.setModelo("Model X Updated");
        request.setQuilometragem(15000);

        VehicleResponse response = VehicleResponse.builder()
                .id(1L)
                .placa("ABC1234")
                .modelo("Model X Updated")
                .quilometragem(15000)
                .build();

        when(service.update(eq(1L), any(UpdateVehicleRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/vehicles/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.modelo").value("Model X Updated"));

        verify(service).update(eq(1L), any(UpdateVehicleRequest.class));
    }

    @Test
    void shouldDeleteVehicle() throws Exception {
        mockMvc.perform(delete("/api/v1/vehicles/1"))
                .andExpect(status().isNoContent());

        verify(service).delete(1L);
    }

    @Test
    void shouldReturnVehicleSummary() throws Exception {
        VehicleSummaryResponse response = VehicleSummaryResponse.builder()
                .total(10)
                .active(8)
                .inactive(2)
                .build();

        when(service.summary()).thenReturn(response);

        mockMvc.perform(get("/api/v1/vehicles/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(10))
                .andExpect(jsonPath("$.active").value(8))
                .andExpect(jsonPath("$.inactive").value(2));

        verify(service).summary();
    }
}
