package br.com.logicore.modules.address.controller;

import br.com.logicore.common.dto.PageResponse;
import br.com.logicore.modules.address.dto.AddressResponse;
import br.com.logicore.modules.address.dto.AddressSummaryResponse;
import br.com.logicore.modules.address.dto.CreateAddressRequest;
import br.com.logicore.modules.address.dto.UpdateAddressRequest;
import br.com.logicore.modules.address.service.AddressService;
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
@RequestMapping({"/api/v1/addresses"})
@Tag(
        name = "Addresses",
        description = "Endpoints for address management."
)
public class AddressController {

    private final AddressService service;

    public AddressController(AddressService service) {
        this.service = service;
    }

    @Operation(
            summary = "Create address",
            description = "Creates a new address."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Address created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content)
    })
    @PostMapping
    public ResponseEntity<AddressResponse> create(
            @RequestBody @Valid CreateAddressRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.create(request));
    }

    @Operation(
            summary = "List addresses",
            description = "Returns a paginated list of addresses with optional search and filters."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Addresses retrieved successfully")
    })
    @GetMapping
    public ResponseEntity<PageResponse<AddressResponse>> findAll(
            @Parameter(description = "Search by CEP, street, number, complement, district, city, state or country")
            @RequestParam(required = false) String search,

            @Parameter(description = "Filter by CEP")
            @RequestParam(required = false) String cep,

            @Parameter(description = "Filter by city")
            @RequestParam(required = false) String cidade,

            @Parameter(description = "Filter by state")
            @RequestParam(required = false) String estado,

            @Parameter(description = "Filter by country")
            @RequestParam(required = false) String pais,

            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {

        return ResponseEntity.ok(service.findAll(search, cep, cidade, estado, pais, pageable));
    }

    @Operation(
            summary = "Address summary",
            description = "Returns address statistics."
    )
    @ApiResponse(responseCode = "200", description = "Summary generated successfully")
    @GetMapping("/summary")
    public ResponseEntity<AddressSummaryResponse> summary() {

        return ResponseEntity.ok(service.summary());
    }

    @Operation(
            summary = "Find address by ID",
            description = "Returns an address by its identifier."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Address found"),
            @ApiResponse(responseCode = "404", description = "Address not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<AddressResponse> findById(
            @Parameter(description = "Address ID")
            @PathVariable Long id) {

        return ResponseEntity.ok(service.findById(id));
    }

    @Operation(
            summary = "Update address",
            description = "Updates address information."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Address updated successfully"),
            @ApiResponse(responseCode = "404", description = "Address not found", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<AddressResponse> update(
            @Parameter(description = "Address ID")
            @PathVariable Long id,
            @RequestBody @Valid UpdateAddressRequest request) {

        return ResponseEntity.ok(service.update(id, request));
    }

    @Operation(
            summary = "Delete address",
            description = "Deletes an address by its identifier."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Address deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Address not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Address ID")
            @PathVariable Long id) {

        service.delete(id);

        return ResponseEntity.noContent().build();
    }

}