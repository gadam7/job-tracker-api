package com.adamidis.learning.jobtracker.job;

import com.adamidis.learning.jobtracker.application.ApplicationStatus;
import com.adamidis.learning.jobtracker.application.JobApplication;
import com.adamidis.learning.jobtracker.job.dto.JobApplicationRequest;
import com.adamidis.learning.jobtracker.job.dto.JobApplicationResponse;
import com.adamidis.learning.jobtracker.repository.JobApplicationRepository;
import com.adamidis.learning.jobtracker.repository.UserRepository;
import com.adamidis.learning.jobtracker.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JobApplicationService {

    private final JobApplicationRepository jobApplicationRepository;
    private final UserRepository userRepository;

    @Transactional
    public JobApplicationResponse createJobApplication(JobApplicationRequest jobApplicationRequest, Authentication authentication) {
        User user = getCurrentUser(authentication);

        JobApplication jobApplication = JobApplication.builder()
                .user(user)
                .company(jobApplicationRequest.companyName())
                .role(jobApplicationRequest.positionRole())
                .status(ApplicationStatus.valueOf(jobApplicationRequest.status()))
                .location(jobApplicationRequest.location())
                .salaryMin(jobApplicationRequest.salaryMin())
                .salaryMax(jobApplicationRequest.salaryMax())
                .jobUrl(jobApplicationRequest.jobUrl())
                .appliedDate(jobApplicationRequest.appliedDate())
                .followUpDate(jobApplicationRequest.followUpDate())
                .notes(jobApplicationRequest.notes())
                .build();

        return jobApplicationResponse(jobApplicationRepository.save(jobApplication));
    }

    @Transactional(readOnly = true)
    public List<JobApplicationResponse> findMyAll(Authentication authentication) {
        Long userId = getCurrentUser(authentication).getId();
        return jobApplicationRepository.findAllByUserIdOrderByCreatedAtDesc(userId).stream().map(this::jobApplicationResponse).toList();
    }

    @Transactional(readOnly = true)
    public JobApplicationResponse findMyById(Long id, Authentication authentication) {
        Long userId = getCurrentUser(authentication).getId();
        JobApplication application = jobApplicationRepository.findByIdAndUserId(id, userId).orElseThrow(() -> new IllegalArgumentException("Job application not found"));
        return jobApplicationResponse(application);
    }

    @Transactional
    public JobApplicationResponse updateMyApplication(Long id, JobApplicationRequest request, Authentication authentication) {
        Long userId = getCurrentUser(authentication).getId();
        JobApplication application = jobApplicationRepository.findByIdAndUserId(id, userId).orElseThrow(() -> new IllegalArgumentException("Job application not found"));

        application.setCompany(request.companyName());
        application.setRole(request.positionRole());
        application.setStatus(ApplicationStatus.valueOf(request.status()));
        application.setLocation(request.location());
        application.setSalaryMin(request.salaryMin());
        application.setSalaryMax(request.salaryMax());
        application.setJobUrl(request.jobUrl());
        application.setAppliedDate(request.appliedDate());
        application.setFollowUpDate(request.followUpDate());
        application.setNotes(request.notes());

        return jobApplicationResponse(jobApplicationRepository.save(application));
    }

    @Transactional
    public void deleteMyApplication(Long id, Authentication authentication) {
        Long userId = getCurrentUser(authentication).getId();
        JobApplication application = jobApplicationRepository.findByIdAndUserId(id, userId).orElseThrow(() -> new IllegalArgumentException("Job application not found"));
        jobApplicationRepository.delete(application);
    }

    private JobApplicationResponse jobApplicationResponse(JobApplication save) {
        return new JobApplicationResponse(
                save.getId(),
                save.getUser().getId(),
                save.getCompany(),
                save.getRole(),
                save.getStatus().name(),
                save.getLocation(),
                save.getSalaryMin(),
                save.getSalaryMax(),
                save.getJobUrl(),
                save.getAppliedDate(),
                save.getFollowUpDate(),
                save.getNotes(),
                save.getCreatedAt(),
                save.getUpdatedAt()
        );
    }

    private User getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        return userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("Registered user not found with this email: " + email));
    }
}
