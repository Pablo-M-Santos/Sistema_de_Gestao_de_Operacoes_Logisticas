package br.com.logicore.modules.cargo.service;

import br.com.logicore.modules.cargo.dto.CargoResponse;
import br.com.logicore.modules.cargo.dto.CreateCargoRequest;
import br.com.logicore.modules.cargo.dto.UpdateCargoRequest;
import br.com.logicore.modules.cargo.entity.Cargo;
import br.com.logicore.modules.cargo.mapper.CargoMapper;
import br.com.logicore.modules.cargo.repository.CargoRepository;
import br.com.logicore.modules.cargo.validator.CargoValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CargoService {

    private final CargoRepository repository;
    private final CargoMapper mapper;
    private final CargoValidator validator;

    public CargoService(
            CargoRepository repository,
            CargoMapper mapper,
            CargoValidator validator) {

        this.repository = repository;
        this.mapper = mapper;
        this.validator = validator;
    }

    @Transactional
    public CargoResponse create(CreateCargoRequest request) {

        validator.validateUniqueName(request.getNome());
        validator.validateUniqueCode(request.getCodigo());

        Cargo cargo = mapper.toEntity(request);

        return mapper.toResponse(repository.save(cargo));
    }

    @Transactional(readOnly = true)
    public List<CargoResponse> findAll() {

        return repository.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CargoResponse findById(Long id) {

        Cargo cargo = repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Cargo not found with ID: " + id));

        return mapper.toResponse(cargo);
    }

    @Transactional
    public CargoResponse update(Long id, UpdateCargoRequest request) {

        Cargo cargo = repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Cargo not found with ID: " + id));

        validator.validateUniqueNameForUpdate(request.getNome(), id);
        validator.validateUniqueCodeForUpdate(request.getCodigo(), id);

        cargo.setNome(request.getNome());
        cargo.setDescricao(request.getDescricao());
        cargo.setCodigo(request.getCodigo());

        if (request.getAtivo() != null) {
            cargo.setAtivo(request.getAtivo());
        }

        return mapper.toResponse(repository.save(cargo));
    }

    @Transactional
    public void delete(Long id) {

        Cargo cargo = repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Cargo not found with ID: " + id));

        cargo.setAtivo(false);

        repository.save(cargo);
    }

}
