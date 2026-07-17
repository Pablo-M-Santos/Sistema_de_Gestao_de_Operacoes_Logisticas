package br.com.logicore.modules.employee.controller;

import br.com.logicore.modules.employee.dto.EmployeeResponse;
import br.com.logicore.modules.employee.dto.CreateEmployeeRequest;
import br.com.logicore.modules.employee.dto.UpdateEmployeeRequest;
import br.com.logicore.modules.employee.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {


    private final EmployeeService service;


    public EmployeeController(EmployeeService service) {
        this.service = service;
    }


    @PostMapping
    public ResponseEntity<EmployeeResponse> create(
            @Valid @RequestBody CreateEmployeeRequest request) {


        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.create(request));

    }


    @GetMapping
    public ResponseEntity<List<EmployeeResponse>> findAll() {

        return ResponseEntity.ok(
                service.findAll()
        );

    }


    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponse> findById(
            @PathVariable Long id) {


        return ResponseEntity.ok(
                service.findById(id)
        );

    }


    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateEmployeeRequest request) {


        return ResponseEntity.ok(
                service.update(id, request)
        );

    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {


        service.delete(id);


        return ResponseEntity.noContent()
                .build();

    }


}