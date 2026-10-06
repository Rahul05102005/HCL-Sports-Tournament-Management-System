package com.sportx.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "teams")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "short_name", length = 10)
    private String shortName;

    @Column(name = "college_name", nullable = false, length = 150)
    private String collegeName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_id", nullable = false)
    private TournamentEntity tournament;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private UserEntity manager;

    @Column(name = "logo_url", length = 255)
    private String logoUrl;

    @Column(name = "registration_status", nullable = false, length = 30)
    @Builder.Default
    private String registrationStatus = "APPROVED"; // PENDING, APPROVED, REJECTED

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
