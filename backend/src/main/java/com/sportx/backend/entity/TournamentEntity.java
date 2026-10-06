package com.sportx.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tournaments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TournamentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sport_id", nullable = false)
    private SportEntity sport;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "organizer_id", nullable = false)
    private UserEntity organizer;

    @Column(nullable = false, length = 30)
    @Builder.Default
    private String format = "ROUND_ROBIN"; // ROUND_ROBIN, KNOCKOUT, HYBRID

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(nullable = false, length = 30)
    @Builder.Default
    private String status = "UPCOMING"; // UPCOMING, ONGOING, COMPLETED, CANCELLED

    @Column(name = "min_teams")
    @Builder.Default
    private Integer minTeams = 4;

    @Column(name = "max_teams")
    @Builder.Default
    private Integer maxTeams = 16;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
