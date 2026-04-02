package io.zorvyn.task.financedashboard.dto;

import io.zorvyn.task.financedashboard.model.TransactionType;

import java.time.LocalDate;

public record FinancialRecordFilter(
        TransactionType type,
        String category,
        LocalDate startDate,
        LocalDate endDate
) {}