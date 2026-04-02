package io.zorvyn.task.financedashboard.service;

import io.zorvyn.task.financedashboard.dto.*;
import io.zorvyn.task.financedashboard.model.TransactionType;
import io.zorvyn.task.financedashboard.repository.FinancialRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final FinancialRecordRepository recordRepository;
    private final FinancialRecordService recordService;

    /**
     * Get enhanced dashboard summary for a date range
     * @param startDate Start of period (inclusive)
     * @param endDate End of period (inclusive)
     * @return Enhanced dashboard with metrics including savings rate, averages, and percentages
     */
    public EnhancedDashboardSummary getDashboardSummary(LocalDate startDate, LocalDate endDate) {
        
        // Validate date range
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("startDate must not be after endDate");
        }
        
        // Get core totals with date filtering
        BigDecimal totalIncome = recordRepository.sumByType(TransactionType.INCOME, startDate, endDate);
        BigDecimal totalExpenses = recordRepository.sumByType(TransactionType.EXPENSE, startDate, endDate);

        if (totalIncome == null) totalIncome = BigDecimal.ZERO;
        if (totalExpenses == null) totalExpenses = BigDecimal.ZERO;

        BigDecimal netBalance = totalIncome.subtract(totalExpenses);

        // Get transaction counts
        Integer totalCount = recordRepository.countByDateBetweenAndDeletedAtIsNull(startDate, endDate);
        Integer incomeCount = recordRepository.countByTypeAndDateBetweenAndDeletedAtIsNull(TransactionType.INCOME, startDate, endDate);
        Integer expenseCount = recordRepository.countByTypeAndDateBetweenAndDeletedAtIsNull(TransactionType.EXPENSE, startDate, endDate);

        if (totalCount == null) totalCount = 0;
        if (incomeCount == null) incomeCount = 0;
        if (expenseCount == null) expenseCount = 0;

        // Calculate derived metrics
        BigDecimal savingsRate = calculateSavingsRate(totalIncome, totalExpenses);
        BigDecimal avgTransactionSize = calculateAverageTransactionSize(
            totalIncome.add(totalExpenses),
            totalCount
        );

        // Get category breakdown with percentages
        Map<String, CategoryBreakdown> categoryTotals = getCategoryTotalsWithBreakdown(startDate, endDate, totalExpenses);
        
        // Get monthly trends
        Map<String, BigDecimal> monthlyTrends = getMonthlyTrends(startDate, endDate);
        
        // Get recent activity
        List<FinancialRecordResponse> recentActivity = getRecentActivity();
        
        // Get metadata
        LocalDateTime lastTransactionDate = recordRepository.findLatestTransactionDate(startDate, endDate);
        PeriodInfo periodInfo = buildPeriodInfo(startDate, endDate);

        return EnhancedDashboardSummary.builder()
                .totalIncome(totalIncome)
                .totalExpenses(totalExpenses)
                .netBalance(netBalance)
                .savingsRate(savingsRate)
                .averageTransactionSize(avgTransactionSize)
                .totalTransactionCount(totalCount)
                .incomeTransactionCount(incomeCount)
                .expenseTransactionCount(expenseCount)
                .categoryTotals(categoryTotals)
                .monthlyTrends(monthlyTrends)
                .recentActivity(recentActivity)
                .period(periodInfo)
                .generatedAt(LocalDateTime.now())
                .lastTransactionDate(lastTransactionDate)
                .build();
    }

    /**
     * Get category breakdown with percentage and average (optimized query returns count)
     */
    private Map<String, CategoryBreakdown> getCategoryTotalsWithBreakdown(
            LocalDate startDate, 
            LocalDate endDate,
            BigDecimal totalExpenses) {
        
        List<Object[]> results = recordRepository.sumByCategory(startDate, endDate);
        Map<String, CategoryBreakdown> breakdown = new HashMap<>();

        for (Object[] result : results) {
            String category = (String) result[0];
            BigDecimal amount = (BigDecimal) result[1];
            Integer count = ((Number) result[2]).intValue();
            
            // Calculate percentage of total expenses
            BigDecimal percentage = totalExpenses.compareTo(BigDecimal.ZERO) > 0
                ? amount.divide(totalExpenses, 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100))
                : BigDecimal.ZERO;
            
            // Calculate average transaction size for this category
            BigDecimal average = count > 0
                ? amount.divide(new BigDecimal(count), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

            breakdown.put(category, CategoryBreakdown.builder()
                    .amount(amount)
                    .transactionCount(count)
                    .average(average)
                    .percentageOfTotal(percentage.doubleValue())
                    .build());
        }

        return breakdown;
    }

    /**
     * Calculate savings rate as percentage
     * Savings Rate = (Income - Expenses) / Income * 100
     */
    private BigDecimal calculateSavingsRate(BigDecimal totalIncome, BigDecimal totalExpenses) {
        if (totalIncome.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        return totalIncome.subtract(totalExpenses)
                .divide(totalIncome, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal(100))
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calculate average transaction size
     */
    private BigDecimal calculateAverageTransactionSize(BigDecimal totalAmount, Integer count) {
        if (count == null || count == 0) {
            return BigDecimal.ZERO;
        }
        return totalAmount.divide(new BigDecimal(count), 2, RoundingMode.HALF_UP);
    }

    /**
     * Build period info for metadata
     */
    private PeriodInfo buildPeriodInfo(LocalDate startDate, LocalDate endDate) {
        long daysCovered = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        String periodLabel = determinePeriodLabel(startDate, endDate);

        return PeriodInfo.builder()
                .startDate(startDate)
                .endDate(endDate)
                .daysCovered((int) daysCovered)
                .period(periodLabel)
                .build();
    }

    /**
     * Determine user-friendly period label
     */
    private String determinePeriodLabel(LocalDate startDate, LocalDate endDate) {
        LocalDate today = LocalDate.now();
        LocalDate yearStart = LocalDate.of(today.getYear(), 1, 1);
        LocalDate monthStart = LocalDate.of(today.getYear(), today.getMonthValue(), 1);

        if (startDate.equals(monthStart) && endDate.equals(today)) {
            return "This Month";
        } else if (startDate.equals(yearStart) && endDate.equals(today)) {
            return "Year to Date";
        } else if (startDate.isBefore(LocalDate.of(2000, 1, 1)) && endDate.equals(today)) {
            return "All Time";
        } else {
            return String.format("Custom (%s to %s)", startDate, endDate);
        }
    }

    /**
     * Get monthly trends with date filtering
     */
    private Map<String, BigDecimal> getMonthlyTrends(LocalDate startDate, LocalDate endDate) {
        List<Object[]> results = recordRepository.getMonthlyTrends(startDate, endDate);
        Map<String, BigDecimal> monthlyTrends = new HashMap<>();

        for (Object[] result : results) {
            Integer year = (Integer) result[0];
            Integer month = (Integer) result[1];
            TransactionType type = (TransactionType) result[2];
            BigDecimal amount = (BigDecimal) result[3];

            String key = String.format("%d-%02d-%s", year, month, type.name());
            monthlyTrends.put(key, amount);
        }

        return monthlyTrends;
    }

    /**
     * Get recent activity (last 10 transactions)
     */
    private List<FinancialRecordResponse> getRecentActivity() {
        return recordRepository.findTop10ByDeletedAtIsNullOrderByDateDesc()
                .stream()
                .map(record -> FinancialRecordResponse.builder()
                        .id(record.getId())
                        .amount(record.getAmount())
                        .type(record.getType().name())
                        .category(record.getCategory())
                        .date(record.getDate())
                        .notes(record.getNotes())
                        .createdBy(record.getCreatedBy().getUsername())
                        .createdAt(record.getCreatedAt())
                        .updatedAt(record.getUpdatedAt())
                        .build())
                .toList();
    }
}
