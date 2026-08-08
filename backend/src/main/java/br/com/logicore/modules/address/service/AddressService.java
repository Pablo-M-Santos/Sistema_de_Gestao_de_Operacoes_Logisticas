package br.com.logicore.modules.address.service;

import br.com.logicore.common.dto.PageResponse;
import br.com.logicore.common.exception.ResourceNotFoundException;
import br.com.logicore.modules.address.dto.AddressSummaryResponse;
import br.com.logicore.modules.address.dto.AddressResponse;
import br.com.logicore.modules.address.dto.CreateAddressRequest;
import br.com.logicore.modules.address.dto.UpdateAddressRequest;
import br.com.logicore.modules.address.entity.Address;
import br.com.logicore.modules.address.mapper.AddressMapper;
import br.com.logicore.modules.address.repository.AddressRepository;
import br.com.logicore.modules.address.repository.spec.AddressSpecifications;
import br.com.logicore.modules.address.validator.AddressValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public PageResponse<AddressResponse> findAll(
            String search,
            String cep,
            String cidade,
            String estado,
            String pais,
            Pageable pageable) {

        Specification<Address> spec = Specification
                .where(AddressSpecifications.withSearch(search))
                .and(AddressSpecifications.withCep(cep))
                .and(AddressSpecifications.withCidade(cidade))
                .and(AddressSpecifications.withEstado(estado))
                .and(AddressSpecifications.withPais(pais));

        Page<AddressResponse> page = repository.findAll(spec, pageable)
                .map(mapper::toResponse);

        return new PageResponse<>(page);
    }

    @Transactional(readOnly = true)
    public AddressResponse findById(Long id) {

        Address address = findAddressById(id);

        return mapper.toResponse(address);
    }

    @Transactional(readOnly = true)
    public AddressSummaryResponse summary() {

        long total = repository.count();
        long withCoordinates = repository.countWithCoordinates();
        long withoutCoordinates = total - withCoordinates;

        return AddressSummaryResponse.builder()
                .total(total)
                .withCoordinates(withCoordinates)
                .withoutCoordinates(withoutCoordinates)
                .build();
    }

    @Transactional
    public AddressResponse update(Long id, UpdateAddressRequest request) {

        Address address = findAddressById(id);

        if (isPresent(request.getCep())) {
            address.setCep(request.getCep());
        }
        if (isPresent(request.getLogradouro())) {
            address.setLogradouro(request.getLogradouro());
        }
        if (isPresent(request.getNumero())) {
            address.setNumero(request.getNumero());
        }
        if (request.getComplemento() != null) {
            address.setComplemento(request.getComplemento());
        }
        if (isPresent(request.getBairro())) {
            address.setBairro(request.getBairro());
        }
        if (isPresent(request.getCidade())) {
            address.setCidade(request.getCidade());
        }
        if (isPresent(request.getEstado())) {
            validator.validateState(request.getEstado());
            address.setEstado(request.getEstado());
        }

        if (isPresent(request.getPais())) {
            address.setPais(request.getPais());
        }

        if (request.getLatitude() != null) {
            validator.validateLatitude(request.getLatitude());
            address.setLatitude(request.getLatitude());
        }

        if (request.getLongitude() != null) {
            validator.validateLongitude(request.getLongitude());
            address.setLongitude(request.getLongitude());
        }

        return mapper.toResponse(repository.save(address));
    }

    @Transactional
    public void delete(Long id) {

        Address address = findAddressById(id);

        repository.delete(address);
    }

    private Address findAddressById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with ID: " + id));
    }

    private boolean isPresent(String value) {
        return value != null && !value.isBlank();
    }

}