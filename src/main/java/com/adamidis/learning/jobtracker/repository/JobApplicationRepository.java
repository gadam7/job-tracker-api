package com.adamidis.learning.jobtracker.repository;

import com.adamidis.learning.jobtracker.application.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
    // Additional query methods can be defined here if needed
    List<JobApplication> findAllByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<JobApplication> findByIdAndUserId(Long id, Long userId);
}
