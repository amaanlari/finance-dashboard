package io.zorvyn.task.financedashboard.repository;

import io.zorvyn.task.financedashboard.model.FinancialRecord;
import io.zorvyn.task.financedashboard.model.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Repository
public interface FinancialRecordRepository extends JpaRepository<FinancialRecord, Long>,
        JpaSpecificationExecutor<FinancialRecord> {

    List<FinancialRecord> findByType(TransactionType type);

    List<FinancialRecord> findByCategory(String category);

    List<FinancialRecord> findByDateBetween(LocalDate startDate, LocalDate endDate);

    List<FinancialRecord> findByTypeAndDateBetween(TransactionType type, LocalDate startDate, LocalDate endDate);

    List<FinancialRecord> findTop10ByOrderByDateDesc();

    @Query("SELECT SUM(f.amount) FROM FinancialRecord f WHERE f.type = :type")
    BigDecimal sumByType(@Param("type") TransactionType type);

    @Query("SELECT f.category, SUM(f.amount) FROM FinancialRecord f GROUP BY f.category")
    List<Object[]> sumByCategory();

    @Query("SELECT FUNCTION('YEAR', f.date), FUNCTION('MONTH', f.date), f.type, SUM(f.amount) " +
           "FROM FinancialRecord f " +
           "GROUP BY FUNCTION('YEAR', f.date), FUNCTION('MONTH', f.date), f.type " +
           "ORDER BY FUNCTION('YEAR', f.date), FUNCTION('MONTH', f.date)")
    List<Object[]> getMonthlyTrends();
}
