package br.com.logicore.modules.perfil.controller;

import br.com.logicore.common.dto.PageResponse;
import br.com.logicore.modules.perfil.dto.CreatePerfilRequest;
import br.com.logicore.modules.perfil.dto.UpdatePerfilRequest;
import br.com.logicore.modules.perfil.dto.PerfilResponse;
import br.com.logicore.modules.perfil.dto.PerfilSummary;
import br.com.logicore.modules.perfil.service.PerfilService;
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
@RequestMapping("/api/v1/perfis")
@Tag(
        name = "Perfis",
        description = "Endpoints for profile management."
)
public class PerfilController {

    private final PerfilService service;

    public PerfilController(PerfilService service) {
        this.service = service;
    }

    @Operation(
            summary = "Create profile",
            description = "Creates a new profile."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Profile created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content),
            @ApiResponse(responseCode = "409", description = "Profile name already exists", content = @Content)
    })
    @PostMapping
    public ResponseEntity<PerfilResponse> create(
            @RequestBody @Valid CreatePerfilRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.create(request));
    }

    @Operation(
            summary = "List profiles",
            description = "Returns a paginated list of profiles with optional search."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profiles retrieved successfully")
    })
    @GetMapping
    public ResponseEntity<PageResponse<PerfilResponse>> findAll(
            @Parameter(description = "Search by profile name or description")
            @RequestParam(required = false) String search,

            @PageableDefault(page = 0, size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {

        return ResponseEntity.ok(service.findAll(search, pageable));
    }

    @Operation(
            summary = "Profile summary",
            description = "Returns profile statistics."
    )
    @ApiResponse(responseCode = "200", description = "Summary generated successfully")
    @GetMapping("/summary")
    public ResponseEntity<PerfilSummary> summary() {
        return ResponseEntity.ok(service.summary());
    }

    @Operation(
            summary = "Find profile by ID",
            description = "Returns a profile by its identifier."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile found"),
            @ApiResponse(responseCode = "404", description = "Profile not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<PerfilResponse> findById(
            @Parameter(description = "Profile ID")
            @PathVariable Long id) {

        return ResponseEntity.ok(service.findById(id));
    }

    @Operation(
            summary = "Update profile",
            description = "Updates profile information."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile updated successfully"),
            @ApiResponse(responseCode = "404", description = "Profile not found", content = @Content),
            @ApiResponse(responseCode = "409", description = "Profile name already exists", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<PerfilResponse> update(
            @Parameter(description = "Profile ID")
            @PathVariable Long id,
            @RequestBody @Valid UpdatePerfilRequest request) {

        return ResponseEntity.ok(service.update(id, request));
    }

    @Operation(
            summary = "Delete profile",
            description = "Deletes a profile permanently."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Profile deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Profile not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Profile ID")
            @PathVariable Long id) {

        service.delete(id);

        return ResponseEntity.noContent().build();
    }
}
