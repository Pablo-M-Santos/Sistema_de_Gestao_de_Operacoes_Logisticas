package br.com.logicore.modules.address.controller;

import br.com.logicore.modules.address.dto.AddressResponse;
import br.com.logicore.modules.address.dto.CreateAddressRequest;
import br.com.logicore.modules.address.dto.UpdateAddressRequest;
import br.com.logicore.modules.address.service.AddressService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
public class AddressController {

    private final AddressService service;

    public AddressController(AddressService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<AddressResponse> create(
            @Valid @RequestBody CreateAddressRequest request) {

        AddressResponse response = service.create(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<AddressResponse>> findAll() {

        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AddressResponse> findById(
            @PathVariable Long id) {

        return ResponseEntity.ok(service.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AddressResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateAddressRequest request) {

        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        service.delete(id);

        return ResponseEntity.noContent().build();
    }

}