package io.zorvyn.task.financedashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnhancedDashboardSummary {
    
    // Core metrics
    private BigDecimal totalIncome;
    private BigDecimal totalExpenses;
    private BigDecimal netBalance;
    
    // Enhanced metrics - KEY ADDITIONS
    private BigDecimal savingsRate;              // % of income saved
    private BigDecimal averageTransactionSize;   // Avg per transaction
    private Integer totalTransactionCount;
    private Integer incomeTransactionCount;
    private Integer expenseTransactionCount;
    
    // Category breakdown
    private Map<String, CategoryBreakdown> categoryTotals;
    
    // Trends
    private Map<String, BigDecimal> monthlyTrends;
    
    // Recent activity
    private List<FinancialRecordResponse> recentActivity;
    
    // Metadata
    private PeriodInfo period;
    private LocalDateTime generatedAt;
    private LocalDateTime lastTransactionDate;
}

