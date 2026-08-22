package br.com.logicore.modules.driver.controller;

import br.com.logicore.common.dto.PageResponse;
import br.com.logicore.modules.driver.dto.DriverResponse;
import br.com.logicore.modules.driver.dto.DriverSummaryResponse;
import br.com.logicore.modules.driver.dto.CreateDriverRequest;
import br.com.logicore.modules.driver.dto.UpdateDriverRequest;
import br.com.logicore.modules.driver.service.DriverService;
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
@RequestMapping({"/api/v1/motoristas"})
@Tag(
        name = "Motoristas",
        description = "Endpoints for driver management."
)
public class DriverController {

    private final DriverService service;

    public DriverController(DriverService service) {
        this.service = service;
    }

    @Operation(
            summary = "Create driver",
            description = "Creates a new driver linked to an existing employee."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Driver created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content),
            @ApiResponse(responseCode = "404", description = "Employee not found", content = @Content),
            @ApiResponse(responseCode = "409", description = "Duplicate CNH or employee already registered as driver", content = @Content)
    })
    @PostMapping
    public ResponseEntity<DriverResponse> create(
            @Valid @RequestBody CreateDriverRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.create(request));
    }

    @Operation(
            summary = "List drivers",
            description = "Returns a paginated list of drivers with optional search and filters."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Drivers retrieved successfully")
    })
    @GetMapping
    public ResponseEntity<PageResponse<DriverResponse>> findAll(
            @Parameter(description = "Search by CNH, category, observations or employee name")
            @RequestParam(required = false) String search,

            @Parameter(description = "Filter by CNH category")
            @RequestParam(required = false) String categoria,

            @Parameter(description = "Filter by employee ID")
            @RequestParam(required = false) Long funcionarioId,

            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {

        return ResponseEntity.ok(service.findAll(search, categoria, funcionarioId, pageable));
    }

    @Operation(
            summary = "Driver summary",
            description = "Returns driver statistics."
    )
    @ApiResponse(responseCode = "200", description = "Summary generated successfully")
    @GetMapping("/summary")
    public ResponseEntity<DriverSummaryResponse> summary() {

        return ResponseEntity.ok(service.summary());
    }

    @Operation(
            summary = "Find driver by ID",
            description = "Returns a driver by its identifier."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Driver found"),
            @ApiResponse(responseCode = "404", description = "Driver not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<DriverResponse> findById(
            @Parameter(description = "Driver ID")
            @PathVariable Long id) {

        return ResponseEntity.ok(service.findById(id));
    }

    @Operation(
            summary = "Find driver by employee ID",
            description = "Returns the driver associated with the given employee."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Driver found"),
            @ApiResponse(responseCode = "404", description = "Driver not found for this employee", content = @Content)
    })
    @GetMapping("/employee/{funcionarioId}")
    public ResponseEntity<DriverResponse> findByFuncionarioId(
            @Parameter(description = "Employee ID")
            @PathVariable Long funcionarioId) {

        return ResponseEntity.ok(service.findByFuncionarioId(funcionarioId));
    }

    @Operation(
            summary = "Update driver",
            description = "Updates driver information."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Driver updated successfully"),
            @ApiResponse(responseCode = "404", description = "Driver not found", content = @Content),
            @ApiResponse(responseCode = "409", description = "CNH already exists", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<DriverResponse> update(
            @Parameter(description = "Driver ID")
            @PathVariable Long id,
            @Valid @RequestBody UpdateDriverRequest request) {

        return ResponseEntity.ok(service.update(id, request));
    }

    @Operation(
            summary = "Delete driver",
            description = "Deletes a driver permanently."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Driver deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Driver not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Driver ID")
            @PathVariable Long id) {

        service.delete(id);

        return ResponseEntity.noContent().build();
    }
}
