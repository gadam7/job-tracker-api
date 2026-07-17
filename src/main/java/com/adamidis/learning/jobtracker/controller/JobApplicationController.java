package com.adamidis.learning.jobtracker.controller;

import com.adamidis.learning.jobtracker.job.JobApplicationService;
import com.adamidis.learning.jobtracker.job.dto.JobApplicationRequest;
import com.adamidis.learning.jobtracker.job.dto.JobApplicationResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class JobApplicationController {

    private final JobApplicationService applicationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('APPLICATION_WRITE')")
    public JobApplicationResponse create(@Valid @RequestBody JobApplicationRequest request, Authentication authentication) {
        return applicationService.createJobApplication(request, authentication);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('APPLICATION_READ')")
    public List<JobApplicationResponse> findMyAll(Authentication authentication) {
        return applicationService.findMyAll(authentication);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('APPLICATION_READ')")
    public JobApplicationResponse findMyById(@PathVariable Long id, Authentication authentication) {
        return applicationService.findMyById(id, authentication);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('APPLICATION_WRITE')")
    public JobApplicationResponse updateMy(@PathVariable Long id, @Valid @RequestBody JobApplicationRequest request, Authentication authentication) {
        return applicationService.updateMyApplication(id, request, authentication);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('APPLICATION_DELETE')")
    public void deleteMy(@PathVariable Long id, Authentication authentication) {
        applicationService.deleteMyApplication(id, authentication);
    }
}