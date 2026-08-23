package br.com.logicore.modules.usuario.controller;

import br.com.logicore.common.dto.PageResponse;
import br.com.logicore.modules.usuario.dto.CreateUsuarioRequest;
import br.com.logicore.modules.usuario.dto.UpdateUsuarioRequest;
import br.com.logicore.modules.usuario.dto.UsuarioResponse;
import br.com.logicore.modules.usuario.dto.UsuarioSummary;
import br.com.logicore.modules.usuario.service.UsuarioService;
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
@RequestMapping("/api/v1/usuarios")
@Tag(
        name = "Usuarios",
        description = "Endpoints for user management."
)
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    @Operation(
            summary = "Create user",
            description = "Creates a new user linked to an existing employee."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content),
            @ApiResponse(responseCode = "404", description = "Employee not found", content = @Content),
            @ApiResponse(responseCode = "409", description = "Email or employee already registered as user", content = @Content)
    })
    @PostMapping
    public ResponseEntity<UsuarioResponse> create(
            @RequestBody @Valid CreateUsuarioRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.create(request));
    }

    @Operation(
            summary = "List users",
            description = "Returns a paginated list of users with optional search and filters."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully")
    })
    @GetMapping
    public ResponseEntity<PageResponse<UsuarioResponse>> findAll(
            @Parameter(description = "Search by user name")
            @RequestParam(required = false) String search,

            @Parameter(description = "Filter by email")
            @RequestParam(required = false) String email,

            @Parameter(description = "Filter by status: ACTIVE, INACTIVE or ALL")
            @RequestParam(required = false) String status,

            @Parameter(description = "Filter by employee ID")
            @RequestParam(required = false) Long funcionarioId,

            @PageableDefault(page = 0, size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {

        return ResponseEntity.ok(service.findAll(search, email, status, funcionarioId, pageable));
    }

    @Operation(
            summary = "User summary",
            description = "Returns user statistics."
    )
    @ApiResponse(responseCode = "200", description = "Summary generated successfully")
    @GetMapping("/summary")
    public ResponseEntity<UsuarioSummary> summary() {
        return ResponseEntity.ok(service.summary());
    }

    @Operation(
            summary = "Find user by ID",
            description = "Returns a user by its identifier."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> findById(
            @Parameter(description = "User ID")
            @PathVariable Long id) {

        return ResponseEntity.ok(service.findById(id));
    }

    @Operation(
            summary = "Update user",
            description = "Updates user information."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content),
            @ApiResponse(responseCode = "409", description = "Email already exists", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> update(
            @Parameter(description = "User ID")
            @PathVariable Long id,
            @RequestBody @Valid UpdateUsuarioRequest request) {

        return ResponseEntity.ok(service.update(id, request));
    }

    @Operation(
            summary = "Delete user",
            description = "Deletes a user by marking it as INACTIVE."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "User ID")
            @PathVariable Long id) {

        service.delete(id);

        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Activate user",
            description = "Changes the user status to ACTIVE."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User activated"),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activate(@Parameter(description = "User ID") @PathVariable Long id) {
        service.activate(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Deactivate user",
            description = "Changes the user status to INACTIVE."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User deactivated"),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(@Parameter(description = "User ID") @PathVariable Long id) {
        service.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}
