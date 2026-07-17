package br.com.logicore.modules.address.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CreateAddressRequest {

    @NotBlank(message = "Zip code is required.")
    @Size(min = 8, max = 8, message = "Zip code must contain exactly 8 characters.")
    private String cep;

    @NotBlank(message = "Street is required.")
    @Size(max = 200, message = "Street must have at most 200 characters.")
    private String logradouro;

    @NotBlank(message = "Number is required.")
    @Size(max = 20, message = "Number must have at most 20 characters.")
    private String numero;

    @Size(max = 200, message = "Complement must have at most 200 characters.")
    private String complemento;

    @NotBlank(message = "District is required.")
    @Size(max = 150, message = "District must have at most 150 characters.")
    private String bairro;

    @NotBlank(message = "City is required.")
    @Size(max = 150, message = "City must have at most 150 characters.")
    private String cidade;

    @NotBlank(message = "State is required.")
    @Size(min = 2, max = 2, message = "State must contain exactly 2 characters.")
    private String estado;

    @Size(max = 100, message = "Country must have at most 100 characters.")
    private String pais;

    private BigDecimal latitude;

    private BigDecimal longitude;
}
