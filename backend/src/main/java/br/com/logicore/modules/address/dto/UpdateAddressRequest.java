package br.com.logicore.modules.address.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class UpdateAddressRequest {

    @Size(min = 8, max = 8, message = "Zip code must contain exactly 8 characters.")
    private String cep;

    @Size(max = 200, message = "Street must have at most 200 characters.")
    private String logradouro;

    @Size(max = 20, message = "Number must have at most 20 characters.")
    private String numero;

    @Size(max = 200, message = "Complement must have at most 200 characters.")
    private String complemento;

    @Size(max = 150, message = "District must have at most 150 characters.")
    private String bairro;

    @Size(max = 150, message = "City must have at most 150 characters.")
    private String cidade;

    @Size(min = 2, max = 2, message = "State must contain exactly 2 characters.")
    private String estado;

    @Size(max = 100, message = "Country must have at most 100 characters.")
    private String pais;

    private BigDecimal latitude;

    private BigDecimal longitude;

}