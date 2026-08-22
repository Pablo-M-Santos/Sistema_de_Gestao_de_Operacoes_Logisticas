package br.com.logicore.modules.vehicle.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class VehicleResponse {

    private Long id;
    private String placa;
    private String renavam;
    private String modelo;
    private String fabricante;
    private Integer anoFabricacao;
    private Integer anoModelo;
    private BigDecimal capacidadePeso;
    private BigDecimal capacidadeVolume;
    private Integer quilometragem;
    private String status;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;
}
