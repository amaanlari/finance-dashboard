package io.zorvyn.task.financedashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PeriodInfo {
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer daysCovered;
    private String period;  // "Q1 2026", "Jan 2026", "Custom", etc.
}

