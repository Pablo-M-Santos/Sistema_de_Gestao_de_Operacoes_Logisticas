package br.com.logicore.modules.cargo.service;

import br.com.logicore.common.dto.PageResponse;
import br.com.logicore.common.exception.ResourceNotFoundException;
import br.com.logicore.modules.cargo.dto.CargoResponse;
import br.com.logicore.modules.cargo.dto.CargoSummaryResponse;
import br.com.logicore.modules.cargo.dto.CreateCargoRequest;
import br.com.logicore.modules.cargo.dto.UpdateCargoRequest;
import br.com.logicore.modules.cargo.entity.Cargo;
import br.com.logicore.modules.cargo.mapper.CargoMapper;
import br.com.logicore.modules.cargo.repository.CargoRepository;
import br.com.logicore.modules.cargo.repository.spec.CargoSpecifications;
import br.com.logicore.modules.cargo.validator.CargoValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
    public PageResponse<CargoResponse> findAll(
            String search,
            Boolean active,
            Pageable pageable) {

        Specification<Cargo> spec = Specification
                .where(CargoSpecifications.withSearch(search))
                .and(CargoSpecifications.withStatus(active));

        Page<CargoResponse> page = repository.findAll(spec, pageable)
                .map(mapper::toResponse);

        return new PageResponse<>(page);
    }

    @Transactional(readOnly = true)
    public CargoSummaryResponse summary() {

        long total = repository.count();

        long active = repository.countByAtivoTrue();

        long inactive = repository.countByAtivoFalse();

        return CargoSummaryResponse.builder()
                .total(total)
                .active(active)
                .inactive(inactive)
                .build();
    }

    @Transactional(readOnly = true)
    public CargoResponse findById(Long id) {
        return mapper.toResponse(findCargoById(id));
    }

    @Transactional
    public CargoResponse update(Long id, UpdateCargoRequest request) {

        Cargo cargo = findCargoById(id);

        if (!cargo.getNome().equalsIgnoreCase(request.getNome())) {
            validator.validateUniqueNameForUpdate(request.getNome(), id);
        }

        if (!cargo.getCodigo().equalsIgnoreCase(request.getCodigo())) {
            validator.validateUniqueCodeForUpdate(request.getCodigo(), id);
        }

        cargo.setNome(request.getNome());
        cargo.setDescricao(request.getDescricao());
        cargo.setCodigo(request.getCodigo());

        return mapper.toResponse(repository.save(cargo));
    }

    @Transactional
    public void activate(Long id) {
        changeStatus(id, true);
    }

    @Transactional
    public void deactivate(Long id) {
        changeStatus(id, false);
    }

    private Cargo findCargoById(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Cargo not found with ID: " + id));
    }

    private void changeStatus(Long id, boolean active) {

        Cargo cargo = findCargoById(id);

        if (!cargo.getAtivo().equals(active)) {
            cargo.setAtivo(active);
        }
    }

}