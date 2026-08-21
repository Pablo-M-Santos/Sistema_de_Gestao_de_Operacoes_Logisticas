package br.com.logicore.modules.client.controller;

import br.com.logicore.common.dto.PageResponse;
import br.com.logicore.modules.client.dto.ClientResponse;
import br.com.logicore.modules.client.dto.ClientSummaryResponse;
import br.com.logicore.modules.client.dto.CreateClientRequest;
import br.com.logicore.modules.client.dto.UpdateClientRequest;
import br.com.logicore.modules.client.service.ClientService;
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
@RequestMapping({"/api/v1/clients"})
@Tag(
        name = "Clients",
        description = "Endpoints for client management."
)
public class ClientController {

    private final ClientService service;

    public ClientController(ClientService service) {
        this.service = service;
    }

    @Operation(
            summary = "Create client",
            description = "Creates a new client."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Client created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content),
            @ApiResponse(responseCode = "409", description = "Client already exists", content = @Content)
    })
    @PostMapping
    public ResponseEntity<ClientResponse> create(
            @Valid @RequestBody CreateClientRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.create(request));
    }

    @Operation(
            summary = "List clients",
            description = "Returns a paginated list of clients with optional search and filters."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Clients retrieved successfully")
    })
    @GetMapping
    public ResponseEntity<PageResponse<ClientResponse>> findAll(
            @Parameter(description = "Search by razao social, nome fantasia, CNPJ, email, phone or contato principal")
            @RequestParam(required = false) String search,

            @Parameter(description = "Filter by status: ACTIVE, INACTIVE or ALL")
            @RequestParam(required = false) String status,

            @Parameter(description = "Filter by address ID")
            @RequestParam(required = false) Long enderecoId,

            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {

        return ResponseEntity.ok(service.findAll(search, status, enderecoId, pageable));
    }

    @Operation(
            summary = "Client summary",
            description = "Returns client statistics."
    )
    @ApiResponse(responseCode = "200", description = "Summary generated successfully")
    @GetMapping("/summary")
    public ResponseEntity<ClientSummaryResponse> summary() {

        return ResponseEntity.ok(service.summary());
    }

    @Operation(
            summary = "Find client by ID",
            description = "Returns a client by its identifier."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Client found"),
            @ApiResponse(responseCode = "404", description = "Client not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<ClientResponse> findById(
            @Parameter(description = "Client ID")
            @PathVariable Long id) {

        return ResponseEntity.ok(service.findById(id));
    }

    @Operation(
            summary = "Update client",
            description = "Updates client information."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Client updated successfully"),
            @ApiResponse(responseCode = "404", description = "Client not found", content = @Content),
            @ApiResponse(responseCode = "409", description = "CNPJ already exists", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<ClientResponse> update(
            @Parameter(description = "Client ID")
            @PathVariable Long id,
            @Valid @RequestBody UpdateClientRequest request) {

        return ResponseEntity.ok(service.update(id, request));
    }

    @Operation(
            summary = "Delete client",
            description = "Deletes a client by marking it as INACTIVE."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Client deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Client not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Client ID")
            @PathVariable Long id) {

        service.delete(id);

        return ResponseEntity.noContent().build();
    }
}
