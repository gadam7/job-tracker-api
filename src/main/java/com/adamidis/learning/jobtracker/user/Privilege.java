package com.adamidis.learning.jobtracker.user;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "privileges")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Privilege {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name; // e.g. APPLICATION_WRITE

    @Column
    private String description;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @ManyToMany(mappedBy = "privileges")
    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    @PrePersist
    void prePersist() {
        this.createdAt = Instant.now();
    }
}
