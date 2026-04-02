package io.zorvyn.task.financedashboard.service;
import io.zorvyn.task.financedashboard.dto.FinancialRecordFilter;
import io.zorvyn.task.financedashboard.dto.FinancialRecordRequest;
import io.zorvyn.task.financedashboard.dto.FinancialRecordResponse;
import io.zorvyn.task.financedashboard.exception.ResourceNotFoundException;
import io.zorvyn.task.financedashboard.model.FinancialRecord;
import io.zorvyn.task.financedashboard.model.Role;
import io.zorvyn.task.financedashboard.model.TransactionType;
import io.zorvyn.task.financedashboard.model.User;
import io.zorvyn.task.financedashboard.repository.FinancialRecordRepository;
import io.zorvyn.task.financedashboard.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FinancialRecordServiceTest {
    @Mock
    private FinancialRecordRepository recordRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private FinancialRecordService recordService;
    private User user;
    private FinancialRecord record;
    private FinancialRecordRequest recordRequest;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("password")
                .role(Role.ANALYST)
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();
        record = FinancialRecord.builder()
                .id(1L)
                .amount(new BigDecimal("1000.00"))
                .type(TransactionType.INCOME)
                .category("Salary")
                .date(LocalDate.now())
                .notes("Monthly salary")
                .createdBy(user)
                .createdAt(LocalDateTime.now())
                .build();
        recordRequest = new FinancialRecordRequest();
        recordRequest.setAmount(new BigDecimal("1000.00"));
        recordRequest.setType(TransactionType.INCOME);
        recordRequest.setCategory("Salary");
        recordRequest.setDate(LocalDate.now());
        recordRequest.setNotes("Monthly salary");
    }

    private void setUpSecurityContext() {
        Authentication authentication = new UsernamePasswordAuthenticationToken(user.getUsername(), null);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
    }

    @Test
    void testCreateRecord() {
        setUpSecurityContext();
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(recordRepository.save(any(FinancialRecord.class))).thenReturn(record);
        FinancialRecordResponse response = recordService.createRecord(recordRequest);
        assertThat(response).isNotNull();
        assertThat(response.getAmount()).isEqualTo(new BigDecimal("1000.00"));
        assertThat(response.getType()).isEqualTo("INCOME");
        assertThat(response.getCategory()).isEqualTo("Salary");
        verify(recordRepository).save(any(FinancialRecord.class));
    }

    @Test
    void testGetRecordById() {
        when(recordRepository.findById(1L)).thenReturn(Optional.of(record));
        FinancialRecordResponse response = recordService.getRecordById(1L);
        assertThat(response).isNotNull();
        assertThat(response.getAmount()).isEqualTo(new BigDecimal("1000.00"));
        assertThat(response.getCategory()).isEqualTo("Salary");
        verify(recordRepository).findById(1L);
    }

    @Test
    void testGetRecordByIdNotFound() {
        when(recordRepository.findById(999L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> recordService.getRecordById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Record not found with id: 999");
        verify(recordRepository).findById(999L);
    }

    @Test
    void testGetRecords() {
        FinancialRecord record2 = FinancialRecord.builder()
                .id(2L)
                .amount(new BigDecimal("500.00"))
                .type(TransactionType.EXPENSE)
                .category("Food")
                .date(LocalDate.now())
                .createdBy(user)
                .createdAt(LocalDateTime.now())
                .build();
        FinancialRecordFilter filter = new FinancialRecordFilter(null, null, null, null);
        when(recordRepository.findAll(any(Specification.class))).thenReturn(Arrays.asList(record, record2));
        List<FinancialRecordResponse> response = recordService.getRecords(filter);
        assertThat(response).hasSize(2);
        assertThat(response.get(0).getAmount()).isEqualTo(new BigDecimal("1000.00"));
        assertThat(response.get(1).getAmount()).isEqualTo(new BigDecimal("500.00"));
        verify(recordRepository).findAll(any(Specification.class));
    }

    @Test
    void testGetRecordsWithInvalidDateRange() {
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = LocalDate.now();
        FinancialRecordFilter filter = new FinancialRecordFilter(null, null, startDate, endDate);
        assertThatThrownBy(() -> recordService.getRecords(filter))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("startDate must not be after endDate");
    }

    @Test
    void testUpdateRecord() {
        when(recordRepository.findById(1L)).thenReturn(Optional.of(record));
        FinancialRecordRequest updateRequest = new FinancialRecordRequest();
        updateRequest.setAmount(new BigDecimal("1500.00"));
        updateRequest.setType(TransactionType.INCOME);
        updateRequest.setCategory("Updated Salary");
        updateRequest.setDate(LocalDate.now());
        updateRequest.setNotes("Updated notes");
        FinancialRecord updatedRecord = FinancialRecord.builder()
                .id(1L)
                .amount(new BigDecimal("1500.00"))
                .type(TransactionType.INCOME)
                .category("Updated Salary")
                .date(LocalDate.now())
                .notes("Updated notes")
                .createdBy(user)
                .createdAt(record.getCreatedAt())
                .build();
        when(recordRepository.save(any(FinancialRecord.class))).thenReturn(updatedRecord);
        FinancialRecordResponse response = recordService.updateRecord(1L, updateRequest);
        assertThat(response).isNotNull();
        assertThat(response.getAmount()).isEqualTo(new BigDecimal("1500.00"));
        assertThat(response.getCategory()).isEqualTo("Updated Salary");
        verify(recordRepository).findById(1L);
        verify(recordRepository).save(any(FinancialRecord.class));
    }

    @Test
    void testUpdateRecordNotFound() {
        when(recordRepository.findById(999L)).thenReturn(Optional.empty());
        FinancialRecordRequest updateRequest = new FinancialRecordRequest();
        updateRequest.setAmount(new BigDecimal("1000.00"));
        assertThatThrownBy(() -> recordService.updateRecord(999L, updateRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Record not found with id: 999");
        verify(recordRepository).findById(999L);
        verify(recordRepository, never()).save(any());
    }

    @Test
    void testDeleteRecord() {
        when(recordRepository.existsById(1L)).thenReturn(true);
        recordService.deleteRecord(1L);
        verify(recordRepository).existsById(1L);
        verify(recordRepository).deleteById(1L);
    }

    @Test
    void testDeleteRecordNotFound() {
        when(recordRepository.existsById(999L)).thenReturn(false);
        assertThatThrownBy(() -> recordService.deleteRecord(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Record not found with id: 999");
        verify(recordRepository).existsById(999L);
        verify(recordRepository, never()).deleteById(any());
    }

    @Test
    void testGetRecordsWithTypeFilter() {
        FinancialRecordFilter filter = new FinancialRecordFilter(TransactionType.INCOME, null, null, null);
        when(recordRepository.findAll(any(Specification.class))).thenReturn(Collections.singletonList(record));
        List<FinancialRecordResponse> response = recordService.getRecords(filter);
        assertThat(response).hasSize(1);
        assertThat(response.getFirst().getType()).isEqualTo("INCOME");
        verify(recordRepository).findAll(any(Specification.class));
    }

    @Test
    void testGetRecordsWithCategoryFilter() {
        FinancialRecordFilter filter = new FinancialRecordFilter(null, "Salary", null, null);
        when(recordRepository.findAll(any(Specification.class))).thenReturn(Collections.singletonList(record));
        List<FinancialRecordResponse> response = recordService.getRecords(filter);
        assertThat(response).hasSize(1);
        assertThat(response.getFirst().getCategory()).isEqualTo("Salary");
        verify(recordRepository).findAll(any(Specification.class));
    }
}
