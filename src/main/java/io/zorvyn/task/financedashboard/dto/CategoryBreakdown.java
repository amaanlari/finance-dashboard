package io.zorvyn.task.financedashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryBreakdown {
    private BigDecimal amount;
    private Integer transactionCount;
    private BigDecimal average;              // amount / count
    private Double percentageOfTotal;        // % of total expenses
}

