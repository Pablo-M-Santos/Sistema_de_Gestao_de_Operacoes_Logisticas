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
import br.com.logicore.modules.client.repository.spec.ClientSpecifications;
import br.com.logicore.modules.client.validator.ClientValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClientService {

    private final ClientRepository repository;
    private final ClientMapper mapper;
    private final ClientValidator validator;
    private final AddressRepository addressRepository;

    public ClientService(ClientRepository repository, ClientMapper mapper, ClientValidator validator, AddressRepository addressRepository) {
        this.repository = repository;
        this.mapper = mapper;
        this.validator = validator;
        this.addressRepository = addressRepository;
    }

    @Transactional
    public ClientResponse create(CreateClientRequest request) {
        validator.validateUniqueCnpj(request.getCnpj());

        Client client = Client.builder()
                .razaoSocial(request.getRazaoSocial())
                .nomeFantasia(request.getNomeFantasia())
                .cnpj(request.getCnpj())
                .inscricaoEstadual(request.getInscricaoEstadual())
                .telefone(request.getTelefone())
                .email(request.getEmail())
                .contatoPrincipal(request.getContatoPrincipal())
                .build();

        if (request.getEnderecoId() != null) {
            Address address = addressRepository.findById(request.getEnderecoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Address not found with ID: " + request.getEnderecoId()));
            client.setEndereco(address);
        }

        return mapper.toResponse(repository.save(client));
    }

    @Transactional(readOnly = true)
    public PageResponse<ClientResponse> findAll(String search, String status, Long enderecoId, Pageable pageable) {
        Specification<Client> spec = Specification
                .where(ClientSpecifications.withSearch(search))
                .and(ClientSpecifications.withStatus(status))
                .and(ClientSpecifications.withEnderecoId(enderecoId));

        Page<ClientResponse> page = repository.findAll(spec, pageable)
                .map(mapper::toResponse);

        return new PageResponse<>(page);
    }

    @Transactional(readOnly = true)
    public ClientSummaryResponse summary() {
        long total = repository.count();
        long active = repository.count();
        long inactive = 0;
        long withAddress = 0;
        long withoutAddress = total;

        return ClientSummaryResponse.builder()
                .total(total)
                .active(active)
                .inactive(inactive)
                .withAddress(withAddress)
                .withoutAddress(withoutAddress)
                .build();
    }

    @Transactional(readOnly = true)
    public ClientResponse findById(Long id) {
        return mapper.toResponse(findClientById(id));
    }

    @Transactional
    public ClientResponse update(Long id, UpdateClientRequest request) {
        Client client = findClientById(id);

        if (isPresent(request.getRazaoSocial())) {
            client.setRazaoSocial(request.getRazaoSocial());
        }
        if (isPresent(request.getNomeFantasia())) {
            client.setNomeFantasia(request.getNomeFantasia());
        }
        if (isPresent(request.getCnpj())) {
            if (!client.getCnpj().equalsIgnoreCase(request.getCnpj())) {
                validator.validateUniqueCnpjForUpdate(request.getCnpj(), id);
            }
            client.setCnpj(request.getCnpj());
        }
        if (request.getInscricaoEstadual() != null) {
            client.setInscricaoEstadual(request.getInscricaoEstadual());
        }
        if (request.getTelefone() != null) {
            client.setTelefone(request.getTelefone());
        }
        if (request.getEmail() != null) {
            client.setEmail(request.getEmail());
        }
        if (request.getContatoPrincipal() != null) {
            client.setContatoPrincipal(request.getContatoPrincipal());
        }
        if (request.getEnderecoId() != null) {
            Address address = addressRepository.findById(request.getEnderecoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Address not found with ID: " + request.getEnderecoId()));
            client.setEndereco(address);
        }
        if (request.getStatus() != null) {
            validator.validateStatus(request.getStatus());
            client.setStatus(request.getStatus());
        }

        return mapper.toResponse(repository.save(client));
    }

    @Transactional
    public void delete(Long id) {
        Client client = findClientById(id);
        client.setStatus(br.com.logicore.modules.client.enums.ClientStatus.INACTIVE);
        repository.save(client);
    }

    private Client findClientById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with ID: " + id));
    }

    private boolean isPresent(String value) {
        return value != null && !value.isBlank();
    }
}
