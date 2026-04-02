package io.zorvyn.task.financedashboard.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.zorvyn.task.financedashboard.dto.FinancialRecordFilter;
import io.zorvyn.task.financedashboard.dto.FinancialRecordRequest;
import io.zorvyn.task.financedashboard.dto.FinancialRecordResponse;
import io.zorvyn.task.financedashboard.exception.ErrorResponse;
import io.zorvyn.task.financedashboard.model.TransactionType;
import io.zorvyn.task.financedashboard.service.FinancialRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/records")
@RequiredArgsConstructor
@Tag(name = "Financial Records", description = "Financial records management endpoints (CRUD operations and filtering)")
@SecurityRequirement(name = "bearerAuth")
public class FinancialRecordController {

    private final FinancialRecordService recordService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST')")
    @Operation(
            summary = "Create a new financial record",
            description = "Creates a new financial record (income or expense). Available to ADMIN and ANALYST roles only."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Record created successfully",
                    content = @Content(schema = @Schema(implementation = FinancialRecordResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden - Only ADMIN and ANALYST can create records",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<FinancialRecordResponse> createRecord(
            @Valid @RequestBody FinancialRecordRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(recordService.createRecord(request));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('VIEWER', 'ANALYST', 'ADMIN')")
    @Operation(
            summary = "Get financial records with optional filters",
            description = """
                    Retrieves financial records. All filter parameters are optional and combinable.
                    When no filters are provided, all records are returned.
                    Available to all authenticated users.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Records retrieved successfully",
                    content = @Content(schema = @Schema(implementation = FinancialRecordResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid filter parameters",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<FinancialRecordResponse>> getRecords(
            @Parameter(description = "Filter by transaction type", example = "INCOME")
            @RequestParam(required = false) TransactionType type,

            @Parameter(description = "Filter by category", example = "Salary")
            @RequestParam(required = false) String category,

            @Parameter(description = "Filter from this date (ISO format, inclusive)", example = "2026-01-01")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,

            @Parameter(description = "Filter up to this date (ISO format, inclusive)", example = "2026-03-31")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        FinancialRecordFilter filter = new FinancialRecordFilter(type, category, startDate, endDate);
        return ResponseEntity.ok(recordService.getRecords(filter));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('VIEWER', 'ANALYST', 'ADMIN')")
    @Operation(
            summary = "Get financial record by ID",
            description = "Retrieves a specific financial record by its ID. Available to all authenticated users."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Record retrieved successfully",
                    content = @Content(schema = @Schema(implementation = FinancialRecordResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Record not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<FinancialRecordResponse> getRecordById(
            @Parameter(description = "Record ID", required = true, example = "1")
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(recordService.getRecordById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST')")
    @Operation(
            summary = "Update a financial record",
            description = "Updates an existing financial record. Available to ADMIN and ANALYST roles only."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Record updated successfully",
                    content = @Content(schema = @Schema(implementation = FinancialRecordResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden - Only ADMIN and ANALYST can update records",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Record not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<FinancialRecordResponse> updateRecord(
            @Parameter(description = "Record ID", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody FinancialRecordRequest request
    ) {
        return ResponseEntity.ok(recordService.updateRecord(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Delete a financial record",
            description = "Deletes a financial record. Available to ADMIN role only."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Record deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden - Only ADMIN can delete records",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Record not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> deleteRecord(
            @Parameter(description = "Record ID", required = true, example = "1")
            @PathVariable Long id
    ) {
        recordService.deleteRecord(id);
        return ResponseEntity.noContent().build();
    }
}