package io.zorvyn.task.financedashboard.service;

import io.zorvyn.task.financedashboard.dto.DashboardSummary;
import io.zorvyn.task.financedashboard.dto.FinancialRecordResponse;
import io.zorvyn.task.financedashboard.model.TransactionType;
import io.zorvyn.task.financedashboard.repository.FinancialRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final FinancialRecordRepository recordRepository;
    private final FinancialRecordService recordService;

    public DashboardSummary getDashboardSummary() {
        BigDecimal totalIncome = recordRepository.sumByType(TransactionType.INCOME);
        BigDecimal totalExpenses = recordRepository.sumByType(TransactionType.EXPENSE);

        if (totalIncome == null) totalIncome = BigDecimal.ZERO;
        if (totalExpenses == null) totalExpenses = BigDecimal.ZERO;

        BigDecimal netBalance = totalIncome.subtract(totalExpenses);

        Map<String, BigDecimal> categoryTotals = getCategoryTotals();
        Map<String, BigDecimal> monthlyTrends = getMonthlyTrends();
        List<FinancialRecordResponse> recentActivity = this.getRecentActivity();

        return DashboardSummary.builder()
                .totalIncome(totalIncome)
                .totalExpenses(totalExpenses)
                .netBalance(netBalance)
                .categoryTotals(categoryTotals)
                .monthlyTrends(monthlyTrends)
                .recentActivity(recentActivity)
                .build();
    }

    private List<FinancialRecordResponse> getRecentActivity() {
        return recordRepository.findTop10ByOrderByDateDesc()
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

    private Map<String, BigDecimal> getCategoryTotals() {
        List<Object[]> results = recordRepository.sumByCategory();
        Map<String, BigDecimal> categoryTotals = new HashMap<>();

        for (Object[] result : results) {
            String category = (String) result[0];
            BigDecimal total = (BigDecimal) result[1];
            categoryTotals.put(category, total);
        }

        return categoryTotals;
    }

    private Map<String, BigDecimal> getMonthlyTrends() {
        List<Object[]> results = recordRepository.getMonthlyTrends();
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
}
