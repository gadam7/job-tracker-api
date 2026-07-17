package com.adamidis.learning.jobtracker.job.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record JobApplicationResponse(
        Long id,
        Long userId,
        String companyName,
        String positionRole,
        String status,
        String location,
        BigDecimal salaryMin,
        BigDecimal salaryMax,
        String jobUrl,
        LocalDate appliedDate,
        LocalDate followUpDate,
        String notes,
        java.time.Instant createdAt,
        java.time.Instant updatedAt) {}
