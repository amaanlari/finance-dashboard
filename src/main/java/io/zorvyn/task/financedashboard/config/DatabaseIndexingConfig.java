package io.zorvyn.task.financedashboard.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Programmatic database indexing configuration.
 * Creates indexes after Hibernate has created the tables.
 * This ensures optimal query performance for dashboard operations.
 */
@Component
@RequiredArgsConstructor
public class DatabaseIndexingConfig {

    private final DataSource dataSource;

    @EventListener(ApplicationReadyEvent.class)
    public void createIndexes() {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {

            // Index on soft-delete flag - used in every dashboard query
            statement.execute("CREATE INDEX IF NOT EXISTS idx_financial_record_deleted_at ON financial_records(deleted_at)");

            // Composite index for type filtering with soft-delete
            statement.execute("CREATE INDEX IF NOT EXISTS idx_financial_record_type_deleted ON financial_records(type, deleted_at)");

            // Composite index for category grouping with soft-delete
            statement.execute("CREATE INDEX IF NOT EXISTS idx_financial_record_category_deleted ON financial_records(category, deleted_at)");

            // Composite index for date range queries with soft-delete
            statement.execute("CREATE INDEX IF NOT EXISTS idx_financial_record_date_deleted ON financial_records(date, deleted_at)");

            // Composite index for type + date filtering
            statement.execute("CREATE INDEX IF NOT EXISTS idx_financial_record_type_date_deleted ON financial_records(type, date, deleted_at)");

            // Composite index for category + date filtering
            statement.execute("CREATE INDEX IF NOT EXISTS idx_financial_record_category_date_deleted ON financial_records(category, date, deleted_at)");

            // Index for finding latest transaction
            statement.execute("CREATE INDEX IF NOT EXISTS idx_financial_record_date_desc ON financial_records(date DESC, deleted_at)");

            // Index on created_by for potential future filtering
            statement.execute("CREATE INDEX IF NOT EXISTS idx_financial_record_created_by ON financial_records(created_by)");

            connection.commit();
        } catch (SQLException e) {
            // Log but don't fail - indexes might already exist or database might not support them
            System.err.println("Note: Database indexes creation: " + e.getMessage());
        }
    }
}

