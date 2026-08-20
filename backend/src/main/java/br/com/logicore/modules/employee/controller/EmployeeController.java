package br.com.logicore.modules.employee.controller;

import br.com.logicore.common.dto.PageResponse;
import br.com.logicore.modules.employee.dto.EmployeeResponse;
import br.com.logicore.modules.employee.dto.CreateEmployeeRequest;
import br.com.logicore.modules.employee.dto.UpdateEmployeeRequest;
import br.com.logicore.modules.employee.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/api/v1/employees"})
@Tag(
        name = "Employees",
        description = "Endpoints for employee management."
)
public class EmployeeController {


    private final EmployeeService service;


    public EmployeeController(EmployeeService service) {
        this.service = service;
    }


    @Operation(
            summary = "Create employee",
            description = "Creates a new employee with cargo, department and address integration."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Employee created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content),
            @ApiResponse(responseCode = "409", description = "Duplicate CPF or registration number", content = @Content)
    })
    @PostMapping
    public ResponseEntity<EmployeeResponse> create(
            @Valid @RequestBody CreateEmployeeRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.create(request));

    }


    @Operation(
            summary = "List employees",
            description = "Returns a paginated list of employees with optional search and filters."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employees retrieved successfully")
    })
    @GetMapping
    public ResponseEntity<PageResponse<EmployeeResponse>> findAll(
            @Parameter(description = "Search by name, CPF, registration number, email or phone")
            @RequestParam(required = false) String search,

            @Parameter(description = "Filter by name")
            @RequestParam(required = false) String nome,

            @Parameter(description = "Filter by CPF")
            @RequestParam(required = false) String cpf,

            @Parameter(description = "Filter by cargo ID")
            @RequestParam(required = false) Long cargoId,

            @Parameter(description = "Filter by department ID")
            @RequestParam(required = false) Long departamentoId,

            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {

        return ResponseEntity.ok(service.findAll(search, nome, cpf, cargoId, departamentoId, pageable));
    }

    @Operation(
            summary = "Employee summary",
            description = "Returns employee statistics."
    )
    @ApiResponse(responseCode = "200", description = "Summary generated successfully")
    @GetMapping("/summary")
    public ResponseEntity<br.com.logicore.modules.employee.dto.EmployeeSummaryResponse> summary() {

        return ResponseEntity.ok(service.summary());
    }


    @Operation(
            summary = "Find employee by ID",
            description = "Returns an employee by its identifier with all related data."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employee found"),
            @ApiResponse(responseCode = "404", description = "Employee not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponse> findById(
            @Parameter(description = "Employee ID")
            @PathVariable Long id) {

        return ResponseEntity.ok(
                service.findById(id)
        );

    }


    @Operation(
            summary = "Update employee",
            description = "Updates employee information including cargo, department and address."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employee updated successfully"),
            @ApiResponse(responseCode = "404", description = "Employee not found", content = @Content),
            @ApiResponse(responseCode = "409", description = "Duplicate CPF or registration number", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponse> update(
            @Parameter(description = "Employee ID")
            @PathVariable Long id,
            @Valid @RequestBody UpdateEmployeeRequest request) {

        return ResponseEntity.ok(
                service.update(id, request)
        );

    }


    @Operation(
            summary = "Delete employee",
            description = "Deletes an employee by marking it as INACTIVE."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Employee deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Employee not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Employee ID")
            @PathVariable Long id) {


        service.delete(id);


        return ResponseEntity.noContent()
                .build();

    }


}