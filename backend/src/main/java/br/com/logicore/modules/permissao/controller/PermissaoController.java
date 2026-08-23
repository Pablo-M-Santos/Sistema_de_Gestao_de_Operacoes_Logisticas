package br.com.logicore.modules.permissao.controller;

import br.com.logicore.common.dto.PageResponse;
import br.com.logicore.modules.permissao.dto.CreatePermissaoRequest;
import br.com.logicore.modules.permissao.dto.UpdatePermissaoRequest;
import br.com.logicore.modules.permissao.dto.PermissaoResponse;
import br.com.logicore.modules.permissao.dto.PermissaoSummary;
import br.com.logicore.modules.permissao.service.PermissaoService;
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
@RequestMapping("/api/v1/permissoes")
@Tag(
        name = "Permissoes",
        description = "Endpoints for permission management."
)
public class PermissaoController {

    private final PermissaoService service;

    public PermissaoController(PermissaoService service) {
        this.service = service;
    }

    @Operation(
            summary = "Create permission",
            description = "Creates a new permission."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Permission created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content),
            @ApiResponse(responseCode = "409", description = "Permission name already exists", content = @Content)
    })
    @PostMapping
    public ResponseEntity<PermissaoResponse> create(
            @RequestBody @Valid CreatePermissaoRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.create(request));
    }

    @Operation(
            summary = "List permissions",
            description = "Returns a paginated list of permissions with optional search."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Permissions retrieved successfully")
    })
    @GetMapping
    public ResponseEntity<PageResponse<PermissaoResponse>> findAll(
            @Parameter(description = "Search by permission name or description")
            @RequestParam(required = false) String search,

            @PageableDefault(page = 0, size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {

        return ResponseEntity.ok(service.findAll(search, pageable));
    }

    @Operation(
            summary = "Permission summary",
            description = "Returns permission statistics."
    )
    @ApiResponse(responseCode = "200", description = "Summary generated successfully")
    @GetMapping("/summary")
    public ResponseEntity<PermissaoSummary> summary() {
        return ResponseEntity.ok(service.summary());
    }

    @Operation(
            summary = "Find permission by ID",
            description = "Returns a permission by its identifier."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Permission found"),
            @ApiResponse(responseCode = "404", description = "Permission not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<PermissaoResponse> findById(
            @Parameter(description = "Permission ID")
            @PathVariable Long id) {

        return ResponseEntity.ok(service.findById(id));
    }

    @Operation(
            summary = "Update permission",
            description = "Updates permission information."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Permission updated successfully"),
            @ApiResponse(responseCode = "404", description = "Permission not found", content = @Content),
            @ApiResponse(responseCode = "409", description = "Permission name already exists", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<PermissaoResponse> update(
            @Parameter(description = "Permission ID")
            @PathVariable Long id,
            @RequestBody @Valid UpdatePermissaoRequest request) {

        return ResponseEntity.ok(service.update(id, request));
    }

    @Operation(
            summary = "Delete permission",
            description = "Deletes a permission permanently."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Permission deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Permission not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Permission ID")
            @PathVariable Long id) {

        service.delete(id);

        return ResponseEntity.noContent().build();
    }
}
