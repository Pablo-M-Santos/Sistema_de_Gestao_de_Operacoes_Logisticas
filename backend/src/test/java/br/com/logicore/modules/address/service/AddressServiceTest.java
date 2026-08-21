package br.com.logicore.modules.address.service;

import br.com.logicore.common.dto.PageResponse;
import br.com.logicore.common.exception.BusinessException;
import br.com.logicore.common.exception.ResourceNotFoundException;
import br.com.logicore.modules.address.dto.AddressResponse;
import br.com.logicore.modules.address.dto.AddressSummaryResponse;
import br.com.logicore.modules.address.dto.CreateAddressRequest;
import br.com.logicore.modules.address.dto.UpdateAddressRequest;
import br.com.logicore.modules.address.entity.Address;
import br.com.logicore.modules.address.mapper.AddressMapper;
import br.com.logicore.modules.address.repository.AddressRepository;
import br.com.logicore.modules.address.validator.AddressValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @Mock
    private AddressRepository repository;

    @Mock
    private AddressMapper mapper;

    @Mock
    private AddressValidator validator;

    @InjectMocks
    private AddressService service;

    @Test
    void shouldCreateAddressSuccessfully() {
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

        Address entity = Address.builder()
                .id(1L)
                .cep("01001000")
                .logradouro("Praça da Sé")
                .numero("100")
                .bairro("Sé")
                .cidade("São Paulo")
                .estado("SP")
                .pais("Brasil")
                .build();

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

        when(mapper.toEntity(request)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.toResponse(entity)).thenReturn(response);

        AddressResponse result = service.create(request);

        assertThat(result).isNotNull();
        assertThat(result.getCep()).isEqualTo("01001000");
        verify(validator).validateState("SP");
        verify(validator).validateLatitude(new BigDecimal("-23.55052000"));
        verify(validator).validateLongitude(new BigDecimal("-46.63330800"));
        verify(repository).save(entity);
    }

    @Test
    void shouldFindAddressByIdSuccessfully() {
        Address address = Address.builder()
                .id(1L)
                .cep("01001000")
                .logradouro("Praça da Sé")
                .build();

        AddressResponse response = AddressResponse.builder()
                .id(1L)
                .cep("01001000")
                .build();

        when(repository.findById(1L)).thenReturn(Optional.of(address));
        when(mapper.toResponse(address)).thenReturn(response);

        AddressResponse result = service.findById(1L);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void shouldThrowExceptionWhenAddressDoesNotExist() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Address not found with ID: 1");
    }

    @Test
    void shouldUpdateAddressSuccessfully() {
        Address address = Address.builder()
                .id(1L)
                .cep("01001000")
                .logradouro("Praça da Sé")
                .numero("100")
                .bairro("Sé")
                .cidade("São Paulo")
                .estado("SP")
                .pais("Brasil")
                .build();

        UpdateAddressRequest request = new UpdateAddressRequest();
        request.setCidade("Campinas");
        request.setLatitude(new BigDecimal("-22.90556"));

        AddressResponse response = AddressResponse.builder()
                .cidade("Campinas")
                .build();

        when(repository.findById(1L)).thenReturn(Optional.of(address));
        when(repository.save(address)).thenReturn(address);
        when(mapper.toResponse(address)).thenReturn(response);

        AddressResponse result = service.update(1L, request);

        assertThat(result.getCidade()).isEqualTo("Campinas");
        assertThat(address.getCidade()).isEqualTo("Campinas");
        assertThat(address.getEstado()).isEqualTo("SP");
        verify(validator, never()).validateState(anyString());
        verify(validator).validateLatitude(new BigDecimal("-22.90556"));
        verify(repository).save(address);
    }

    @Test
    void shouldDeleteAddress() {
        Address address = Address.builder().id(1L).build();

        when(repository.findById(1L)).thenReturn(Optional.of(address));

        service.delete(1L);

        verify(repository).delete(address);
    }

    @Test
    void shouldThrowBusinessExceptionWhenDeleteReferencedAddress() {
        Address address = Address.builder().id(1L).build();

        when(repository.findById(1L)).thenReturn(Optional.of(address));
        doThrow(new DataIntegrityViolationException("FK constraint"))
                .when(repository).delete(address);

        assertThatThrownBy(() -> service.delete(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Address cannot be deleted because it is referenced by other records.");

        verify(repository).delete(address);
    }

    @Test
    void shouldReturnAddressSummary() {
        when(repository.count()).thenReturn(10L);
        when(repository.countWithCoordinates()).thenReturn(7L);

        AddressSummaryResponse result = service.summary();

        assertThat(result.getTotal()).isEqualTo(10);
        assertThat(result.getWithCoordinates()).isEqualTo(7);
        assertThat(result.getWithoutCoordinates()).isEqualTo(3);
    }

    @Test
    void shouldReturnPagedAddresses() {
        Pageable pageable = PageRequest.of(0, 20);

        Address address = Address.builder()
                .id(1L)
                .cep("01001000")
                .logradouro("Praça da Sé")
                .build();

        AddressResponse response = AddressResponse.builder()
                .id(1L)
                .cep("01001000")
                .logradouro("Praça da Sé")
                .build();

        Page<Address> page = new PageImpl<>(List.of(address), pageable, 1);

        when(repository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
        when(mapper.toResponse(address)).thenReturn(response);

        PageResponse<AddressResponse> result = service.findAll(null, null, null, null, null, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getCep()).isEqualTo("01001000");
    }

    @Test
    void shouldIgnoreBlankSearchAndFilters() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<Address> page = new PageImpl<>(List.of());

        when(repository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

        PageResponse<AddressResponse> result = service.findAll("   ", "", "", "", "", pageable);

        assertThat(result.getContent()).isEmpty();
    }
}

