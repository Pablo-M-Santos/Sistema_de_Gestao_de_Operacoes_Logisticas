package br.com.logicore.modules.address.mapper;

import br.com.logicore.modules.address.dto.AddressResponse;
import br.com.logicore.modules.address.dto.CreateAddressRequest;
import br.com.logicore.modules.address.entity.Address;
import org.springframework.stereotype.Component;

@Component
public class AddressMapper {

    public Address toEntity(CreateAddressRequest request) {

        return Address.builder()
                .cep(request.getCep())
                .logradouro(request.getLogradouro())
                .numero(request.getNumero())
                .complemento(request.getComplemento())
                .bairro(request.getBairro())
                .cidade(request.getCidade())
                .estado(request.getEstado())
                .pais(request.getPais() == null || request.getPais().isBlank()
                        ? "Brasil"
                        : request.getPais())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .build();
    }

    public AddressResponse toResponse(Address address) {

        return AddressResponse.builder()
                .id(address.getId())
                .cep(address.getCep())
                .logradouro(address.getLogradouro())
                .numero(address.getNumero())
                .complemento(address.getComplemento())
                .bairro(address.getBairro())
                .cidade(address.getCidade())
                .estado(address.getEstado())
                .pais(address.getPais())
                .latitude(address.getLatitude())
                .longitude(address.getLongitude())
                .criadoEm(address.getCriadoEm())
                .atualizadoEm(address.getAtualizadoEm())
                .build();
    }

}