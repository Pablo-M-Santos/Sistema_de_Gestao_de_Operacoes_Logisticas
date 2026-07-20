package br.com.logicore.modules.department.controller;

import br.com.logicore.modules.department.dto.CreateDepartmentRequest;
import br.com.logicore.modules.department.dto.DepartmentResponse;
import br.com.logicore.modules.department.dto.UpdateDepartmentRequest;
import br.com.logicore.modules.department.service.DepartmentService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/departments")
public class DepartmentController {

    private final DepartmentService service;

    public DepartmentController(DepartmentService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<DepartmentResponse> create(@RequestBody @Valid CreateDepartmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @GetMapping
    public ResponseEntity<Page<DepartmentResponse>> getAll(
            Pageable pageable,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Boolean active) {
        return ResponseEntity.ok(service.findAll(pageable, name, active));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DepartmentResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DepartmentResponse> update(
            @PathVariable Long id,
            @RequestBody @Valid UpdateDepartmentRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}