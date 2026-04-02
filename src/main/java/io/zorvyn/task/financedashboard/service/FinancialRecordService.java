package io.zorvyn.task.financedashboard.service;

import io.zorvyn.task.financedashboard.dto.FinancialRecordRequest;
import io.zorvyn.task.financedashboard.dto.FinancialRecordResponse;
import io.zorvyn.task.financedashboard.exception.ResourceNotFoundException;
import io.zorvyn.task.financedashboard.model.FinancialRecord;
import io.zorvyn.task.financedashboard.model.TransactionType;
import io.zorvyn.task.financedashboard.model.User;
import io.zorvyn.task.financedashboard.repository.FinancialRecordRepository;
import io.zorvyn.task.financedashboard.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FinancialRecordService {

    private final FinancialRecordRepository recordRepository;
    private final UserRepository userRepository;

    @Transactional
    public FinancialRecordResponse createRecord(FinancialRecordRequest request) {
        User currentUser = getCurrentUser();

        FinancialRecord record = FinancialRecord.builder()
                .amount(request.getAmount())
                .type(request.getType())
                .category(request.getCategory())
                .date(request.getDate())
                .notes(request.getNotes())
                .createdBy(currentUser)
                .build();

        record = recordRepository.save(record);
        return mapToResponse(record);
    }

    public List<FinancialRecordResponse> getAllRecords() {
        return recordRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public FinancialRecordResponse getRecordById(Long id) {
        FinancialRecord record = recordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Record not found with id: " + id));
        return mapToResponse(record);
    }

    public List<FinancialRecordResponse> getRecordsByType(TransactionType type) {
        return recordRepository.findByType(type).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<FinancialRecordResponse> getRecordsByCategory(String category) {
        return recordRepository.findByCategory(category).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<FinancialRecordResponse> getRecordsByDateRange(LocalDate startDate, LocalDate endDate) {
        return recordRepository.findByDateBetween(startDate, endDate).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<FinancialRecordResponse> getRecentRecords() {
        return recordRepository.findTop10ByOrderByDateDesc().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public FinancialRecordResponse updateRecord(Long id, FinancialRecordRequest request) {
        FinancialRecord record = recordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Record not found with id: " + id));

        record.setAmount(request.getAmount());
        record.setType(request.getType());
        record.setCategory(request.getCategory());
        record.setDate(request.getDate());
        record.setNotes(request.getNotes());

        record = recordRepository.save(record);
        return mapToResponse(record);
    }

    @Transactional
    public void deleteRecord(Long id) {
        if (!recordRepository.existsById(id)) {
            throw new ResourceNotFoundException("Record not found with id: " + id);
        }
        recordRepository.deleteById(id);
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Current user not found"));
    }

    private FinancialRecordResponse mapToResponse(FinancialRecord record) {
        return FinancialRecordResponse.builder()
                .id(record.getId())
                .amount(record.getAmount())
                .type(record.getType().name())
                .category(record.getCategory())
                .date(record.getDate())
                .notes(record.getNotes())
                .createdBy(record.getCreatedBy().getUsername())
                .createdAt(record.getCreatedAt())
                .updatedAt(record.getUpdatedAt())
                .build();
    }
}
