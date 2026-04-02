package io.zorvyn.task.financedashboard.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.zorvyn.task.financedashboard.dto.EnhancedDashboardSummary;
import io.zorvyn.task.financedashboard.exception.ErrorResponse;
import io.zorvyn.task.financedashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Dashboard analytics and summary endpoints")
@SecurityRequirement(name = "bearerAuth")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    @PreAuthorize("hasAnyRole('VIEWER', 'ANALYST', 'ADMIN')")
    @Operation(
            summary = "Get enhanced dashboard summary with optional date filtering",
            description = """
                    Returns comprehensive financial analytics including:
                    - Total income, expenses, net balance
                    - Savings rate (% of income saved)
                    - Average transaction size
                    - Transaction counts (total, income, expense)
                    - Category breakdown with percentages and averages
                    - Monthly trends
                    - Recent activity (last 10 transactions)
                    - Period metadata (days covered, period label)
                    
                    Date Parameters (both optional):
                    - If neither: returns all-time summary
                    - If only startDate: from that date to today
                    - If only endDate: from earliest to that date
                    - If both: for that specific range
                    
                    Available to VIEWER, ANALYST, and ADMIN roles.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Dashboard summary retrieved successfully",
                    content = @Content(schema = @Schema(implementation = EnhancedDashboardSummary.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid date range (startDate after endDate)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Invalid or missing JWT token",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - Insufficient permissions",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<EnhancedDashboardSummary> getDashboardSummary(
            @Parameter(description = "Start date (ISO format, inclusive). Example: 2026-01-01")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,

            @Parameter(description = "End date (ISO format, inclusive). Example: 2026-03-31")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        // Default to all-time if not provided
        LocalDate start = startDate != null ? startDate : LocalDate.of(2000, 1, 1);
        LocalDate end = endDate != null ? endDate : LocalDate.now();

        return ResponseEntity.ok(dashboardService.getDashboardSummary(start, end));
    }
}
