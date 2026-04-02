package io.zorvyn.task.financedashboard.util;

import io.zorvyn.task.financedashboard.model.FinancialRecord;
import io.zorvyn.task.financedashboard.model.Role;
import io.zorvyn.task.financedashboard.model.TransactionType;
import io.zorvyn.task.financedashboard.model.User;
import io.zorvyn.task.financedashboard.repository.FinancialRecordRepository;
import io.zorvyn.task.financedashboard.repository.UserRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final FinancialRecordRepository recordRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Create users with different roles
        User admin = User.builder()
                .username("admin")
                .email("admin@example.com")
                .password(passwordEncoder.encode("admin123"))
                .role(Role.ADMIN)
                .active(true)
                .build();

        User analyst = User.builder()
                .username("analyst")
                .email("analyst@example.com")
                .password(passwordEncoder.encode("analyst123"))
                .role(Role.ANALYST)
                .active(true)
                .build();

        User viewer = User.builder()
                .username("viewer")
                .email("viewer@example.com")
                .password(passwordEncoder.encode("viewer123"))
                .role(Role.VIEWER)
                .active(true)
                .build();

        userRepository.save(admin);
        userRepository.save(analyst);
        userRepository.save(viewer);

        // Create sample financial records
        FinancialRecord record1 = FinancialRecord.builder()
                .amount(new BigDecimal("5000.00"))
                .type(TransactionType.INCOME)
                .category("Salary")
                .date(LocalDate.now().minusDays(30))
                .notes("Monthly salary")
                .createdBy(admin)
                .build();

        FinancialRecord record2 = FinancialRecord.builder()
                .amount(new BigDecimal("1200.00"))
                .type(TransactionType.EXPENSE)
                .category("Rent")
                .date(LocalDate.now().minusDays(28))
                .notes("Monthly rent payment")
                .createdBy(admin)
                .build();

        FinancialRecord record3 = FinancialRecord.builder()
                .amount(new BigDecimal("300.00"))
                .type(TransactionType.EXPENSE)
                .category("Groceries")
                .date(LocalDate.now().minusDays(25))
                .notes("Weekly grocery shopping")
                .createdBy(analyst)
                .build();

        FinancialRecord record4 = FinancialRecord.builder()
                .amount(new BigDecimal("150.00"))
                .type(TransactionType.EXPENSE)
                .category("Utilities")
                .date(LocalDate.now().minusDays(20))
                .notes("Electricity and water bill")
                .createdBy(analyst)
                .build();

        FinancialRecord record5 = FinancialRecord.builder()
                .amount(new BigDecimal("2000.00"))
                .type(TransactionType.INCOME)
                .category("Freelance")
                .date(LocalDate.now().minusDays(15))
                .notes("Freelance project payment")
                .createdBy(admin)
                .build();

        FinancialRecord record6 = FinancialRecord.builder()
                .amount(new BigDecimal("75.00"))
                .type(TransactionType.EXPENSE)
                .category("Entertainment")
                .date(LocalDate.now().minusDays(10))
                .notes("Movie tickets and dinner")
                .createdBy(analyst)
                .build();

        FinancialRecord record7 = FinancialRecord.builder()
                .amount(new BigDecimal("500.00"))
                .type(TransactionType.EXPENSE)
                .category("Healthcare")
                .date(LocalDate.now().minusDays(7))
                .notes("Medical checkup")
                .createdBy(admin)
                .build();

        FinancialRecord record8 = FinancialRecord.builder()
                .amount(new BigDecimal("200.00"))
                .type(TransactionType.EXPENSE)
                .category("Transportation")
                .date(LocalDate.now().minusDays(5))
                .notes("Monthly transport pass")
                .createdBy(analyst)
                .build();

        FinancialRecord record9 = FinancialRecord.builder()
                .amount(new BigDecimal("1000.00"))
                .type(TransactionType.INCOME)
                .category("Investment")
                .date(LocalDate.now().minusDays(3))
                .notes("Stock dividend")
                .createdBy(admin)
                .build();

        FinancialRecord record10 = FinancialRecord.builder()
                .amount(new BigDecimal("100.00"))
                .type(TransactionType.EXPENSE)
                .category("Education")
                .date(LocalDate.now().minusDays(1))
                .notes("Online course subscription")
                .createdBy(analyst)
                .build();

        recordRepository.save(record1);
        recordRepository.save(record2);
        recordRepository.save(record3);
        recordRepository.save(record4);
        recordRepository.save(record5);
        recordRepository.save(record6);
        recordRepository.save(record7);
        recordRepository.save(record8);
        recordRepository.save(record9);
        recordRepository.save(record10);

        System.out.println("===========================================");
        System.out.println("Sample data initialized successfully!");
        System.out.println("===========================================");
        System.out.println("Test Users:");
        System.out.println("1. Admin - username: admin, password: admin123");
        System.out.println("2. Analyst - username: analyst, password: analyst123");
        System.out.println("3. Viewer - username: viewer, password: viewer123");
        System.out.println("===========================================");
    }
}
