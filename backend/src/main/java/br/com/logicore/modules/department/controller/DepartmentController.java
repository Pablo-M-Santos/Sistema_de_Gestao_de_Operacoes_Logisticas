package br.com.logicore.modules.department.controller;

import br.com.logicore.common.dto.PageResponse;
import br.com.logicore.modules.department.dto.CreateDepartmentRequest;
import br.com.logicore.modules.department.dto.DepartmentResponse;
import br.com.logicore.modules.department.dto.DepartmentSummaryResponse;
import br.com.logicore.modules.department.dto.UpdateDepartmentRequest;
import br.com.logicore.modules.department.service.DepartmentService;
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
@RequestMapping("/api/v1/departments")
@Tag(
        name = "Departments",
        description = "Endpoints for department management."
)
public class DepartmentController {

    private final DepartmentService service;

    public DepartmentController(DepartmentService service) {
        this.service = service;
    }

    @Operation(
            summary = "Create department",
            description = "Creates a new department."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Department created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content),
            @ApiResponse(responseCode = "409", description = "Department already exists", content = @Content)
    })
    @PostMapping
    public ResponseEntity<DepartmentResponse> create(
            @RequestBody @Valid CreateDepartmentRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.create(request));
    }

    @Operation(
            summary = "List departments",
            description = "Returns a paginated list of departments with optional search and status filters."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Departments retrieved successfully")
    })
    @GetMapping
    public ResponseEntity<PageResponse<DepartmentResponse>> findAll(
            @Parameter(
                    description = "Search by department name or abbreviation"
            )
            @RequestParam(required = false) String search,

            @Parameter(
                    description = "Filter by status: ACTIVE, INACTIVE or ALL"
            )
            @RequestParam(required = false) String status,
            @PageableDefault(page = 0, size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {

        return ResponseEntity.ok(service.findAll(search, status, pageable));
    }

    @Operation(
            summary = "Department summary",
            description = "Returns department statistics."
    )
    @ApiResponse(responseCode = "200", description = "Summary generated successfully")
    @GetMapping("/summary")
    public ResponseEntity<DepartmentSummaryResponse> summary() {
        return ResponseEntity.ok(service.summary());
    }

    @Operation(
            summary = "Find department by ID",
            description = "Returns a department by its identifier."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Department found"),
            @ApiResponse(responseCode = "404", description = "Department not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<DepartmentResponse> findById(@Parameter(description = "Department ID")
                                                       @PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @Operation(
            summary = "Update department",
            description = "Updates department information."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Department updated successfully"),
            @ApiResponse(responseCode = "404", description = "Department not found", content = @Content),
            @ApiResponse(responseCode = "409", description = "Department name already exists", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<DepartmentResponse> update(
            @Parameter(description = "Department ID")
            @PathVariable Long id,
            @RequestBody @Valid UpdateDepartmentRequest request) {

        return ResponseEntity.ok(service.update(id, request));
    }

    @Operation(
            summary = "Activate department",
            description = "Changes the department status to ACTIVE."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Department activated"),
            @ApiResponse(responseCode = "404", description = "Department not found", content = @Content)
    })
    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activate(@Parameter(description = "Department ID") @PathVariable Long id) {

        service.activate(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Deactivate department",
            description = "Changes the department status to INACTIVE."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Department deactivated"),
            @ApiResponse(responseCode = "404", description = "Department not found", content = @Content)
    })
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(@Parameter(description = "Department ID") @PathVariable Long id) {
        service.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}