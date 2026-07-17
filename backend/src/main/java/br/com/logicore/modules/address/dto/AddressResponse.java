package br.com.logicore.modules.address.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class AddressResponse {

    private Long id;

    private String cep;

    private String logradouro;

    private String numero;

    private String complemento;

    private String bairro;

    private String cidade;

    private String estado;

    private String pais;

    private BigDecimal latitude;

    private BigDecimal longitude;

    private LocalDateTime criadoEm;

    private LocalDateTime atualizadoEm;
}
