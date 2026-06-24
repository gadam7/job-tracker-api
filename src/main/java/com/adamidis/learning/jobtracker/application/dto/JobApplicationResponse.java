package com.adamidis.learning.jobtracker.application.dto;

import com.adamidis.learning.jobtracker.application.ApplicationStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record JobApplicationResponse(
        Long id,
        String company,
        String role,
        ApplicationStatus status,
        String location,
        BigDecimal salaryMin,
        BigDecimal salaryMax,
        String jobUrl,
        LocalDate appliedDate,
        LocalDate followUpDate,
        String notes,
        Instant createdAt,
        Instant updatedAt
) {
}
