package br.com.logicore.modules.cargo.controller;

import br.com.logicore.common.dto.PageResponse;
import br.com.logicore.modules.cargo.dto.CargoResponse;
import br.com.logicore.modules.cargo.dto.CargoSummaryResponse;
import br.com.logicore.modules.cargo.dto.CreateCargoRequest;
import br.com.logicore.modules.cargo.dto.UpdateCargoRequest;
import br.com.logicore.modules.cargo.service.CargoService;
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

import java.util.List;

@RestController
@RequestMapping("/api/v1/cargos")
@Tag(
        name = "Cargos",
        description = "Endpoints for cargo management."
)
public class CargoController {

    private final CargoService service;

    public CargoController(CargoService service) {
        this.service = service;
    }

    @Operation(
            summary = "Create cargo",
            description = "Creates a new cargo."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Cargo created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content),
            @ApiResponse(responseCode = "409", description = "Cargo already exists", content = @Content)
    })
    @PostMapping
    public ResponseEntity<CargoResponse> create(
            @RequestBody @Valid CreateCargoRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.create(request));
    }

    @Operation(
            summary = "List cargos",
            description = "Returns a paginated list of cargos with optional search and active filters."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cargos retrieved successfully")
    })
    @GetMapping
    public ResponseEntity<PageResponse<CargoResponse>> findAll(

            @Parameter(
                    description = "Search by cargo name or code"
            )
            @RequestParam(required = false) String search,

            @Parameter(
                    description = "Filter by active status: true or false"
            )
            @RequestParam(required = false) Boolean active,

            @PageableDefault(
                    page = 0,
                    size = 20,
                    sort = "id",
                    direction = Sort.Direction.ASC
            ) Pageable pageable) {

        return ResponseEntity.ok(service.findAll(search, active, pageable));
    }

    @Operation(
            summary = "Cargo summary",
            description = "Returns cargo statistics."
    )
    @ApiResponse(responseCode = "200", description = "Summary generated successfully")
    @GetMapping("/summary")
    public ResponseEntity<CargoSummaryResponse> summary() {
        return ResponseEntity.ok(service.summary());
    }

    @Operation(
            summary = "Find cargo by ID",
            description = "Returns a cargo by its identifier."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cargo found"),
            @ApiResponse(responseCode = "404", description = "Cargo not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<CargoResponse> findById(
            @Parameter(description = "Cargo ID")
            @PathVariable Long id) {

        return ResponseEntity.ok(service.findById(id));
    }

    @Operation(
            summary = "Update cargo",
            description = "Updates cargo information."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cargo updated successfully"),
            @ApiResponse(responseCode = "404", description = "Cargo not found", content = @Content),
            @ApiResponse(responseCode = "409", description = "Cargo already exists", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<CargoResponse> update(

            @Parameter(description = "Cargo ID")
            @PathVariable Long id,

            @RequestBody @Valid UpdateCargoRequest request) {

        return ResponseEntity.ok(service.update(id, request));
    }

    @Operation(
            summary = "Activate cargo",
            description = "Changes the cargo status to active."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Cargo activated"),
            @ApiResponse(responseCode = "404", description = "Cargo not found", content = @Content)
    })
    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activate(
            @Parameter(description = "Cargo ID")
            @PathVariable Long id) {

        service.activate(id);

        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Deactivate cargo",
            description = "Changes the cargo status to inactive."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Cargo deactivated"),
            @ApiResponse(responseCode = "404", description = "Cargo not found", content = @Content)
    })
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(
            @Parameter(description = "Cargo ID")
            @PathVariable Long id) {

        service.deactivate(id);

        return ResponseEntity.noContent().build();
    }

}