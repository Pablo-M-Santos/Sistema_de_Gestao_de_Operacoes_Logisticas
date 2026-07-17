package br.com.logicore.modules.address.service;

import br.com.logicore.modules.address.dto.AddressResponse;
import br.com.logicore.modules.address.dto.CreateAddressRequest;
import br.com.logicore.modules.address.dto.UpdateAddressRequest;
import br.com.logicore.modules.address.entity.Address;
import br.com.logicore.modules.address.mapper.AddressMapper;
import br.com.logicore.modules.address.repository.AddressRepository;
import br.com.logicore.modules.address.validator.AddressValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AddressService {

    private final AddressRepository repository;
    private final AddressMapper mapper;
    private final AddressValidator validator;

    public AddressService(
            AddressRepository repository,
            AddressMapper mapper,
            AddressValidator validator) {

        this.repository = repository;
        this.mapper = mapper;
        this.validator = validator;
    }

    @Transactional
    public AddressResponse create(CreateAddressRequest request) {

        validator.validateState(request.getEstado());
        validator.validateLatitude(request.getLatitude());
        validator.validateLongitude(request.getLongitude());

        Address address = mapper.toEntity(request);

        return mapper.toResponse(repository.save(address));
    }

    @Transactional(readOnly = true)
    public List<AddressResponse> findAll() {

        return repository.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public AddressResponse findById(Long id) {

        Address address = repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Address not found with ID: " + id));

        return mapper.toResponse(address);
    }

    @Transactional
    public AddressResponse update(Long id, UpdateAddressRequest request) {

        Address address = repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Address not found with ID: " + id));

        validator.validateState(request.getEstado());
        validator.validateLatitude(request.getLatitude());
        validator.validateLongitude(request.getLongitude());

        address.setCep(request.getCep());
        address.setLogradouro(request.getLogradouro());
        address.setNumero(request.getNumero());
        address.setComplemento(request.getComplemento());
        address.setBairro(request.getBairro());
        address.setCidade(request.getCidade());
        address.setEstado(request.getEstado());

        if (request.getPais() != null && !request.getPais().isBlank()) {
            address.setPais(request.getPais());
        }

        address.setLatitude(request.getLatitude());
        address.setLongitude(request.getLongitude());

        return mapper.toResponse(repository.save(address));
    }

    @Transactional
    public void delete(Long id) {

        Address address = repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Address not found with ID: " + id));

        repository.delete(address);
    }

}