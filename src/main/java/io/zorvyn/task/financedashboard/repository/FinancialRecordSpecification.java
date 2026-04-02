package io.zorvyn.task.financedashboard.repository;

import io.zorvyn.task.financedashboard.dto.FinancialRecordFilter;
import io.zorvyn.task.financedashboard.model.FinancialRecord;
import io.zorvyn.task.financedashboard.model.TransactionType;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class FinancialRecordSpecification {

    private FinancialRecordSpecification() {}

    public static Specification<FinancialRecord> withFilter(FinancialRecordFilter filter) {
        return Specification
                .where(hasType(filter.type()))
                .and(hasCategory(filter.category()))
                .and(onOrAfterDate(filter.startDate()))
                .and(onOrBeforeDate(filter.endDate()));
    }

    private static Specification<FinancialRecord> hasType(TransactionType type) {
        return (root, query, cb) ->
                type == null ? null : cb.equal(root.get("type"), type);
    }

    private static Specification<FinancialRecord> hasCategory(String category) {
        return (root, query, cb) ->
                (category == null || category.isBlank()) ? null
                        : cb.equal(cb.lower(root.get("category")), category.toLowerCase());
    }

    private static Specification<FinancialRecord> onOrAfterDate(LocalDate startDate) {
        return (root, query, cb) ->
                startDate == null ? null : cb.greaterThanOrEqualTo(root.get("date"), startDate);
    }

    private static Specification<FinancialRecord> onOrBeforeDate(LocalDate endDate) {
        return (root, query, cb) ->
                endDate == null ? null : cb.lessThanOrEqualTo(root.get("date"), endDate);
    }
}