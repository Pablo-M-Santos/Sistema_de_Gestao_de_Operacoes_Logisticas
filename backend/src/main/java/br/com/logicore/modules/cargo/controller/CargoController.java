package br.com.logicore.modules.cargo.controller;

import br.com.logicore.modules.cargo.dto.CargoResponse;
import br.com.logicore.modules.cargo.dto.CreateCargoRequest;
import br.com.logicore.modules.cargo.dto.UpdateCargoRequest;
import br.com.logicore.modules.cargo.service.CargoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cargos")
public class CargoController {

    private final CargoService service;

    public CargoController(CargoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<CargoResponse> create(
            @Valid @RequestBody CreateCargoRequest request) {

        CargoResponse response = service.create(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<CargoResponse>> findAll() {

        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CargoResponse> findById(
            @PathVariable Long id) {

        return ResponseEntity.ok(service.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CargoResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCargoRequest request) {

        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        service.delete(id);

        return ResponseEntity.noContent().build();
    }

}
