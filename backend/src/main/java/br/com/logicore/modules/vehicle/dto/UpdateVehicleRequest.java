package br.com.logicore.modules.vehicle.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class UpdateVehicleRequest {

    @Size(max = 10, message = "Placa must have at most 10 characters.")
    private String placa;

    @Size(max = 11, message = "RENAVAM must have at most 11 characters.")
    private String renavam;

    @Size(max = 100, message = "Modelo must have at most 100 characters.")
    private String modelo;

    @Size(max = 100, message = "Fabricante must have at most 100 characters.")
    private String fabricante;

    private Integer anoFabricacao;

    private Integer anoModelo;

    private BigDecimal capacidadePeso;

    private BigDecimal capacidadeVolume;

    private Integer quilometragem;

    private String status;
}
