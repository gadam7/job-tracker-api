package com.adamidis.learning.jobtracker.job.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record JobApplicationRequest(
        @NotBlank @Size(max = 120) String companyName,
        @NotBlank @Size(max = 120) String positionRole,
        @NotBlank @Size(max = 20) String status,
        @Size(max = 120) String location,
        BigDecimal salaryMin,
        BigDecimal salaryMax,
        @Size(max = 500) String jobUrl,
        LocalDate appliedDate,
        LocalDate followUpDate,
        String notes) {}
