package io.zorvyn.task.financedashboard.service;

import io.zorvyn.task.financedashboard.dto.FinancialRecordFilter;
import io.zorvyn.task.financedashboard.dto.FinancialRecordRequest;
import io.zorvyn.task.financedashboard.dto.FinancialRecordResponse;
import io.zorvyn.task.financedashboard.exception.ResourceNotFoundException;
import io.zorvyn.task.financedashboard.model.FinancialRecord;
import io.zorvyn.task.financedashboard.model.User;
import io.zorvyn.task.financedashboard.repository.FinancialRecordRepository;
import io.zorvyn.task.financedashboard.repository.FinancialRecordSpecification;
import io.zorvyn.task.financedashboard.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

        return mapToResponse(recordRepository.save(record));
    }

    public List<FinancialRecordResponse> getRecords(FinancialRecordFilter filter) {
        if (filter.startDate() != null && filter.endDate() != null
                && filter.startDate().isAfter(filter.endDate())) {
            throw new IllegalArgumentException("startDate must not be after endDate");
        }

        return recordRepository.findAll(FinancialRecordSpecification.withFilter(filter))
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public FinancialRecordResponse getRecordById(Long id) {
        return recordRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Record not found with id: " + id));
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

        return mapToResponse(recordRepository.save(record));
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
        return userRepository.findByUsername(authentication.getName())
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