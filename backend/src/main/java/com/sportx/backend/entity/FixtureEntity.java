package com.sportx.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "fixtures")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FixtureEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_id", nullable = false)
    private TournamentEntity tournament;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "home_team_id", nullable = false)
    private TeamEntity homeTeam;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "away_team_id", nullable = false)
    private TeamEntity awayTeam;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "venue_id")
    private VenueEntity venue;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "referee_id")
    private UserEntity referee;

    @Column(name = "scheduled_time", nullable = false)
    private LocalDateTime scheduledTime;

    @Column(name = "round_name", length = 50)
    @Builder.Default
    private String roundName = "Group Stage";

    @Column(nullable = false, length = 30)
    @Builder.Default
    private String status = "SCHEDULED"; // SCHEDULED, IN_PROGRESS, COMPLETED, POSTPONED, CANCELLED
}
