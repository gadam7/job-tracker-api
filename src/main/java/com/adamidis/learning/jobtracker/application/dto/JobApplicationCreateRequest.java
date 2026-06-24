package com.adamidis.learning.jobtracker.application.dto;

import com.adamidis.learning.jobtracker.application.ApplicationStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record JobApplicationCreateRequest(
        @NotBlank @Size(max = 140) String company,
        @NotBlank @Size(max = 140) String role,
        ApplicationStatus status,
        @Size(max = 120) String location,
        BigDecimal salaryMin,
        BigDecimal salaryMax,
        @Size(max = 500) String jobUrl,
        LocalDate appliedDate,
        LocalDate followUpDate,
        String notes) {}
