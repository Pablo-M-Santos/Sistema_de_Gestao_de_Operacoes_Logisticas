package br.com.logicore.modules.usuarioperfil.controller;

import br.com.logicore.common.dto.PageResponse;
import br.com.logicore.modules.usuarioperfil.dto.CreateUsuarioPerfilRequest;
import br.com.logicore.modules.usuarioperfil.dto.UsuarioPerfilResponse;
import br.com.logicore.modules.usuarioperfil.dto.UsuarioPerfilSummary;
import br.com.logicore.modules.usuarioperfil.service.UsuarioPerfilService;
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
@RequestMapping("/api/v1/usuarios-perfis")
@Tag(
        name = "UsuariosPerfis",
        description = "Endpoints for user-profile association management."
)
public class UsuarioPerfilController {

    private final UsuarioPerfilService service;

    public UsuarioPerfilController(UsuarioPerfilService service) {
        this.service = service;
    }

    @Operation(
            summary = "Create user-profile association",
            description = "Creates a new association between a user and a profile."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Association created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content),
            @ApiResponse(responseCode = "404", description = "User or profile not found", content = @Content),
            @ApiResponse(responseCode = "409", description = "Association already exists", content = @Content)
    })
    @PostMapping
    public ResponseEntity<UsuarioPerfilResponse> create(
            @RequestBody @Valid CreateUsuarioPerfilRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.create(request));
    }

    @Operation(
            summary = "List user-profile associations",
            description = "Returns a paginated list of associations with optional search and filters."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Associations retrieved successfully")
    })
    @GetMapping
    public ResponseEntity<PageResponse<UsuarioPerfilResponse>> findAll(
            @Parameter(description = "Search by user or profile name")
            @RequestParam(required = false) String search,

            @Parameter(description = "Filter by user ID")
            @RequestParam(required = false) Long usuarioId,

            @Parameter(description = "Filter by profile ID")
            @RequestParam(required = false) Long perfilId,

            @PageableDefault(page = 0, size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {

        return ResponseEntity.ok(service.findAll(search, usuarioId, perfilId, pageable));
    }

    @Operation(
            summary = "Association summary",
            description = "Returns association statistics."
    )
    @ApiResponse(responseCode = "200", description = "Summary generated successfully")
    @GetMapping("/summary")
    public ResponseEntity<UsuarioPerfilSummary> summary() {
        return ResponseEntity.ok(service.summary());
    }

    @Operation(
            summary = "Find association by ID",
            description = "Returns an association by its identifier."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Association found"),
            @ApiResponse(responseCode = "404", description = "Association not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioPerfilResponse> findById(
            @Parameter(description = "Association ID")
            @PathVariable Long id) {

        return ResponseEntity.ok(service.findById(id));
    }

    @Operation(
            summary = "Delete association",
            description = "Removes the association between user and profile."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Association removed successfully"),
            @ApiResponse(responseCode = "404", description = "Association not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Association ID")
            @PathVariable Long id) {

        service.delete(id);

        return ResponseEntity.noContent().build();
    }
}
