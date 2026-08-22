package br.com.logicore.modules.vehicle.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CreateVehicleRequest {

    @NotBlank(message = "Placa is required.")
    @Size(max = 10, message = "Placa must have at most 10 characters.")
    private String placa;

    @NotBlank(message = "RENAVAM is required.")
    @Size(max = 11, message = "RENAVAM must have at most 11 characters.")
    private String renavam;

    @NotBlank(message = "Modelo is required.")
    @Size(max = 100, message = "Modelo must have at most 100 characters.")
    private String modelo;

    @NotBlank(message = "Fabricante is required.")
    @Size(max = 100, message = "Fabricante must have at most 100 characters.")
    private String fabricante;

    @NotNull(message = "Ano fabricação is required.")
    private Integer anoFabricacao;

    @NotNull(message = "Ano modelo is required.")
    private Integer anoModelo;

    @NotNull(message = "Capacidade peso is required.")
    private BigDecimal capacidadePeso;

    @NotNull(message = "Capacidade volume is required.")
    private BigDecimal capacidadeVolume;

    private Integer quilometragem;
}
