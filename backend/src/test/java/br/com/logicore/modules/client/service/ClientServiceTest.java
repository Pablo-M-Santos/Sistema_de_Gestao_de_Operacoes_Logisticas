package br.com.logicore.modules.client.service;

import br.com.logicore.common.dto.PageResponse;
import br.com.logicore.common.exception.ResourceNotFoundException;
import br.com.logicore.modules.address.entity.Address;
import br.com.logicore.modules.address.repository.AddressRepository;
import br.com.logicore.modules.client.dto.ClientResponse;
import br.com.logicore.modules.client.dto.ClientSummaryResponse;
import br.com.logicore.modules.client.dto.CreateClientRequest;
import br.com.logicore.modules.client.dto.UpdateClientRequest;
import br.com.logicore.modules.client.entity.Client;
import br.com.logicore.modules.client.mapper.ClientMapper;
import br.com.logicore.modules.client.repository.ClientRepository;
import br.com.logicore.modules.client.validator.ClientValidator;
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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    private ClientRepository repository;

    @Mock
    private ClientMapper mapper;

    @Mock
    private ClientValidator validator;

    @Mock
    private AddressRepository addressRepository;

    @InjectMocks
    private ClientService service;

    @Test
    void shouldCreateClientSuccessfully() {
        CreateClientRequest request = new CreateClientRequest();
        request.setRazaoSocial("Tech Corp");
        request.setCnpj("12345678901234");

        Client entity = Client.builder()
                .id(1L)
                .razaoSocial("Tech Corp")
                .cnpj("12345678901234")
                .build();

        ClientResponse response = ClientResponse.builder()
                .id(1L)
                .razaoSocial("Tech Corp")
                .cnpj("12345678901234")
                .build();

        when(mapper.toResponse(any(Client.class))).thenReturn(response);
        when(repository.save(any(Client.class))).thenReturn(entity);

        ClientResponse result = service.create(request);

        assertThat(result).isNotNull();
        assertThat(result.getRazaoSocial()).isEqualTo("Tech Corp");
        verify(validator).validateUniqueCnpj("12345678901234");
        verify(repository).save(any(Client.class));
    }

    @Test
    void shouldFindClientByIdSuccessfully() {
        Client client = Client.builder()
                .id(1L)
                .razaoSocial("Tech Corp")
                .build();

        ClientResponse response = ClientResponse.builder()
                .id(1L)
                .razaoSocial("Tech Corp")
                .build();

        when(repository.findById(1L)).thenReturn(Optional.of(client));
        when(mapper.toResponse(client)).thenReturn(response);

        ClientResponse result = service.findById(1L);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void shouldThrowExceptionWhenClientDoesNotExist() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(1L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldUpdateClientSuccessfully() {
        Client client = Client.builder()
                .id(1L)
                .razaoSocial("Old Name")
                .build();

        UpdateClientRequest request = new UpdateClientRequest();
        request.setRazaoSocial("New Name");

        ClientResponse response = ClientResponse.builder()
                .razaoSocial("New Name")
                .build();

        when(repository.findById(1L)).thenReturn(Optional.of(client));
        when(repository.save(client)).thenReturn(client);
        when(mapper.toResponse(client)).thenReturn(response);

        ClientResponse result = service.update(1L, request);

        assertThat(result.getRazaoSocial()).isEqualTo("New Name");
        verify(repository).save(client);
    }

    @Test
    void shouldDeleteClient() {
        Client client = Client.builder()
                .id(1L)
                .status(br.com.logicore.modules.client.enums.ClientStatus.ACTIVE)
                .build();

        when(repository.findById(1L)).thenReturn(Optional.of(client));

        service.delete(1L);

        assertThat(client.getStatus()).isEqualTo(br.com.logicore.modules.client.enums.ClientStatus.INACTIVE);
        verify(repository).save(client);
    }

    @Test
    void shouldReturnClientSummary() {
        when(repository.count()).thenReturn(10L);

        ClientSummaryResponse result = service.summary();

        assertThat(result.getTotal()).isEqualTo(10);
    }

    @Test
    void shouldReturnPagedClients() {
        Pageable pageable = PageRequest.of(0, 20);

        Client client = Client.builder()
                .id(1L)
                .razaoSocial("Tech Corp")
                .build();

        ClientResponse response = ClientResponse.builder()
                .id(1L)
                .razaoSocial("Tech Corp")
                .build();

        Page<Client> page = new PageImpl<>(List.of(client));

        when(repository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
        when(mapper.toResponse(client)).thenReturn(response);

        PageResponse<ClientResponse> result = service.findAll(null, null, null, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getRazaoSocial()).isEqualTo("Tech Corp");
    }
}
