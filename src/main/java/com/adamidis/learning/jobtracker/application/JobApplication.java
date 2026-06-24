package com.adamidis.learning.jobtracker.application;

import com.adamidis.learning.jobtracker.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "job_applications",
indexes = {
        @Index(name = "idx_app_user_status", columnList = "user_id, status"),
        @Index(name = "idx_app_user_company", columnList = "user_id, company"),
        @Index(name = "idx_app_user_followup", columnList = "user_id, follow_up_date")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String company;

    @Column(nullable = false)
    private String role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status;

    private String location;

    @Column(name = "salary_min", scale = 2)
    private BigDecimal salaryMin;

    @Column(name = "salary_max", scale = 2)
    private BigDecimal salaryMax;

    @Column(name = "job_url")
    private String jobUrl;

    @Column(name = "applied_date")
    private LocalDate appliedDate;

    @Column(name = "follow_up_date")
    private LocalDate followUpDate;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.status == null) {
            this.status = ApplicationStatus.SAVED;
        }
    }

    @PreUpdate
    void preUpdate() {
        this.updatedAt = Instant.now();
    }
}
