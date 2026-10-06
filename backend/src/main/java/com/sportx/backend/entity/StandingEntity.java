package com.sportx.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "standings", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"tournament_id", "team_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StandingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_id", nullable = false)
    private TournamentEntity tournament;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "team_id", nullable = false)
    private TeamEntity team;

    @Column(nullable = false)
    @Builder.Default
    private Integer played = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer won = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer drawn = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer lost = 0;

    @Column(name = "score_for", nullable = false)
    @Builder.Default
    private Integer scoreFor = 0;

    @Column(name = "score_against", nullable = false)
    @Builder.Default
    private Integer scoreAgainst = 0;

    @Column(name = "goal_difference", nullable = false)
    @Builder.Default
    private Integer goalDifference = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer points = 0;

    @Column(name = "form_guide", length = 20)
    @Builder.Default
    private String formGuide = "";

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    protected void onSave() {
        this.goalDifference = (this.scoreFor != null ? this.scoreFor : 0) - (this.scoreAgainst != null ? this.scoreAgainst : 0);
        this.updatedAt = LocalDateTime.now();
    }
}
