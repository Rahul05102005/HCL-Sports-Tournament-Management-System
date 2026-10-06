package com.sportx.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "matches")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatchEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fixture_id", unique = true, nullable = false)
    private FixtureEntity fixture;

    @Column(name = "home_score", nullable = false)
    @Builder.Default
    private Integer homeScore = 0;

    @Column(name = "away_score", nullable = false)
    @Builder.Default
    private Integer awayScore = 0;

    @Column(name = "match_status", nullable = false, length = 30)
    @Builder.Default
    private String matchStatus = "SCHEDULED"; // SCHEDULED, LIVE, HALF_TIME, COMPLETED, ABANDONED

    @Column(name = "current_minute")
    private Integer currentMinute;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "winner_team_id")
    private TeamEntity winnerTeam;

    @Column(name = "is_draw", nullable = false)
    @Builder.Default
    private Boolean isDraw = false;

    @Column(length = 255)
    private String notes;
}
