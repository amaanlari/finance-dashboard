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
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FinancialRecordRepository extends JpaRepository<FinancialRecord, Long>,
        JpaSpecificationExecutor<FinancialRecord> {

    List<FinancialRecord> findByTypeAndDeletedAtIsNull(TransactionType type);

    List<FinancialRecord> findByCategoryAndDeletedAtIsNull(String category);

    List<FinancialRecord> findByDateBetweenAndDeletedAtIsNull(LocalDate startDate, LocalDate endDate);

    List<FinancialRecord> findByTypeAndDateBetweenAndDeletedAtIsNull(TransactionType type, LocalDate startDate, LocalDate endDate);

    List<FinancialRecord> findTop10ByDeletedAtIsNullOrderByDateDesc();

    // Optimized sum by type with date range filtering
    @Query("SELECT SUM(f.amount) FROM FinancialRecord f WHERE f.type = :type AND f.deletedAt IS NULL AND f.date BETWEEN :startDate AND :endDate")
    BigDecimal sumByType(@Param("type") TransactionType type, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // Legacy method for backward compatibility
    @Query("SELECT SUM(f.amount) FROM FinancialRecord f WHERE f.type = :type AND f.deletedAt IS NULL")
    BigDecimal sumByTypeAllTime(@Param("type") TransactionType type);

    // Enhanced sum by category with transaction count
    @Query("SELECT f.category, SUM(f.amount), COUNT(f) FROM FinancialRecord f WHERE f.deletedAt IS NULL AND f.date BETWEEN :startDate AND :endDate GROUP BY f.category")
    List<Object[]> sumByCategory(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // Legacy method for backward compatibility
    @Query("SELECT f.category, SUM(f.amount) FROM FinancialRecord f WHERE f.deletedAt IS NULL GROUP BY f.category")
    List<Object[]> sumByCategoryAllTime();

    // Monthly trends with date range filtering
    @Query("SELECT FUNCTION('YEAR', f.date), FUNCTION('MONTH', f.date), f.type, SUM(f.amount) " +
           "FROM FinancialRecord f " +
           "WHERE f.deletedAt IS NULL AND f.date BETWEEN :startDate AND :endDate " +
           "GROUP BY FUNCTION('YEAR', f.date), FUNCTION('MONTH', f.date), f.type " +
           "ORDER BY FUNCTION('YEAR', f.date), FUNCTION('MONTH', f.date)")
    List<Object[]> getMonthlyTrends(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // Legacy method for backward compatibility
    @Query("SELECT FUNCTION('YEAR', f.date), FUNCTION('MONTH', f.date), f.type, SUM(f.amount) " +
           "FROM FinancialRecord f " +
           "WHERE f.deletedAt IS NULL " +
           "GROUP BY FUNCTION('YEAR', f.date), FUNCTION('MONTH', f.date), f.type " +
           "ORDER BY FUNCTION('YEAR', f.date), FUNCTION('MONTH', f.date)")
    List<Object[]> getMonthlyTrendsAllTime();

    // Count transactions in date range
    Integer countByDateBetweenAndDeletedAtIsNull(LocalDate startDate, LocalDate endDate);

    // Count transactions by type in date range
    Integer countByTypeAndDateBetweenAndDeletedAtIsNull(TransactionType type, LocalDate startDate, LocalDate endDate);

    // Find latest transaction date in range
    @Query("SELECT MAX(f.date) FROM FinancialRecord f WHERE f.deletedAt IS NULL AND f.date BETWEEN :startDate AND :endDate")
    LocalDateTime findLatestTransactionDate(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
