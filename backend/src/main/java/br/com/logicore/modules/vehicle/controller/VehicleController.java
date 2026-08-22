package br.com.logicore.modules.vehicle.controller;

import br.com.logicore.common.dto.PageResponse;
import br.com.logicore.modules.vehicle.dto.VehicleResponse;
import br.com.logicore.modules.vehicle.dto.VehicleSummaryResponse;
import br.com.logicore.modules.vehicle.dto.CreateVehicleRequest;
import br.com.logicore.modules.vehicle.dto.UpdateVehicleRequest;
import br.com.logicore.modules.vehicle.service.VehicleService;
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
@RequestMapping({"/api/v1/vehicles"})
@Tag(
        name = "Vehicles",
        description = "Endpoints for vehicle management."
)
public class VehicleController {

    private final VehicleService service;

    public VehicleController(VehicleService service) {
        this.service = service;
    }

    @Operation(
            summary = "Create vehicle",
            description = "Creates a new vehicle."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Vehicle created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content),
            @ApiResponse(responseCode = "409", description = "Vehicle already exists", content = @Content)
    })
    @PostMapping
    public ResponseEntity<VehicleResponse> create(
            @Valid @RequestBody CreateVehicleRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.create(request));
    }

    @Operation(
            summary = "List vehicles",
            description = "Returns a paginated list of vehicles with optional search and filters."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vehicles retrieved successfully")
    })
    @GetMapping
    public ResponseEntity<PageResponse<VehicleResponse>> findAll(
            @Parameter(description = "Search by placa, renavam, modelo or fabricante")
            @RequestParam(required = false) String search,

            @Parameter(description = "Filter by status: ACTIVE, INACTIVE or ALL")
            @RequestParam(required = false) String status,

            @Parameter(description = "Filter by ano fabricacao")
            @RequestParam(required = false) Integer anoFabricacao,

            @Parameter(description = "Filter by ano modelo")
            @RequestParam(required = false) Integer anoModelo,

            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {

        return ResponseEntity.ok(service.findAll(search, status, anoFabricacao, anoModelo, pageable));
    }

    @Operation(
            summary = "Vehicle summary",
            description = "Returns vehicle statistics."
    )
    @ApiResponse(responseCode = "200", description = "Summary generated successfully")
    @GetMapping("/summary")
    public ResponseEntity<VehicleSummaryResponse> summary() {

        return ResponseEntity.ok(service.summary());
    }

    @Operation(
            summary = "Find vehicle by ID",
            description = "Returns a vehicle by its identifier."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vehicle found"),
            @ApiResponse(responseCode = "404", description = "Vehicle not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<VehicleResponse> findById(
            @Parameter(description = "Vehicle ID")
            @PathVariable Long id) {

        return ResponseEntity.ok(service.findById(id));
    }

    @Operation(
            summary = "Update vehicle",
            description = "Updates vehicle information."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vehicle updated successfully"),
            @ApiResponse(responseCode = "404", description = "Vehicle not found", content = @Content),
            @ApiResponse(responseCode = "409", description = "Placa or RENAVAM already exists", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<VehicleResponse> update(
            @Parameter(description = "Vehicle ID")
            @PathVariable Long id,
            @Valid @RequestBody UpdateVehicleRequest request) {

        return ResponseEntity.ok(service.update(id, request));
    }

    @Operation(
            summary = "Delete vehicle",
            description = "Deletes a vehicle by marking it as INACTIVE."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Vehicle deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Vehicle not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Vehicle ID")
            @PathVariable Long id) {

        service.delete(id);

        return ResponseEntity.noContent().build();
    }
}
